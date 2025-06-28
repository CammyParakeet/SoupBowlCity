package city.soupbowl.dev.games.api;

import cc.aviara.annotations.AviaraBean;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface Game extends AviaraBean {
    String id();
    boolean isRunning();
    Set<UUID> players();
    Set<UUID> spectators();

    GamePhase phase();
    void setPhase(GamePhase phase);

    boolean addPlayer(Player player);
    boolean removePlayer(Player player);

    void start();
    void stop();
    void tick();
}
