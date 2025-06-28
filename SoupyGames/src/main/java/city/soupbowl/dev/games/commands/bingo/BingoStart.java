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

import java.util.Optional;

@AutoService(AviaraBean.class)
@AviaraComponent
public class BingoStart implements AviaraBean {

    @Inject
    private GameManager gameManager;

    public AbstractAviaraSubCommand get() {
        return CommandBuilder.create("start")
                .permission("soupy.game.bingo.admin")
                .usage("/bingo start")
                .executor(ctx -> {
                    Optional<BingoGame> bingo = gameManager.getGame("bingo")
                            .filter(g -> g instanceof BingoGame)
                            .map(g -> (BingoGame) g);

                    if (bingo.isEmpty()) {
                        ctx.sender().sendMessage(MiniMsg.resolve("<red>No bingo game exists to start - use /bingo create"));
                        return;
                    }

                    BingoGame game = bingo.get();

                    if (game.phase() != GamePhase.WAITING) {
                        ctx.sender().sendMessage(MiniMsg.resolve("<red>Bingo game is already running or has started"));
                        return;
                    }

                    game.start();
                    ctx.sender().sendMessage(MiniMsg.resolve("<green>Started Bingo game"));
                })
                .asSubcommand();
    }

}
