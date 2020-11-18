package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.*;

public class PaddingPane extends Element implements Interactable {
    private final Element child;
    private final float paddingBottom;
    private final float paddingLeft;
    private final float paddingRight;
    private final float paddingTop;
    private SolidColor background;
    private Color color = GlobalTheme.backgroundColor;
    private Hitbox hitbox = null;
    private Rectangle hitboxesFor = null;
    private boolean insideHitbox = false;

    public PaddingPane(@NotNull Element child, float padding) {
        this.child = child;
        paddingBottom = padding;
        paddingLeft = padding;
        paddingRight = padding;
        paddingTop = padding;
    }

    public PaddingPane(@NotNull Element child, float paddingTop, float paddingBottom, float paddingLeft, float paddingRight) {
        this.child = child;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
    }

    @Override
    public void onMouseDown() {
        if (child instanceof Interactable) {
            ((Interactable) child).onMouseDown();
        }
    }

    @Override
    public void onMouseLeave() {
        if (child instanceof Interactable) {
            ((Interactable) child).onMouseLeave();
        }
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
        if (child instanceof Interactable) {
            ((Interactable) child).onMouseUp();
        }
    }

    public void setColor(Color color) {
        this.color = color;
        if (background != null) background.setColor(color);
    }

    @Override
    public void draw(float x, float y, float w, float h) {
        if (hitboxesFor == null ||
                hitboxesFor.height != h ||
                hitboxesFor.width != w ||
                hitboxesFor.x != x ||
                hitboxesFor.y != y)
            hitbox = new RectangularHitbox(x + paddingLeft, y + paddingBottom, w - paddingRight - paddingLeft, h - paddingTop - paddingBottom);

        background.draw(x, y, w, h);
        child.draw(x + paddingLeft, y + paddingBottom, w - paddingRight - paddingLeft, h - paddingTop - paddingBottom);
    }

    @Override
    public void setup() {
        child.setup();
        background = new SolidColor();
        background.setup();
        background.setColor(color);
    }

    @Override
    public void dispose() {
        child.dispose();
        background.dispose();
    }
}
