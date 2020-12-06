package pl.greenmc.tob.graphics.scenes.game;

import com.badlogic.gdx.Gdx;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.*;

public abstract class Dialog extends Overlay implements Interactable {
    private final Element child;
    private final float height;
    private final float width;
    private Hitbox hitbox;
    private boolean insideHitbox = false;

    public Dialog(@NotNull Element child, float width, float height) {
        this.child = child;
        this.width = width;
        this.height = height;
        hitbox = new RectangularHitbox((Gdx.graphics.getWidth() - width) / 2, (Gdx.graphics.getHeight() - height) / 2, width, height);
    }

    @Override
    public void onMouseDown() {
        if (child instanceof Interactable) ((Interactable) child).onMouseDown();
    }

    @Override
    public void onMouseMove(int x, int y) {
        if (child instanceof Interactable) {
            boolean current = false;
            if (hitbox.testMouseCoordinates(x, y)) {
                if (!insideHitbox) {
                    insideHitbox = true;
                    ((Interactable) child).onMouseEnter();
                }
                current = true;
                ((Interactable) child).onMouseMove(x, y);
            }
            if (insideHitbox && !current) {
                ((Interactable) child).onMouseLeave();
                insideHitbox = false;
            }
            ((Interactable) child).onMouseMove(x, y);
        }
    }

    @Override
    public void onMouseUp() {
        if (child instanceof Interactable) ((Interactable) child).onMouseUp();
    }

    @Override
    public void onScroll(float x, float y) {
        if (child instanceof Interactable) ((Interactable) child).onScroll(x, y);
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    @Override
    public void setup() {
        child.setup();
    }

    @Override
    public void draw() {
        child.draw((Gdx.graphics.getWidth() - width) / 2, (Gdx.graphics.getHeight() - height) / 2, width, height);
    }

    @Override
    public void resize(int width, int height) {
        hitbox = new RectangularHitbox((width - this.width) / 2, (height - this.height) / 2, this.width, this.height);
        child.resize(width, height);
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        child.dispose();
    }

    protected Element getChild() {
        return child;
    }
}
