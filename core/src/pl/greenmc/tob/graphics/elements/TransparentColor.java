package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.graphics.Color;

public class TransparentColor extends SolidColor {
    @Override
    public Color getColor() {
        return new Color(0, 0, 0, 0);
    }

    @Override
    public void draw(float x, float y, float w, float h) {

    }
}
