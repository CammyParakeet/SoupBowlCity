package city.soupbowl.dev.games.games.bingo.task;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface BingoTask {
    String id();
    Component displayName();
    default List<Component> extraLore() { return List.of(); };
    boolean isCompletedBy(Player player);

    /**
     * Returns the ItemStack representation for GUI display
     *
     * @param completed whether the task is completed
     * @return rendered item
     */
    ItemStack renderItem(boolean completed);
}
