package pl.greenmc.tob.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.game.map.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import static pl.greenmc.tob.game.util.Logger.warning;
import static pl.greenmc.tob.game.util.Utilities.boundInt;

public class GameState {
    private static final int DICE_MAX = 6;
    private static final int DICE_MIN = 1;
    private static final int NUM_DICES = 2;
    private static final long STARTING_BALANCE = 2000000L;
    private static final float START_STAND_MULTIPLIER = 2.0f;
    private final boolean ALLOW_ACTIONS_WHILE_IN_JAIL = false;
    private final float TIMEOUT_MULTIPLIER = 1.0f;
    private final ArrayList<AfterSellAction> afterSellActions = new ArrayList<>();
    private final Map map;
    private final long[] playerBalances; //Player balance
    private final boolean[] playerInJail;
    private final int[] playerInJailTurns;
    private final int[] playerPositions; //Player positions on board
    private final Integer[] playersIDs;
    private final ArrayList<Integer> propertiesToSell = new ArrayList<>();
    private final int[] rolledNumbers = new int[NUM_DICES]; //Last rolled numbers
    private final int startingPlayerNum;
    private final int[] tileLevels;
    private final Integer[] tileOwners;
    private BuyDecision buyDecision = null;
    private JailDecision jailDecision = null;
    private int loopNumber = 0;
    private boolean playerRolled = false;
    private int sellAmount = 0;
    private State state = State.AWAITING_ROLL;
    private Integer tileToBuy = null;
    private long timeoutStart = 0;
    private Timer timer;
    private int turnOf;

    public GameState(@NotNull Integer[] playersIDs, @NotNull Map map) {
        this.playersIDs = playersIDs;
        this.playerBalances = new long[playersIDs.length];
        for (int i = 0; i < playersIDs.length; i++)
            this.playerBalances[i] = STARTING_BALANCE;
        this.playerPositions = new int[playersIDs.length];
        this.playerInJailTurns = new int[playersIDs.length];
        this.playerInJail = new boolean[playersIDs.length];
        this.tileOwners = new Integer[map.getTiles().length];
        this.tileLevels = new int[map.getTiles().length];
        this.startingPlayerNum = (int) Math.floor(Math.random() * playersIDs.length);
        this.turnOf = startingPlayerNum;
        this.map = map;
        for (int i = 0; i < map.getTiles().length; i++) {
            tileOwners[i] = null;
        }
        for (int i = 0; i < map.getTiles().length; i++) {
            tileLevels[i] = 0;
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

    public int getStartingPlayerNum() {
        return startingPlayerNum;
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
                } else if (jailDecision != null) {
                    switch (jailDecision) {
                        case ROLL:
                            break;
                        case PAY:
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
                } else if (checkTimeout(state.getTimeout(TIMEOUT_MULTIPLIER))) {
                    //Default to roll
                    setJailDecision(JailDecision.ROLL);
                    return;
                }
                break;
            case AWAITING_ROLL:
                if (playerRolled) {
                    changeState(State.PLAYER_MOVING);
                    return;
                } else if (checkTimeout(state.getTimeout(TIMEOUT_MULTIPLIER))) {
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
                        if (ALLOW_ACTIONS_WHILE_IN_JAIL)
                            endTurn();
                        else
                            changeState(State.END_ROUND);
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
                if (tileToBuy == null)
                    changeState(State.END_ROUND);
                else if (buyDecision != null) {
                    switch (buyDecision) {
                        case BUY:
                            if (isTileBuyable(tileToBuy) &&
                                    takePlayerMoney(turnOf, getPropertyPrice(tileToBuy))) {
                                setTileOwner(tileToBuy, turnOf);
                                //TODO Raise event
                            } else
                                setBuyDecision(BuyDecision.DONT_BUY);
                            break;
                        case DONT_BUY:
                            //TODO Start auction if config allows
                            break;
                    }
                } else if (checkTimeout(state.getTimeout(TIMEOUT_MULTIPLIER))) {
                    //Default to not buy
                    setBuyDecision(BuyDecision.DONT_BUY);
                    return;
                }
                break;
            case SELL:
                if (propertiesToSell.size() > 0) {
                    int totalValue = 0;
                    for (Integer tile : propertiesToSell) {
                        if (tile != null && getTileOwner(tile) == turnOf)
                            totalValue += getPropertyValue(tile);
                        else {
                            //Invalid data
                            autoSell(turnOf);
                            return;
                        }
                    }
                    if (totalValue < sellAmount) {
                        autoSell(turnOf);
                    } else {
                        for (Integer tile : propertiesToSell) {
                            setTileOwner(tile, null);
                            givePlayerMoney(turnOf, getPropertyValue(tile));
                        }
                        for (AfterSellAction action : afterSellActions) {
                            if (action.getTo() == null) {
                                if (!takePlayerMoney(turnOf, action.getAmount()))
                                    warning("Unable to collect " + action.getAmount());
                            } else {
                                if (!playerPayPlayer(turnOf, action.getTo(), action.getAmount()))
                                    warning("Unable to collect " + action.getAmount());
                            }
                        }
                        changeState(State.END_ROUND);
                    }
                } else if (checkTimeout(state.getTimeout(TIMEOUT_MULTIPLIER))) {
                    autoSell(turnOf);
                }
                break;
            case AUCTION:
                //TODO
                break;
            case END_ROUND:
                //TODO
                //TODO Check for extra moves (cards, doubles etc.)
                break;
        }
    }

    private int getTileOwner(int tile) {
        return tileOwners[tile % map.getTiles().length];
    }

    private void autoSell(int player) {
        //TODO Implement auto sell
        //ex. Sort player's properties by value asc
        //Select until sellAmount is reached
        //Unselect from beginning while (sellAmount > totalValue)
    }

    private int getPropertyValue(int tile) {
        int value = 0;
        if (isTileBuyable(tile)) {
            Tile tile1 = map.getTiles()[tile % map.getTiles().length];
            switch (tile1.getType()) {
                case UTILITY:
                case STATION:
                    value = getPropertyPrice(tile);
                    break;
                case CITY:
                    Tile.CityTileData data = (Tile.CityTileData) tile1.getData();
                    value = data.getValue() + data.getImprovementCost() * getTileLevel(tile);
                    break;
            }
        }
        return value;
    }

    private int getTileLevel(int tile) {
        return tileLevels[tile % map.getTiles().length];
    }

    private void setTileOwner(int tile, Integer owner) {
        tileOwners[tile % map.getTiles().length] = owner;
        //TODO Raise event
    }

    private int getPropertyPrice(int tile) {
        int price = 0;
        if (isTileBuyable(tile)) {
            Tile tile1 = map.getTiles()[tile % map.getTiles().length];
            switch (tile1.getType()) {
                case UTILITY:
                    price = ((Tile.UtilityTileData) tile1.getData()).getValue();
                    break;
                case STATION:
                    price = ((Tile.StationTileData) tile1.getData()).getValue();
                    break;
                case CITY:
                    price = ((Tile.CityTileData) tile1.getData()).getValue();
                    break;
            }
        }
        return price;
    }

    private boolean isTileBuyable(int tile) {
        switch (map.getTiles()[tile % map.getTiles().length].getType()) {
            case CITY:
            case STATION:
            case UTILITY:
                return true;
            default:
                return false;
        }
    }

    private void setBuyDecision(BuyDecision decision) {
        buyDecision = decision;
    }

    private void endTurn() {
        turnOf = (turnOf + 1) % playersIDs.length;
        if (turnOf == startingPlayerNum) loopNumber++;
        resetVariables();
        changeState(State.AWAITING_JAIL);
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

    private void resetVariables() {
        jailDecision = null;
        buyDecision = null;
        playerRolled = false;
        tileToBuy = null;
        sellAmount = 0;
        afterSellActions.clear();
        propertiesToSell.clear();
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

    private void processTileEntry(int player, int tile) {
        Tile tile1 = getTile(tile);
        if (tile1 != null) {
            switch (tile1.getType()) {
                case START:
                    givePlayerMoney(player, (int) (((Tile.StartTileData) tile1.getData()).getStartMoney() * START_STAND_MULTIPLIER));
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
                    Tile.GoToJailTileData data = (Tile.GoToJailTileData) tile1.getData();
                    Tile[] tiles = data.getTileGroup().getTiles();
                    int jailNum = -1;
                    for (int i = 0; i < tiles.length; i++) {
                        if (tiles[i].getType() == Tile.TileType.JAIL) {
                            jailNum = i;
                            break;
                        }
                    }
                    if (jailNum == -1) {
                        warning("No jail found for GO_TO_JAIL tile!");
                        break;
                    }
                    setPlayerInJail(player, true);
                    movePlayer(player, jailNum);
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

    private void givePlayerMoney(int player, int amount) {
        playerBalances[player] += amount;
        //TODO Raise event
    }

    private boolean takePlayerMoney(int player, int amount) {
        if (playerBalances[player] < amount)
            return false;
        playerBalances[player] -= amount;
        //TODO Raise event
        return true;
    }

    private boolean playerPayPlayer(int from, int to, int amount) {
        if (playerBalances[from] < amount)
            return false;
        playerBalances[from] -= amount;
        playerBalances[to] += amount;
        //TODO Raise event
        return true;
    }

    private void initiateSell(int amount, AfterSellAction[] actions) {
        sellAmount = amount;
        afterSellActions.clear();
        Collections.addAll(afterSellActions, actions);
        changeState(State.SELL);
    }

    private long getPlayerBalance(int player) {
        return playerBalances[player];
    }

    enum BuyDecision {
        BUY,
        DONT_BUY
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
        AWAITING_BUY(15000),
        AUCTION(60000), //TODO Set auction time with parameter
        SELL(45000),
        END_ROUND(15000); //TODO Reset on action

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

    private class AfterSellAction {
        private final int amount;
        private final Integer to;

        public AfterSellAction(Integer to, int amount) {
            this.to = to;
            this.amount = amount;
        }

        public Integer getTo() {
            return to;
        }

        public int getAmount() {
            return amount;
        }
    }
}
