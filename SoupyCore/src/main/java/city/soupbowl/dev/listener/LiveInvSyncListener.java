package city.soupbowl.dev.listener;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.annotations.composition.BukkitListener;
import cc.aviara.annotations.inject.Inject;
import cc.aviara.api.schedule.SchedulerService;
import city.soupbowl.dev.manager.InventoryMirrorManager;
import city.soupbowl.dev.player.inventory.InventoryViewSession;
import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.Set;

@Slf4j
//@AutoService(AviaraBean.class)
//@BukkitListener
public class LiveInvSyncListener implements Listener, AviaraBean {

    private InventoryMirrorManager mirrorManager;
    private SchedulerService scheduler;

    private void sync(Player target) {
        Set<Player> viewers = mirrorManager.getInvViewersOf(target);
        if (viewers.isEmpty()) return;

        log.debug("Target '{}' has viewers: {}", target.getName(), viewers.stream().map(Player::getName).toList());

        scheduler.runNextTick(() -> {
            Set<InventoryViewSession> sessions = mirrorManager.getSessionsOf(target.getUniqueId());
            if (sessions.isEmpty()) return;

            scheduler.runNextTick(() -> {
                for (InventoryViewSession session : sessions) {
                    if (session.type() != InventoryViewSession.ViewType.INVENTORY) continue;
                    Player viewer = Bukkit.getPlayer(session.viewerId());
                    if (viewer == null) continue;

                    log.debug("Sending update function with viewer '{}' for target '{}'", viewer.getName(), target.getName());
                    session.update(viewer, target); // invokes the registered sync logic
                }
            });
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player target)) return;
        sync(target);
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        sync(event.getPlayer());
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player target) {
            sync(target);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        sync(event.getPlayer());
    }

    @EventHandler
    public void onArmorChange(PlayerItemDamageEvent event) {
        sync(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        sync(event.getPlayer());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player target) {
            sync(target);
        }
    }

}
