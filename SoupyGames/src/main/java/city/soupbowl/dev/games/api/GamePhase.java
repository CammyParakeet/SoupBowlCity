package city.soupbowl.dev.games.api;

public enum GamePhase {
    WAITING,     // players joining, pre-start
    COUNTDOWN,   // pre-game countdown
    ACTIVE,      // game is in progress
    ENDED        // game finished
}
