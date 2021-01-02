package pl.greenmc.tob.graphics.backgrounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import pl.greenmc.tob.graphics.Background;
import pl.greenmc.tob.graphics.GlobalTheme;

public class SolidBackground extends Background {
    private Color color = GlobalTheme.backgroundColor;

    @Override
    public void draw(float x, float y, float w, float h) {
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
    }

    @Override
    public void setup() {
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void resize(int width, int height) {

    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {

    }
}
