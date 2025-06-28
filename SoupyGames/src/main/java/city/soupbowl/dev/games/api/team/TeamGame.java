package city.soupbowl.dev.games.api.team;

import city.soupbowl.dev.games.api.AbstractGame;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * An abstract base for any team-based game implementation
 * <p>
 * Provides team management features on top of {@link AbstractGame}
 *
 * @author Cammy
 */
public abstract class TeamGame<T extends Team> extends AbstractGame {

    @Setter
    @Getter
    protected int maxTeamSize;

    protected final Map<String, T> teams = new HashMap<>();

    /**
     * Adds a player to a team. Automatically creates the team if it does not exist
     *
     * @param name The team name
     * @param player The player to add
     */
    public void addToTeam(String name, Player player) {
        T team = teams.computeIfAbsent(name, this::createTeam);
        team.add(player);
        addPlayer(player);
    }

    /**
     * Removes a player from a team
     *
     * @param name The team name
     * @param player The player to remove
     */
    public void removeFromTeam(String name, Player player) {
        T team = teams.get(name);
        if (team != null) {
            team.remove(player);
            if (team.members().isEmpty()) teams.remove(name);
        }
        removePlayer(player);
    }

    /**
     * Gets the name of the team a player belongs to
     *
     * @param player The player
     * @return The team name or null
     */
    public @Nullable String getTeamOf(Player player) {
        UUID id = player.getUniqueId();
        return teams.values().stream()
                .filter(t -> t.members().contains(id))
                .map(Team::name)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the name of the team a player belongs to
     *
     * @param playerId The id of the player
     * @return The team name or null
     */
    public @Nullable String getTeamOfPlayer(UUID playerId) {
        return teams.values().stream()
                .filter(t -> t.members().contains(playerId))
                .map(Team::name)
                .findFirst()
                .orElse(null);
    }

    /**
     * Broadcasts a message to all players in a team
     *
     * @param name The team name
     * @param message The component to send
     */
    public void sendTeamMessage(String name, Component message) {
        T team = teams.get(name);
        if (team != null) team.broadcast(message);
    }

    public Collection<T> teams() {
        return teams.values();
    }

    /**
     * Factory method to create a new team by name
     */
    protected abstract T createTeam(String name);

    @Override
    public void stop() {
        teams.clear();
        super.stop();
    }
}
