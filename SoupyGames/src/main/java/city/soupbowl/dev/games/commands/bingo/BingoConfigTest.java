package city.soupbowl.dev.games.commands.bingo;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.annotations.AviaraComponent;
import cc.aviara.core.commands.AbstractAviaraSubCommand;
import cc.aviara.core.commands.builder.CommandBuilder;
import city.soupbowl.dev.games.games.bingo.config.BingoTaskConfig;
import city.soupbowl.dev.utils.MiniMsg;
import com.google.auto.service.AutoService;

import javax.inject.Inject;
import java.util.List;

@AutoService(AviaraBean.class)
@AviaraComponent
public class BingoConfigTest implements AviaraBean {

    @Inject private BingoTaskConfig config;

    public AbstractAviaraSubCommand get() {
        return CommandBuilder.create("config")
                .aliases("config", "test-config")
                .permission("soupy.game.bingo.admin")
                .executor(ctx -> {
                    List<BingoTaskConfig.CollectTaskEntry> entries = config.getCollectEntries();

                    if (entries.isEmpty()) {
                        MiniMsg.sendMsgOr(ctx.sender(), "<red>No collect tasks configured!", "No collect tasks configured.");
                        return;
                    }

                    MiniMsg.sendMsgOr(ctx.sender(), "<gray>Logging all collect task entries:", "=== Collect Task Entries ===");

                    for (BingoTaskConfig.CollectTaskEntry entry : entries) {
                        String line = String.format(
                                "- %s [%d-%d] (weight=%.2f)",
                                entry.getItem().name(),
                                entry.getMin(),
                                entry.getMax(),
                                entry.getWeight()
                        );

                        MiniMsg.sendMsgOr(ctx.sender(), "<white>" + line, line);
                    }
                })
                .asSubcommand();
    }

}
