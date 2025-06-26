package city.soupbowl.dev.admin.listener;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.annotations.composition.BukkitListener;
import cc.aviara.annotations.dev.Todo;
import cc.aviara.annotations.inject.Inject;
import cc.aviara.api.schedule.SchedulerService;
import city.soupbowl.dev.manager.InventoryMirrorManager;
import city.soupbowl.dev.player.inventory.SoupyInventories;
import city.soupbowl.dev.utils.InventoryUtils;
import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

@Slf4j
@AutoService(AviaraBean.class)
@BukkitListener
public class AdminInventoryListener implements Listener, AviaraBean {

    @Inject private SchedulerService scheduler;
    @Inject private InventoryMirrorManager mirrorInventory;

    @Todo(reason = "Online player view listener")
    @EventHandler
    public void onAdminInventoryClick(InventoryClickEvent event) {
        Inventory view = event.getInventory();
        if (!(view.getHolder() instanceof SoupyInventories.AdminInventoryHolder holder)) return;
        int slot = event.getRawSlot();

        if (holder.readOnly() || isInUnusedRow(slot)) {
            event.setCancelled(true);
            return;
        }

        boolean top = event.getView().getTopInventory() == event.getClickedInventory();

        Player target = Bukkit.getPlayer(holder.targetPlayerId());
        if (target == null) return;

        if (event.getClick().isShiftClick() && !top) {
            event.setCancelled(true);
            return;
        }

        scheduler.runNextTick(() -> {
            PlayerInventory inv = target.getInventory();

            switch (slot) {
                case 0 -> inv.setBoots(view.getItem(0));
                case 1 -> inv.setLeggings(view.getItem(1));
                case 2 -> inv.setChestplate(view.getItem(2));
                case 3 -> inv.setHelmet(view.getItem(3));
                case 4, 6, 7, 8 -> event.setCancelled(true);
                case 5 -> inv.setItemInOffHand(view.getItem(5));
                default -> {
                    if (slot >= 9 && slot < InventoryUtils.slotFromRow(4)) {
                        inv.setItem(slot, view.getItem(slot));
                    } else if (slot >= InventoryUtils.slotFromRow(5) && slot < InventoryUtils.slotFromRow(6)) {
                        inv.setItem(slot - InventoryUtils.slotFromRow(5), view.getItem(slot));
                    }
                }
            }
        });
    }

    private boolean isInUnusedRow(int slot) {
        return slot == 4 ||
                (slot > 5 && slot < 9) ||
                (slot >= InventoryUtils.slotFromRow(4)
                && slot < InventoryUtils.slotFromRow(5));
    }

    @EventHandler
    public void onAdminClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof SoupyInventories.AdminInventoryHolder holder)) return;
        if (holder.readOnly()) return;

        mirrorInventory.unregisterViewer(holder.targetPlayerId(), event.getPlayer().getUniqueId());

        Player target = Bukkit.getPlayer(holder.targetPlayerId());
        if (target == null) return;

        syncAdminMirrorToTarget(target, event.getInventory());
    }

    public static void syncAdminMirrorToTarget(Player target, Inventory mirror) {
        PlayerInventory inv = target.getInventory();

        inv.setBoots(mirror.getItem(0));
        inv.setLeggings(mirror.getItem(1));
        inv.setChestplate(mirror.getItem(2));
        inv.setHelmet(mirror.getItem(3));
        inv.setItemInOffHand(mirror.getItem(5));

        for (int i = 9; i < InventoryUtils.slotFromRow(4); i++) {
            inv.setItem(i, mirror.getItem(i));
        }
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, mirror.getItem(InventoryUtils.slotFromRow(5) + i));
        }
    }

}
