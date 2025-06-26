package city.soupbowl.dev.manager;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.api.service.AviaraManager;
import cc.aviara.api.service.Manager;
import cc.aviara.registry.KeyedRegistry;
import city.soupbowl.dev.player.inventory.InventoryViewSession;
import com.google.auto.service.AutoService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@AutoService(AviaraBean.class)
@AviaraManager
public class InventoryMirrorManager extends KeyedRegistry.ConcurrentKeyedRegistry<UUID, Set<InventoryViewSession>> implements Manager {

    public void registerViewer(
            UUID viewer,
            UUID target,
            InventoryViewSession.ViewType viewType,
            boolean readOnly,
            @Nullable BiConsumer<Player, Player> updateHandler
            ) {
        getRegistry().computeIfAbsent(target, $ -> new HashSet<>())
                .add(new InventoryViewSession(viewer, viewType, readOnly, updateHandler));
    }

    public void unregisterViewer(UUID target, UUID viewer) {
        Set<InventoryViewSession> sessions = get(target);
        if (sessions == null) return;

        sessions.removeIf(session -> session.viewerId().equals(viewer));
        if (sessions.isEmpty()) remove(target);
    }

    public Set<InventoryViewSession> getSessionsOf(UUID targetId) {
        Set<InventoryViewSession> sessions = get(targetId);
        return (sessions == null) ? Set.of() : sessions;
    }

    public Set<Player> getInvViewersOf(Player target) {
        return getViewersOf(target, InventoryViewSession.ViewType.INVENTORY, true);
    }


    public Set<Player> getViewersOf(Player target, InventoryViewSession.ViewType type, boolean copy) {
        Set<Player> viewers = getSessionsOf(target.getUniqueId()).stream()
                .filter(s -> s.type() == type)
                .map(s -> Bukkit.getPlayer(s.viewerId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return (copy) ? new HashSet<>(viewers) : viewers;
    }

    @Override
    public void onDisable() {
        this.clear();
    }

}
