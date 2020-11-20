package pl.greenmc.tob.graphics.backgrounds;

import com.badlogic.gdx.graphics.Texture;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.Background;
import pl.greenmc.tob.graphics.elements.Image;

public class ImageBackground extends Background {
    private final Image.Align align;
    private final Texture texture;
    private Background child;
    private Image image;

    public ImageBackground(Texture texture, Image.Align align) {
        this.texture = texture;
        this.align = align;
    }

    @Override
    public void draw(float x, float y, float w, float h) {
        if (child != null) child.draw(x, y, w, h);
        image.draw(x, y, w, h);
    }

    @Override
    public void setup() {
        image = new Image(texture, align);
        image.setup();
    }

    @Override
    public void resize(int width, int height) {
        if (child != null) child.resize(width, height);
        image.resize(width, height);
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        image.dispose();
        if (child != null) child.dispose();
    }

    public ImageBackground setChild(@NotNull Background background) {
        if (child != null) child.dispose();
        child = background;
        child.setup();
        return this;
    }
}
