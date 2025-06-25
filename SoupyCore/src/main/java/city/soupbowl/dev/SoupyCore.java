package city.soupbowl.dev;

import cc.aviara.core.AviaraPlugin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SoupyCore extends AviaraPlugin {

    @Override
    protected void postEnable() {
        log.info("Soupbowl City Core Plugin is Alive and Soupy!");
    }

}
