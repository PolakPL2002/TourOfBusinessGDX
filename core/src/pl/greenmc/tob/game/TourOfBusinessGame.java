package pl.greenmc.tob.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.netty.*;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.ConfirmationPacket;
import pl.greenmc.tob.game.netty.packets.Packet;
import pl.greenmc.tob.game.netty.packets.game.GetSelfPacket;
import pl.greenmc.tob.game.netty.packets.game.events.*;
import pl.greenmc.tob.game.netty.packets.game.events.lobby.*;
import pl.greenmc.tob.game.netty.packets.game.lobby.GetLobbyPacket;
import pl.greenmc.tob.game.netty.packets.game.lobby.GetSelfLobbyPacket;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.scenes.ErrorScene;
import pl.greenmc.tob.graphics.scenes.LoadingScene;
import pl.greenmc.tob.graphics.scenes.game.GameScene;
import pl.greenmc.tob.graphics.scenes.menus.JoinGameMenu;
import pl.greenmc.tob.graphics.scenes.menus.LobbyMenu;
import pl.greenmc.tob.graphics.scenes.menus.MainMenu;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.*;

public class TourOfBusinessGame {
    private final AssetManager assetManager = new AssetManager();
    private final ArrayList<String> musicToLoad = new ArrayList<>();
    private final ArrayList<String> soundsToLoad = new ArrayList<>();
    private final ArrayList<String> texturesToLoad = new ArrayList<>();
    private int connectRetriesLeft = 3;
    private LoadState loadState = LoadState.LOADING_TEXTURES;
    private Player self = null;
    private TourOfBusinessServer tourOfBusinessServer = null;

    public TourOfBusinessGame(boolean headless) {
        if (!headless) {
            LoadingScene loading = (LoadingScene) TOB.getScene();
            loading.setGameInstance(this);

            //Logo
            texturesToLoad.add("test.png");
//            texturesToLoad.add("logo.png");

            //Default map
            texturesToLoad.add("textures/maps/default/board.png");
            texturesToLoad.add("textures/maps/default/chance.png");
            texturesToLoad.add("textures/maps/default/city1.png");
            texturesToLoad.add("textures/maps/default/city2.png");
            texturesToLoad.add("textures/maps/default/city3.png");
            texturesToLoad.add("textures/maps/default/city4.png");
            texturesToLoad.add("textures/maps/default/city5.png");
            texturesToLoad.add("textures/maps/default/city6.png");
            texturesToLoad.add("textures/maps/default/city7.png");
            texturesToLoad.add("textures/maps/default/city8.png");
            texturesToLoad.add("textures/maps/default/city9.png");
            texturesToLoad.add("textures/maps/default/city10.png");
            texturesToLoad.add("textures/maps/default/city11.png");
            texturesToLoad.add("textures/maps/default/city12.png");
            texturesToLoad.add("textures/maps/default/city13.png");
            texturesToLoad.add("textures/maps/default/city14.png");
            texturesToLoad.add("textures/maps/default/city15.png");
            texturesToLoad.add("textures/maps/default/city16.png");
            texturesToLoad.add("textures/maps/default/city17.png");
            texturesToLoad.add("textures/maps/default/city18.png");
            texturesToLoad.add("textures/maps/default/city19.png");
            texturesToLoad.add("textures/maps/default/city20.png");
            texturesToLoad.add("textures/maps/default/city21.png");
            texturesToLoad.add("textures/maps/default/city22.png");
            texturesToLoad.add("textures/maps/default/communityChest.png");
            texturesToLoad.add("textures/maps/default/electricCompany.png");
            texturesToLoad.add("textures/maps/default/goToJail.png");
            texturesToLoad.add("textures/maps/default/incomeTax.png");
            texturesToLoad.add("textures/maps/default/jail.png");
            texturesToLoad.add("textures/maps/default/luxuryTax.png");
            texturesToLoad.add("textures/maps/default/parking.png");
            texturesToLoad.add("textures/maps/default/start.png");
            texturesToLoad.add("textures/maps/default/station1.png");
            texturesToLoad.add("textures/maps/default/station2.png");
            texturesToLoad.add("textures/maps/default/station3.png");
            texturesToLoad.add("textures/maps/default/station4.png");
            texturesToLoad.add("textures/maps/default/waterWorks.png");

            //Game UI
            texturesToLoad.add("textures/ui/game/player1.png");
            texturesToLoad.add("textures/ui/game/player2.png");
            texturesToLoad.add("textures/ui/game/player3.png");
            texturesToLoad.add("textures/ui/game/player4.png");
            texturesToLoad.add("textures/ui/game/player5.png");
            texturesToLoad.add("textures/ui/game/player6.png");
            texturesToLoad.add("textures/ui/game/player7.png");
            texturesToLoad.add("textures/ui/game/player8.png");

            texturesToLoad.add("textures/ui/game/player1OverlayS.png");
            texturesToLoad.add("textures/ui/game/player2OverlayS.png");
            texturesToLoad.add("textures/ui/game/player3OverlayS.png");
            texturesToLoad.add("textures/ui/game/player4OverlayS.png");
            texturesToLoad.add("textures/ui/game/player5OverlayS.png");
            texturesToLoad.add("textures/ui/game/player6OverlayS.png");
            texturesToLoad.add("textures/ui/game/player7OverlayS.png");
            texturesToLoad.add("textures/ui/game/player8OverlayS.png");

            texturesToLoad.add("textures/ui/game/player1OverlayL.png");
            texturesToLoad.add("textures/ui/game/player2OverlayL.png");
            texturesToLoad.add("textures/ui/game/player3OverlayL.png");
            texturesToLoad.add("textures/ui/game/player4OverlayL.png");
            texturesToLoad.add("textures/ui/game/player5OverlayL.png");
            texturesToLoad.add("textures/ui/game/player6OverlayL.png");
            texturesToLoad.add("textures/ui/game/player7OverlayL.png");
            texturesToLoad.add("textures/ui/game/player8OverlayL.png");

            texturesToLoad.add("textures/ui/game/player1Piece.png");
            texturesToLoad.add("textures/ui/game/player2Piece.png");
            texturesToLoad.add("textures/ui/game/player3Piece.png");
            texturesToLoad.add("textures/ui/game/player4Piece.png");
            texturesToLoad.add("textures/ui/game/player5Piece.png");
            texturesToLoad.add("textures/ui/game/player6Piece.png");
            texturesToLoad.add("textures/ui/game/player7Piece.png");
            texturesToLoad.add("textures/ui/game/player8Piece.png");

            texturesToLoad.add("textures/ui/game/overlayS.png");
            texturesToLoad.add("textures/ui/game/overlayL.png");

            texturesToLoad.add("textures/ui/game/overlayTargetS.png");
            texturesToLoad.add("textures/ui/game/overlayTargetL.png");

            texturesToLoad.add("textures/ui/game/levelS.png");
            texturesToLoad.add("textures/ui/game/levelL.png");

            //Menu UI
            texturesToLoad.add("textures/ui/menu/background.png");

//            musicToLoad.add("music/music1.wav");

            loadTextures();
        } else {
            tourOfBusinessServer = new TourOfBusinessServer();
        }
    }

    private void loadTextures() {
        TextureLoader.TextureParameter textureParameter = new TextureLoader.TextureParameter();
        textureParameter.genMipMaps = true;
        texturesToLoad.forEach(texture -> assetManager.load(texture, Texture.class, textureParameter));
    }

    public TourOfBusinessServer getServer() {
        return tourOfBusinessServer;
    }

    public Player getSelf() {
        return self;
    }

    public int getNumTextures() {
        return texturesToLoad.size();
    }

    public int getNumSounds() {
        return soundsToLoad.size() + musicToLoad.size();
    }

    public LoadState getLoadState() {
        return loadState;
    }

    public void setLoadState(@NotNull LoadState loadState) {
        switch (loadState) {
            case LOADING_SOUNDS:
                if (!assetManager.isFinished()) throw new RuntimeException("Load state changed while not ready");
                if (this.loadState != LoadState.LOADING_TEXTURES) throw new RuntimeException("Invalid progression");
                loadSounds();
                break;
            case CONNECTING:
                if (!assetManager.isFinished()) throw new RuntimeException("Load state changed while not ready");
                if (this.loadState != LoadState.LOADING_SOUNDS) throw new RuntimeException("Invalid progression");
                connect();
                break;
            case DONE:
                if (this.loadState != LoadState.CONNECTING) throw new RuntimeException("Invalid progression");
                log("Loading done!");
                updateSelf();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu()));
                        checkInGame();
                    }
                }, 100); //Delay to show done message
        }
        this.loadState = loadState;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    private void checkInGame() {
        try {
            NettyClient.getInstance().getClientHandler().send(new GetSelfLobbyPacket(), new SentPacket.Callback() {
                @Override
                public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                    try {
                        if (response == null) throw new InvalidPacketException();
                        Lobby lobby = GetLobbyPacket.parseResponse(response).getLobby();

                        if (lobby != null) {
                            switch (lobby.getLobbyState()) {
                                case CONFIGURING:
                                case IN_GAME:
                                    //Change to lobby
                                    TOB.runOnGLThread(() -> TOB.changeScene(new LobbyMenu(lobby.getID())));
                                    break;
                            }
                        }
                        log("Got self lobby status.");
                    } catch (InvalidPacketException e) {
                        error("Failed to check in game status!");
                        error(e);
                    }
                }

                @Override
                public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                    //TODO Handle some reasons?
                    warning("Failed to check in game status! Trying again... (message=" + uuid + ", reason=" + reason + ")");
                }
            }, true);
        } catch (ConnectionNotAliveException e) {
            //TODO Handle?
            error("Failed to check in game status!");
            error(e);
        }
    }

    private void updateSelf() {
        try {
            NettyClient.getInstance().getClientHandler().send(new GetSelfPacket(), new SentPacket.Callback() {
                @Override
                public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                    try {
                        if (response == null) throw new InvalidPacketException();
                        self = GetSelfPacket.parseResponse(response);
                        if (self != null)
                            TOB.runOnGLThread(() -> Gdx.graphics.setTitle("TourOfBusiness - " + self.getName()));
                        if (self == null) throw new InvalidPacketException();
                        log("Got self data: name=" + self.getName() + ", id=" + self.getID());
                    } catch (InvalidPacketException e) {
                        error("Failed to get self profile!");
                        error(e);
                    }
                }

                @Override
                public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                    //TODO Handle some reasons?
                    warning("Failed to get self profile! Trying again... (message=" + uuid + ", reason=" + reason + ")");
                }
            }, true);
        } catch (ConnectionNotAliveException e) {
            //TODO Handle?
            error("Failed to get self profile!");
            error(e);
        }
    }

    private void loadSounds() {
        soundsToLoad.forEach(texture -> assetManager.load(texture, Sound.class));
        musicToLoad.forEach(texture -> assetManager.load(texture, Music.class));
    }

    private void connect() {
        connectRetriesLeft--;
        NettyClient.getInstance().connect(null, null, this::connectionLost, () -> setLoadState(LoadState.DONE), new PacketReceivedHandler() {
            @Override
            public void onPacketReceived(Container container, Packet packet, @Nullable String identity) {
                log("Packet received: " + packet);
                Scene scene = TOB.getScene();
                if (packet instanceof LobbyCreatedPacket) {
                    if (scene instanceof JoinGameMenu) {
                        //Refresh list
                        ((JoinGameMenu) scene).onLobbyCreated(((LobbyCreatedPacket) packet).getLobbyID());
                    }
                } else if (packet instanceof LobbyRemovedPacket) {
                    if (scene instanceof JoinGameMenu) {
                        //Refresh list
                        ((JoinGameMenu) scene).onLobbyRemoved(((LobbyRemovedPacket) packet).getLobbyID());
                    } else if (scene instanceof LobbyMenu) {
                        ((LobbyMenu) scene).onLobbyRemoved(((LobbyRemovedPacket) packet).getLobbyID());
                    }
                } else if (packet instanceof GameStartedPacket) {
                    if (scene instanceof JoinGameMenu) {
                        //Refresh list
                        ((JoinGameMenu) scene).onGameStarted(((GameStartedPacket) packet).getLobbyID());
                    } else if (scene instanceof LobbyMenu) {
                        ((LobbyMenu) scene).onGameStarted(((GameStartedPacket) packet).getLobbyID());
                    }
                } else if (packet instanceof PlayerJoinedPacket) {
                    if (scene instanceof LobbyMenu) {
                        ((LobbyMenu) scene).onPlayerJoined(((PlayerJoinedPacket) packet).getPlayerID());
                    }
                } else if (packet instanceof PlayerLeftPacket) {
                    if (scene instanceof LobbyMenu) {
                        ((LobbyMenu) scene).onPlayerLeft(((PlayerLeftPacket) packet).getPlayerID());
                    }
                } else if (packet instanceof PlayerReadyStateChanged) {
                    if (scene instanceof LobbyMenu) {
                        PlayerReadyStateChanged playerReadyStateChanged = (PlayerReadyStateChanged) packet;
                        ((LobbyMenu) scene).onPlayerReadyStateChanged(playerReadyStateChanged.getPlayerID(), playerReadyStateChanged.isReady());
                    }
                } else if (packet instanceof GameStateChangedPacket) {
                    if (scene instanceof GameScene) {
                        ((GameScene) scene).onGameStateChanged(((GameStateChangedPacket) packet).getStateData());
                    }
                } else if (packet instanceof PlayerMovedPacket) {
                    if (scene instanceof GameScene) {
                        final PlayerMovedPacket playerMovedPacket = (PlayerMovedPacket) packet;
                        ((GameScene) scene).onPlayerMoved(playerMovedPacket.getPlayer(), playerMovedPacket.getPosition(), playerMovedPacket.isAnimated());
                    }
                } else if (packet instanceof PlayerStateChangedPacket) {
                    if (scene instanceof GameScene) {
                        final PlayerStateChangedPacket playerStateChangedPacket = (PlayerStateChangedPacket) packet;
                        ((GameScene) scene).onPlayerStateChanged(playerStateChangedPacket.getPlayer(), playerStateChangedPacket.getState());
                    }
                } else if (packet instanceof TileModifiedPacket) {
                    if (scene instanceof GameScene) {
                        final TileModifiedPacket tileModifiedPacket = (TileModifiedPacket) packet;
                        ((GameScene) scene).onTileModified(tileModifiedPacket.getTile(), tileModifiedPacket.getOwner(), tileModifiedPacket.getLevel());
                    }
                } else if (packet instanceof PayEventPacket) {
                    if (scene instanceof GameScene) {
                        final PayEventPacket payEventPacket = (PayEventPacket) packet;
                        ((GameScene) scene).onPay(payEventPacket.getFrom(), payEventPacket.getTo(), payEventPacket.getAmount());
                    }
                } else if (packet instanceof RollEventPacket) {
                    if (scene instanceof GameScene) {
                        final RollEventPacket rollEventPacket = (RollEventPacket) packet;
                        ((GameScene) scene).onRoll(rollEventPacket.getPlayer(), rollEventPacket.getNumbers());
                    }
                } else if (packet instanceof CardGrantedEvent) {
                    if (scene instanceof GameScene) {
                        final CardGrantedEvent cardGrantedEvent = (CardGrantedEvent) packet;
                        ((GameScene) scene).onCardGranted(cardGrantedEvent.getPlayer(), cardGrantedEvent.getCard());
                    }
                }
                //There are no packets that require data response from client
                try {
                    NettyClient.getInstance().getClientHandler().send(new ConfirmationPacket(container.messageUUID, true, true), null, false);
                } catch (ConnectionNotAliveException e) {
                    warning("Failed to send confirmation packet.");
                    warning(e);
                }
            }
        });
    }

    private void connectionLost() {
        if (loadState == LoadState.CONNECTING) {
            if (connectRetriesLeft == 0) {
                error("Failed to connect!");
                TOB.runOnGLThread(() -> TOB.changeScene(new ErrorScene("Nie udało się nawiązać połączenia z serwerem", 10000)));
            } else {
                ((LoadingScene) TOB.getScene()).setConnectRetriesLeft(connectRetriesLeft);
                connect();
            }
        } else {
            TOB.runOnGLThread(() -> TOB.changeScene(new ErrorScene("Utracono połączenie z serwerem\nUruchom grę ponownie", 10000)));
        }
    }

    public enum LoadState {
        LOADING_TEXTURES,
        LOADING_SOUNDS,
        CONNECTING,
        DONE
    }
}
