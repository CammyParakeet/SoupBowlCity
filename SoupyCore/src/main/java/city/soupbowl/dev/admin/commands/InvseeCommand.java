package city.soupbowl.dev.admin.commands;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.annotations.inject.Inject;
import cc.aviara.api.command.AviaraCommand;
import cc.aviara.api.command.CommandContext;
import cc.aviara.core.commands.AbstractAviaraCommand;
import city.soupbowl.dev.manager.InventoryMirrorManager;
import city.soupbowl.dev.player.inventory.InventoryViewSession;
import city.soupbowl.dev.player.inventory.SoupyInventories;
import city.soupbowl.dev.utils.MiniMsg;
import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Slf4j
@AutoService(AviaraBean.class)
@AviaraCommand(
        name = "invsee",
        permission = "soupy.invsee",
        usageMessage = "/invsee <player> [top|bottom]",
        requiredArgs = 1,
        playerOnly = true
)
public class InvseeCommand extends AbstractAviaraCommand {

    @Inject
    private InventoryMirrorManager mirrorInventory;

    public InvseeCommand() {
        super("invsee");

        registerTabCompleter(0, (ctx, partial) -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(partial.toLowerCase()))
                .toList());

        registerDefaultTabCompleter((ctx, partial) -> {
            List<String> suggestions = new ArrayList<>();

            // Prevent duplicate flags
            boolean alreadyLive = Arrays.asList(ctx.args()).contains("--live");
            boolean alreadyReadOnly = Arrays.asList(ctx.args()).contains("--readonly");
            boolean hasMode = Arrays.stream(ctx.args()).anyMatch(arg -> arg.equalsIgnoreCase("top") || arg.equalsIgnoreCase("bottom"));

            if (!hasMode && Stream.of("top", "bottom").anyMatch(opt -> opt.startsWith(partial))) {
                suggestions.addAll(Stream.of("top", "bottom")
                        .filter(opt -> opt.startsWith(partial.toLowerCase()))
                        .toList());
            }

            if (!alreadyReadOnly && "--readonly".startsWith(partial.toLowerCase())) {
                suggestions.add("--readonly");
            }

            if (!alreadyLive && ctx.sender().hasPermission("soupy.invsee.admin") && "--live".startsWith(partial.toLowerCase())) {
                suggestions.add("--live");
            }

            return suggestions;
        });
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        if (!(ctx.sender() instanceof Player sender)) return false;

        Player target = Bukkit.getPlayer(ctx.args()[0]);
        if (target == null || !target.isOnline()) {
            MiniMsg.sendMsg(sender, "<red>That player is not online.");
            return true;
        }

        if (target.equals(sender)) {
            MiniMsg.sendMsg(sender, "<gray>You cannot open your own inventory with /invsee.");
            return true;
        }

        String mode = "bottom";
        boolean forceLive = false;
        boolean readOnly = false;

        for (int i = 1; i < ctx.args().length; i++) {
            String arg = ctx.args()[i].toLowerCase(Locale.ROOT);
            switch (arg) {
                case "top", "bottom" -> mode = arg;

                case "--live" -> {
                    if (sender.hasPermission("soupy.invsee.admin")) {
                        forceLive = true;
                    } else {
                        MiniMsg.sendMsg(sender, "<red>You do not have permission to use --live.");
                        return true;
                    }
                }

                case "--readonly" -> {
                    readOnly = true; // Explicit override
                }

                default -> {
                    MiniMsg.sendMsg(sender, "<red>Unknown argument: <gray>" + arg);
                    return true;
                }
            }
        }

        boolean effectiveReadonly = !forceLive || readOnly;

        switch (mode) {
            case "top" -> {
                // TODO live vs not
                Inventory top = target.getOpenInventory().getTopInventory();

                if (top.getType() == InventoryType.CRAFTING) {
                    MiniMsg.sendMsg(sender, "<gray>That player is not viewing a container.");
                    return true;
                }

                int size = top.getSize();

                Inventory proxy = Bukkit.createInventory(null, size, target.getName() + "'s Container");
                proxy.setContents(Arrays.copyOf(top.getContents(), size));

                sender.openInventory(proxy);
                MiniMsg.sendMsg(sender, "<green>Opening <yellow>" + target.getName() + "'s <green>open container.");
            }
            case "bottom" -> {
                Inventory view = SoupyInventories.createAdminInventoryView(target, effectiveReadonly);
                sender.openInventory(view);

                mirrorInventory.registerViewer(
                        sender.getUniqueId(),
                        target.getUniqueId(),
                        InventoryViewSession.ViewType.INVENTORY,
                        effectiveReadonly,
                        (viewer, playerViewed) -> {
                            log.debug("Updating viewer: '{}' with new view from target '{}'", viewer.getName(), playerViewed.getName());
                            Inventory updated = SoupyInventories.createAdminInventoryView(playerViewed, effectiveReadonly);
                            viewer.openInventory(updated);
                        }
                );
            }
        }



        return true;
    }


}
