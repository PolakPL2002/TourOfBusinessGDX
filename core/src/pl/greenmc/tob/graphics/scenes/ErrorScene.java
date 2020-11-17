package pl.greenmc.tob.graphics.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.elements.ProgressBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static pl.greenmc.tob.game.util.Utilities.LATIN_EXTENDED;

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
    private ProgressBar progressBar = new ProgressBar();
    private Color textColor = GlobalTheme.textColor;
    private long timeStart;

    public ErrorScene(String error, long timeout) {
        this.error = error;
        this.timeout = timeout;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

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
        progressBar.setText(decimalFormat.format(timeLeft / 1000.0) + "s");
        progressBar.draw((width - size) / 2, (float) (height * 0.3), size, (float) (height / 36.0));
    }

    @Override
    public void setup() {
        batch = new SpriteBatch();
        timeStart = System.currentTimeMillis();

        progressBar.setup();
        progressBar.setFontSize(Gdx.graphics.getHeight() / 72);
        progressBar.setMax(timeout);
        progressBar.setTextMode(ProgressBar.TextMode.CUSTOM);
        progressBar.setBackgroundColor(backgroundColor);

        parameter.size = Gdx.graphics.getHeight() / 36;
        parameter.characters = LATIN_EXTENDED;
        if (font != null) font.dispose();
        font = generator.generateFont(parameter);

        layout = new GlyphLayout(font, error);
    }

    @Override
    public void dispose() {
        progressBar.dispose();
        generator.dispose();
        font.dispose();
        batch.dispose();

    }
}
