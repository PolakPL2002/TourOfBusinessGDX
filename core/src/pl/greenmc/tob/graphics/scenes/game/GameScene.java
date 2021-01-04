package pl.greenmc.tob.graphics.scenes.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.GameState;
import pl.greenmc.tob.game.Player;
import pl.greenmc.tob.game.map.Card;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.game.map.Tile;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.*;
import pl.greenmc.tob.game.util.Utilities;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Hitbox;
import pl.greenmc.tob.graphics.Interactable;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.scenes.game.dialogs.*;
import pl.greenmc.tob.graphics.scenes.game.dialogs.trade.IncomingTradeDialog;
import pl.greenmc.tob.graphics.scenes.game.dialogs.trade.SelectPlayerDialog;
import pl.greenmc.tob.graphics.scenes.game.dialogs.trade.TradeDialog;

import java.util.*;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;
import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.GameState.*;
import static pl.greenmc.tob.game.util.Logger.*;
import static pl.greenmc.tob.game.util.Utilities.*;

public class GameScene extends Scene implements Interactable {
    private final Object endGenerationLock = new Object();
    private final Map map;
    private final HashSet<Integer> nameRequestsSent = new HashSet<>();
    private final HashMap<Integer, String> playerNames = new HashMap<>();
    private SpriteBatch batch;
    private Integer clickOnTile = null;
    private Dialog dialog = null;
    private EndGamePage endGamePage = EndGamePage.MAIN;
    private FrameBuffer frameBuffer;
    private Game3D game3D;
    private GamePlayersStats gamePlayersStats;
    private GameState.GameSettings gameSettings;
    private Vector2 lastMousePos = null;
    private long[] playerBalances; //Player balance
    private boolean[] playerBankrupt;
    private Card[][] playerCards;
    private int[] playerIDs;
    private boolean[] playerInJail;
    private int[] playerInJailTurns;
    private int[] playerPositions; //Player positions on board
    private int selfNum = -1;
    private long sellAmount;
    private int startingPlayerNum;
    private GameState.State state;
    private TileClickAction tileClickAction = TileClickAction.DETAILS;
    private int[] tileLevels = new int[0];
    private Integer[] tileOwners = new Integer[0];
    private Trade trade = null;

    public GameScene(Map map) {
        this.map = map;
    }

    public void onCardGranted(int player, @NotNull Card card) {
        gamePlayersStats.showMessage("Gracz " + getPlayerName(playerIDs[player]) + " otrzymał kartę " + card.getName(), 4000);
        updatePlayersStats();
    }

    private String getPlayerName(int playerID) {
        return playerNames.getOrDefault(playerID, "<Ładowanie...>");
    }

    public static void onEndAction() {
        try {
            NettyClient.getInstance().getClientHandler().send(
                    new EndGameTimeoutResetPacket(),
                    new SentPacket.Callback.BlankCallback(), false);
        } catch (ConnectionNotAliveException e) {
            warning(e);
        }
    }

    public void onGameStateChanged(@NotNull GameState.Data data) {
        gameSettings = data.getGameSettings();
        playerBalances = data.getPlayerBalances();
        playerBankrupt = data.getPlayerBankrupt();
        playerIDs = data.getPlayerIDs();
        getNames();
        playerInJail = data.getPlayerInJail();
        playerInJailTurns = data.getPlayerInJailTurns();
        playerPositions = data.getPlayerPositions();
        startingPlayerNum = data.getStartingPlayerNum();
        GameState.State previousState = this.state;
        this.state = data.getState();
        sellAmount = data.getSellAmount();
        boolean modified = false;
        if (tileLevels.length == data.getTileLevels().length) {
            for (int i = 0; i < tileLevels.length; i++) {
                if (tileLevels[i] != data.getTileLevels()[i]) {
                    modified = true;
                    break;
                }
            }
        } else
            modified = true;
        if (!modified && tileOwners.length == data.getTileOwners().length) {
            for (int i = 0; i < tileOwners.length; i++) {
                if (!Objects.equals(tileOwners[i], data.getTileOwners()[i])) {
                    modified = true;
                    break;
                }
            }
        } else
            modified = true;
        tileLevels = data.getTileLevels();
        tileOwners = data.getTileOwners();
        playerCards = data.getPlayerCards();
        if (game3D != null) {
            for (int i = 0; i < tileOwners.length; i++) {
                game3D.setTileOwner(i, tileOwners[i]);
            }
            for (int i = 0; i < tileLevels.length; i++) {
                game3D.setTileLevel(i, tileLevels[i]);
            }
        }
        if (gamePlayersStats != null) {
            gamePlayersStats.setNumPlayers(playerIDs.length);
            gamePlayersStats.setTimeout(data.getTimeoutLeft(), this.state == GameState.State.PLAYER_MOVING ? 0 : data.getTimeoutTotal());
            gamePlayersStats.setCurrentPlayer(data.getTurnOf());
        }
        if (game3D != null)
            game3D.setNumPlayers(playerIDs.length);
        updatePlayersStats();
        final Integer id = getPlayerByID(TOB.getGame().getSelf().getID());
        if (id != null)
            selfNum = id;
        else {
            //TODO Leave game
        }
        tileClickAction = TileClickAction.DETAILS;
        if (data.getTurnOf() == selfNum || (trade != null && data.getState() == State.AWAITING_TRADE)) {
            //TODO Show auction dialog
            switch (this.state) {
                case AWAITING_JAIL:
                    Tile tile = map.getTiles()[playerPositions[selfNum] % map.getTiles().length];
                    boolean showBail = false, showCard = false;
                    long bailAmount = 0;
                    if (tile.getType() == Tile.TileType.JAIL) {
                        bailAmount = (long) (((Tile.JailTileData) tile.getData()).getBailMoney() * gameSettings.getEventMoneyMultiplier());
                        showBail = playerBalances[selfNum % playerBalances.length] >= bailAmount;
                        showCard = playerHasCard(Arrays.asList(playerCards[selfNum]), Card.CardType.GET_OUT_OF_JAIL);
                    }
                    final long finalBailAmount = bailAmount;
                    final boolean finalShowBail = showBail;
                    final boolean finalShowCard = showCard;
                    TOB.runOnGLThread(() -> changeDialog(new JailDialog(finalBailAmount, finalShowCard, finalShowBail)));
                    break;
                case AWAITING_ROLL:
                    TOB.runOnGLThread(() -> changeDialog(new RollDialog()));
                    break;
                case AWAITING_BUY:
                    Integer tileToBuy = data.getTileToBuy();
                    if (tileToBuy != null)
                        TOB.runOnGLThread(() -> changeDialog(new BuyDialog(map.getTiles()[tileToBuy % map.getTiles().length], gameSettings)));
                    break;
                case SELL:
                    if (game3D != null) {
                        game3D.clearSelectedTiles();
                    }
                    tileClickAction = TileClickAction.SELECT;

                    updateSellDialog();
                    break;
                case END_ROUND:
                    if (previousState != GameState.State.END_ROUND) {
                        endGamePage = EndGamePage.MAIN;
                        changeEndDialog();
                    }
                    break;
                case AWAITING_TRADE:
                    log(String.valueOf(trade));
                    if (trade != null)
                        TOB.runOnGLThread(() -> changeDialog(new IncomingTradeDialog(trade,
                                () -> {
                                    try {
                                        NettyClient.getInstance().getClientHandler().send(new TradeResponsePacket(TradeResponsePacket.TradeResponse.ACCEPT), new SentPacket.Callback.BlankCallback(), false);
                                    } catch (ConnectionNotAliveException e) {
                                        error(e);
                                    }
                                },
                                () -> {
                                    try {
                                        NettyClient.getInstance().getClientHandler().send(new TradeResponsePacket(TradeResponsePacket.TradeResponse.REJECT), new SentPacket.Callback.BlankCallback(), false);
                                    } catch (ConnectionNotAliveException e) {
                                        error(e);
                                    }
                                },
                                map, getPlayerName(trade.getPlayer1ID()), getPlayerName(trade.getPlayer2ID()))));
                    else {
                        TOB.runOnGLThread(() -> changeDialog(null));
                        if (gamePlayersStats != null)
                            gamePlayersStats.showMessage("Oczekiwanie na wymianę...", 5000);
                    }
                    break;
                default:
                    TOB.runOnGLThread(() -> changeDialog(null));
            }
        } else if (this.state != GameState.State.AUCTION && !(dialog != null && dialog instanceof PropertyDialog)) {
            TOB.runOnGLThread(() -> changeDialog(null));
        }
        if (this.state == GameState.State.AUCTION) {
            TOB.runOnGLThread(() -> changeDialog(new AuctionDialog()));
        }
        if (modified)
            for (Tile tile : map.getTiles())
                updateTileText(tile);
    }

    public void onIncomingTrade(int playerID, @NotNull TradeDialog.Property[] player1Offer, @NotNull TradeDialog.Property[] player2Offer) {
        trade = new GameState.Trade(playerID, playerIDs[selfNum], player1Offer, player2Offer);
    }

    @Override
    public void onMouseUp() {
        if (game3D != null && clickOnTile != null && Objects.equals(clickOnTile, game3D.getSelectedTile()))
            switch (tileClickAction) {
                case SELECT:
                    if (Objects.equals(tileOwners[clickOnTile], selfNum))
                        game3D.toggleSelection(clickOnTile);
                    if (dialog != null && dialog instanceof SellDialog)
                        updateSellDialog();
                    break;
                case DETAILS:
                    if (dialog == null) {
                        TOB.runOnGLThread(() -> {
                            Integer tileOwner = tileOwners[clickOnTile];
                            changeDialog(new PropertyDialog(map, map.getTiles()[clickOnTile % map.getTiles().length], gameSettings, () -> TOB.runOnGLThread(() -> changeDialog(null)), tileOwner == null ? "Pole nie należy do nikogo" : "Pole należy do " + getPlayerName(playerIDs[tileOwner])));
                        });
                    }
                    break;
            }

        if (dialog != null) dialog.onMouseUp();
    }

    @Override
    public void onMouseDown() {
        if (game3D != null)
            clickOnTile = game3D.getSelectedTile();
        if (dialog != null) dialog.onMouseDown();
    }

    @Override
    public void onMouseMove(int x, int y) {
        lastMousePos = new Vector2(x, y);
        if (dialog != null) dialog.onMouseMove(x, y);
        if (game3D != null) {
            HashMap<Tile, Hitbox> hitboxes = game3D.getHitboxes();
            boolean found = false;
            for (Tile tile : hitboxes.keySet()) {
                if (hitboxes.get(tile).testMouseCoordinates(x, y)) {
                    game3D.setSelectedTile(tile);
                    found = true;
                    break;
                }
            }
            if (!found) game3D.setSelectedTile((Integer) null);
        }
    }

    private void changeDialog(@Nullable Dialog newDialog) {
        disposeObject(dialog);
        dialog = newDialog;
        if (newDialog != null) {
            newDialog.setup();
            if (lastMousePos != null) newDialog.onMouseMove((int) lastMousePos.x, (int) lastMousePos.y);
        }
    }

    private void updateSellDialog() {
        long totalValue = 0;
        if (game3D != null) {
            for (Integer tile : game3D.getSelectedTiles()) {
                totalValue += getPropertyValue(map.getTiles()[tile % map.getTiles().length], tileLevels[tile % map.getTiles().length], gameSettings);
            }
        }

        long finalTotalValue = totalValue;
        TOB.runOnGLThread(() -> changeDialog(new SellDialog(playerBalances[selfNum] + sellAmount, playerBalances[selfNum] + finalTotalValue,
                () -> {
                    try {
                        if (game3D == null) return;
                        NettyClient.getInstance().getClientHandler().send(new MultipleSellPacket(ArrayUtils.toPrimitive(game3D.getSelectedTiles())), new SentPacket.Callback.BlankCallback(), false);
                        game3D.clearSelectedTiles();
                    } catch (ConnectionNotAliveException e) {
                        warning(e);
                    }
                },
                () -> {
                    try {
                        NettyClient.getInstance().getClientHandler().send(new AutoSellPacket(), new SentPacket.Callback.BlankCallback(), false);
                        if (game3D != null) game3D.clearSelectedTiles();
                    } catch (ConnectionNotAliveException e) {
                        warning(e);
                    }
                }
        )));
    }

    public void onTileModified(int tile, Integer owner, int level) {
        if (game3D != null) {
            game3D.setTileOwner(tile, owner);
            game3D.setTileLevel(tile, level);
        }
        if (tileOwners[tile % map.getTiles().length] == null && owner != null) {
            //Tile bought
            if (gamePlayersStats != null)
                gamePlayersStats.showMessage("Gracz " + getPlayerName(playerIDs[owner]) + " kupił " + getTileName(map.getTiles()[tile % map.getTiles().length]) + ".", 5000);
        } else if (tileOwners[tile % map.getTiles().length] != null && owner == null) {
            if (gamePlayersStats != null)
                gamePlayersStats.showMessage("Gracz " + getPlayerName(playerIDs[tileOwners[tile % map.getTiles().length]]) + " sprzedał " + getTileName(map.getTiles()[tile % map.getTiles().length]) + ".", 5000);
        }
        if (map.getTiles()[tile % map.getTiles().length].getType() == Tile.TileType.CITY && tileLevels[tile % map.getTiles().length] != level) {
            if (tileLevels[tile % map.getTiles().length] > level) {
                //Tile downgraded
                if (gamePlayersStats != null && tileOwners[tile % map.getTiles().length] != null)
                    gamePlayersStats.showMessage("Gracz " + getPlayerName(playerIDs[tileOwners[tile % map.getTiles().length]]) + " odlepszył " + getTileName(map.getTiles()[tile % map.getTiles().length]) + " do poziomu " + level + ".", 5000);
            } else {
                //Tile upgraded
                if (gamePlayersStats != null && tileOwners[tile % map.getTiles().length] != null)
                    gamePlayersStats.showMessage("Gracz " + getPlayerName(playerIDs[tileOwners[tile % map.getTiles().length]]) + " ulepszył " + getTileName(map.getTiles()[tile % map.getTiles().length]) + " do poziomu " + level + ".", 5000);
            }
        }
        tileOwners[tile % map.getTiles().length] = owner;
        tileLevels[tile % map.getTiles().length] = level;
        updateTileText(map.getTiles()[tile % map.getTiles().length]);
        TOB.runOnGLThread(() -> {
            if (dialog != null)
                log(dialog.toString());
            if (dialog != null && dialog instanceof EndManageDialog) {
                changeEndDialog();
            }
        });
    }

    public void onPay(@Nullable Integer from, @Nullable Integer to, long amount) {
        if (amount == 0) return;
        if (from != null) {
            gamePlayersStats.showMessage(getPlayerName(playerIDs[from]) + "\n" + makeMoney(-amount), 2500);
        }
        if (to != null) {
            gamePlayersStats.showMessage(getPlayerName(playerIDs[to]) + "\n" + makeMoney(amount), 2500);
        }
    }

    public void onPlayerMoved(int player, int position, boolean animate) {
        playerPositions[player] = position;
        if (game3D != null)
            game3D.movePlayer(player, position, animate);
    }

    public void onPlayerStateChanged(int player, @NotNull GameState.PlayerState state) {
        playerBalances[player] = state.getBalance();
        if (state.isBankrupt() && !playerBankrupt[player])
            gamePlayersStats.showMessage(getPlayerName(playerIDs[player]) + " zbankrutował!", 2500);
        if (state.isJailed() && !playerInJail[player])
            gamePlayersStats.showMessage(getPlayerName(playerIDs[player]) + " trafił do więzienia!", 2500);
        playerBankrupt[player] = state.isBankrupt();
        playerInJail[player] = state.isJailed();
        updatePlayersStats();
    }

    public void onRoll(int player, @NotNull int[] numbers) {
        StringBuilder n = new StringBuilder();
        int sum = 0;
        for (int number : numbers) {
            if (n.length() > 0) n.append(" + ");
            n.append(number);
            sum += number;
        }
        n.append(" = ").append(sum);
        if (gamePlayersStats != null) {
            gamePlayersStats.showMessage(getPlayerName(playerIDs[player]) + "\n" + n.toString(), 2500);
            boolean draw = true;
            for (int i = 0; i < numbers.length - 1 && draw; i++)
                draw = numbers[i] == numbers[i + 1];
            if (draw) gamePlayersStats.showMessage("Dublet!", 5000);
        }
    }

    @Override
    public void onScroll(float x, float y) {
        if (dialog != null) dialog.onScroll(x, y);
    }

    public void onTradeResponse(int playerID, @NotNull TradeResponsePacket.TradeResponse response) {
        trade = null;
        if (gamePlayersStats != null)
            gamePlayersStats.showMessage("Gracz " + getPlayerName(playerID) + " " + (response == TradeResponsePacket.TradeResponse.ACCEPT ? "zaakceptował" : "odrzucił") + " wymianę.", 5000);
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        disposeObject(batch);
        disposeObject(frameBuffer);
        disposeObject(game3D);
        disposeObject(gamePlayersStats);
        disposeObject(dialog);
    }

    @Override
    public void setup() {
        batch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Math.max(Gdx.graphics.getWidth(), 1), Math.max(Gdx.graphics.getHeight(), 1), true);
        game3D = new Game3D(map);
        game3D.setup();
        gamePlayersStats = new GamePlayersStats();
        gamePlayersStats.setup();
        updateState();
    }

    @Override
    public void render() {
        frameBuffer.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //AA disabled
        if (dialog != null) dialog.draw();

        frameBuffer.end();
//        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(GlobalTheme.backgroundColor.r, GlobalTheme.backgroundColor.g, GlobalTheme.backgroundColor.b, GlobalTheme.backgroundColor.a);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //AA Enabled
        game3D.render();
        gamePlayersStats.render();

        batch.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.draw(frameBuffer.getColorBufferTexture(), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        disposeObject(batch);
        disposeObject(frameBuffer);

        batch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Math.max(Gdx.graphics.getWidth(), 1), Math.max(Gdx.graphics.getHeight(), 1), true);

        game3D.resize(width, height);
        gamePlayersStats.resize(width, height);
        if (dialog != null) dialog.resize(width, height);
    }

    private void updatePlayersStats() {
        if (gamePlayersStats != null)
            for (int i = 0; i < playerBalances.length; i++) {
                gamePlayersStats.setPlayerBalance(i, playerBalances[i]);
                gamePlayersStats.setPlayerName(i, getPlayerName(playerIDs[i]));
                gamePlayersStats.setPlayerInJail(i, playerInJail[i]);
                gamePlayersStats.setPlayerBankrupt(i, playerBankrupt[i]);
                gamePlayersStats.setPlayerCards(i, playerCards[i]);
            }
        if (game3D != null)
            for (int i = 0; i < playerBalances.length; i++) {
                game3D.setShowPlayer(i, !playerBankrupt[i]);
            }
    }

    public static String getTileName(@NotNull Tile tile) {
        switch (tile.getType()) {
            case START:
                return "Start";
            case CITY:
                return ((Tile.CityTileData) tile.getData()).getName();
            case STATION:
                return ((Tile.StationTileData) tile.getData()).getName();
            case UTILITY:
                return ((Tile.UtilityTileData) tile.getData()).getName();
            case CHANCE:
                return "Szansa";
            case COMMUNITY_CHEST:
                return "Kasa społeczna";
            case PLACEHOLDER:
                return "Nic ciekawego";
            case JAIL:
                return "Więzienie";
            case GO_TO_JAIL:
                return "Idź do więzienia";
            case CHAMPIONSHIPS:
                return "Mistrzostwa";
            case TRAVEL:
                return "Podróż";
            case INCOME_TAX:
                return "Podatek dochodowy";
            case LUXURY_TAX:
                return "Podatek luksusowy";
        }
        return "";
    }

    public static Color getTileGroupColor(@NotNull Tile tile) {
        switch (tile.getType()) {
            case CITY:
                return ((Tile.CityTileData) tile.getData()).getTileGroup().getColor();
            case STATION:
                return ((Tile.StationTileData) tile.getData()).getTileGroup().getColor();
            case UTILITY:
                return ((Tile.UtilityTileData) tile.getData()).getTileGroup().getColor();
            case JAIL:
                return ((Tile.JailTileData) tile.getData()).getTileGroup().getColor();
            case GO_TO_JAIL:
                return ((Tile.GoToJailTileData) tile.getData()).getTileGroup().getColor();
        }
        return Color.BLACK;
    }

    public static String getTileGroupName(@NotNull Tile tile) {
        switch (tile.getType()) {
            case CITY:
                return ((Tile.CityTileData) tile.getData()).getTileGroup().getName();
            case STATION:
                return ((Tile.StationTileData) tile.getData()).getTileGroup().getName();
            case UTILITY:
                return ((Tile.UtilityTileData) tile.getData()).getTileGroup().getName();
            case JAIL:
                return ((Tile.JailTileData) tile.getData()).getTileGroup().getName();
            case GO_TO_JAIL:
                return ((Tile.GoToJailTileData) tile.getData()).getTileGroup().getName();
        }
        return "";
    }

    private void updateTileText(@NotNull Tile tile) {
        Color color = Color.BLACK;
        String text = "";
        if (tile.getType() == Tile.TileType.CITY || tile.getType() == Tile.TileType.STATION) {
            if (getTileOwner(getTileNumber(tile)) != null) {
                text = makeShortMoney(getPropertyRent(tile, getTileLevel(getTileNumber(tile)), new int[0], gameSettings));
            } else {
                color = GlobalTheme.scheme.color900();
                text = makeShortMoney(getPropertyPrice(tile, gameSettings));
            }
        } else if (tile.getType() == Tile.TileType.UTILITY) {
            if (getTileOwner(getTileNumber(tile)) == null) {
                color = GlobalTheme.scheme.color900();
                text = makeShortMoney(getPropertyPrice(tile, gameSettings));
            }
        } else if (tile.getType() == Tile.TileType.START) {
            text = makeShortMoney(((Tile.StartTileData) tile.getData()).getStartMoney());
            color = GlobalTheme.scheme.color900();
        } else if (tile.getType() == Tile.TileType.LUXURY_TAX) {
            text = makeShortMoney(((Tile.LuxuryTaxTileData) tile.getData()).getCost());
        } else if (tile.getType() == Tile.TileType.INCOME_TAX) {
            text = makeShortMoney(((Tile.IncomeTaxTileData) tile.getData()).getCost());
        }
        final String finalText = text;
        final Color finalColor = color;
        TOB.runOnGLThread(() -> game3D.updateTileText(tile, finalText, finalColor, Color.GRAY, 0));
    }

    private int getTileLevel(int tile) {
        return GameState.getTileLevel(tile, map, tileLevels, tileOwners);
    }

    private Integer getTileOwner(int tile) {
        return GameState.getTileOwner(tile, tileOwners, map);
    }

    private void changeEndDialog() {
        synchronized (endGenerationLock) {
            switch (endGamePage) {
                case MAIN:
                    TOB.runOnGLThread(() -> changeDialog(new EndDialog(() -> {
                        endGamePage = EndGamePage.MANAGE;
                        changeEndDialog();
                    }, () -> {
                        endGamePage = EndGamePage.TRADE;
                        changeEndDialog();
                    })));
                    break;
                case MANAGE:
                    ArrayList<Tile.TileGroup> tileGroups = new ArrayList<>();
                    ArrayList<Tile> tiles = new ArrayList<>();
                    Tile[] mapTiles = map.getTiles();
                    for (int i = 0; i < mapTiles.length; i++) {
                        Tile tile = mapTiles[i];
                        Tile.TileGroup group = null;
                        switch (tile.getType()) {
                            case CITY:
                                group = ((Tile.CityTileData) tile.getData()).getTileGroup();
                                break;
                            case UTILITY:
                                group = ((Tile.UtilityTileData) tile.getData()).getTileGroup();
                                break;
                            case STATION:
                                group = ((Tile.StationTileData) tile.getData()).getTileGroup();
                                break;
                        }
                        if (tileOwners[i] != null && tileOwners[i] == selfNum) {
                            tiles.add(tile);
                            if (group != null && !tileGroups.contains(group))
                                tileGroups.add(group);
                        }
                    }
                    TOB.runOnGLThread(() -> changeDialog(new EndManageDialog(
                            gameSettings, () -> {
                        endGamePage = EndGamePage.MAIN;
                        changeEndDialog();
                    },
                            (@NotNull Tile tile) -> TOB.runOnGLThread(() -> changeDialog(new YesNoDialog(
                                    "Czy na pewno chcesz sprzedać " + getTileName(tile) + " za " + makeMoney(getPropertyValue(tile, tileLevels[getTileNumber(tile)], gameSettings)) + "?",
                                    () -> {
                                        try {
                                            changeEndDialog();
                                            NettyClient.getInstance().getClientHandler().send(new SellPacket(getTileNumber(tile)),
                                                    new SentPacket.Callback.BlankCallback(), false);
                                        } catch (ConnectionNotAliveException e) {
                                            warning(e);
                                        }
                                    }, this::changeEndDialog, GameScene::onEndAction, 3000))),
                            (@NotNull Tile tile, boolean isUpgrade) -> TOB.runOnGLThread(() -> changeDialog(new YesNoDialog(
                                    "Czy na pewno chcesz " + (isUpgrade ? "ulepszyć" : "sprzedać ulepszenie") + " " + getTileName(tile) + " za " + makeMoney(getPropertyImprovementCost(tile, gameSettings)) + "?",
                                    () -> {
                                        try {
                                            changeEndDialog();
                                            NettyClient.getInstance().getClientHandler().send(new ImprovePacket(getTileNumber(tile), isUpgrade),
                                                    new SentPacket.Callback.BlankCallback(), false);
                                        } catch (ConnectionNotAliveException e) {
                                            warning(e);
                                        }
                                    }, this::changeEndDialog, GameScene::onEndAction, 3000))),
                            map, tileGroups.toArray(new Tile.TileGroup[0]), tiles.toArray(new Tile[0]), tileLevels)));
                    break;
                case TRADE:
                    HashMap<String, Integer> names = new HashMap<>();
                    for (int playerID : playerIDs) {
                        if (playerID == playerIDs[selfNum]) continue;
                        names.put(getPlayerName(playerID), getPlayerByID(playerID));
                    }
                    TOB.runOnGLThread(() -> changeDialog(new SelectPlayerDialog(names, () -> {
                        endGamePage = EndGamePage.MAIN;
                        changeEndDialog();
                    }, (int playerNum) -> {
                        final ArrayList<TradeDialog.Property> player1Properties = new ArrayList<>(),
                                player2Properties = new ArrayList<>();

                        player1Properties.add(new TradeDialog.Property(TradeDialog.Property.PropertyType.MONEY, playerBalances[selfNum], new long[0], 0));
                        player2Properties.add(new TradeDialog.Property(TradeDialog.Property.PropertyType.MONEY, playerBalances[playerNum], new long[0], 0));

                        for (int tile = 0; tile < map.getTiles().length; tile++) {
                            long[] group;
                            if (gameSettings.requireAllTilesInGroupToUpgrade() && map.getTiles()[tile].getType() == Tile.TileType.CITY) {
                                ArrayList<Long> tmp = new ArrayList<>();
                                Tile.TileGroup tileGroup = ((Tile.CityTileData) map.getTiles()[tile].getData()).getTileGroup();
                                boolean anyUpgraded = false;
                                for (Tile tile1 : tileGroup.getTiles()) {
                                    int tileNumber = getTileNumber(tile1);
                                    anyUpgraded |= tileLevels[tileNumber] > 0;
                                    if (tileNumber != tile)
                                        tmp.add((long) tileNumber);
                                }
                                if (anyUpgraded)
                                    group = ArrayUtils.toPrimitive(tmp.toArray(new Long[0]));
                                else
                                    group = new long[0];
                            } else
                                group = new long[0];
                            if (Objects.equals(tileOwners[tile], selfNum))
                                player1Properties.add(new TradeDialog.Property(TradeDialog.Property.PropertyType.TILE, tile, group, getPropertyValue(map.getTiles()[tile], tileLevels[tile], gameSettings)));
                            else if (Objects.equals(tileOwners[tile], playerNum))
                                player2Properties.add(new TradeDialog.Property(TradeDialog.Property.PropertyType.TILE, tile, group, getPropertyValue(map.getTiles()[tile], tileLevels[tile], gameSettings)));
                        }

                        changeDialog(new TradeDialog(map, getPlayerName(playerIDs[selfNum]),
                                getPlayerName(playerIDs[playerNum]), player1Properties.toArray(new TradeDialog.Property[0]), player2Properties.toArray(new TradeDialog.Property[0]), () -> {
                            endGamePage = EndGamePage.MAIN;
                            changeEndDialog();
                        }, ((player1Offer, player2Offer) -> {
                            endGamePage = EndGamePage.MAIN;
                            changeEndDialog();
                            try {
                                NettyClient.getInstance().getClientHandler().send(new TradePacket(playerIDs[playerNum], player1Offer, player2Offer),
                                        new SentPacket.Callback() {
                                            @Override
                                            public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                                                if (response == null) {
                                                    error("Null response.");
                                                    return;
                                                }
                                                try {
                                                    boolean valid = TradePacket.parseResponse(response);
                                                    if (!valid) {
                                                        if (gamePlayersStats != null)
                                                            gamePlayersStats.showMessage("Invalid trade.", 5000);
                                                    }
                                                } catch (InvalidPacketException e) {
                                                    error(e);
                                                }
                                            }

                                            @Override
                                            public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                                                if (gamePlayersStats != null)
                                                    gamePlayersStats.showMessage("Failed to send trade.", 5000);
                                            }
                                        }, false);
                            } catch (ConnectionNotAliveException e) {
                                if (gamePlayersStats != null)
                                    gamePlayersStats.showMessage("Failed to send trade.", 5000);
                                error(e);
                            }
                        })));
                    })));
                    break;
            }
        }
    }

    private int getTileNumber(@NotNull Tile tile) {
        return Utilities.getTileNumber(map, tile);
    }

    private void updateState() {
        try {
            NettyClient.getInstance().getClientHandler().send(new GetGameStatePacket(), new SentPacket.Callback() {
                @Override
                public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                    try {
                        if (response != null) {
                            final GameState.Data data = GetGameStatePacket.parseResponse(response);
                            if (data != null)
                                onGameStateChanged(data);
                            else {
                                //TODO We are not in game, leave.
                            }
                        } else {
                            error("Received empty response!");
                            //TODO Leave with error
                        }
                    } catch (InvalidPacketException e) {
                        error(e);
                        //TODO Leave with error
                    }
                }

                @Override
                public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                    error("Failed to get game state: " + reason);
                    //TODO Leave with error
                }
            }, false);
        } catch (ConnectionNotAliveException e) {
            e.printStackTrace();
        }
    }

    private void getNames() {
        for (int id : playerIDs) {
            if (!nameRequestsSent.contains(id)) {
                try {
                    NettyClient.getInstance().getClientHandler().send(new GetPlayerPacket(id), new SentPacket.Callback() {
                        @Override
                        public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                            if (response == null) {
                                fail();
                                return;
                            }
                            try {
                                Player player = GetPlayerPacket.parseResponse(response);
                                if (player == null) {
                                    fail();
                                    return;
                                }
                                playerNames.put(player.getID(), player.getName());
                                updatePlayersStats();
                            } catch (InvalidPacketException e) {
                                fail();
                                error(e);
                            }
                        }

                        private void fail() {
                            playerNames.put(id, "<Failed>");
                            nameRequestsSent.remove(id);
                            error("Failed to get name of player " + id);
                            updatePlayersStats();
                        }

                        @Override
                        public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                            fail();
                        }
                    }, true);
                } catch (ConnectionNotAliveException e) {
                    playerNames.put(id, "<Failed>");
                    nameRequestsSent.remove(id);
                    error("Failed to get name of player " + id);
                    error(e);
                    updatePlayersStats();
                }
                nameRequestsSent.add(id);
            }
        }
    }

    @Nullable
    @Contract(pure = true)
    private Integer getPlayerByID(int playerID) {
        for (int i = 0; i < playerIDs.length; i++)
            if (playerIDs[i] == playerID)
                return i;
        return null;
    }

    private enum EndGamePage {
        MAIN,
        MANAGE,
        TRADE
    }

    private enum TileClickAction {
        SELECT,
        DETAILS
    }
}
