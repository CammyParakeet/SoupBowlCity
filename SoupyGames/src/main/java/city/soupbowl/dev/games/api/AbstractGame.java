package city.soupbowl.dev.games.api;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractGame implements Game {

    protected final Set<UUID> players = new HashSet<>();
    protected final Set<UUID> spectators = new HashSet<>();

    private GamePhase phase = GamePhase.WAITING;
    protected boolean running = false;

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public void start() {
        this.running = true;
    }

    @Override
    public GamePhase phase() {
        return phase;
    }

    @Override
    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    @Override
    public Set<UUID> spectators() {
        return spectators;
    }

    @Override
    public Set<UUID> players() {
        return players;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public String id() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean addPlayer(Player player) {
        return this.players.add(player.getUniqueId());
    }

    @Override
    public boolean removePlayer(Player player) {
        return this.players.remove(player.getUniqueId());
    }

}
