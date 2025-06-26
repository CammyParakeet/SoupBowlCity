package city.soupbowl.dev.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class ItemUtils {

    @NotNull
    public ItemStack getSafe(@Nullable ItemStack stack) {
        return (stack == null) ? ItemStack.empty() : stack;
    }

}
