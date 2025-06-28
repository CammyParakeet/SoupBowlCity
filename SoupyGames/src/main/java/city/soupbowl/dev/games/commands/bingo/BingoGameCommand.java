package city.soupbowl.dev.games.commands.bingo;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.annotations.handler.PostConstruct;
import cc.aviara.annotations.inject.Inject;
import cc.aviara.api.command.AviaraCommand;
import cc.aviara.api.command.CommandContext;
import cc.aviara.core.commands.AbstractAviaraCommand;
import com.google.auto.service.AutoService;
import org.jetbrains.annotations.NotNull;

@AutoService(AviaraBean.class)
@AviaraCommand(
    name = "bingo",
    usageMessage = "/bingo <start|stop|solo|join|list>",
    aliases = "bingo-game",
    requiredArgs = 1
)
public class BingoGameCommand extends AbstractAviaraCommand {

    @Inject BingoCreate bingoCreate;
    @Inject BingoStart bingoStart;
    @Inject BingoStop bingoStop;
    @Inject BingoJoin bingoJoin;
    @Inject BingoConfigTest configTest;

    protected BingoGameCommand() {
        super("bingo");
    }

    @PostConstruct
    public void setupSub() {
        registerSubcommands(
                bingoCreate.get(),
                bingoStart.get(),
                bingoStop.get(),
                bingoJoin.get(),
                configTest.get()
        );
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        //ctx.sender().sendMessage(Component.text("Usage: /bingo <start|stop|solo|join|list>"));
        return false;
    }

}
