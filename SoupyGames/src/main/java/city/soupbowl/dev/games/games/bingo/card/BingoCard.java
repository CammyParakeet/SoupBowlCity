package city.soupbowl.dev.games.games.bingo.card;

import city.soupbowl.dev.games.games.bingo.task.BingoTask;
import city.soupbowl.dev.utils.MiniMsg;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Data
@Accessors(fluent = true)
public class BingoCard {

    private final UUID cardId = UUID.randomUUID();
    private final List<BingoTask> tasks = new ArrayList<>();
    private final Set<String> completed = new HashSet<>();

    public void markIfCompleted(Player player) {
        for (BingoTask task : tasks) {
            if (!completed.contains(task.id()) && task.isCompletedBy(player)) {
                completed.add(task.id());
                // Trigger glow update, sound, etc.
            }
        }
    }

    public boolean isTaskCompleted(BingoTask task) {
        return completed.contains(task.id());
    }

    public ItemStack createCardItem(Player player) {
        ItemStack paper = new ItemStack(Material.PAPER);
        paper.editMeta(meta -> {
            meta.displayName(MiniMsg.resolve("<gold><bold>Bingo Card</bold></gold>"));

            List<Component> lore = List.of(
                    MiniMsg.resolve("<gray>Right-click to view your tasks."),
                    MiniMsg.resolve("<dark_gray>Do not lose this item!"),
                    MiniMsg.resolve("<white>Card ID: <yellow>" + cardId().toString().substring(0, 8) + "</yellow>")
            );

            meta.lore(lore);
        });

        return paper;
    }

}
