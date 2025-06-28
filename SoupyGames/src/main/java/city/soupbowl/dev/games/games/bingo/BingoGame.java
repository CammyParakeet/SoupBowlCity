package city.soupbowl.dev.games.games.bingo;

import cc.aviara.api.schedule.SchedulerService;
import city.soupbowl.dev.games.SoupyGames;
import city.soupbowl.dev.games.api.GamePhase;
import city.soupbowl.dev.games.api.team.Team;
import city.soupbowl.dev.games.api.team.TeamGame;
import city.soupbowl.dev.games.games.bingo.card.BingoCard;
import city.soupbowl.dev.games.manager.GameActionBarManager;
import city.soupbowl.dev.games.manager.GameManager;
import city.soupbowl.dev.utils.ColorUtils;
import city.soupbowl.dev.utils.MiniMsg;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BingoGame extends TeamGame<BingoTeam> {

    private final int waitingTimeSeconds;
    private final int startCountdownTimeSeconds;
    private final int gameDurationSeconds;

    private final boolean useGlobalCard;

    private final SchedulerService scheduler;
    private final SoupyGames gamesPlugin;
    private final GameManager gameManager;
    private final GameActionBarManager actionBarManager;

    private final Set<UUID> soloPlayers = new HashSet<>();

    private final Map<UUID, BingoCard> allCards = new HashMap<>();

    public BingoGame(
            int waitingTimeSeconds,
            int startCountdownTimeSeconds,
            int gameDurationSeconds,
            boolean useGlobalCard,
            @NotNull final SchedulerService schedulerService,
            @NotNull final SoupyGames soupyGames,
            @NotNull final GameManager gameManager,
            @NotNull final GameActionBarManager actionBarManager
    ) {
        this.gameDurationSeconds = gameDurationSeconds;
        this.waitingTimeSeconds = waitingTimeSeconds;
        this.startCountdownTimeSeconds = startCountdownTimeSeconds;
        this.useGlobalCard = useGlobalCard;
        this.scheduler = schedulerService;
        this.gamesPlugin = soupyGames;
        this.gameManager = gameManager;
        this.actionBarManager = actionBarManager;
        setMaxTeamSize(2);
    }

    @Override
    public String id() {
        return "bingo";
    }

    @Override
    public boolean isRunning() {
        return phase() != GamePhase.ENDED;
    }

    /*
     * ===================
     * Solo Player Support
     * ===================
     */

    public void markAsSolo(Player player) {
        soloPlayers.add(player.getUniqueId());
    }

    public boolean isSolo(Player player) {
        return soloPlayers.contains(player.getUniqueId());
    }

    public void startWaiting() {
        this.setPhase(GamePhase.WAITING);
        if (this.waitingTimeSeconds < 0) return; // forced start only
        // todo countdown?
    }

    @Override
    public void start() {
        super.start();

        if (players().isEmpty()) {
            log.warn("[Bingo] Cannot start game with no players...");
            stop();
            return;
        }

        assignUnteamedPlayers();
        assignTeamColors();

        @Nullable BingoCard shared = (useGlobalCard) ? generateCard() : null;

        for (BingoTeam team : teams()) {
            @NotNull BingoCard card = (shared != null) ? shared : generateCard();
            team.setTeamCard(card);
            allCards.put(card.cardId(), card);

            Color color = team.color();
            for (UUID uuid : team.members()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && color != null) {
                    ColorUtils.applyTeamColorName(player, color, team.name());

                    player.sendMessage(MiniMsg.resolve("<green>You have been given a bingo card!"));
                }
            }
        }

        startStartCountdown();
    }

    private void assignUnteamedPlayers() {
        List<UUID> unassigned = players().stream()
                .filter(p -> getTeamOfPlayer(p) == null)
                .filter(uuid -> !soloPlayers.contains(uuid))
                .toList();

        int teamIndex = teams().size();

        for (int i = 0; i < unassigned.size(); i += maxTeamSize) {
            String teamName = "team" + (++teamIndex);
            for (int j = 0; j < maxTeamSize && i + j < unassigned.size(); j++) {
                UUID uuid = unassigned.get(i + j);
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    addToTeam(teamName, player);
                }
            }
        }

        for (UUID soloUuid : soloPlayers) {
            if (getTeamOfPlayer(soloUuid) == null) {
                Player player = Bukkit.getPlayer(soloUuid);
                if (player != null) {
                    String soloTeamName = "solo-" + player.getName().toLowerCase(Locale.ROOT);
                    addToTeam(soloTeamName, player);
                }
            }
        }
    }

    private void assignTeamColors() {
        List<BingoTeam> sorted = new ArrayList<>(teams());
        int total = sorted.size();

        for (int i = 0; i < total; i++) {
            float hue = (float) i / total; // even hue spacing
            Color color = ColorUtils.hsvToColor(hue, 0.85f, 0.95f);

            BingoTeam team = sorted.get(i);
            team.color(color);
        }
    }

    private void startStartCountdown() {
        setPhase(GamePhase.COUNTDOWN);

        AtomicInteger timeLeft = new AtomicInteger(this.startCountdownTimeSeconds);

        actionBarManager.showPersistent(this, () -> {
            int seconds = timeLeft.get();
            return "<gray>Starting in <gold><b>" + seconds + "</b></gold> seconds...";
        });

        scheduler.runTimer(() -> {
            int now = timeLeft.decrementAndGet();

            if (now <= 0) {
                actionBarManager.clearAll(this);
                Bukkit.broadcast(MiniMsg.resolve("<green><b>Bingo has started!"));
                beginGame(); // Transition to live game phase
                return;
            }

            if (now <= 5) {
                for (UUID uuid : players()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                    }
                }
            }
        }, 1, 20);
    }

    private void beginGame() {
        setPhase(GamePhase.ACTIVE);

        // todo config/command
        startGameTimerCountdown(this.gameDurationSeconds);
    }

    private void startGameTimerCountdown(int totalSeconds) {
        AtomicInteger timeLeft = new AtomicInteger(totalSeconds);

        actionBarManager.showPersistent(this, () -> {
            int seconds = timeLeft.get();
            int mins = seconds / 60;
            int secs = seconds % 60;
            String time = String.format("%d:%02d", mins, secs);
            return "<gray>Time left: <white><b>" + time + "</b></white>";
        });

        scheduler.runTimer(() -> {
            int now = timeLeft.decrementAndGet();

            if (now <= 0) {
                actionBarManager.clearAll(this);
                Bukkit.broadcast(MiniMsg.resolve("<red><b>Bingo has ended!"));
                stop(); // ends the game
                return;
            }

            // Optional: warning sounds
            if (now == 60 || now == 30 || now <= 10) {
                for (UUID uuid : players()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f);
                    }
                }
            }
        }, 20, 20); // delay 1s, repeat every 1s
    }

    private BingoCard generateCard() {
        // Temporary stub - later will pull from config and randomize
        return new BingoCard();
    }

    @Override
    public void tick() {

    }

    @Override
    public void stop() {
        if (phase() == GamePhase.ENDED) return;
        setPhase(GamePhase.ENDED);
        reset();
        super.stop();
    }

    private void reset() {
        for (BingoTeam team : teams.values()) {
            String teamId = team.name().toLowerCase(Locale.ROOT);

            for (UUID uuid : team.members()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    // Reset Adventure name stuff
                    player.displayName(null);
                    player.playerListName(null);
                }
            }
        }
    }

    @Override
    protected BingoTeam createTeam(String name) {
        return new BingoTeam(name, Color.WHITE);
    }

}
