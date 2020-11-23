package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.GlobalTheme;

import static pl.greenmc.tob.game.util.Utilities.LATIN_EXTENDED;

public class Label extends Element {
    private final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Regular.ttf"));
    private final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private Color backgroundColor = new Color(0, 0, 0, 0);
    private SpriteBatch batch;
    private Color borderColor = new Color(0, 0, 0, 0);
    private BitmapFont font;
    private int fontSize;
    private GlyphLayout layout;
    private boolean renderBorder;
    private ShapeRenderer renderer;
    private boolean setUp = false;
    private String text;
    private Color textColor = GlobalTheme.textColor;

    public Label(String text, int fontSize, boolean renderBorder) {
        this.text = text;
        this.fontSize = fontSize;
        this.renderBorder = renderBorder;
    }

    public boolean isRenderBorder() {
        return renderBorder;
    }

    public void setRenderBorder(boolean renderBorder) {
        this.renderBorder = renderBorder;
    }

    @Override
    public void setup() {
        if (setUp) return;
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        setFontSize(fontSize);
        setUp = true;
    }

    public void setFontSize(int size) {
        fontSize = size;
        if (renderer != null) {
            parameter.size = size;
            parameter.characters = LATIN_EXTENDED;
            if (font != null) font.dispose();
            font = generator.generateFont(parameter);
            layout = new GlyphLayout(font, text);
        }
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

    @Override
    public void draw(float x, float y, float w, float h) {
        x = Math.round(x);
        y = Math.round(y);
        w = Math.round(w);
        h = Math.round(h);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        renderer.setAutoShapeType(true);
        renderer.set(ShapeRenderer.ShapeType.Filled);

        //Background
        renderer.setColor(backgroundColor);
        renderer.rect(x, y, w, h);

        //Border
        if (renderBorder) {
            renderer.setColor(borderColor);
            renderer.set(ShapeRenderer.ShapeType.Line);
            renderer.line(x, y + 1, x + w, y + 1);
            renderer.line(x + 1, y + 1, x + 1, y + h);
            renderer.line(x + w, y + 1, x + w, y + h);
            renderer.line(x + 1, y + h, x + w, y + h);
        }
        renderer.end();

        batch.begin();
        font.setColor(textColor);
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
}
