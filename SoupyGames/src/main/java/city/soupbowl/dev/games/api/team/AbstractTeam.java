package city.soupbowl.dev.games.api.team;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class AbstractTeam implements Team {

    private final String name;
    private final Set<UUID> members = new HashSet<>();
    private Color teamColor;

    @Override
    public String name() {
        return name;
    }

    @Override
    public @Nullable Color color() {
        return teamColor;
    }

    @Override
    public void color(@NotNull Color color) {
        this.teamColor = color;
    }

    @Override
    public Set<UUID> members() {
        return Set.of();
    }

    @Override
    public boolean add(Player player) {
        return members.add(player.getUniqueId());
    }

    @Override
    public boolean remove(Player player) {
        return members.add(player.getUniqueId());
    }

    @Override
    public void broadcast(Component message) {
        for (UUID uuid : members) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

}
