package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pl.greenmc.tob.graphics.Element;

public class Image extends Element {
    private final Align alignMode;
    private final Texture texture;
    private SpriteBatch batch;

    public Image(Texture texture, Align alignMode) {
        this.texture = texture;
        this.alignMode = alignMode;
    }

    @Override
    public void draw(float x, float y, float w, float h) {
        //TODO Add scaling modes
        batch.begin();
        switch (alignMode) {
            case STRETCH:
                batch.draw(texture, x, y, w, h);
                break;
            case FIT_ASPECT:
                if (h * texture.getWidth() / texture.getHeight() > w) {
                    //Width limited
                    float height = w * texture.getHeight() / texture.getWidth();
                    batch.draw(texture, x, y + (h - height) / 2, w, height);
                } else {
                    //Height limited
                    float width = h * texture.getWidth() / texture.getHeight();
                    batch.draw(texture, x + (w - width) / 2, y, width, h);
                }
                break;
            case CROP_ASPECT:
                if (h * texture.getWidth() / texture.getHeight() < w) {
                    //Width limited
                    float height = w * texture.getHeight() / texture.getWidth();
                    batch.draw(texture, x, y + (h - height) / 2, w, height);
                } else {
                    //Height limited
                    float width = h * texture.getWidth() / texture.getHeight();
                    batch.draw(texture, x + (w - width) / 2, y, width, h);
                }
                break;
        }
        batch.end();
    }

    @Override
    public void setup() {
        batch = new SpriteBatch();
    }

    @Override
    public void resize(int width, int height) {
        batch.dispose();
        batch = new SpriteBatch();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public enum Align {
        STRETCH,
        FIT_ASPECT,
        CROP_ASPECT
    }
}
