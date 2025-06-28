package city.soupbowl.dev.games.games.bingo;

import city.soupbowl.dev.games.api.team.AbstractTeam;
import city.soupbowl.dev.games.games.bingo.card.BingoCard;
import lombok.Setter;
import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

public class BingoTeam extends AbstractTeam {

    @Setter
    private BingoCard teamCard;

    public BingoTeam(String name, Color teamColor) {
        super(name);
        color(teamColor);
    }

    @Nullable public BingoCard card() {
        return this.teamCard;
    }

}
