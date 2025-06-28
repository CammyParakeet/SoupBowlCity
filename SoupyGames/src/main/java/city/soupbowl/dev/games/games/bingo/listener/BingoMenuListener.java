package city.soupbowl.dev.games.games.bingo.listener;

import cc.aviara.annotations.AviaraBean;
import cc.aviara.annotations.composition.BukkitListener;
import city.soupbowl.dev.games.games.bingo.card.BingoCardHolder;
import com.google.auto.service.AutoService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;

@AutoService(AviaraBean.class)
@BukkitListener
public class BingoMenuListener implements Listener, AviaraBean {

    private boolean isBingoCardMenu(InventoryView view) {
        return view.getTopInventory().getHolder() instanceof BingoCardHolder;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (isBingoCardMenu(event.getView())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (isBingoCardMenu(event.getView())) {
            event.setCancelled(true);
        }
    }

}
