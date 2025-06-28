package city.soupbowl.dev.games.commands.bingo;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.annotations.AviaraComponent;
import cc.aviara.annotations.inject.Inject;
import cc.aviara.core.commands.AbstractAviaraSubCommand;
import cc.aviara.core.commands.builder.CommandBuilder;
import city.soupbowl.dev.games.games.bingo.BingoGame;
import city.soupbowl.dev.games.games.bingo.BingoGameFactory;
import city.soupbowl.dev.games.manager.GameManager;
import city.soupbowl.dev.utils.MiniMsg;
import com.google.auto.service.AutoService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@AutoService(AviaraBean.class)
@AviaraComponent
public class BingoCreate implements AviaraBean {

    private @Inject GameManager gameManager;
    private @Inject BingoGameFactory bingoFactory;

    public AbstractAviaraSubCommand get() {
        return CommandBuilder.create("create")
                .permission("soupy.game.bingo.admin")
                .usage("/bingo create [--global-card] <wait> <countdown> <duration>")
                .aliases("new", "create") // todo fix this in libs for subs
                .executor(ctx -> {
                    // todo - currently only allow 1 running
                    if (gameManager.isGameRunning("bingo")) {
                        ctx.sender().sendMessage(MiniMsg.resolve("<red>Bingo is already running."));
                        return;
                    }

                    List<String> args = List.of(ctx.args());

                    boolean shared = args.contains("--global-card");
                    List<String> numbers = args.stream()
                            .filter(a -> !a.startsWith("--"))
                            .toList();

                    int waitTime = parseOrDefault(numbers, 0, -1);
                    int countdown = parseOrDefault(numbers, 1, 15);
                    int duration = parseOrDefault(numbers, 2, 300);

                    BingoGame game = bingoFactory.create(
                            waitTime, countdown, duration, shared
                    );

                    gameManager.registerGame(game);

                    ctx.sender().sendMessage(MiniMsg.resolve("<green>Created Bingo game in <gray>WAITING</gray> mode."));
                    if (waitTime > 0) {
                        game.startWaiting();
                        ctx.sender().sendMessage(MiniMsg.resolve("<gray>Starting automatically in <yellow>" + waitTime + "</yellow> seconds."));
                    }
                })
                .defaultTabCompleter((ctx, partial) -> {
                    List<String> args = List.of(ctx.args());

                    // Determine which non-flag arg is being typed
                    List<String> nonFlags = args.stream().filter(a -> !a.startsWith("--")).toList();
                    List<String> suggestions = getStrings(partial, args, nonFlags);

                    return suggestions.stream()
                            .filter(s -> s.toLowerCase().startsWith(partial.toLowerCase()))
                            .toList();
                })
                .asSubcommand();
    }

    private @NotNull List<String> getStrings(String partial, List<String> args, List<String> nonFlags) {
        List<String> suggestions = new ArrayList<>();

        if (!args.contains("--global-card") && "--global-card".startsWith(partial)) {
            suggestions.add("--global-card");
        }

        switch (nonFlags.size()) {
            case 0 -> suggestions.add("<number:wait-time>");
            case 1 -> suggestions.add("<number:countdown-time>");
            case 2 -> suggestions.add("<number:game-time>");
        }
        return suggestions;
    }

    private int parseOrDefault(List<String> args, int index, int fallback) {
        if (index < args.size()) {
            try {
                return Integer.parseInt(args.get(index));
            } catch (NumberFormatException ignored) {}
        }
        return fallback;
    }


}
