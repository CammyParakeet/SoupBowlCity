package city.soupbowl.dev.player.inventory;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.BiConsumer;

public record InventoryViewSession(
        UUID viewerId,
        ViewType type,
        boolean readOnly,
        @Nullable BiConsumer<Player /*viewer*/, Player /*target*/> updateHandler
) {
    public enum ViewType {
        INVENTORY,
        ENDER_CHEST,
        ARMOR,
        STATS,
        CUSTOM
    }
    public void update(Player viewer, Player target) {
        if (updateHandler != null) {
            updateHandler.accept(viewer, target);
        } else {
            viewer.updateInventory();
        }
    }
}
