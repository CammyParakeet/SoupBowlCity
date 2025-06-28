package city.soupbowl.dev.games.games.bingo.card;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
public class BingoCardHolder implements InventoryHolder {

    private final BingoCard card;

    public BingoCardHolder(BingoCard card) {
        this.card = card;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
