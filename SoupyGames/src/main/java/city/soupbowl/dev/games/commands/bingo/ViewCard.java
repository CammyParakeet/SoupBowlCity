package city.soupbowl.dev.games.commands.bingo;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.annotations.AviaraComponent;
import cc.aviara.core.commands.AbstractAviaraSubCommand;
import cc.aviara.core.commands.builder.CommandBuilder;
import city.soupbowl.dev.games.api.Game;
import city.soupbowl.dev.games.games.bingo.BingoGame;
import city.soupbowl.dev.games.games.bingo.BingoTeam;
import city.soupbowl.dev.games.games.bingo.card.BingoCard;
import city.soupbowl.dev.games.games.bingo.card.BingoCardMenu;
import city.soupbowl.dev.games.manager.GameManager;
import city.soupbowl.dev.utils.MiniMsg;
import com.google.auto.service.AutoService;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@AutoService(AviaraBean.class)
@AviaraComponent
public class ViewCard implements AviaraBean {

    @Inject private GameManager gameManager;

    public AbstractAviaraSubCommand get() {
        return CommandBuilder.create("view-card")
                .aliases("view", "view-card", "card")
                .playerOnly(true)
                .permission("soupy.game.bingo.admin")
                .executor(ctx -> {
                    Player viewer = (Player) ctx.sender();

                    if (!gameManager.isGameRunning("bingo")) {
                        MiniMsg.sendMsg(viewer, "<red>No active bingo game!");
                        return;
                    }

                    String inputName = ctx.args()[0];

                    BingoGame bingo = (BingoGame) gameManager.getGame("bingo").orElseThrow();
                    BingoTeam team = bingo.teams().stream()
                            .filter(t -> t.name().equalsIgnoreCase(inputName))
                            .findFirst().orElse(null);

                    if (team == null) {
                        MiniMsg.sendMsg(viewer, "<red>Team '<white>" + inputName + "</white>' not found.");
                        return;
                    }

                    BingoCard card = team.card();
                    if (card == null) {
                        MiniMsg.sendMsg(viewer, "<red>That team doesn't have a bingo card yet.");
                        return;
                    }

                    new BingoCardMenu(card, viewer).open();
                })
                .asSubcommand();
    }

}
