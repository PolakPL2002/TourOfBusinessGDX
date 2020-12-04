package pl.greenmc.tob.graphics.scenes.game;

import com.badlogic.gdx.Gdx;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.GameState;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.GetGameStatePacket;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Interactable;
import pl.greenmc.tob.graphics.Scene;

import java.util.HashMap;
import java.util.UUID;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;
import static pl.greenmc.tob.game.util.Logger.error;

public class GameScene extends Scene implements Interactable {
    private final Map map;
    private Game3D game3D;
    private GamePlayersStats gamePlayersStats;
    private long[] playerBalances; //Player balance
    private boolean[] playerBankrupt;
    private int[] playerIDs;
    private boolean[] playerInJail;
    private int[] playerInJailTurns;
    private HashMap<Integer, String> playerNames = new HashMap<>();
    private int[] playerPositions; //Player positions on board
    private int startingPlayerNum;
    private GameState.State state;
    private int[] tileLevels;
    private Integer[] tileOwners;

    public GameScene(Map map) {
        this.map = map;
    }

    @Override
    public void onMouseDown() {

    }

    @Override
    public void onMouseMove(int x, int y) {
        gamePlayersStats.onMouseMove(x, y);
    }

    @Override
    public void onMouseUp() {

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

    private void updatePlayersStats() {
        if (gamePlayersStats != null)
            for (int i = 0; i < playerBalances.length; i++) {
                gamePlayersStats.setPlayerBalance(i, playerBalances[i]);
                gamePlayersStats.setPlayerName(i, playerNames.getOrDefault(playerIDs[i], ""));
            }
    }

    @Override
    public void onScroll(float x, float y) {

    }

    public void onTileModified(int tile, Integer owner, int level) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(GlobalTheme.backgroundColor.r, GlobalTheme.backgroundColor.g, GlobalTheme.backgroundColor.b, GlobalTheme.backgroundColor.a);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        game3D.render();
        gamePlayersStats.render();
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
                        }
                    } catch (InvalidPacketException e) {
                        error(e);
                    }
                }

                @Override
                public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                    error("Failed to get game state: " + reason);
                }
            }, false);
        } catch (ConnectionNotAliveException e) {
            e.printStackTrace();
        }
    }

    public void onGameStateChanged(@NotNull GameState.Data data) {
        playerBalances = data.getPlayerBalances();
        playerBankrupt = data.getPlayerBankrupt();
        playerIDs = data.getPlayerIDs();
        playerInJail = data.getPlayerInJail();
        playerInJailTurns = data.getPlayerInJailTurns();
        playerPositions = data.getPlayerPositions();
        startingPlayerNum = data.getStartingPlayerNum();
        this.state = data.getState();
        tileLevels = data.getTileLevels();
        tileOwners = data.getTileOwners();
        if (gamePlayersStats != null) {
            gamePlayersStats.setNumPlayers(playerIDs.length);
            gamePlayersStats.setTimeout(data.getTimeoutLeft(), data.getTimeoutTotal());
            gamePlayersStats.setCurrentPlayer(data.getTurnOf());
        }
        if (game3D != null)
            game3D.setNumPlayers(playerIDs.length);
        updatePlayersStats();
    }

    @Override
    public void resize(int width, int height) {
        game3D.resize(width, height);
        gamePlayersStats.resize(width, height);
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        game3D.dispose();
        gamePlayersStats.dispose();
    }
}
