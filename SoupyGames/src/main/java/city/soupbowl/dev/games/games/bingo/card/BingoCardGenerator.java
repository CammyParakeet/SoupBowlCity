package city.soupbowl.dev.games.games.bingo.card;

import cc.aviara.annotations.AviaraComponent;
import cc.aviara.annotations.inject.Inject;
import city.soupbowl.dev.games.games.bingo.config.BingoTaskConfig;
import city.soupbowl.dev.games.games.bingo.task.BingoTask;

import java.util.List;
import java.util.Random;

@AviaraComponent
public class BingoCardGenerator {

    @Inject private BingoTaskConfig taskConfig;
    private final Random random = new Random();

    public BingoCard generateCard() {
        BingoCard card = new BingoCard();
        List<BingoTask>
    }

    private List<BingoTask> pickWeightedTasks(int count) {
        List<BingoTaskConfig.CollectTaskEntry> entries = taskConfig.getCollectEntries();
        if (entries.isEmpty()) return List.of();

        
    }

}
