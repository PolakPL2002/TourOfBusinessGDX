package pl.greenmc.tob.graphics.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.elements.ProgressBar;

public class IntroScene extends Scene {
    SpriteBatch batch;
    Texture logo;
    ProgressBar progressBar = new ProgressBar();

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        int height = Gdx.graphics.getHeight();
        int width = Gdx.graphics.getWidth();
        float size = Math.max(width / 2, height / 2);
        batch.draw(logo, (width - size) / 2, (height - size) / 2, size, size);
        batch.end();
        progressBar.setValue(progressBar.getValue() + 1);
        if (progressBar.getValue() > 2137) progressBar.setValue(0);
        progressBar.draw((width - size) / 2, (float) (height * 0.3), size, (float) (height / 36.0));
    }

    @Override
    public void setup() {
        batch = new SpriteBatch();
        logo = new Texture("logo.png");
        progressBar.setMax(2137);
        progressBar.setFontSize((int) (Gdx.graphics.getHeight() / 72.0));
        progressBar.setTextMode(ProgressBar.TextMode.INT_INT_PRC);
    }

    @Override
    public void dispose() {
        batch.dispose();
        logo.dispose();
        progressBar.dispose();
    }
}
