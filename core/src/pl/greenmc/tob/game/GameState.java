package pl.greenmc.tob.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.game.map.Tile;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static pl.greenmc.tob.game.util.Utilities.boundInt;

public class GameState {
    private static final int DICE_MAX = 6;
    private static final int DICE_MIN = 1;
    private static final int NUM_DICES = 2;
    private final Map map;
    private final boolean[] playerInJail;
    private final int[] playerInJailTurns;
    private final int[] playerPositions; //Player positions on board (playerNum, boardID)
    private final Integer[] playersIDs;
    private final int[] rolledNumbers = new int[NUM_DICES]; //Last rolled numbers
    private final int startingPlayerNum;
    private JailDecision jailDecision = null;
    private boolean playerRolled = false;
    private State state = State.AWAITING_ROLL;
    private HashMap<Integer, Integer> tileLevels = new HashMap<>(); //tileNum, tileLevel
    private HashMap<Integer, Integer> tileOwners = new HashMap<>(); //tileNum, playerNum
    private float timeoutMultiplier = 1.0f; //TODO Add this as config entry
    private long timeoutStart = 0;
    private Timer timer;
    private int turnOf;

    public GameState(@NotNull Integer[] playersIDs, @NotNull Map map) {
        this.playersIDs = playersIDs;
        this.playerPositions = new int[playersIDs.length];
        this.playerInJailTurns = new int[playersIDs.length];
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

    public void startTicking() {
        stopTicking();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gameTick();
            }
        }, 0, 100); //10 tps
    }

    public void stopTicking() {
        if (timer != null) timer.cancel();
        timer = null;
    }

    private void gameTick() {
        switch (state) {
            case AWAITING_JAIL:
                if (!isPlayerInJail(turnOf)) {
                    //Player is not in jail
                    changeState(State.AWAITING_ROLL);
                    return;
                } else if (getPlayerTurnsInJail(turnOf) >= getJailMaxTurns(getPlayerPosition(turnOf))) {
                    setPlayerInJail(turnOf, false);
                    changeState(State.AWAITING_ROLL);
                    return;
                }
                if (jailDecision != null) {
                    switch (jailDecision) {
                        case ROLL:
                            break;
                        case PAY:
                            //TODO Check if player has enough money and update jailed flag
                            if (takePlayerMoney(turnOf, getJailBail(getPlayerPosition(turnOf)))) {
                                //Player had enough money
                                setPlayerInJail(turnOf, false);
                            } else {
                                setJailDecision(null);
                                return;
                            }
                            break;
                        case CARD:
                            //TODO Check if player has card and update jailed flag
                            //TODO Implement special function cards
                    }
                    changeState(State.AWAITING_ROLL);
                    return;
                } else if (checkTimeout(state.getTimeout(timeoutMultiplier))) {
                    //Default to roll
                    setJailDecision(JailDecision.ROLL);
                    return;
                }
                break;
            case AWAITING_ROLL:
                if (playerRolled) {
                    changeState(State.PLAYER_MOVING);
                    return;
                } else if (checkTimeout(state.getTimeout(timeoutMultiplier))) {
                    roll();
                    return;
                }
                break;
            case PLAYER_MOVING:
                if (playerRolled) {
                    boolean draw = true;
                    for (int i = 0; i < NUM_DICES - 1; i++) //noinspection ConstantConditions
                        draw = draw && rolledNumbers[i] == rolledNumbers[i + 1];
                    if (isPlayerInJail(turnOf) && !draw) {
                        //Player stays in jail
                        //TODO Send some event?
                        //TODO Add config entry to allow round end screen in jail
                    } else {
                        int num = 0;
                        for (int i = 0; i < NUM_DICES; i++) num += rolledNumbers[i];
                        movePlayer(turnOf, num);
                        playerRolled = false;
                    }
                }
                if (checkTimeout(1000)) {
                    //Animation finished
                    int num = 0;
                    for (int i = 0; i < NUM_DICES; i++) num += rolledNumbers[i];
                    for (int i = 0; i < num; i++)
                        processTilePass(turnOf, boundInt(playerPositions[turnOf] - num + i, map.getTiles().length));
                    processTileEntry(turnOf, getPlayerPosition(turnOf));
                }
                break;
            case AWAITING_BUY:
                //TODO
                break;
            case SELL:
                //TODO
                break;
            case AUCTION:
                //TODO
                break;
            case END_ROUND:
                //TODO
                break;
        }
    }

    private int getJailMaxTurns(int tile) {
        Tile tile1 = getTile(tile);
        if (tile1 != null && tile1.getType() == Tile.TileType.JAIL)
            return ((Tile.JailTileData) tile1.getData()).getMaxRounds();
        else
            return -1;
    }

    @Nullable
    private Tile getTile(int tile) {
        if (tile < map.getTiles().length) {
            return map.getTiles()[tile];
        } else return null;
    }

    private int getJailBail(int tile) {
        Tile tile1 = getTile(tile);
        if (tile1 != null && tile1.getType() == Tile.TileType.JAIL)
            return ((Tile.JailTileData) tile1.getData()).getBailMoney();
        else
            return 0;
    }

    private int getPlayerPosition(int player) {
        return boundInt(playerPositions[player], map.getTiles().length);
    }

    private void setPlayerInJail(int player, boolean inJail) {
        if (inJail && !isPlayerInJail(player)) {
            playerInJail[player] = true;
            playerInJailTurns[player] = 0;
        } else if (!inJail && isPlayerInJail(player)) {
            playerInJail[player] = false;
        }
    }

    private int getPlayerTurnsInJail(int player) {
        if (isPlayerInJail(player))
            return playerInJailTurns[player];
        else
            return 0;
    }

    private void setJailDecision(@Nullable JailDecision decision) {
        jailDecision = decision;
    }

    private void processTileEntry(int player, int tile) {
        Tile tile1 = getTile(tile);
        if (tile1 != null) {
            switch (tile1.getType()) {
                case START:
                    //TODO Add config entry for start stand multiplier
                    givePlayerMoney(player, ((Tile.StartTileData) tile1.getData()).getStartMoney());
                    break;
                case CITY:
                    //TODO
                    break;
                case JAIL:
                    //TODO
                    break;
                case CHANCE:
                    //TODO
                    break;
                case TRAVEL:
                    //TODO
                    break;
                case STATION:
                    //TODO
                    break;
                case UTILITY:
                    //TODO
                    break;
                case GO_TO_JAIL:
                    //TODO
                    break;
                case INCOME_TAX:
                    //TODO
                    break;
                case LUXURY_TAX:
                    //TODO
                    break;
                case PLACEHOLDER:
                    //TODO
                    break;
                case CHAMPIONSHIPS:
                    //TODO
                    break;
                case COMMUNITY_CHEST:
                    //TODO
                    break;
            }
        }
    }

    private void processTilePass(int player, int tile) {
        Tile tile1 = getTile(tile);
        if (tile1 != null) {
            //noinspection SwitchStatementWithTooFewBranches
            switch (tile1.getType()) {
                case START:
                    givePlayerMoney(player, ((Tile.StartTileData) tile1.getData()).getStartMoney());
                    break;
            }
        }
    }

    private void givePlayerMoney(int player, int amount) {
        //TODO Implement financial system
    }

    private void changeState(State newState) {
        //TODO Add some checks
        resetTimeout();
        state = newState;
    }

    private void resetTimeout() {
        timeoutStart = System.nanoTime();
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

    private boolean isPlayerInJail(int player) {
        return playerInJail[player];
    }

    private boolean takePlayerMoney(int player, int amount) {
        //TODO Implement financial system
        //Return false if player does not have enough money
        return false;
    }

    public int getStartingPlayerNum() {
        return startingPlayerNum;
    }

    private boolean playerPayPlayer(int from, int to, int amount) {
        //TODO Implement financial system
        //Return false if player does not have enough money
        return false;
    }

    private int getPlayerBalance(int player) {
        //TODO Implement financial system
        return 0;
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
        AWAITING_JAIL(15000),
        AWAITING_ROLL(15000),
        PLAYER_MOVING,
        AWAITING_BUY,
        AUCTION,
        SELL,
        END_ROUND;

        private final int timeout;

        State() {
            this(0);
        }

        State(int timeout) {
            this.timeout = timeout;
        }

        public int getTimeout() {
            return timeout;
        }

        public int getTimeout(float multiplier) {
            return (int) (timeout * multiplier);
        }
    }
}
