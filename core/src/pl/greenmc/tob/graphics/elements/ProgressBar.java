package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.GlobalTheme;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static com.badlogic.gdx.graphics.GL20.*;
import static pl.greenmc.tob.game.util.Utilities.LATIN_EXTENDED;

public class ProgressBar extends Element {
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));
    private final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Regular.ttf"));
    private final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private Color backgroundColor = GlobalTheme.progressBarBackgroundColor;
    private SpriteBatch batch;
    private Color borderColor = GlobalTheme.progressBarBorderColor;
    private BitmapFont font;
    private Color foregroundColor = GlobalTheme.progressBarColor;
    private GlyphLayout layout;
    private double max = 100;
    private double min = 0;
    private ShapeRenderer renderer;
    private boolean setUp = false;
    private String text = "";
    private Color textColor = GlobalTheme.textColor;
    private TextMode textMode = TextMode.FLOAT;
    private double value = 0;

    @Override
    public void setup() {
        if (setUp) return;
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        setFontSize(12);
        setUp = true;
    }

    public void setFontSize(int size) {
        parameter.size = size;
        parameter.characters = LATIN_EXTENDED;
        if (font != null) font.dispose();
        font = generator.generateFont(parameter);
        layout = new GlyphLayout(font, text);
    }

    @Override
    public void resize(int width, int height) {
        if (renderer != null) renderer.dispose();
        if (batch != null) batch.dispose();

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextMode getTextMode() {
        return textMode;
    }

    public void setTextMode(TextMode textMode) {
        this.textMode = textMode;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void draw(float x, float y, float w, float h) {
        x = Math.round(x);
        y = Math.round(y);
        w = Math.round(w);
        h = Math.round(h);
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setAutoShapeType(true);
        renderer.set(ShapeRenderer.ShapeType.Filled);

        //Background
        renderer.setColor(backgroundColor);
        renderer.rect(x, y, w, h);

        //Foreground
        renderer.setColor(foregroundColor);
        double prc = (value - min) / (max - min);
        if (prc > 1) prc = 1;
        if (prc < 0) prc = 0;
        renderer.rect(x, y, (float) ((w - 1) * prc), h - 1);

        //Border
        renderer.setColor(borderColor);
        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.line(x, y + 1, x + w, y + 1);
        renderer.line(x + 1, y + 1, x + 1, y + h);
        renderer.line(x + w, y + 1, x + w, y + h);
        renderer.line(x + 1, y + h, x + w, y + h);

        renderer.end();

        batch.begin();
        font.setColor(textColor);
        final String text;
        switch (textMode) {
            case FLOAT:
                text = value + "/" + max;
                break;
            case INT:
                text = (int) value + "/" + (int) max;
                break;
            case FLOAT_FLOAT_PRC:
                text = value + "/" + max + " (" + decimalFormat.format(prc * 100) + "%)";
                break;
            case INT_FLOAT_PRC:
                text = (int) value + "/" + (int) max + " (" + decimalFormat.format(prc * 100) + "%)";
                break;
            case FLOAT_INT_PRC:
                text = value + "/" + max + " (" + (int) (prc * 100) + "%)";
                break;
            case INT_INT_PRC:
                text = (int) value + "/" + (int) max + " (" + (int) (prc * 100) + "%)";
                break;
            case CUSTOM:
                text = this.text;
                break;
            case NONE:
            default:
                text = "";
        }

        layout.setText(font, text);

        final float fontX = x + (w - layout.width) / 2;
        final float fontY = y + (h + layout.height) / 2;
        font.draw(batch, layout, fontX, fontY);
        batch.end();
    }

    public void dispose() {
        generator.dispose();
        if (renderer != null) renderer.dispose();
        if (batch != null) batch.dispose();
        font.dispose();
    }

    public enum TextMode {
        FLOAT,
        INT,
        FLOAT_FLOAT_PRC,
        INT_FLOAT_PRC,
        FLOAT_INT_PRC,
        INT_INT_PRC,
        NONE,
        CUSTOM
    }
}
