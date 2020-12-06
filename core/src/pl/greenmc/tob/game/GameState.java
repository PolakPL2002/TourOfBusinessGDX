package pl.greenmc.tob.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.game.map.Tile;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;
import pl.greenmc.tob.game.netty.packets.game.events.GameStateChangedPacket;
import pl.greenmc.tob.game.netty.packets.game.events.PlayerMovedPacket;
import pl.greenmc.tob.game.netty.packets.game.events.PlayerStateChangedPacket;
import pl.greenmc.tob.game.netty.packets.game.events.TileModifiedPacket;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static pl.greenmc.tob.game.TourOfBusinessServer.getServer;
import static pl.greenmc.tob.game.util.Logger.*;
import static pl.greenmc.tob.game.util.Utilities.boundInt;

public class GameState {
    private static final boolean AUCTIONS_ENABLED = false;
    private static final int DICE_MAX = 6;
    private static final int DICE_MIN = 1;
    private static final int NUM_DICES = 2;
    private static final long STARTING_BALANCE = 2000000L;
    private static final boolean START_AUCTION_ON_DONT_BUY = true;
    private static final boolean START_AUCTION_ON_INSUFFICIENT_FUNDS = true;
    private static final float START_STAND_MULTIPLIER = 2.0f;
    private final boolean ALLOW_ACTIONS_WHILE_IN_JAIL = false;
    private final float TIMEOUT_MULTIPLIER = 1f;
    private final ArrayList<AfterSellAction> afterSellActions = new ArrayList<>();
    private final Map map;
    private final long[] playerBalances; //Player balance 
    private final boolean[] playerBankrupt;
    private final int[] playerIDs;
    private final boolean[] playerInJail;
    private final int[] playerInJailTurns;
    private final int[] playerPositions; //Player positions on board 
    private final ArrayList<Integer> propertiesToSell = new ArrayList<>();
    private final int[] rolledNumbers = new int[NUM_DICES]; //Last rolled numbers
    private final int startingPlayerNum;
    private final int[] tileLevels;
    private final Integer[] tileOwners;
    private BuyDecision buyDecision = null;
    private int drawsInRow = 0;
    private JailDecision jailDecision = null;
    private int loopNumber = 0;
    private boolean playerRolled = false;
    private long sellAmount = 0;
    private State state = State.AWAITING_JAIL;
    private Integer tileToBuy = null;
    private long timeoutStart = 0;
    private Timer timer;
    private int turnOf;

    public GameState(@NotNull Integer[] playerIDs, @NotNull Map map) {
        this.playerIDs = ArrayUtils.toPrimitive(playerIDs);
        this.playerBalances = new long[playerIDs.length];
        for (int i = 0; i < playerIDs.length; i++)
            this.playerBalances[i] = STARTING_BALANCE;
        this.playerPositions = new int[playerIDs.length];
        this.playerInJailTurns = new int[playerIDs.length];
        this.playerInJail = new boolean[playerIDs.length];
        this.playerBankrupt = new boolean[playerIDs.length];
        this.tileOwners = new Integer[map.getTiles().length];
        this.tileLevels = new int[map.getTiles().length];
        this.startingPlayerNum = (int) Math.floor(Math.random() * playerIDs.length);
        this.turnOf = startingPlayerNum;
        this.map = map;
        for (int i = 0; i < map.getTiles().length; i++) {
            tileOwners[i] = null;
            tileLevels[i] = 0;
        }
    }

    public void onBuyDecision(int playerID, @NotNull BuyDecision decision) {
        if (Objects.equals(getPlayerNumFromID(playerID), turnOf) && state == State.AWAITING_BUY)
            setBuyDecision(decision);
        else
            warning("Player " + playerID + " tried to set buy decision outside of their turn!");
    }

    @Nullable
    @Contract(pure = true)
    private Integer getPlayerNumFromID(int playerID) {
        for (int i = 0; i < playerIDs.length; i++)
            if (playerIDs[i] == playerID)
                return i;
        return null;
    }

    private void setBuyDecision(BuyDecision decision) {
        buyDecision = decision;
    }

    public void onJailDecision(int playerID, JailDecision decision) {
        if (Objects.equals(getPlayerNumFromID(playerID), turnOf) && state == State.AWAITING_JAIL)
            setJailDecision(decision);
        else
            warning("Player " + playerID + " tried to set jail decision outside of their turn!");
    }

    private void setJailDecision(@Nullable JailDecision decision) {
        jailDecision = decision;
    }

    public void onRollPacket(int playerID) {
        if (Objects.equals(getPlayerNumFromID(playerID), turnOf) && state == State.AWAITING_ROLL)
            roll();
        else
            warning("Player " + playerID + " tried to roll outside of their turn!");
    }

    private void roll() {
        if (state == State.AWAITING_ROLL) {
            for (int i = 0; i < rolledNumbers.length; i++) {
                rolledNumbers[i] = (int) Math.floor(Math.random() * (DICE_MAX - DICE_MIN + 1) + DICE_MIN);
            }
            playerRolled = true;
        }
        log("Rolled: " + Arrays.toString(rolledNumbers));
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
        resetTimeout();
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
                    if (draw) drawsInRow++;
                    else drawsInRow = 0;
                    if (isPlayerInJail(turnOf) && !draw) {
                        //Player stays in jail
                        //TODO Send some event?
                        if (ALLOW_ACTIONS_WHILE_IN_JAIL)
                            endTurn();
                        else
                            changeState(State.END_ROUND);
                    } else {
                        setPlayerInJail(turnOf, false);
                        int num = 0;
                        for (int i = 0; i < NUM_DICES; i++) num += rolledNumbers[i];
                        movePlayer(turnOf, num);
                        playerRolled = false;
                    }
                }
                int num = 0;
                for (int i = 0; i < NUM_DICES; i++) num += rolledNumbers[i];
                if (checkTimeout(state.getTimeout() * num)) {
                    //Animation finished
                    for (int i = 1; i < num; i++)
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
                            } else
                                setBuyDecision(BuyDecision.DONT_BUY);
                            break;
                        case DONT_BUY:
                            if (AUCTIONS_ENABLED && START_AUCTION_ON_DONT_BUY)
                                startAuction(tileToBuy);
                            else
                                changeState(State.END_ROUND);
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
                            if (!autoSell(turnOf, getPlayerBalance(turnOf) + sellAmount)) {
                                bankruptPlayer(turnOf);
                            }
                            return;
                        }
                    }
                    if (totalValue < sellAmount) {
                        if (!autoSell(turnOf, getPlayerBalance(turnOf) + sellAmount)) {
                            bankruptPlayer(turnOf);
                        }
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
                    if (!autoSell(turnOf, getPlayerBalance(turnOf) + sellAmount)) {
                        bankruptPlayer(turnOf);
                    }
                }
                break;
            case AUCTION:
                //TODO
                if (checkTimeout(state.getTimeout(TIMEOUT_MULTIPLIER))) {
                    changeState(State.END_ROUND);
                }
                break;
            case END_ROUND:
                //TODO
                if (drawsInRow > 0) {
                    resetVariables();
                    changeState(State.AWAITING_JAIL);
                }
                if (checkTimeout(state.getTimeout(TIMEOUT_MULTIPLIER))) {
                    endTurn();
                }
                break;
        }
    }

    private void bankruptPlayer(int player) {
        takePlayerMoney(player, getPlayerBalance(player));
        for (int property : getPlayerProperties(player))
            setTileOwner(property, null);
        playerBankrupt[player % playerIDs.length] = true;
        onPlayerBankrupt(player);
    }

    private void onPlayerBankrupt(int player) {
        sendPacketToAllPlayers(new PlayerStateChangedPacket(player, getPlayerState(player)));
    }

    private void startAuction(int tileToBuy) {
        //TODO
        this.tileToBuy = tileToBuy;
        changeState(State.AUCTION);
    }

    /**
     * @param player     Player number
     * @param totalValue Total amount that player should have after autoSell
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean autoSell(int player, long totalValue) {
        //ex. Sort player's properties by value asc
        //Select until sellAmount is reached
        //Unselect from beginning while (sellAmount > totalValue)
        if (getPlayerValue(player) < totalValue) return false;
        propertiesToSell.clear();
        final int[] playerProperties = getPlayerProperties(player);

        HashMap<Integer, Long> propertyValues = new HashMap<>();
        for (int property : playerProperties)
            propertyValues.put(property, getPropertyValue(property));

        final Integer[] sorted = ArrayUtils.toObject(playerProperties);
        Arrays.sort(sorted, Comparator.comparing(propertyValues::get));
        System.arraycopy(ArrayUtils.toPrimitive(sorted), 0, playerProperties, 0, sorted.length);

        ArrayList<Integer> propertiesToSell = new ArrayList<>();
        long value = getPlayerBalance(player);
        for (int playerProperty : playerProperties) {
            if (value >= totalValue) break;
            value += getPropertyValue(playerProperty);
            propertiesToSell.add(playerProperty);
        }

        value = getPlayerBalance(player);

        for (int i = propertiesToSell.size() - 1; i >= 0; i--) {
            if (value >= totalValue) break;
            value += getPropertyValue(propertiesToSell.get(i));
            this.propertiesToSell.add(propertiesToSell.get(i));
        }
        return true;
    }

    private void setTileOwner(int tile, Integer owner) {
        tileOwners[tile % map.getTiles().length] = owner;
        if (owner == null) {
            //TODO Remove all upgrades from tile
            //TODO In all tiles in group for upgrades mode remove upgrades from all tiles
        }
        onTileModified(tile);
    }

    private void onTileModified(int tile) {
        sendPacketToAllPlayers(new TileModifiedPacket(tile, getTileOwner(tile), getTileLevel(tile)));
    }

    private void endTurn() {
        if (isPlayerInJail(turnOf))
            playerInJailTurns[turnOf]++;
        if (getPlayersLeftInGame() < 2)
            endGame();
        turnOf = (turnOf + 1) % playerIDs.length;
        if (turnOf == startingPlayerNum) loopNumber++;
        while (isPlayerBankrupt(turnOf)) {
            turnOf = (turnOf + 1) % playerIDs.length;
            if (turnOf == startingPlayerNum) loopNumber++;
        }

        resetVariables();
        changeState(State.AWAITING_JAIL);
    }

    private void endGame() {
        //TODO
    }

    private boolean isPlayerBankrupt(int player) {
        return playerBankrupt[player % playerIDs.length];
    }

    private int getPlayersLeftInGame() {
        int i = 0;
        for (boolean x : playerBankrupt)
            if (!x) i++;
        return i;
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

    private int getJailMaxTurns(int tile) {
        Tile tile1 = getTile(tile);
        if (tile1.getType() == Tile.TileType.JAIL)
            return ((Tile.JailTileData) tile1.getData()).getMaxRounds();
        else
            return -1;
    }

    @NotNull
    private Tile getTile(int tile) {
        return map.getTiles()[tile % map.getTiles().length];
    }

    private int getJailBail(int tile) {
        Tile tile1 = getTile(tile);
        if (tile1.getType() == Tile.TileType.JAIL)
            return ((Tile.JailTileData) tile1.getData()).getBailMoney();
        else
            return 0;
    }

    private int getPlayerPosition(int player) {
        return boundInt(playerPositions[player], map.getTiles().length);
    }

    private void setPlayerInJail(int player, boolean inJail) {
        log("setPlayerInJail: " + player + ", " + inJail);
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

    private void processTilePass(int player, int tile) {
        log("Player " + player + " has passed tile " + tile);
        Tile tile1 = getTile(tile);
        //noinspection SwitchStatementWithTooFewBranches
        switch (tile1.getType()) {
            case START:
                givePlayerMoney(player, ((Tile.StartTileData) tile1.getData()).getStartMoney());
                break;
        }
    }

    private void givePlayerMoney(int player, long amount) {
        playerBalances[player] += amount;
        onPlayerBalanceChanged(player);
    }

    private void processTileEntry(int player, int tile) {
        log("Player " + player + " has entered tile " + tile);
        Tile tile1 = getTile(tile);
        switch (tile1.getType()) {
            case START:
                givePlayerMoney(player, (int) (((Tile.StartTileData) tile1.getData()).getStartMoney() * START_STAND_MULTIPLIER));
                changeState(State.END_ROUND);
                break;
            case STATION:
            case UTILITY:
            case CITY:
                final Integer owner = getTileOwner(tile);
                if (owner == null) {
                    //Buy ability
                    if (getPropertyPrice(tile) <= getPlayerBalance(player)) {
                        tileToBuy = tile;
                        changeState(State.AWAITING_BUY);
                    } else {
                        if (AUCTIONS_ENABLED && START_AUCTION_ON_INSUFFICIENT_FUNDS) {
                            startAuction(tile);
                        } else
                            changeState(State.END_ROUND);
                    }
                } else if (owner == player) {
                    changeState(State.END_ROUND);
                } else {
                    //Pay owner
                    final long rent = getPropertyRent(tile);
                    if (!playerPayPlayer(player, owner, rent)) {
                        initiateSell(rent - getPlayerBalance(player), new AfterSellAction[]{new AfterSellAction(owner, rent)});
                    }
                }
                break;
            case JAIL:
                final Tile.JailTileData jailTileData = (Tile.JailTileData) tile1.getData();
                boolean goToJailFound = false;
                for (Tile jailTile : jailTileData.getTileGroup().getTiles()) {
                    if (jailTile.getType() == Tile.TileType.GO_TO_JAIL) {
                        goToJailFound = true;
                        break;
                    }
                }
                if (!goToJailFound) {
                    //There is no GO_TO_JAIL in group, jail in hybrid mode
                    setPlayerInJail(player, true);
                    movePlayer(player, tile);
                }

                changeState(State.END_ROUND);
                break;
            case CHANCE:
                //TODO
                changeState(State.END_ROUND);
                break;
            case TRAVEL:
                //TODO
                changeState(State.END_ROUND);
                break;
            case GO_TO_JAIL:
                final Tile.GoToJailTileData goToJailTileData = (Tile.GoToJailTileData) tile1.getData();
                Tile[] tiles = goToJailTileData.getTileGroup().getTiles();
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
                final int tileNumber = getTileNumber(tiles[jailNum]);
                if (tileNumber > -1)
                    movePlayerToTile(player, tileNumber);
                else
                    warning("Jail tile number not found.");
                changeState(State.END_ROUND);
                break;
            case INCOME_TAX:
                final Tile.IncomeTaxTileData incomeTaxTileData = (Tile.IncomeTaxTileData) tile1.getData();
                if (!takePlayerMoney(player, incomeTaxTileData.getCost())) {
                    initiateSell(incomeTaxTileData.getCost() - getPlayerBalance(player), new AfterSellAction[]{new AfterSellAction(null, incomeTaxTileData.getCost())});
                } else
                    changeState(State.END_ROUND);
                break;
            case LUXURY_TAX:
                final Tile.LuxuryTaxTileData luxuryTaxTileData = (Tile.LuxuryTaxTileData) tile1.getData();
                if (!takePlayerMoney(player, luxuryTaxTileData.getCost())) {
                    initiateSell(luxuryTaxTileData.getCost() - getPlayerBalance(player), new AfterSellAction[]{new AfterSellAction(null, luxuryTaxTileData.getCost())});
                } else
                    changeState(State.END_ROUND);
                break;
            case PLACEHOLDER:
                final Tile.PlaceholderTileData placeholderTileData = (Tile.PlaceholderTileData) tile1.getData();
                final int charge = placeholderTileData.getCharge();
                if (charge > 0) {
                    if (!takePlayerMoney(player, charge)) {
                        initiateSell(charge - getPlayerBalance(player), new AfterSellAction[]{new AfterSellAction(null, charge)});
                    } else
                        changeState(State.END_ROUND);
                } else if (charge < 0) {
                    givePlayerMoney(player, -charge);
                    changeState(State.END_ROUND);
                } else
                    changeState(State.END_ROUND);
                break;
            case CHAMPIONSHIPS:
                //TODO
                changeState(State.END_ROUND);
                break;
            case COMMUNITY_CHEST:
                //TODO
                changeState(State.END_ROUND);
                break;
        }
    }

    private void movePlayerToTile(int player, int tileNumber) {
        playerPositions[player] = tileNumber;
        log("Moved player " + player + " to tile " + getPlayerPosition(player));
        onPlayerMoved(player, false);
    }

    private int getTileNumber(Tile tile) {
        int i = 0;
        for (Tile t : map.getTiles()) {
            if (t == tile)
                return i;
            i++;
        }
        return -1;
    }

    private long getPropertyRent(int tile) {
        //TODO
        return 0;
    }

    /**
     * @param timeout Timeout in ms
     * @return Whether timeout is triggered
     */
    private boolean checkTimeout(int timeout) {
        return System.nanoTime() - timeoutStart > timeout * 1000000L;
    }

    private void movePlayer(int player, int numTiles) {
        playerPositions[player] = playerPositions[player] + numTiles;
        log("Moved player " + player + " to tile " + getPlayerPosition(player));
        onPlayerMoved(player, true);
    }

    private void onPlayerMoved(int player, boolean animate) {
        sendPacketToAllPlayers(new PlayerMovedPacket(player, getPlayerPosition(player), animate));
    }

    private boolean isPlayerInJail(int player) {
        return playerInJail[player];
    }

    private boolean takePlayerMoney(int player, long amount) {
        if (getPlayerBalance(player) < amount)
            return false;
        playerBalances[player % playerIDs.length] -= amount;
        onPlayerBalanceChanged(player);
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean playerPayPlayer(int from, int to, long amount) {
        if (playerBalances[from] < amount)
            return false;
        playerBalances[from] -= amount;
        playerBalances[to] += amount;
        onPlayerBalanceChanged(from);
        onPlayerBalanceChanged(to);
        return true;
    }

    @NotNull
    @Contract("_ -> new")
    private PlayerState getPlayerState(int player) {
        return new PlayerState(getPlayerBalance(player), isPlayerInJail(player), isPlayerBankrupt(player));
    }

    private void onPlayerBalanceChanged(int player) {
        sendPacketToAllPlayers(new PlayerStateChangedPacket(player, getPlayerState(player)));
    }

    private void sendPacketToAllPlayers(Packet packet) {
        for (int playerID : playerIDs) {
            getServer().sendPacketToPlayerByID(packet, playerID);
        }
    }

    private void initiateSell(long amount, AfterSellAction[] actions) {
        sellAmount = amount;
        afterSellActions.clear();
        Collections.addAll(afterSellActions, actions);
        if (getPlayerValue(turnOf) - getPlayerBalance(turnOf) < amount) {
            bankruptPlayer(turnOf);
        }
        changeState(State.SELL);
    }

    private long getPlayerValue(int player) {
        long total = getPlayerBalance(player);
        final int[] playerProperties = getPlayerProperties(player);
        for (int property : playerProperties) {
            total += getPropertyValue(property);
        }
        return total;
    }

    private long getPropertyValue(int tile) {
        //TODO Consider price modifier
        long value = 0;
        if (isTileBuyable(tile)) {
            Tile tile1 = map.getTiles()[tile % map.getTiles().length];
            switch (tile1.getType()) {
                case UTILITY:
                case STATION:
                    value = getPropertyPrice(tile);
                    break;
                case CITY:
                    Tile.CityTileData data = (Tile.CityTileData) tile1.getData();
                    value = data.getValue() + (long) data.getImprovementCost() * getTileLevel(tile);
                    break;
            }
        }
        return value;
    }

    private int getTileLevel(int tile) {
        return tileLevels[tile % map.getTiles().length];
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

    @NotNull
    private int[] getPlayerProperties(int player) {
        ArrayList<Integer> out = new ArrayList<>();
        for (int i = 0; i < map.getTiles().length; i++)
            if (getTileOwner(i) == player) out.add(i);
        int[] realOut = new int[out.size()];
        for (int i = 0; i < out.size(); i++)
            realOut[i] = out.get(i);
        return realOut;
    }

    private Integer getTileOwner(int tile) {
        return tileOwners[tile % map.getTiles().length];
    }

    private long getPlayerBalance(int player) {
        return playerBalances[player % playerIDs.length];
    }

    private void changeState(State newState) {
        //TODO Add some checks
        log("State changed to " + newState);
        resetTimeout();
        state = newState;
        onStateChanged();
    }

    private void onStateChanged() {
        sendPacketToAllPlayers(new GameStateChangedPacket(new GameState.Data(this)));
    }

    private void resetTimeout() {
        timeoutStart = System.nanoTime();
    }

    public enum BuyDecision {
        BUY,
        DONT_BUY
    }

    public enum JailDecision {
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
    public enum State {
        AWAITING_JAIL(15000),
        AWAITING_ROLL(15000),
        PLAYER_MOVING(150),
        AWAITING_BUY(15000),
        AUCTION(60000), //TODO Set auction time with parameter
        SELL(45000),
        END_ROUND(15000); //TODO Reset on action

        private final int timeout;

        @SuppressWarnings("unused")
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

    private static class AfterSellAction {
        private final long amount;
        private final Integer to;

        public AfterSellAction(Integer to, long amount) {
            this.to = to;
            this.amount = amount;
        }

        public Integer getTo() {
            return to;
        }

        public long getAmount() {
            return amount;
        }
    }

    public static class Data {
        public static final String TYPE = "GAME_STATE_DATA";
        private final Map map;
        private final long[] playerBalances; //Player balance 
        private final boolean[] playerBankrupt;
        private final int[] playerIDs;
        private final boolean[] playerInJail;
        private final int[] playerInJailTurns;
        private final int[] playerPositions; //Player positions on board
        private final int startingPlayerNum;
        private final State state;
        private final int[] tileLevels;
        private final Integer[] tileOwners;
        private final Integer tileToBuy;
        private final long timeoutLeft;
        private final int timeoutTotal;
        private final int turnOf;

        @Contract(pure = true)
        Data(@NotNull GameState state) {
            map = state.map;
            playerBalances = state.playerBalances;
            playerBankrupt = state.playerBankrupt;
            playerIDs = state.playerIDs;
            playerInJail = state.playerInJail;
            playerInJailTurns = state.playerInJailTurns;
            playerPositions = state.playerPositions;
            startingPlayerNum = state.startingPlayerNum;
            this.state = state.state;
            tileLevels = state.tileLevels;
            tileOwners = state.tileOwners;
            turnOf = state.turnOf;
            tileToBuy = state.tileToBuy;
            int timeout = 0;
            switch (state.state) {
                case AWAITING_JAIL:
                case AWAITING_BUY:
                case AWAITING_ROLL:
                case AUCTION:
                case SELL:
                case END_ROUND:
                    timeout = state.state.getTimeout(state.TIMEOUT_MULTIPLIER);
                    break;
                case PLAYER_MOVING:
                    int num = 0;
                    for (int i = 0; i < NUM_DICES; i++) num += state.rolledNumbers[i];
                    timeout = state.state.getTimeout() * num;
                    break;
            }
            timeoutTotal = timeout;
            timeoutLeft = timeout - (System.nanoTime() - state.timeoutStart) / 1000000L;
        }

        public Data(@NotNull JsonObject jsonObject) throws InvalidPacketException {
            if (jsonObject.has("type")) {
                //Check type
                try {
                    JsonElement type = jsonObject.get("type");
                    if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                        //Decode values
                        String mapID;
                        JsonElement map = jsonObject.get("map");
                        if (map != null && map.isJsonPrimitive()) mapID = map.getAsString();
                        else throw new InvalidPacketException();

                        Object o;
                        try {
                            o = Map.class.getClassLoader().loadClass(mapID).getConstructor(Boolean.class).newInstance(true);
                        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            error("Failed to load class!");
                            error(e);
                            throw new InvalidPacketException();
                        }
                        if (!(o instanceof Map)) throw new InvalidPacketException();

                        this.map = (Map) o;

                        JsonElement startingPlayerNum = jsonObject.get("startingPlayerNum");
                        if (startingPlayerNum != null && startingPlayerNum.isJsonPrimitive())
                            this.startingPlayerNum = startingPlayerNum.getAsInt();
                        else throw new InvalidPacketException();

                        JsonElement turnOf = jsonObject.get("turnOf");
                        if (turnOf != null && turnOf.isJsonPrimitive())
                            this.turnOf = turnOf.getAsInt();
                        else throw new InvalidPacketException();

                        JsonElement timeoutTotal = jsonObject.get("timeoutTotal");
                        if (timeoutTotal != null && timeoutTotal.isJsonPrimitive())
                            this.timeoutTotal = timeoutTotal.getAsInt();
                        else throw new InvalidPacketException();

                        JsonElement tileToBuy = jsonObject.get("tileToBuy");
                        if (tileToBuy != null) {
                            if (tileToBuy.isJsonPrimitive())
                                this.tileToBuy = tileToBuy.getAsInt();
                            else if (tileToBuy.isJsonNull())
                                this.tileToBuy = null;
                            else throw new InvalidPacketException();
                        } else throw new InvalidPacketException();

                        JsonElement timeoutLeft = jsonObject.get("timeoutLeft");
                        if (timeoutLeft != null && timeoutLeft.isJsonPrimitive())
                            this.timeoutLeft = timeoutLeft.getAsLong();
                        else throw new InvalidPacketException();

                        JsonElement state = jsonObject.get("state");
                        if (state != null && state.isJsonPrimitive())
                            this.state = State.valueOf(state.getAsString());
                        else throw new InvalidPacketException();

                        JsonElement playerBalances = jsonObject.get("playerBalances");
                        if (playerBalances != null && playerBalances.isJsonArray()) {
                            final JsonArray array = playerBalances.getAsJsonArray();
                            this.playerBalances = new long[array.size()];
                            for (int i = 0; i < array.size(); i++) {
                                JsonElement balance = array.get(i);
                                if (balance != null && balance.isJsonPrimitive()) {
                                    this.playerBalances[i] = balance.getAsLong();
                                } else throw new InvalidPacketException();
                            }
                        } else throw new InvalidPacketException();

                        JsonElement playerBankrupt = jsonObject.get("playerBankrupt");
                        if (playerBankrupt != null && playerBankrupt.isJsonArray()) {
                            final JsonArray array = playerBankrupt.getAsJsonArray();
                            this.playerBankrupt = new boolean[array.size()];
                            for (int i = 0; i < array.size(); i++) {
                                JsonElement isBankrupt = array.get(i);
                                if (isBankrupt != null && isBankrupt.isJsonPrimitive()) {
                                    this.playerBankrupt[i] = isBankrupt.getAsBoolean();
                                } else throw new InvalidPacketException();
                            }
                        } else throw new InvalidPacketException();

                        JsonElement playerInJail = jsonObject.get("playerInJail");
                        if (playerInJail != null && playerInJail.isJsonArray()) {
                            final JsonArray array = playerInJail.getAsJsonArray();
                            this.playerInJail = new boolean[array.size()];
                            for (int i = 0; i < array.size(); i++) {
                                JsonElement isInJail = array.get(i);
                                if (isInJail != null && isInJail.isJsonPrimitive()) {
                                    this.playerInJail[i] = isInJail.getAsBoolean();
                                } else throw new InvalidPacketException();
                            }
                        } else throw new InvalidPacketException();

                        JsonElement playerInJailTurns = jsonObject.get("playerInJailTurns");
                        if (playerInJailTurns != null && playerInJailTurns.isJsonArray()) {
                            final JsonArray array = playerInJailTurns.getAsJsonArray();
                            this.playerInJailTurns = new int[array.size()];
                            for (int i = 0; i < array.size(); i++) {
                                JsonElement inJailTurns = array.get(i);
                                if (inJailTurns != null && inJailTurns.isJsonPrimitive()) {
                                    this.playerInJailTurns[i] = inJailTurns.getAsInt();
                                } else throw new InvalidPacketException();
                            }
                        } else throw new InvalidPacketException();

                        JsonElement playerPositions = jsonObject.get("playerPositions");
                        if (playerPositions != null && playerPositions.isJsonArray()) {
                            final JsonArray array = playerPositions.getAsJsonArray();
                            this.playerPositions = new int[array.size()];
                            for (int i = 0; i < array.size(); i++) {
                                JsonElement position = array.get(i);
                                if (position != null && position.isJsonPrimitive()) {
                                    this.playerPositions[i] = position.getAsInt();
                                } else throw new InvalidPacketException();
                            }
                        } else throw new InvalidPacketException();

                        JsonElement tileLevels = jsonObject.get("tileLevels");
                        if (tileLevels != null && tileLevels.isJsonArray()) {
                            final JsonArray array = tileLevels.getAsJsonArray();
                            this.tileLevels = new int[array.size()];
                            for (int i = 0; i < array.size(); i++) {
                                JsonElement level = array.get(i);
                                if (level != null && level.isJsonPrimitive()) {
                                    this.tileLevels[i] = level.getAsInt();
                                } else throw new InvalidPacketException();
                            }
                        } else throw new InvalidPacketException();

                        JsonElement playerIDs = jsonObject.get("playerIDs");
                        if (playerIDs != null && playerIDs.isJsonArray()) {
                            final JsonArray array = playerIDs.getAsJsonArray();
                            this.playerIDs = new int[array.size()];
                            for (int i = 0; i < array.size(); i++) {
                                JsonElement level = array.get(i);
                                if (level != null && level.isJsonPrimitive()) {
                                    this.playerIDs[i] = level.getAsInt();
                                } else throw new InvalidPacketException();
                            }
                        } else throw new InvalidPacketException();

                        JsonElement tileOwners = jsonObject.get("tileOwners");
                        if (tileOwners != null && tileOwners.isJsonArray()) {
                            final JsonArray array = tileOwners.getAsJsonArray();
                            this.tileOwners = new Integer[array.size()];
                            for (int i = 0; i < array.size(); i++) {
                                JsonElement owner = array.get(i);
                                if (owner != null) {
                                    if (owner.isJsonPrimitive())
                                        this.tileOwners[i] = owner.getAsInt();
                                    else if (owner.isJsonNull())
                                        this.tileOwners[i] = null;
                                    else throw new InvalidPacketException();
                                } else throw new InvalidPacketException();
                            }
                        } else throw new InvalidPacketException();
                    } else throw new InvalidPacketException();
                } catch (ClassCastException ignored) {
                    throw new InvalidPacketException();
                }
            } else throw new InvalidPacketException();
        }

        public Integer getTileToBuy() {
            return tileToBuy;
        }

        public int getTimeoutTotal() {
            return timeoutTotal;
        }

        public int getTurnOf() {
            return turnOf;
        }

        public long getTimeoutLeft() {
            return timeoutLeft;
        }

        public Map getMap() {
            return map;
        }

        public long[] getPlayerBalances() {
            return playerBalances;
        }

        public boolean[] getPlayerBankrupt() {
            return playerBankrupt;
        }

        public int[] getPlayerIDs() {
            return playerIDs;
        }

        public boolean[] getPlayerInJail() {
            return playerInJail;
        }

        public int[] getPlayerInJailTurns() {
            return playerInJailTurns;
        }

        public int[] getPlayerPositions() {
            return playerPositions;
        }

        public int getStartingPlayerNum() {
            return startingPlayerNum;
        }

        public State getState() {
            return state;
        }

        public int[] getTileLevels() {
            return tileLevels;
        }

        public Integer[] getTileOwners() {
            return tileOwners;
        }

        @NotNull
        public JsonObject toJsonObject() {
            JsonObject out = new JsonObject();
            out.addProperty("type", TYPE);
            out.addProperty("map", map.getClass().getCanonicalName());
            out.addProperty("state", state.name());
            out.addProperty("startingPlayerNum", startingPlayerNum);
            out.addProperty("timeoutLeft", timeoutLeft);
            out.addProperty("turnOf", turnOf);
            out.addProperty("timeoutTotal", timeoutTotal);
            out.addProperty("tileToBuy", tileToBuy);

            JsonArray playerBalances = new JsonArray();
            for (long balance : this.playerBalances) {
                playerBalances.add(balance);
            }
            out.add("playerBalances", playerBalances);

            JsonArray playerBankrupt = new JsonArray();
            for (boolean isBankrupt : this.playerBankrupt) {
                playerBankrupt.add(isBankrupt);
            }
            out.add("playerBankrupt", playerBankrupt);

            JsonArray playerInJail = new JsonArray();
            for (boolean inJail : this.playerInJail) {
                playerInJail.add(inJail);
            }
            out.add("playerInJail", playerInJail);

            JsonArray playerInJailTurns = new JsonArray();
            for (int inJailTurns : this.playerInJailTurns) {
                playerInJailTurns.add(inJailTurns);
            }
            out.add("playerInJailTurns", playerInJailTurns);

            JsonArray playerPositions = new JsonArray();
            for (int position : this.playerPositions) {
                playerPositions.add(position);
            }
            out.add("playerPositions", playerPositions);

            JsonArray tileLevels = new JsonArray();
            for (int level : this.tileLevels) {
                tileLevels.add(level);
            }
            out.add("tileLevels", tileLevels);

            JsonArray playerIDs = new JsonArray();
            for (int id : this.playerIDs) {
                playerIDs.add(id);
            }
            out.add("playerIDs", playerIDs);

            JsonArray tileOwners = new JsonArray();
            for (Integer owner : this.tileOwners) {
                tileOwners.add(owner);
            }
            out.add("tileOwners", tileOwners);

            return out;
        }
    }

    public static class PlayerState {
        public static String TYPE = "PLAYER_STATE";
        private final long balance;
        private final boolean bankrupt;
        private final boolean jailed;

        PlayerState(long balance, boolean jailed, boolean bankrupt) {
            this.balance = balance;
            this.jailed = jailed;
            this.bankrupt = bankrupt;
        }

        public PlayerState(@NotNull JsonObject jsonObject) throws InvalidPacketException {
            if (jsonObject.has("type")) {
                //Check type
                try {
                    JsonElement type = jsonObject.get("type");
                    if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                        //Decode values
                        JsonElement balance = jsonObject.get("balance");
                        if (balance != null && balance.isJsonPrimitive()) this.balance = balance.getAsLong();
                        else throw new InvalidPacketException();

                        JsonElement jailed = jsonObject.get("jailed");
                        if (jailed != null && jailed.isJsonPrimitive()) this.jailed = jailed.getAsBoolean();
                        else throw new InvalidPacketException();

                        JsonElement bankrupt = jsonObject.get("bankrupt");
                        if (bankrupt != null && bankrupt.isJsonPrimitive()) this.bankrupt = bankrupt.getAsBoolean();
                        else throw new InvalidPacketException();
                    } else throw new InvalidPacketException();
                } catch (ClassCastException ignored) {
                    throw new InvalidPacketException();
                }
            } else throw new InvalidPacketException();
        }

        public long getBalance() {
            return balance;
        }

        public boolean isBankrupt() {
            return bankrupt;
        }

        public boolean isJailed() {
            return jailed;
        }

        public JsonObject toJsonObject() {
            JsonObject out = new JsonObject();
            out.addProperty("type", TYPE);
            out.addProperty("balance", balance);
            out.addProperty("jailed", jailed);
            out.addProperty("bankrupt", bankrupt);
            return out;
        }
    }
}
