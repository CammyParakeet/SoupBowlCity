package city.soupbowl.dev.utils;

import cc.aviara.annotations.dev.Todo;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Utility class for sending MiniMessage formatted text to players and console friendly fallbacks
 */
@Todo(reason = "This should be in libs utils for adv")
@UtilityClass
public class MiniMsg {

    private final MiniMessage miniMsg = MiniMessage.miniMessage();

    /**
     * Sends a MiniMessage-formatted message to a player
     *
     * @param player   the target player
     * @param rawText  the MiniMessage-formatted string (e.g. "&lt;green&gt;Success!")
     */
    public void sendMsg(Player player, String rawText) {
        player.sendMessage(miniMsg.deserialize(rawText));
    }

    /**
     * Sends a MiniMessage message to a player if the sender is a player, otherwise sends plain fallback text
     *
     * @param sender            the command sender
     * @param miniMessageText   the MiniMessage string to send if player
     * @param defaultText       plain text fallback for console or non-player senders
     */
    public void sendMsgOr(CommandSender sender, String miniMessageText, String defaultText) {
        if (sender instanceof Player p) {
            sendMsg(p, miniMessageText);
        } else {
            sender.sendMessage(defaultText);
        }
    }

}
