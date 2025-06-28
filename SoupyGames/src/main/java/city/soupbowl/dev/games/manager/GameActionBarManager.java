package city.soupbowl.dev.games.manager;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.annotations.AviaraComponent;
import cc.aviara.annotations.inject.Inject;
import cc.aviara.api.schedule.SchedulerService;
import cc.aviara.api.schedule.WrappedTask;
import cc.aviara.api.service.Manager;
import city.soupbowl.dev.games.api.Game;
import city.soupbowl.dev.games.api.team.Team;
import city.soupbowl.dev.utils.MiniMsg;
import com.google.auto.service.AutoService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@AutoService(AviaraBean.class)
@AviaraComponent(value = Manager.class, bindSingleton = true)
public class GameActionBarManager implements Manager {

    @Inject GameManager gameManager;
    @Inject SchedulerService scheduler;

    private final Map<UUID, WrappedTask> persistentBars = new HashMap<>();

    /**
     * Sends an action bar message to all players in a game
     */
    public void sendToGame(Game game, String message) {
        for (UUID uuid : game.players()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                send(player, message);
            }
        }
    }

    /**
     * Sends an action bar to one player
     */
    public void send(Player player, String message) {
        player.sendActionBar(MiniMsg.resolve(message));
    }

    /**
     * Sends an action bar message to all players on a team
     */
    public void sendToTeam(Team team, String message) {
        for (UUID uuid : team.members()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                send(player, message);
            }
        }
    }

    /**
     * Shows a persistent (repeating) action bar to a player using a dynamic supplier
     */
    public void showPersistent(Player player, Supplier<String> messageSupplier) {
        clearPersistent(player); // cancel any existing one

        WrappedTask task = scheduler.runTimerAsync(() -> {
            if (!player.isOnline()) {
                clearPersistent(player);
                return;
            }
            send(player, messageSupplier.get());
        }, 0, 1); // every tick

        persistentBars.put(player.getUniqueId(), task);
    }

    /**
     * Shows a persistent message to all players in a game
     */
    public void showPersistent(Game game, Supplier<String> messageSupplier) {
        for (UUID uuid : game.players()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                showPersistent(player, messageSupplier);
            }
        }
    }

    /**
     * Clears the persistent action bar message for a player
     */
    public void clearPersistent(Player player) {
        WrappedTask task = persistentBars.remove(player.getUniqueId());
        if (task != null) scheduler.cancelTask(task);
        player.sendActionBar(Component.empty());
    }

    /**
     * Clears all persistent messages in a game
     */
    public void clearAll(Game game) {
        for (UUID uuid : game.players()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                clearPersistent(player);
            }
        }
    }

}
