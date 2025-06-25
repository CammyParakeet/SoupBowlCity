package city.soupbowl.dev.commands;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.api.command.AviaraCommand;
import cc.aviara.api.command.CommandContext;
import cc.aviara.core.commands.AbstractAviaraCommand;
import com.google.auto.service.AutoService;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;

public class SimpleModeCommands {

    @AutoService(AviaraBean.class)
    @AviaraCommand(name = "gmc")
    static class GmcCommand extends AbstractAviaraCommand {
        protected GmcCommand() {
            super("gmc");
        }
        @Override
        public boolean execute(@NotNull CommandContext ctx) {
            return GameModeUtil.applyModeFromCommand(ctx, GameMode.CREATIVE, true, 0);
        }
    }

    @AutoService(AviaraBean.class)
    @AviaraCommand(name = "gms")
    static class GmsCommand extends AbstractAviaraCommand {
        protected GmsCommand() {
            super("gms");
        }
        @Override
        public boolean execute(@NotNull CommandContext ctx) {
            return GameModeUtil.applyModeFromCommand(ctx, GameMode.SURVIVAL, true, 0);
        }
    }

    @AutoService(AviaraBean.class)
    @AviaraCommand(name = "gma")
    static class GmaCommand extends AbstractAviaraCommand {
        protected GmaCommand() {
            super("gma");
        }
        @Override
        public boolean execute(@NotNull CommandContext ctx) {
            return GameModeUtil.applyModeFromCommand(ctx, GameMode.ADVENTURE, true, 0);
        }
    }

    @AutoService(AviaraBean.class)
    @AviaraCommand(name = "gmsp")
    static class GmspCommand extends AbstractAviaraCommand {
        protected GmspCommand() {
            super("gmsp");
        }
        @Override
        public boolean execute(@NotNull CommandContext ctx) {
            return GameModeUtil.applyModeFromCommand(ctx, GameMode.SPECTATOR, true, 0);
        }
    }

}
