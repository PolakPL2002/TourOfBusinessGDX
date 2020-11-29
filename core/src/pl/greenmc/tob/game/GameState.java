package pl.greenmc.tob.game;

import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.map.Map;

import java.util.HashMap;

public class GameState {
    private static final int DICE_MAX = 6;
    private static final int DICE_MIN = 1;
    private static final int NUM_DICES = 2;
    private final Map map;
    private final boolean[] playerInJail;
    private final int[] playerPositions; //Player positions on board (playerNum, boardID)
    private final Integer[] playersIDs;
    private final int[] rolledNumbers = new int[NUM_DICES]; //Last rolled numbers
    private final int startingPlayerNum;
    private JailDecision jailDecision = null;
    private boolean playerRolled = false;
    private State state = State.AWAITING_ROLL;
    private HashMap<Integer, Integer> tileLevels = new HashMap<>(); //tileNum, tileLevel
    private HashMap<Integer, Integer> tileOwners = new HashMap<>(); //tileNum, playerNum
    private float timeoutMultiplier = 1.0f;
    private long timeoutStart = 0;
    private int turnOf;

    public GameState(@NotNull Integer[] playersIDs, @NotNull Map map) {
        this.playersIDs = playersIDs;
        this.playerPositions = new int[playersIDs.length];
        this.playerInJail = new boolean[playersIDs.length];
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

    private void gameTick() {
        switch (state) {
            case AWAITING_JAIL:
                //TODO Check if player is in jail
                //TODO Check if jail maxTurns is reached
                if (jailDecision != null) {
                    switch (jailDecision) {
                        case ROLL:
                            break;
                        case PAY:
                            //TODO Check if player has enough money and update jailed flag
                            break;
                        case CARD:
                            //TODO Check if player has enough money and update jailed flag
                    }
                    changeState(State.AWAITING_ROLL);
                    return;
                } else if (checkTimeout((int) (15000 * timeoutMultiplier))) {
                    //Default to roll
                    jailDecision = JailDecision.ROLL;
                    return;
                }
                break;
            case AWAITING_ROLL:
                if (playerRolled) {
                    changeState(State.PLAYER_MOVING);
                    return;
                } else if (checkTimeout((int) (15000 * timeoutMultiplier))) {
                    roll();
                    return;
                }
                break;
            case PLAYER_MOVING:
                //TODO Check if player is in jail
                if (playerRolled) {
                    int num = 0;
                    for (int i = 0; i < NUM_DICES; i++) num += rolledNumbers[i];
                    movePlayer(turnOf, num);
                    playerRolled = false;
                }
                if (checkTimeout(1000)) {
                    //Animation finished
                    int num = 0;
                    for (int i = 0; i < NUM_DICES; i++) num += rolledNumbers[i];
                    for (int i = 0; i < num; i++)
                        processTilePass(turnOf, boundInt(playerPositions[turnOf] - num + i, map.getTiles().length));
                    processTileEntry(turnOf, boundInt(playerPositions[turnOf], map.getTiles().length));
                }
                break;
        }
    }

    private int boundInt(int x, int y) {
        int i = x % y;
        if (i < 0) i += y;
        return i;
    }

    private void processTileEntry(int player, int tile) {

    }

    private void processTilePass(int player, int tile) {

    }

    private void changeState(State newState) {
        //TODO Add some checks
        timeoutStart = System.nanoTime();
        state = newState;
    }

    /**
     * @param timeout Timeout in ms
     * @return Whether timeout is triggered
     */
    private boolean checkTimeout(int timeout) {
        return System.nanoTime() - timeoutStart > timeout * 1000000L;
    }

    private void roll() {
        if (state == State.AWAITING_ROLL) {
            for (int i = 0; i < rolledNumbers.length; i++) {
                rolledNumbers[i] = (int) Math.floor(Math.random() * (DICE_MAX - DICE_MIN + 1) + DICE_MIN);
            }
            playerRolled = true;
        }
    }

    private void movePlayer(int player, int numTiles) {
        playerPositions[player] = playerPositions[player] + numTiles;
        //TODO Raise event
    }

    private void startRound() {
        jailDecision = null;
        playerRolled = false;
    }

    enum JailDecision {
        ROLL,
        PAY,
        CARD
    }

    /* Round routine:
    0 (AWAITING_JAIL). If player is jailed show jail dialog and await decision (15s, just roll on timeout)
    1 (AWAITING_ROLL). Await for player roll (15s)
    2 (PLAYER_MOVING). Move player to destination
    3.1. Player on city/station tile
        3.1.1 (AWAITING_BUY). If there is no owner and player has enough money show buy popup and await (15s)
            3.1.1.1. Player buys
            3.1.1.2. Player doesn't buy and auction begins
            3.1.1.3. Timeout is reached, start auction
        3.1.2. If there is no owner start auction
        3.1.3. Player pays
            3.1.3.1 (SELL). If player does not have enough money show sell dialog.
                3.1.3.1.1. Player chooses what they sell (45s, 3.2.1.2 on timeout)
                3.1.3.1.2. Properties are automatically sold according to some formula
                3.1.3.1.3. Player goes bankrupt
    3.2. Player on action tile
        3.2.1 (SELL). If action involves payment and player does not have enough money show sell dialog.
            3.2.1.1. Player chooses what they sell (45s, 3.2.1.2 on timeout)
            3.2.1.2. Properties are automatically sold according to some formula
            3.2.1.3. Player goes bankrupt
        3.2.2. Then perform action
    4.1. Player has extra move available, go to 0.
    4.2 (END_ROUND). Show end round menu (10s, unless some action is taken)
    */
    enum State {
        AWAITING_JAIL,
        AWAITING_ROLL,
        PLAYER_MOVING,
        AWAITING_BUY,
        AUCTION,
        SELL,
        END_ROUND
    }
}
