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

import java.util.*;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;
import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.GameState.*;
import static pl.greenmc.tob.game.util.Logger.*;
import static pl.greenmc.tob.game.util.Utilities.makeMoney;

//TODO Make dialogs responsive

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
    private int[] tileLevels;
    private Integer[] tileOwners;

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
        if (data.getTurnOf() == selfNum) {
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
                default:
                    TOB.runOnGLThread(() -> changeDialog(null));
            }
        } else if (this.state != GameState.State.AUCTION) {
            TOB.runOnGLThread(() -> changeDialog(null));
        }
        if (this.state == GameState.State.AUCTION) {
            TOB.runOnGLThread(() -> changeDialog(new AuctionDialog()));
        }
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
                    //TODO Open tile details if no other popup is shown (except end game)
                    break;
            }

        if (dialog != null) dialog.onMouseUp();
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

    private void changeDialog(@Nullable Dialog newDialog) {
        if (dialog != null) dialog.dispose();
        dialog = newDialog;
        if (newDialog != null) {
            newDialog.setup();
            if (lastMousePos != null) newDialog.onMouseMove((int) lastMousePos.x, (int) lastMousePos.y);
        }
    }

    public void onPay(@Nullable Integer from, @Nullable Integer to, long amount) {
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
        if (gamePlayersStats != null)
            gamePlayersStats.showMessage(getPlayerName(playerIDs[player]) + "\n" + n.toString(), 2500);
    }

    @Override
    public void onScroll(float x, float y) {
        if (dialog != null) dialog.onScroll(x, y);
    }

    public void onTileModified(int tile, Integer owner, int level) {
        if (game3D != null) {
            game3D.setTileOwner(tile, owner);
            game3D.setTileLevel(tile, level);
        }
        tileOwners[tile % map.getTiles().length] = owner;
        tileLevels[tile % map.getTiles().length] = level;
        //Synchronized to await end of generation
        TOB.runOnGLThread(() -> {
            synchronized (endGenerationLock) {
                if (dialog != null)
                    log(dialog.toString());
                if (dialog != null && dialog instanceof EndManageDialog) {
                    changeEndDialog();
                }
            }
        });
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
        batch.dispose();
        frameBuffer.dispose();

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

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        batch.dispose();
        frameBuffer.dispose();
        game3D.dispose();
        gamePlayersStats.dispose();
        if (dialog != null) dialog.dispose();
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
                            () -> {
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
