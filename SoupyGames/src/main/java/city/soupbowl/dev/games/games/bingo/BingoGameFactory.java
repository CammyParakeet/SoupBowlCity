package city.soupbowl.dev.games.games.bingo;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.annotations.AviaraComponent;
import cc.aviara.annotations.inject.Inject;
import cc.aviara.api.schedule.SchedulerService;
import city.soupbowl.dev.games.SoupyGames;
import city.soupbowl.dev.games.manager.GameActionBarManager;
import city.soupbowl.dev.games.manager.GameManager;
import com.google.auto.service.AutoService;

@AutoService(AviaraBean.class)
@AviaraComponent(bindSingleton = true)
public class BingoGameFactory implements AviaraBean {

    @Inject private SchedulerService schedulerService;
    @Inject private SoupyGames soupyGames;
    @Inject private GameManager gameManager;
    @Inject private GameActionBarManager actionBarManager;

    public BingoGame create(
            int waitingTimeSeconds,
            int startCountdownTimeSeconds,
            int gameDurationSeconds,
            boolean useGlobalCard
    ) {
        return new BingoGame(
            waitingTimeSeconds,
            startCountdownTimeSeconds,
            gameDurationSeconds,
            useGlobalCard,
            schedulerService,
            soupyGames,
            gameManager,
            actionBarManager
        );
    }

}
