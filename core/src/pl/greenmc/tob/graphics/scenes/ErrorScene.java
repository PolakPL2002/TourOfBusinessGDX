package pl.greenmc.tob.graphics.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Align;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.elements.ProgressBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static pl.greenmc.tob.game.util.Logger.log;
import static pl.greenmc.tob.game.util.Utilities.LATIN_EXTENDED;
import static pl.greenmc.tob.game.util.Utilities.disposeObject;

public class ErrorScene extends Scene {
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));
    private final String error;
    private final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Regular.ttf"));
    private final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private final long timeout;
    private Color backgroundColor = GlobalTheme.errorBackgroundColor;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private ProgressBar progressBar;
    private Color textColor = GlobalTheme.textColor;
    private long timeStart;
    private FrameBuffer frameBuffer;

    public ErrorScene(String error, long timeout) {
        this.error = error;
        this.timeout = timeout;
    }

    @Override
    public void render() {
        frameBuffer.begin();
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.setColor(textColor);

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        layout.setText(font, error, textColor, (float) (width * 0.8), Align.center, true);

        font.draw(batch, layout, (float) (width * 0.1), (height + layout.height) / 2);
        batch.end();
        float size = (float) (width * 0.5);
        long timeLeft = timeout - System.currentTimeMillis() + timeStart;
        progressBar.setValue(timeLeft);
        if (timeLeft <= 0) Gdx.app.exit();
        if (timeLeft < 0) timeLeft = 0;
        progressBar.setText(decimalFormat.format(timeLeft / 1000.0) + "s");
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
        log("ErrorScene set up with message '" + error + "' and timeout " + timeout + "ms.");
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Math.max(Gdx.graphics.getWidth(), 1), Math.max(Gdx.graphics.getHeight(), 1), false);
        batch = new SpriteBatch();
        timeStart = System.currentTimeMillis();

        progressBar = new ProgressBar();
        progressBar.setup();
        progressBar.setFontSize(Gdx.graphics.getHeight() / 72);
        progressBar.setMax(timeout);
        progressBar.setTextMode(ProgressBar.TextMode.CUSTOM);
        progressBar.setBackgroundColor(backgroundColor);

        parameter.size = Gdx.graphics.getHeight() / 36;
        parameter.characters = LATIN_EXTENDED;
        disposeObject(font);
        font = generator.generateFont(parameter);

        layout = new GlyphLayout(font, error);
    }

    @Override
    public void resize(int width, int height) {
        disposeObject(frameBuffer);
        disposeObject(batch);

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Math.max(Gdx.graphics.getWidth(), 1), Math.max(Gdx.graphics.getHeight(), 1), false);
        batch = new SpriteBatch();
        timeStart = System.currentTimeMillis();

        progressBar.resize(width, height);
    }

    @Override
    public void dispose() {
        disposeObject(frameBuffer);
        disposeObject(progressBar);
        disposeObject(generator);
        disposeObject(font);
        disposeObject(batch);
    }
}
