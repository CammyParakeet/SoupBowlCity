package city.soupbowl.dev.commands;

import cc.aviara.api.command.CommandContext;
import city.soupbowl.dev.utils.MiniMsg;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Shared utility for applying gamemodes with optional sender fallback logic and consistent message output
 */
@UtilityClass
public class GameModeUtil {

    /**
     * Applies a target gamemode to a specified player argument or the sender, depending on context
     *
     * @param ctx               the command context
     * @param mode              the {@link GameMode} to apply
     * @param defaultToSender   whether to fall back to the sender if the target player is not found
     * @param playerArgIndex    the index of the player argument (usually 0 or 1)
     *
     * @return true if a valid player was modified or already in the correct mode; false if the operation failed
     */
    public static boolean applyModeFromCommand(CommandContext ctx, GameMode mode, boolean defaultToSender, int playerArgIndex) {
        CommandSender sender = ctx.sender();
        Player player;

        if (ctx.args().length >= 2) {
            // Player argument was provided
            Player target = Bukkit.getPlayer(ctx.args()[playerArgIndex]);

            if (target == null) {
                if (!defaultToSender) {
                    MiniMsg.sendMsgOr(sender,
                            "<red>Player not found.",
                            "Player not found.");
                    return false;
                }
                // Fall back to sender if allowed
                if (sender instanceof Player sPlayer) {
                    player = sPlayer;
                } else {
                    MiniMsg.sendMsgOr(sender,
                            "<red>You must be a player to use this without specifying a valid target.",
                            "Console cannot run this without a valid player.");
                    return false;
                }
            } else {
                player = target;
            }

        } else {
            // No player argument - fallback to sender only
            if (sender instanceof Player sPlayer) {
                player = sPlayer;
            } else {
                MiniMsg.sendMsgOr(sender,
                        "<red>You must be a player to use this without specifying a target.",
                        "Console cannot run this without a target.");
                return false;
            }
        }

        if (player.getGameMode() == mode) {
            MiniMsg.sendMsg(player, "<gray>You are already in <yellow>" + mode.name().toLowerCase());
            if (!player.equals(sender)) {
                MiniMsg.sendMsgOr(sender,
                        "<gray>" + player.getName() + " is already in <yellow>" + mode.name().toLowerCase(),
                        player.getName() + " is already in " + mode.name().toLowerCase());
            }
            return true;
        }

        player.setGameMode(mode);

        if (!player.equals(sender)) {
            MiniMsg.sendMsgOr(
                    sender,
                    "<gray>Set " + player.getName() + "'s gamemode to <yellow>" + mode.name().toLowerCase(),
                    "Set " + player.getName() + "'s gamemode to " + mode.name().toLowerCase());
        }

        MiniMsg.sendMsg(player, "<green>Your gamemode is now <yellow>" + mode.name().toLowerCase());
        return true;
    }

}
