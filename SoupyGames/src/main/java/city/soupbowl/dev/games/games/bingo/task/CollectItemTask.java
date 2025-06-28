package city.soupbowl.dev.games.games.bingo.task;

import city.soupbowl.dev.utils.MiniMsg;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class CollectItemTask implements BingoTask {

    private final Material material;
    private final int amount;

    @Override
    public String id() {
        return "collect:" + material.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Component displayName() {
        return MiniMsg.resolve("<yellow>Collect " + amount + " " + format(material));
    }

    @Override
    public List<Component> extraLore() {
        return List.of(
                MiniMsg.resolve("<gray>Item: <white>" + format(material)),
                MiniMsg.resolve("<gray>Amount: <white>" + amount)
        );
    }

    @Override
    public boolean isCompletedBy(Player player) {
        int count = Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> item.getType() == material)
                .mapToInt(ItemStack::getAmount)
                .sum();
        return count >= amount;
    }

    @Override
    public ItemStack renderItem(boolean completed) {
        ItemStack icon = new ItemStack(this.material);

        icon.editMeta(meta -> {
            meta.displayName(
                    completed
                            ? MiniMsg.resolve("<green><bold>✓</bold> " + format(material))
                            : MiniMsg.resolve("<yellow>• " + format(material))
            );

            List<Component> lore = new ArrayList<>();
            lore.add(MiniMsg.resolve(completed
                    ? "<gray>Status: <green>Complete"
                    : "<gray>Status: <yellow>Incomplete"));
            lore.addAll(extraLore());

            meta.lore(lore);
        });

        return icon;
    }

    private static String format(Material material) {
        String[] words = material.name().toLowerCase(Locale.ROOT).split("_");
        return Arrays.stream(words)
                .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

}
