package city.soupbowl.dev.games.api.team;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a team within a game. Implementations may hold additional metadata
 *
 * @author Cammy
 */
public interface Team {
    /**
     * Gets the unique name or ID of this team
     */
    String name();

    /**
     * Optional color out the gate for teams
     */
    @Nullable default Color color() { return null; }

    /**
     * Optional setter for color out the gate for teams
     */
    default void color(@NotNull Color color) {}

    /**
     * Gets all players in the team
     */
    Set<UUID> members();

    /**
     * Adds a player to the team
     */
    boolean add(Player player);

    /**
     * Removes a player from the team
     */
    boolean remove(Player player);

    /**
     * Sends a message to all online members
     */
    void broadcast(Component message);
}
