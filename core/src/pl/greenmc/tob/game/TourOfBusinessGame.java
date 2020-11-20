package pl.greenmc.tob.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.server.NettyServer;
import pl.greenmc.tob.graphics.scenes.ErrorScene;
import pl.greenmc.tob.graphics.scenes.LoadingScene;
import pl.greenmc.tob.graphics.scenes.menus.MainMenu;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.error;
import static pl.greenmc.tob.game.util.Logger.log;

public class TourOfBusinessGame {
    private final AssetManager assetManager = new AssetManager();
    private final ArrayList<String> musicToLoad = new ArrayList<>();
    private final ArrayList<String> soundsToLoad = new ArrayList<>();
    private final ArrayList<String> texturesToLoad = new ArrayList<>();
    private int connectRetriesLeft = 3;
    private LoadState loadState = LoadState.LOADING_TEXTURES;

    public TourOfBusinessGame(boolean headless) {
        if (!headless) {
            LoadingScene loading = (LoadingScene) TOB.getScene();
            loading.setGameInstance(this);

            //Logo
            texturesToLoad.add("test.png");
            texturesToLoad.add("logo.png");

            //Default map
            texturesToLoad.add("textures/maps/default/board.png");
            texturesToLoad.add("textures/maps/default/chance.png");
            texturesToLoad.add("textures/maps/default/communityChest.png");
            texturesToLoad.add("textures/maps/default/goToJail.png");
            texturesToLoad.add("textures/maps/default/group1.png");
            texturesToLoad.add("textures/maps/default/group2.png");
            texturesToLoad.add("textures/maps/default/group3.png");
            texturesToLoad.add("textures/maps/default/group4.png");
            texturesToLoad.add("textures/maps/default/incomeTax.png");
            texturesToLoad.add("textures/maps/default/jail.png");
            texturesToLoad.add("textures/maps/default/parking.png");
            texturesToLoad.add("textures/maps/default/start.png");
            texturesToLoad.add("textures/maps/default/station.png");

            //Game UI
            texturesToLoad.add("textures/ui/game/player1.png");
            texturesToLoad.add("textures/ui/game/player2.png");
            texturesToLoad.add("textures/ui/game/player3.png");
            texturesToLoad.add("textures/ui/game/player4.png");
            texturesToLoad.add("textures/ui/game/player5.png");
            texturesToLoad.add("textures/ui/game/player6.png");
            texturesToLoad.add("textures/ui/game/player7.png");
            texturesToLoad.add("textures/ui/game/player8.png");

            //Menu UI
            texturesToLoad.add("textures/ui/menu/background.png");

//            musicToLoad.add("music/music1.wav");

            loadTextures();
        } else {
            NettyServer.getInstance().start(null);
        }
    }

    private void loadTextures() {
        TextureLoader.TextureParameter textureParameter = new TextureLoader.TextureParameter();
        textureParameter.genMipMaps = true;
        texturesToLoad.forEach(texture -> assetManager.load(texture, Texture.class, textureParameter));
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
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu()));
                    }
                }, 100); //Delay to show done message
        }
        this.loadState = loadState;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    private void loadSounds() {
        soundsToLoad.forEach(texture -> assetManager.load(texture, Sound.class));
        musicToLoad.forEach(texture -> assetManager.load(texture, Music.class));
    }

    private void connect() {
        connectRetriesLeft--;
        NettyClient.getInstance().connect(null, null, this::connectionLost, () -> setLoadState(LoadState.DONE));
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
