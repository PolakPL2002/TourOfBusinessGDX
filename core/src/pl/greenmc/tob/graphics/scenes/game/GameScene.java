package pl.greenmc.tob.graphics.scenes.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.GameState;
import pl.greenmc.tob.game.Player;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.game.map.Tile;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.GetGameStatePacket;
import pl.greenmc.tob.game.netty.packets.game.GetPlayerPacket;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Hitbox;
import pl.greenmc.tob.graphics.Interactable;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.scenes.game.dialogs.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;
import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.error;
import static pl.greenmc.tob.game.util.Utilities.makeMoney;

public class GameScene extends Scene implements Interactable {
    private final Map map;
    private final HashSet<Integer> nameRequestsSent = new HashSet<>();
    private final HashMap<Integer, String> playerNames = new HashMap<>();
    private Dialog dialog = null;
    private Game3D game3D;
    private GamePlayersStats gamePlayersStats;
    private Vector2 lastMousePos = null;
    private long[] playerBalances; //Player balance
    private boolean[] playerBankrupt;
    private int[] playerIDs;
    private boolean[] playerInJail;
    private int[] playerInJailTurns;
    private int[] playerPositions; //Player positions on board
    private int selfNum = -1;
    private int startingPlayerNum;
    private GameState.State state;
    private int[] tileLevels;
    private Integer[] tileOwners;

    public GameScene(Map map) {
        this.map = map;
    }

    public void onGameStateChanged(@NotNull GameState.Data data) {
        playerBalances = data.getPlayerBalances();
        playerBankrupt = data.getPlayerBankrupt();
        playerIDs = data.getPlayerIDs();
        getNames();
        playerInJail = data.getPlayerInJail();
        playerInJailTurns = data.getPlayerInJailTurns();
        playerPositions = data.getPlayerPositions();
        startingPlayerNum = data.getStartingPlayerNum();
        this.state = data.getState();
        tileLevels = data.getTileLevels();
        tileOwners = data.getTileOwners();
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
        if (data.getTurnOf() == selfNum) {
            //TODO Show sell and auction dialogs
            switch (this.state) {
                case AWAITING_JAIL:
                    TOB.runOnGLThread(() -> changeDialog(new JailDialog()));
                    break;
                case AWAITING_ROLL:
                    TOB.runOnGLThread(() -> changeDialog(new RollDialog()));
                    break;
                case AWAITING_BUY:
                    Integer tileToBuy = data.getTileToBuy();
                    if (tileToBuy != null)
                        TOB.runOnGLThread(() -> changeDialog(new BuyDialog(map.getTiles()[tileToBuy % map.getTiles().length])));
                    break;
                case END_ROUND:
                    TOB.runOnGLThread(() -> changeDialog(new EndDialog()));
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
        if (dialog != null) dialog.onMouseDown();
    }

    private void updatePlayersStats() {
        if (gamePlayersStats != null)
            for (int i = 0; i < playerBalances.length; i++) {
                gamePlayersStats.setPlayerBalance(i, playerBalances[i]);
                gamePlayersStats.setPlayerName(i, getPlayerName(playerIDs[i]));
            }
    }

    @Override
    public void onMouseUp() {
        if (dialog != null) dialog.onMouseUp();
    }

    public void onPay(@Nullable Integer from, @Nullable Integer to, long amount) {
        if (from != null) {
            gamePlayersStats.showMessage(getPlayerName(playerIDs[from]) + "\n" + makeMoney(-amount), 2500);
        }
        if (to != null) {
            gamePlayersStats.showMessage(getPlayerName(playerIDs[to]) + "\n" + makeMoney(amount), 2500);
        }
    }

    private String getPlayerName(int playerID) {
        return playerNames.getOrDefault(playerID, "<Ładowanie...>");
    }

    public void onPlayerMoved(int player, int position, boolean animate) {
        playerPositions[player] = position;
        if (game3D != null)
            game3D.movePlayer(player, position, animate);
    }

    public void onPlayerStateChanged(int player, @NotNull GameState.PlayerState state) {
        playerBalances[player] = state.getBalance();
        playerBankrupt[player] = state.isBankrupt();
        playerInJail[player] = state.isJailed();
        updatePlayersStats();
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

    public void onRoll(int player, @NotNull int[] numbers) {
        StringBuilder n = new StringBuilder();
        int sum = 0;
        for (int number : numbers) {
            if (n.length() > 0) n.append(" + ");
            n.append(number);
            sum += number;
        }
        n.append(" = ").append(sum);
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
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(GlobalTheme.backgroundColor.r, GlobalTheme.backgroundColor.g, GlobalTheme.backgroundColor.b, GlobalTheme.backgroundColor.a);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        game3D.render();
        gamePlayersStats.render();
        if (dialog != null) dialog.draw();
    }

    @Override
    public void setup() {
        for (int i = 1; i <= 8; i++) {
            playerNames.put(i + 12, "Client " + i);//TODO
        }
        game3D = new Game3D(map);
        game3D.setup();
        gamePlayersStats = new GamePlayersStats();
        gamePlayersStats.setup();
        updateState();
    }

    @Override
    public void resize(int width, int height) {
        game3D.resize(width, height);
        gamePlayersStats.resize(width, height);
        if (dialog != null) dialog.resize(width, height);
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        game3D.dispose();
        gamePlayersStats.dispose();
        if (dialog != null) dialog.dispose();
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

    private void changeDialog(@Nullable Dialog newDialog) {
        if (dialog != null) dialog.dispose();
        dialog = newDialog;
        if (newDialog != null) {
            newDialog.setup();
            if (lastMousePos != null) newDialog.onMouseMove((int) lastMousePos.x, (int) lastMousePos.y);
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
}
