package city.soupbowl.dev.games.games.bingo.config;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.api.config.ConfigSerializable;
import cc.aviara.api.config.annotation.Config;
import cc.aviara.api.config.annotation.ConfigField;
import cc.aviara.api.config.annotation.ConfigPath;
import com.google.auto.service.AutoService;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@AutoService(AviaraBean.class)
@Config(fileName = "bingo-tasks", section = "tasks")
public class BingoTaskConfig implements Config.Model {

    @ConfigPath("collect")
    private List<CollectTaskEntry> collectEntries = new ArrayList<>();

    @Data
    public static class CollectTaskEntry implements ConfigSerializable {
        @ConfigField(order = 0)
        private Material item;

        @ConfigField(order = 1)
        private int min = 3;

        @ConfigField(order = 2)
        private int max = 7;

        @ConfigField(order = 3)
        private double weight = 1.0;
    }


}
