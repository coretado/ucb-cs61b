package byog.Core;

import java.io.Serializable;

public class GameState implements Serializable {
    private static final long serialVersionUID = 23011996L;
    private String gameState;

    public GameState(String gamestate) {
        this.gameState = gamestate;
    }

    public String getGameState() {
        return gameState;
    }
}
