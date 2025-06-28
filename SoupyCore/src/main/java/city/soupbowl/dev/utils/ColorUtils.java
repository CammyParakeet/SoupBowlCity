package city.soupbowl.dev.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import org.bukkit.Color;
import org.bukkit.entity.Player;

@UtilityClass
public class ColorUtils {

    public Color hsvToColor(float hue, float saturation, float value) {
        int rgb = java.awt.Color.HSBtoRGB(hue, saturation, value);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return Color.fromRGB(r, g, b);
    }

    public void applyTeamColorName(Player player, Color teamColor, String teamName) {
        TextColor teamColorText = TextColor.color(teamColor.getRed(), teamColor.getGreen(), teamColor.getBlue());
        TextColor creamyWhite = TextColor.fromHexString("#fdf3e9");

        Component prefix = Component.text("[", creamyWhite)
                .append(Component.text(teamName, teamColorText))
                .append(Component.text("] ", creamyWhite));

        Component name = Component.text(player.getName(), teamColorText);
        Component full = prefix.append(name);

        player.displayName(full);
        player.playerListName(full);
    }

}
