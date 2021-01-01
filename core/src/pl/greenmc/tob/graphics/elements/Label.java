package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
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
    private Color outlineColor = Color.WHITE;
    private float outlineWidth = 0;
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

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
        setFontSize(fontSize);
    }

    public void setFontSize(int size) {
        if (size < 5) size = 5;
        fontSize = size;
        if (renderer != null) {
            parameter.size = size;
            parameter.characters = LATIN_EXTENDED;
            parameter.color = textColor;
            parameter.borderColor = outlineColor;
            parameter.borderWidth = outlineWidth;
            if (font != null) font.dispose();
            font = generator.generateFont(parameter);
            layout = new GlyphLayout(font, text);
        }
    }

    public void setOutlineWidth(float outlineWidth) {
        this.outlineWidth = outlineWidth;
        setFontSize(fontSize);
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
        setFontSize(fontSize);
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
        x = (int) Math.floor(x);
        y = (int) Math.floor(y);
        w = (int) Math.floor(w);
        h = (int) Math.floor(h);
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
        layout.setText(font, text, Color.WHITE, (float) (w * 0.8), Align.center, true);

        font.draw(batch, layout, x + (float) (w * 0.1), y + (h + layout.height) / 2);
        batch.end();
    }

    public void dispose() {
        generator.dispose();
        if (renderer != null) renderer.dispose();
        if (batch != null) batch.dispose();
        font.dispose();
    }
}
