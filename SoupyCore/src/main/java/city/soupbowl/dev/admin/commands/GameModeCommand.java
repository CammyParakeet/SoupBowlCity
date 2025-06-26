package city.soupbowl.dev.admin.commands;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.api.command.AviaraCommand;
import cc.aviara.api.command.CommandContext;
import cc.aviara.core.commands.AbstractAviaraCommand;
import cc.aviara.utils.bukkit.tab.TabUtils;
import city.soupbowl.dev.admin.utils.GameModeUtil;
import city.soupbowl.dev.utils.MiniMsg;
import com.google.auto.service.AutoService;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@AutoService(AviaraBean.class)
@AviaraCommand(
        name = "gamemode",
        aliases = {"mode", "gm"},
        permission = "soupy.gamemode",
        usageMessage = "/gamemode <c|s|a|sp> [player]"
)
public class GameModeCommand extends AbstractAviaraCommand {

    protected GameModeCommand() {
        super("gamemode");
        setUsage("/gamemode <c|s|a|sp> [player]");
        registerDefaultTabCompleter((ctx, partial) -> {
            int index = ctx.args().length - 1;

            return switch (index) {
                case 0 -> Stream.of("c", "s", "a", "sp")
                        .filter(s -> s.startsWith(partial.toLowerCase(Locale.ROOT)))
                        .toList();
                case 1 -> TabUtils.completePlayerNames(partial);
                default -> List.of();
            };
        });
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        if (ctx.args().length == 0) {
            return false;
        }

        String modeArg = ctx.args()[0].toLowerCase();
        GameMode mode = switch (modeArg) {
            case "c", "creative" -> GameMode.CREATIVE;
            case "s", "survival" -> GameMode.SURVIVAL;
            case "a", "adventure" -> GameMode.ADVENTURE;
            case "sp", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };

        if (mode == null) {
            MiniMsg.sendMsgOr(ctx.sender(),
                    "<red>Invalid mode: " + modeArg,
                    "Invalid mode: " + modeArg);
            return false;
        }

        return GameModeUtil.applyModeFromCommand(ctx, mode, false, 1); // strict: player must be valid
    }

}
