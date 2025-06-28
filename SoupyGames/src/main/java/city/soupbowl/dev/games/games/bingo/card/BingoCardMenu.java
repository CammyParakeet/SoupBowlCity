package city.soupbowl.dev.games.games.bingo.card;

import city.soupbowl.dev.games.games.bingo.task.BingoTask;
import city.soupbowl.dev.utils.MiniMsg;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

@RequiredArgsConstructor
public class BingoCardMenu {

    private final BingoCard card;
    private final Player viewer;

    public Inventory build() {
        Inventory inventory = Bukkit.createInventory(
                new BingoCardHolder(card),
                9 * 6, // Full 6-row grid
                MiniMsg.resolve("<blue><bold>Bingo Card</bold></blue>")
        );

        List<BingoTask> tasks = card.tasks();
        int maxTasks = Math.min(tasks.size(), 9); // Only display up to 3x3

        int[] slots = {
                12, 13, 14, // row 2, columns 3-5
                21, 22, 23, // row 3
                30, 31, 32  // row 4
        };

        for (int i = 0; i < maxTasks; i++) {
            BingoTask task = tasks.get(i);
            boolean complete = card.isTaskCompleted(task);
            inventory.setItem(slots[i], task.renderItem(complete));
        }

        return inventory;
    }

    public void open() {
        viewer.openInventory(build());
    }

}
