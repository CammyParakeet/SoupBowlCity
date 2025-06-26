package city.soupbowl.dev.player.inventory;

import city.soupbowl.dev.utils.InventoryUtils;
import city.soupbowl.dev.utils.MiniMsg;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Utility for creating enhanced admin views of a player's inventory
 */
@UtilityClass
public class SoupyInventories {

    public Inventory createAdminInventoryView(@NotNull Player target, boolean readOnly) {
        Inventory view = Bukkit.createInventory(
                new AdminInventoryHolder(target.getUniqueId(), readOnly),
                InventoryUtils.slotFromRow(6),
                Component.text(target.getName() + "'s Inventory"));

        PlayerInventory targetInv = target.getInventory();

        // Armor (feet -> head)
        view.setItem(0, targetInv.getBoots());
        view.setItem(1, targetInv.getLeggings());
        view.setItem(2, targetInv.getChestplate());
        view.setItem(3, targetInv.getHelmet());

        // Player Head
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        skull.editMeta(meta -> {
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(target);
            }
            meta.displayName(MiniMsg.resolve("<bold>" + target.getName()));
            meta.addItemFlags(ItemFlag.values());
        });
        view.setItem(8, skull);

        // Offhand
        view.setItem(5, targetInv.getItemInOffHand());

        // Unused slots
        ItemStack unused = new ItemStack(Material.BARRIER);
        unused.editMeta(meta -> meta.displayName(MiniMsg.resolve("<reset><red>Unused Slot")));
        view.setItem(4, unused);
        view.setItem(6, unused);
        view.setItem(7, unused);

        // Main inventory (9-35) -> starts at slot 9 in this GUI
        ItemStack[] contents = target.getInventory().getContents();
        for (int i = 9; i < InventoryUtils.slotFromRow(4); i++) {
            view.setItem(i, contents[i]);
        }

        // Gap
        for (int slot = InventoryUtils.slotFromRow(4); slot < InventoryUtils.slotFromRow(5); slot++) {
            view.setItem(slot, unused);
        }

        // Hotbar (0-8) -> map to last row
        for (int i = 0; i < 9; i ++) {
            view.setItem(InventoryUtils.slotFromRow(5) + i, contents[i]);
        }

        return view;
    }


    public record AdminInventoryHolder(UUID targetPlayerId, boolean readOnly) implements InventoryHolder {

        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }

}
