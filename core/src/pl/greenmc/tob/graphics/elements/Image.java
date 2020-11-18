package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pl.greenmc.tob.graphics.Element;

public class Image extends Element {
    private final Texture texture;
    private SpriteBatch batch;

    public Image(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void draw(float x, float y, float w, float h) {
        //TODO Add scaling modes
        batch.begin();
        batch.draw(texture, x, y, w, h);
        batch.end();
    }

    @Override
    public void setup() {
        batch = new SpriteBatch();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
