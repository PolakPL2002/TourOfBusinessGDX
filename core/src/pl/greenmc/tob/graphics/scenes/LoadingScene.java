package pl.greenmc.tob.graphics.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pl.greenmc.tob.game.TourOfBusinessGame;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.elements.ProgressBar;

public class LoadingScene extends Scene {
    private final Color backgroundColor = GlobalTheme.backgroundColor;
    private SpriteBatch batch;
    private int connectRetriesLeft = -1;
    private TourOfBusinessGame game = null;
    private Texture logo;
    private ProgressBar progressBar = new ProgressBar();

    public void setConnectRetriesLeft(int connectRetriesLeft) {
        this.connectRetriesLeft = connectRetriesLeft;
    }

    @Override
    public void render() {
        //Process loading
        if (game != null) {
            int numTextures = game.getNumTextures();
            int numSounds = game.getNumSounds();
            switch (game.getLoadState()) {
                case LOADING_TEXTURES:
                    progressBar.setValue(0.5 * (numTextures - game.getAssetManager().getQueuedAssets()) / numTextures);
                    progressBar.setText("Wczytywanie grafik (" + (numTextures - game.getAssetManager().getQueuedAssets()) + "/" + numTextures + ")");
                    if (game.getAssetManager().update()) game.setLoadState(TourOfBusinessGame.LoadState.LOADING_SOUNDS);
                    break;
                case LOADING_SOUNDS:
                    progressBar.setValue(0.5 + 0.25 * (numSounds - game.getAssetManager().getQueuedAssets()) / numSounds);
                    progressBar.setText("Wczytywanie dźwięków (" + (numSounds - game.getAssetManager().getQueuedAssets()) + "/" + numSounds + ")");
                    if (game.getAssetManager().update()) {
                        game.setLoadState(TourOfBusinessGame.LoadState.CONNECTING);
                        progressBar.setValue(0.75);
                    }
                    break;
                case CONNECTING:
                    progressBar.setValue(0.75 + 0.25 * (1 - (1 - (progressBar.getValue() - 0.75) / 0.25) * 0.998)); //1000 = num textures
                    if (progressBar.getValue() > 0.98) progressBar.setValue(0.98);
                    progressBar.setText("Łączenie z serwerem..." + (connectRetriesLeft != -1 ? " (Pozostało prób: " + connectRetriesLeft + ")" : ""));
                    break;
                case DONE:
                    progressBar.setValue(1);
                    progressBar.setText("Gotowe!");
            }
        }

        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        batch.begin();
        int height = Gdx.graphics.getHeight();
        int width = Gdx.graphics.getWidth();
        float size = Math.max(width / 2, height / 2);
        batch.draw(logo, (width - size) / 2, (height - size) / 2, size, size);
        batch.end();
        progressBar.draw((width - size) / 2, (float) (height * 0.3), size, (float) (height / 36.0));
    }

    @Override
    public void setup() {
        batch = new SpriteBatch();
        logo = new Texture("logo.png");
        progressBar.setup();
        progressBar.setMax(1);
        progressBar.setFontSize((int) (Gdx.graphics.getHeight() / 72.0));
        progressBar.setTextMode(ProgressBar.TextMode.CUSTOM);
    }

    @Override
    public void dispose() {
        batch.dispose();
        logo.dispose();
        progressBar.dispose();
    }

    public void updateText(String text) {
        progressBar.setText(text);
    }

    public void setGameInstance(TourOfBusinessGame game) {
        this.game = game;
    }
}
