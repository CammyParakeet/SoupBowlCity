package city.soupbowl.dev.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class InventoryUtils {

    public int slotFromRow(int r) {
        return r * 9;
    }

}
