package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Interactable;

import static pl.greenmc.tob.game.util.Utilities.LATIN_EXTENDED;

public class Button extends Element implements Interactable {
    private final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Regular.ttf"));
    private final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private Color backgroundColor = GlobalTheme.buttonBackgroundColor;
    private SpriteBatch batch;
    private Color borderColor = GlobalTheme.buttonBorderColor;
    private Runnable clickCallback = null;
    private Color clickColor = GlobalTheme.buttonClickColor;
    private boolean clicked = false;
    private BitmapFont font;
    private int fontSize = 12;
    private boolean hover = false;
    private Color hoverColor = GlobalTheme.buttonHoverColor;
    private GlyphLayout layout;
    private ShapeRenderer renderer;
    private boolean setUp = false;
    private String text;
    private Color textColor = GlobalTheme.textColor;

    public Button(String text) {
        this.text = text;
    }

    @Override
    public void onMouseDown() {
        if (hover)
            clicked = true;
    }

    @Override
    public void onMouseEnter() {
        hover = true;
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
    }

    @Override
    public void onMouseLeave() {
        hover = false;
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    @Override
    public void onMouseUp() {
        if (clicked && hover && clickCallback != null) clickCallback.run();
        clicked = false;
    }

    public void setClickCallback(Runnable clickCallback) {
        this.clickCallback = clickCallback;
    }

    public Color getClickColor() {
        return clickColor;
    }

    public void setClickColor(Color clickColor) {
        this.clickColor = clickColor;
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

    public Color getHoverColor() {
        return hoverColor;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    @Override
    public void draw(float x, float y, float w, float h) {
        x = (int) Math.floor(x);
        y = (int) Math.floor(y);
        w = (int) Math.floor(w);
        h = (int) Math.floor(h);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setAutoShapeType(true);
        renderer.set(ShapeRenderer.ShapeType.Filled);

        //Background
        renderer.setColor(hover ? (clicked ? clickColor : hoverColor) : backgroundColor);
        renderer.rect(x, y, w, h);

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
