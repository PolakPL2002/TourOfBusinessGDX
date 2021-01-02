package pl.greenmc.tob.graphics.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import pl.greenmc.tob.game.TourOfBusinessGame;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.elements.ProgressBar;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static pl.greenmc.tob.game.util.Utilities.disposeObject;

public class LoadingScene extends Scene {
    private final Color backgroundColor = GlobalTheme.backgroundColor;
    private SpriteBatch batch;
    private int connectRetriesLeft = -1;
    private TourOfBusinessGame game = null;
    private Texture logo;
    private ProgressBar progressBar;
    private FrameBuffer frameBuffer;

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

        frameBuffer.begin();
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        int height = Gdx.graphics.getHeight();
        int width = Gdx.graphics.getWidth();
        float size = Math.min(width / 2, height / 2);
        batch.draw(logo, (width - size) / 2, (height - size) / 2, size, size);
        batch.end();
        if (progressBar.getFontSize() != (int) (Gdx.graphics.getHeight() / 72.0))
            progressBar.setFontSize((int) (Gdx.graphics.getHeight() / 72.0));
        progressBar.draw((width - size) / 2, (float) (height * 0.3), size, (float) (height / 36.0));
        frameBuffer.end();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(frameBuffer.getColorBufferTexture(), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void setup() {
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Math.max(Gdx.graphics.getWidth(), 1), Math.max(Gdx.graphics.getHeight(), 1), false);
        batch = new SpriteBatch();
        logo = new Texture(Gdx.files.internal("logo.png"), true);
        logo.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        progressBar = new ProgressBar();
        progressBar.setup();
        progressBar.setMax(1);
        progressBar.setFontSize((int) (Gdx.graphics.getHeight() / 72.0));
        progressBar.setTextMode(ProgressBar.TextMode.CUSTOM);
    }

    @Override
    public void resize(int width, int height) {
        disposeObject(frameBuffer);
        disposeObject(batch);

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Math.max(Gdx.graphics.getWidth(), 1), Math.max(Gdx.graphics.getHeight(), 1), false);
        batch = new SpriteBatch();
        progressBar.resize(width, height);
    }

    @Override
    public void dispose() {
        disposeObject(frameBuffer);
        disposeObject(batch);
        disposeObject(logo);
        disposeObject(progressBar);
    }

    public void updateText(String text) {
        progressBar.setText(text);
    }

    public void setGameInstance(TourOfBusinessGame game) {
        this.game = game;
    }
}
