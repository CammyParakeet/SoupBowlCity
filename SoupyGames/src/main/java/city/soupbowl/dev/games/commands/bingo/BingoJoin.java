package city.soupbowl.dev.games.commands.bingo;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.annotations.AviaraComponent;
import cc.aviara.annotations.inject.Inject;
import cc.aviara.core.commands.AbstractAviaraSubCommand;
import cc.aviara.core.commands.builder.CommandBuilder;
import city.soupbowl.dev.games.api.GamePhase;
import city.soupbowl.dev.games.games.bingo.BingoGame;
import city.soupbowl.dev.games.manager.GameManager;
import city.soupbowl.dev.utils.MiniMsg;
import com.google.auto.service.AutoService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@AutoService(AviaraBean.class)
@AviaraComponent
public class BingoJoin implements AviaraBean {

    @Inject private GameManager gameManager;

    public AbstractAviaraSubCommand get() {
        return CommandBuilder.create("join")
                .usage("/bingo join [--solo]")
                .playerOnly(true)
                .aliases("join", "enter")
                .executor(ctx -> {
                    String[] args = ctx.args();
                    boolean solo = Arrays.asList(args).contains("--solo");

                    // Filter out flags
                    List<String> nonFlags = Arrays.stream(args)
                            .filter(a -> !a.startsWith("--"))
                            .toList();

                    // Resolve target player
                    Player target;
                    if (!nonFlags.isEmpty()) {
                        String name = nonFlags.getFirst();
                        Player found = Bukkit.getPlayer(name);

                        if (found == null) {
                            ctx.sender().sendMessage(MiniMsg.resolve("<red>Could not find player '<white>" + name + "</white>'."));
                            return;
                        }

                        if (!(ctx.sender().hasPermission("soupy.game.bingo.admin") || ctx.sender().equals(found))) {
                            ctx.sender().sendMessage(MiniMsg.resolve("<red>You do not have permission to join other players."));
                            return;
                        }

                        target = found;
                    } else if (ctx.sender() instanceof Player p) {
                        target = p;
                    } else {
                        ctx.sender().sendMessage(MiniMsg.resolve("<red>Console must specify a player."));
                        return;
                    }

                    Optional<BingoGame> bingo = gameManager.getGame("bingo")
                            .filter(g -> g instanceof BingoGame)
                            .map(g -> (BingoGame) g);

                    if (bingo.isEmpty()) {
                        ctx.sender().sendMessage(MiniMsg.resolve("<red>No bingo game is running."));
                        return;
                    }

                    BingoGame game = bingo.get();

                    if (game.players().contains(target.getUniqueId())) {
                        ctx.sender().sendMessage(MiniMsg.resolve("<yellow>" + target.getName() + " is already in the game."));
                        return;
                    }

                    // Add to game
                    if (solo) {
                        game.markAsSolo(target);
                        target.sendMessage(MiniMsg.resolve("<gray>You joined as <yellow>solo</yellow>."));
                    }

                    game.addPlayer(target);

                    if (game.phase() == GamePhase.ACTIVE || game.phase() == GamePhase.COUNTDOWN) {
                        String teamName = (solo ? "solo-" : "late-") + target.getName().toLowerCase(Locale.ROOT);
                        game.addToTeam(teamName, target);

                        target.sendMessage(MiniMsg.resolve("<gray>You joined <white>late</white>. Your progress will still count."));
                    } else {
                        target.sendMessage(MiniMsg.resolve("<green>You joined the bingo game."));
                    }

                    // Notify sender if they targeted someone else
                    if (!ctx.sender().equals(target)) {
                        ctx.sender().sendMessage(MiniMsg.resolve("<gray>Added <yellow>" + target.getName() + "</yellow> to the game."));
                    }
                })
                .defaultTabCompleter((ctx, partial) -> {
                    return List.of("--solo");
//                    List<String> suggestions = new ArrayList<>();
//                    if (!List.of(ctx.args()).contains("--solo") && "--solo".startsWith(partial)) {
//                        suggestions.add("--solo");
//                    }
//                    return suggestions;
                })
                .asSubcommand();
    }

}
