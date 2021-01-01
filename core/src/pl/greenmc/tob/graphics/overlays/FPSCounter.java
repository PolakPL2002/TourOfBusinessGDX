package pl.greenmc.tob.graphics.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import pl.greenmc.tob.graphics.Overlay;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class FPSCounter extends Overlay {
    private SpriteBatch batch;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));
    private final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Regular.ttf"));
    private final int numSamples;
    private final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private final long[] samples;
    private BitmapFont font;
    private GlyphLayout layout;
    private int sampleID = 0;
    private Color textColor = Color.LIGHT_GRAY;

    public FPSCounter(int fontSize, int numSamples) {
        this.numSamples = numSamples;
        samples = new long[numSamples];
        setFontSize(fontSize);
        for (int i = 0; i < numSamples; i++) {
            samples[i] = -1;
        }
    }

    public void setFontSize(int size) {
        if (size < 5) size = 5;
        parameter.size = size;
        font = generator.generateFont(parameter);
        layout = new GlyphLayout(font, "");
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    @Override
    public void setup() {
        batch = new SpriteBatch();
    }

    @Override
    public void draw() {
        long time = System.nanoTime();
        if (samples[numSamples - 1] != -1) {
            double delta = (time - samples[sampleID]) / (double) numSamples;
            double fps = 1000000000.0 / delta;
            String text = decimalFormat.format(fps) + " FPS";
            layout.setText(font, text);
            batch.begin();
            font.setColor(textColor);
            font.draw(batch, text, 0, Gdx.graphics.getHeight() - (layout.height / 2));
            batch.end();
        }
        samples[sampleID] = time;
        sampleID++;
        if (sampleID == numSamples) sampleID = 0;
    }

    @Override
    public void resize(int width, int height) {
        batch.dispose();
        batch = new SpriteBatch();
    }

    @Override
    public void dispose() {
        batch.dispose();
        generator.dispose();
        if (font != null) font.dispose();
    }
}
