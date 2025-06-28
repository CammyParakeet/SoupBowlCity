package city.soupbowl.dev.games.manager;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.api.service.AviaraManager;
import cc.aviara.api.service.Manager;
import cc.aviara.registry.KeyedRegistry;
import city.soupbowl.dev.games.api.Game;
import com.google.auto.service.AutoService;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@AutoService(AviaraBean.class)
@AviaraManager
public class GameManager extends KeyedRegistry.ConcurrentKeyedRegistry<String, Game> implements Manager {

    public Set<Game> getGames() {
        return new HashSet<>(getAll().values());
    }

    public Optional<Game> getGame(String id) {
        return Optional.ofNullable(get(id));
    }

    public void registerGame(Game game) {
        register(game.id(), game);
    }

    public Optional<Game> gameOf(Player player) {
        return getGames().stream().filter(g -> g.players().contains(player.getUniqueId())).findFirst();
    }

    public boolean isGameRunning(String id) {
        return getGame(id).map(Game::isRunning).orElse(false);
    }

    public void stopGame(String id) {
        getGame(id).ifPresent(Game::stop);
    }

    public void tickAll() {
        for (Game game : getGames()) {
            if (game.isRunning()) game.tick();
        }
    }

}
