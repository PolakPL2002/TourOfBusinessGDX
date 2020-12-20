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
import pl.greenmc.tob.game.netty.packets.game.EndGameActionPacket;
import pl.greenmc.tob.game.netty.packets.game.events.*;
import pl.greenmc.tob.game.util.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static pl.greenmc.tob.game.TourOfBusinessServer.getServer;
import static pl.greenmc.tob.game.util.Logger.*;
import static pl.greenmc.tob.game.util.Utilities.boundInt;

public class GameState {
    private final ArrayList<AfterSellAction> afterSellActions = new ArrayList<>();
    private final GameSettings gameSettings;
    private final Map map;
    @NotNull
    private final Runnable onEndGame;
    private final long[] playerBalances; //Player balance
    private final boolean[] playerBankrupt;
    private final int[] playerIDs;
    private final boolean[] playerInJail;
    private final int[] playerInJailTurns;
    private final int[] playerPositions; //Player positions on board
    private final ArrayList<Integer> propertiesToSell = new ArrayList<>();
    private final int[] rolledNumbers; //Last rolled numbers
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

    public GameState(@NotNull Integer[] playerIDs, @NotNull Map map, @NotNull Runnable onEndGame, @NotNull GameSettings gameSettings) {
        this.playerIDs = ArrayUtils.toPrimitive(playerIDs);
        this.playerBalances = new long[playerIDs.length];
        this.onEndGame = onEndGame;
        this.gameSettings = gameSettings;
        for (int i = 0; i < playerIDs.length; i++)
            this.playerBalances[i] = gameSettings.getStartingBalance();
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
        rolledNumbers = new int[gameSettings.getNumDices()];
    }

    public void onAutoSellPacket(int playerID) {
        if (Objects.equals(getPlayerNumFromID(playerID), turnOf) && state == State.SELL) {
            autoSell(turnOf, getPlayerBalance(turnOf) + sellAmount);
        } else
            warning("Player " + playerID + " tried to sell outside of their turn!");
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

    public void onEndGameTimeoutReset(int playerID) {
        if (Objects.equals(getPlayerNumFromID(playerID), turnOf) && state == State.END_ROUND) {
            resetTimeout();
            onStateChanged();
        } else
            warning("Player " + playerID + " tried to reset timeout outside of their turn!");
    }

    public void onEndGameAction(int playerID, EndGameActionPacket.EndGameAction action) {
        if (Objects.equals(getPlayerNumFromID(playerID), turnOf) && state == State.END_ROUND) {
            if (action instanceof EndGameActionPacket.ActionEndTurn) {
                endTurn();
            }
        } else
            warning("Player " + playerID + " tried to set end game action outside of their turn!");
    }

    private long getPropertyImprovementCost(int tile) {
        return getPropertyImprovementCost(map.getTiles()[tile % map.getTiles().length]);
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

    private boolean isPlayerInJail(int player) {
        return playerInJail[player];
    }

    private void changeState(State newState) {
        //TODO Add some checks
        if (newState == State.AWAITING_JAIL) {
            if (!isPlayerInJail(turnOf))
                newState = State.AWAITING_ROLL;
        }
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

    public void onImprovePacket(int playerID, int tile, boolean isUpgrade) {
        if (Objects.equals(getPlayerNumFromID(playerID), turnOf) && state == State.END_ROUND) {
            if (Objects.equals(getTileOwner(tile), turnOf) && tile > -1) {
                boolean allow = true;
                Tile tile1 = getTile(tile);
                if (gameSettings.requireAllTilesInGroupToUpdate()) {
                    if (tile1.getType() == Tile.TileType.CITY) {
                        Tile.CityTileData data = (Tile.CityTileData) tile1.getData();
                        for (Tile tile2 : data.getTileGroup().getTiles()) {
                            int tileNumber = getTileNumber(tile2);
                            if (getTileOwner(tileNumber) != turnOf) {
                                allow = false;
                                break;
                            }
                        }
                    } else
                        allow = false;
                }
                if (allow && tile1.getType() == Tile.TileType.CITY) {
                    if (isUpgrade &&
                            getTileLevel(tile) < ((Tile.CityTileData) tile1.getData()).getMaxLevel() &&
                            takePlayerMoney(turnOf, getPropertyImprovementCost(tile))) {
                        setTileLevel(tile, getTileLevel(tile) + 1);
                    } else if (!isUpgrade && getTileLevel(tile) > 0) {
                        givePlayerMoney(turnOf, getPropertyImprovementCost(tile));
                        setTileLevel(tile, getTileLevel(tile) + 1);
                    }
                } else {
                    warning("Improvement requirements not met by player " + playerID);
                }
            } else
                warning("Player " + playerID + " tried to improve other players tile!");
        } else
            warning("Player " + playerID + " tried to improve of their turn!");
    }

    @NotNull
    private Tile getTile(int tile) {
        return map.getTiles()[tile % map.getTiles().length];
    }

    private int getTileLevel(int tile) {
        return tileLevels[tile % map.getTiles().length];
    }

    private int getTileNumber(@NotNull Tile tile) {
        return Utilities.getTileNumber(map, tile);
    }

    private Integer getTileOwner(int tile) {
        return tileOwners[tile % map.getTiles().length];
    }

    private boolean takePlayerMoney(int player, long amount) {
        if (getPlayerBalance(player) < amount)
            return false;
        playerBalances[player % playerIDs.length] -= amount;
        onPlayerBalanceChanged(player);
        onPay(player, null, amount);
        return true;
    }

    private void onPay(@Nullable Integer from, @Nullable Integer to, long amount) {
        sendPacketToAllPlayers(new PayEventPacket(from, to, amount));
    }

    private void onPlayerBalanceChanged(int player) {
        sendPacketToAllPlayers(new PlayerStateChangedPacket(player, getPlayerState(player)));
    }

    private void sendPacketToAllPlayers(Packet packet) {
        for (int playerID : playerIDs) {
            getServer().sendPacketToPlayerByID(packet, playerID);
        }
    }

    @NotNull
    @Contract("_ -> new")
    private PlayerState getPlayerState(int player) {
        return new PlayerState(getPlayerBalance(player), isPlayerInJail(player), isPlayerBankrupt(player));
    }

    private long getPlayerBalance(int player) {
        return playerBalances[player % playerIDs.length];
    }

    private void setTileLevel(int tile, int level) {
        tileLevels[tile % map.getTiles().length] = level;
        onTileModified(tile);
    }

    private void onTileModified(int tile) {
        sendPacketToAllPlayers(new TileModifiedPacket(tile, getTileOwner(tile), getTileLevel(tile)));
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

    public void onMultipleSellPacket(int playerID, int[] tiles) {
        if (Objects.equals(getPlayerNumFromID(playerID), turnOf) && state == State.SELL) {
            propertiesToSell.clear();
            for (int tile : tiles)
                propertiesToSell.add(tile);
        } else
            warning("Player " + playerID + " tried to sell outside of their turn!");
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
                rolledNumbers[i] = (int) Math.floor(Math.random() * (gameSettings.getDiceMax() - gameSettings.getDiceMin() + 1) + gameSettings.getDiceMin());
            }
            playerRolled = true;
        }
        log("Rolled: " + Arrays.toString(rolledNumbers));
        onRolled();
    }

    private void onRolled() {
        sendPacketToAllPlayers(new RollEventPacket(turnOf, rolledNumbers));
    }

    public void onSellPacket(int playerID, int tile) {
        if (Objects.equals(getPlayerNumFromID(playerID), turnOf) && state == State.END_ROUND) {
            if (Objects.equals(getTileOwner(tile), turnOf) && tile > -1) {
                if (gameSettings.requireAllTilesInGroupToUpdate()) {
                    Tile tile1 = getTile(tile);
                    if (tile1.getType() == Tile.TileType.CITY) {
                        Tile.CityTileData data = (Tile.CityTileData) tile1.getData();
                        for (Tile tile2 : data.getTileGroup().getTiles()) {
                            int tileNumber = getTileNumber(tile2);
                            if (getTileOwner(tileNumber) == turnOf) {
                                givePlayerMoney(turnOf, data.getImprovementCost() * getTileLevel(tileNumber));
                                setTileLevel(tileNumber, 0);
                            }
                        }
                    }
                }
                givePlayerMoney(turnOf, getPropertyValue(tile));
                setTileOwner(tile, null);
            } else
                warning("Player " + playerID + " tried to sell other players tile!");
        } else
            warning("Player " + playerID + " tried to sell outside of their turn!");
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

    public int getStartingPlayerNum() {
        return startingPlayerNum;
    }

    public void stopTicking() {
        if (timer != null) timer.cancel();
        timer = null;
    }

    public static long getPropertyImprovementCost(@NotNull Tile tile) {
        if (tile.getType() == Tile.TileType.CITY) {
            Tile.CityTileData data = (Tile.CityTileData) tile.getData();
            return data.getImprovementCost();
        }
        return 0;
    }

    public static long getPropertyValue(@NotNull Tile tile, int tileLevel) {
        switch (tile.getType()) {
            case UTILITY:
            case STATION:
                return getPropertyPrice(tile);
            case CITY:
                Tile.CityTileData data = (Tile.CityTileData) tile.getData();
                return data.getValue() + data.getImprovementCost() * tileLevel;
        }
        return 0;
    }

    public static long getPropertyPrice(@NotNull Tile tile) {
        switch (tile.getType()) {
            case UTILITY:
                return ((Tile.UtilityTileData) tile.getData()).getValue();
            case STATION:
                return ((Tile.StationTileData) tile.getData()).getValue();
            case CITY:
                return ((Tile.CityTileData) tile.getData()).getValue();
        }
        return 0;
    }

    private void setTileOwner(int tile, Integer owner) {
        tileOwners[tile % map.getTiles().length] = owner;
        if (owner == null) {
            if (gameSettings.requireAllTilesInGroupToUpdate()) {
                Tile tile1 = getTile(tile);
                if (tile1.getType() == Tile.TileType.CITY) {
                    for (Tile tile2 : ((Tile.CityTileData) tile1.getData()).getTileGroup().getTiles()) {
                        setTileLevel(getTileNumber(tile2), 0);
                    }
                }
            }
            setTileLevel(tile, 0);
        }
        onTileModified(tile);
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
                } else if (checkTimeout(state.getTimeout(gameSettings.getTimeoutMultiplier()))) {
                    //Default to roll
                    setJailDecision(JailDecision.ROLL);
                    return;
                }
                break;
            case AWAITING_ROLL:
                if (playerRolled) {
                    changeState(State.PLAYER_MOVING);
                    return;
                } else if (checkTimeout(state.getTimeout(gameSettings.getTimeoutMultiplier()))) {
                    roll();
                    return;
                }
                break;
            case PLAYER_MOVING:
                if (playerRolled) {
                    boolean draw = true;
                    for (int i = 0; i < gameSettings.getNumDices() - 1; i++)
                        draw = draw && rolledNumbers[i] == rolledNumbers[i + 1];
                    if (draw) drawsInRow++;
                    else drawsInRow = 0;
                    if (drawsInRow >= gameSettings.getDrawsToJail() && !isPlayerInJail(turnOf)) {
                        boolean moved = false;
                        Tile[] tiles = map.getTiles();
                        for (int i = 0; i < tiles.length; i++) {
                            Tile tile = tiles[i];
                            if (tile.getType() == Tile.TileType.JAIL) {
                                setPlayerInJail(turnOf, true);
                                movePlayerToTile(turnOf, i);
                                moved = true;
                                break;
                            }
                        }
                        if (moved) {
                            if (!gameSettings.allowActionsWhileInJail())
                                endTurn();
                            else
                                changeState(State.END_ROUND);
                            return;
                        }
                    }
                    if (isPlayerInJail(turnOf) && !draw) {
                        //Player stays in jail
                        if (!gameSettings.allowActionsWhileInJail())
                            endTurn();
                        else
                            changeState(State.END_ROUND);
                    } else {
                        setPlayerInJail(turnOf, false);
                        int num = 0;
                        for (int i = 0; i < gameSettings.getNumDices(); i++) num += rolledNumbers[i];
                        movePlayer(turnOf, num);
                        playerRolled = false;
                    }
                }
                int num = 0;
                for (int i = 0; i < gameSettings.getNumDices(); i++) num += rolledNumbers[i];
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
                                changeState(State.END_ROUND);
                            } else
                                setBuyDecision(BuyDecision.DONT_BUY);
                            break;
                        case DONT_BUY:
                            if (gameSettings.auctionsEnabled() && gameSettings.startAuctionOnDontBuy())
                                startAuction(tileToBuy);
                            else
                                changeState(State.END_ROUND);
                            break;
                    }
                } else if (checkTimeout(state.getTimeout(gameSettings.getTimeoutMultiplier()))) {
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
                } else if (checkTimeout(state.getTimeout(gameSettings.getTimeoutMultiplier()))) {
                    if (!autoSell(turnOf, getPlayerBalance(turnOf) + sellAmount)) {
                        bankruptPlayer(turnOf);
                    }
                }
                break;
            case AUCTION:
                //TODO
                if (checkTimeout(state.getTimeout(gameSettings.getTimeoutMultiplier()))) {
                    changeState(State.END_ROUND);
                }
                break;
            case END_ROUND:
                //TODO
                if (drawsInRow > 0) {
                    resetVariables();
                    changeState(State.AWAITING_JAIL);
                }
                if (checkTimeout(state.getTimeout(gameSettings.getTimeoutMultiplier()))) {
                    endTurn();
                }
                break;
        }
    }

    private void setBuyDecision(BuyDecision decision) {
        buyDecision = decision;
    }

    private void endTurn() {
        if (isPlayerInJail(turnOf))
            playerInJailTurns[turnOf]++;
        if (getPlayersLeftInGame() < 2) {
            endGame();
            return;
        }
        turnOf = (turnOf + 1) % playerIDs.length;
        if (turnOf == startingPlayerNum) loopNumber++;
        while (isPlayerBankrupt(turnOf)) {
            turnOf = (turnOf + 1) % playerIDs.length;
            if (turnOf == startingPlayerNum) loopNumber++;
        }

        resetVariables();
        drawsInRow = 0;
        changeState(State.AWAITING_JAIL);
    }

    private void endGame() {
        stopTicking();
        sendPacketToAllPlayers(new EndGameEventPacket());
        onEndGame.run();
    }

    private void setPlayerInJail(int player, boolean inJail) {
        log("setPlayerInJail: " + player + ", " + inJail);
        if (inJail && !isPlayerInJail(player)) {
            playerInJail[player] = true;
            playerInJailTurns[player] = 0;
            drawsInRow = 0;
            onPlayerJailChanged(player);
        } else if (!inJail && isPlayerInJail(player)) {
            playerInJail[player] = false;
            onPlayerJailChanged(player);
        }
    }

    private void onPlayerJailChanged(int player) {
        sendPacketToAllPlayers(new PlayerStateChangedPacket(player, getPlayerState(player)));
    }

    private void initiateSell(long amount, AfterSellAction[] actions) {
        sellAmount = amount;
        afterSellActions.clear();
        Collections.addAll(afterSellActions, actions);
        if (getPlayerValue(turnOf) - getPlayerBalance(turnOf) < amount) {
            bankruptPlayer(turnOf);
            return;
        }
        changeState(State.SELL);
    }

    private void bankruptPlayer(int player) {
        takePlayerMoney(player, getPlayerBalance(player));
        for (int property : getPlayerProperties(player))
            setTileOwner(property, null);
        playerBankrupt[player % playerIDs.length] = true;
        onPlayerBankrupt(player);
        endTurn();
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

    private long getPropertyValue(int tile) {
        //TODO Consider price modifier
        long value = 0;
        if (isTileBuyable(tile)) {
            return getPropertyValue(map.getTiles()[tile % map.getTiles().length], getTileLevel(tile));
        }
        return value;
    }

    private int getJailMaxTurns(int tile) {
        Tile tile1 = getTile(tile);
        if (tile1.getType() == Tile.TileType.JAIL)
            return ((Tile.JailTileData) tile1.getData()).getMaxRounds();
        else
            return -1;
    }

    private long getJailBail(int tile) {
        Tile tile1 = getTile(tile);
        if (tile1.getType() == Tile.TileType.JAIL)
            return ((Tile.JailTileData) tile1.getData()).getBailMoney();
        else
            return 0;
    }

    private int getPlayerPosition(int player) {
        return boundInt(playerPositions[player], map.getTiles().length);
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
        onPay(null, player, amount);
    }

    private void movePlayerToTile(int player, int tileNumber) {
        playerPositions[player] = tileNumber;
        log("Moved player " + player + " to tile " + getPlayerPosition(player));
        onPlayerMoved(player, false);
    }

    private long getPropertyRent(int tile) {
        Tile tile1 = getTile(tile);
        switch (tile1.getType()) {
            case CITY:
                Tile.CityTileData cityTileData = (Tile.CityTileData) tile1.getData();
                return cityTileData.getRent(getTileLevel(tile));
            case STATION:
                Tile.StationTileData stationTileData = (Tile.StationTileData) tile1.getData();
                return stationTileData.getRent(getTileLevel(tile));
            case UTILITY:
                Tile.UtilityTileData utilityTileData = (Tile.UtilityTileData) tile1.getData();
                long sum = 0;
                for (int num : rolledNumbers)
                    sum += num;
                return utilityTileData.getMultiplier(getTileLevel(tile)) * sum;
        }
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean playerPayPlayer(int from, int to, long amount) {
        if (playerBalances[from] < amount)
            return false;
        playerBalances[from] -= amount;
        playerBalances[to] += amount;
        onPlayerBalanceChanged(from);
        onPlayerBalanceChanged(to);
        onPay(from, to, amount);
        return true;
    }

    private long getPlayerValue(int player) {
        long total = getPlayerBalance(player);
        final int[] playerProperties = getPlayerProperties(player);
        for (int property : playerProperties) {
            total += getPropertyValue(property);
        }
        return total;
    }

    private void processTileEntry(int player, int tile) {
        log("Player " + player + " has entered tile " + tile);
        Tile tile1 = getTile(tile);
        switch (tile1.getType()) {
            case START:
                givePlayerMoney(player, (int) (((Tile.StartTileData) tile1.getData()).getStartMoney() * gameSettings.getStartStandMultiplier()));
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
                        if (gameSettings.auctionsEnabled() && gameSettings.startAuctionOnInsufficientFunds()) {
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
                    } else
                        changeState(State.END_ROUND);
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
                final long charge = placeholderTileData.getCharge();
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

    private long getPropertyPrice(int tile) {
        if (isTileBuyable(tile)) {
            return getPropertyPrice(map.getTiles()[tile % map.getTiles().length]);
        }
        return 0;
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
            if (Objects.equals(player, getTileOwner(i))) out.add(i);
        int[] realOut = new int[out.size()];
        for (int i = 0; i < out.size(); i++)
            realOut[i] = out.get(i);
        return realOut;
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
        END_ROUND(10000); //TODO Reset on action

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
        private final long sellAmount;
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
            sellAmount = state.sellAmount;
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
                    timeout = state.state.getTimeout(state.gameSettings.getTimeoutMultiplier());
                    break;
                case PLAYER_MOVING:
                    int num = 0;
                    for (int i = 0; i < state.gameSettings.getNumDices(); i++) num += state.rolledNumbers[i];
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

                        JsonElement sellAmount = jsonObject.get("sellAmount");
                        if (sellAmount != null && sellAmount.isJsonPrimitive())
                            this.sellAmount = sellAmount.getAsLong();
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

        public long getSellAmount() {
            return sellAmount;
        }

        @NotNull
        public JsonObject toJsonObject() {
            JsonObject out = new JsonObject();
            out.addProperty("type", TYPE);
            out.addProperty("map", map.getClass().getCanonicalName());
            out.addProperty("state", state.name());
            out.addProperty("startingPlayerNum", startingPlayerNum);
            out.addProperty("timeoutLeft", timeoutLeft);
            out.addProperty("sellAmount", sellAmount);
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

    public static class GameSettings {
        private boolean allowActionsWhileInJail = false;
        private boolean auctionsEnabled = false;
        private int diceMax = 6;
        private int diceMin = 1;
        private int drawsToJail = 3;
        private int numDices = 2;
        private boolean requireAllTilesInGroupToUpdate = true;
        private boolean startAuctionOnDontBuy = true;
        private boolean startAuctionOnInsufficientFunds = true;
        private float startStandMultiplier = 2.0f;
        private long startingBalance = 15000000L;
        private float timeoutMultiplier = 1f;

        public GameSettings() {
        }

        public boolean allowActionsWhileInJail() {
            return allowActionsWhileInJail;
        }

        public void setAllowActionsWhileInJail(boolean allowActionsWhileInJail) {
            this.allowActionsWhileInJail = allowActionsWhileInJail;
        }

        public boolean auctionsEnabled() {
            return auctionsEnabled;
        }

        public void setAuctionsEnabled(boolean auctionsEnabled) {
            this.auctionsEnabled = auctionsEnabled;
        }

        public int getDiceMax() {
            return diceMax;
        }

        public void setDiceMax(int diceMax) {
            this.diceMax = diceMax;
        }

        public int getDiceMin() {
            return diceMin;
        }

        public void setDiceMin(int diceMin) {
            this.diceMin = diceMin;
        }

        public int getDrawsToJail() {
            return drawsToJail;
        }

        public void setDrawsToJail(int drawsToJail) {
            this.drawsToJail = drawsToJail;
        }

        public int getNumDices() {
            return numDices;
        }

        public void setNumDices(int numDices) {
            this.numDices = numDices;
        }

        public boolean requireAllTilesInGroupToUpdate() {
            return requireAllTilesInGroupToUpdate;
        }

        public void setRequireAllTilesInGroupToUpdate(boolean requireAllTilesInGroupToUpdate) {
            this.requireAllTilesInGroupToUpdate = requireAllTilesInGroupToUpdate;
        }

        public boolean startAuctionOnDontBuy() {
            return startAuctionOnDontBuy;
        }

        public void setStartAuctionOnDontBuy(boolean startAuctionOnDontBuy) {
            this.startAuctionOnDontBuy = startAuctionOnDontBuy;
        }

        public boolean startAuctionOnInsufficientFunds() {
            return startAuctionOnInsufficientFunds;
        }

        public void setStartAuctionOnInsufficientFunds(boolean startAuctionOnInsufficientFunds) {
            this.startAuctionOnInsufficientFunds = startAuctionOnInsufficientFunds;
        }

        public float getStartStandMultiplier() {
            return startStandMultiplier;
        }

        public void setStartStandMultiplier(float startStandMultiplier) {
            this.startStandMultiplier = startStandMultiplier;
        }

        public long getStartingBalance() {
            return startingBalance;
        }

        public void setStartingBalance(long startingBalance) {
            this.startingBalance = startingBalance;
        }

        public float getTimeoutMultiplier() {
            return timeoutMultiplier;
        }

        public void setTimeoutMultiplier(float timeoutMultiplier) {
            this.timeoutMultiplier = timeoutMultiplier;
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
