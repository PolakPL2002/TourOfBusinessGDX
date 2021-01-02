package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.GlobalTheme;

import static pl.greenmc.tob.game.util.Utilities.disposeObject;

public class SolidColor extends Element {
    private Color color = GlobalTheme.backgroundColor;
    private ShapeRenderer renderer;

    public SolidColor() {
    }

    public SolidColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void draw(float x, float y, float w, float h) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(color);
        renderer.rect(x, y, w, h);
        renderer.end();
    }

    @Override
    public void setup() {
        renderer = new ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {
        disposeObject(renderer);
        renderer = new ShapeRenderer();
    }

    @Override
    public void dispose() {
        disposeObject(renderer);
    }
}
