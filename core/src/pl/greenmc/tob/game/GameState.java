package pl.greenmc.tob.game;

import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.map.Map;

import java.util.HashMap;

public class GameState {
    private final Map map;
    private final Integer[] playersIDs;
    private final int startingPlayerNum;
    private State state = State.AWAITING_ROLL;
    private HashMap<Integer, Integer> tileLevels = new HashMap<>(); //tileNum, tileLevel
    private HashMap<Integer, Integer> tileOwners = new HashMap<>(); //tileNum, playerNum
    private int turnOf;

    public GameState(@NotNull Integer[] playersIDs, @NotNull Map map) {
        this.playersIDs = playersIDs;
        startingPlayerNum = (int) Math.floor(Math.random() * playersIDs.length);
        turnOf = startingPlayerNum;
        this.map = map;
        for (int i = 0; i < map.getTiles().length; i++) {
            tileOwners.put(i, null);
        }
        for (int i = 0; i < map.getTiles().length; i++) {
            tileLevels.put(i, 0);
        }
    }

    public int getStartingPlayerNum() {
        return startingPlayerNum;
    }

    //Awaiting roll -> player moving ->
    //      player_buy
    //          player_auction
    //      player_interaction
    //          player_sell
    enum State {
        AWAITING_ROLL,
        PLAYER_MOVING,
        AWAITING_DECISION,
    }
}
