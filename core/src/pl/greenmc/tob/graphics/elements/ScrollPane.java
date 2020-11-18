package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Hitbox;
import pl.greenmc.tob.graphics.Interactable;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ScrollPane extends Element implements Interactable {
    protected final ArrayList<Element> children = new ArrayList<>();
    protected final HashMap<Hitbox, Element> hitboxes = new HashMap<>();
    protected final HashMap<Element, Boolean> insideHitbox = new HashMap<>();
    protected boolean autoHideScroll = true;
    protected Color backgroundColor = GlobalTheme.backgroundColor;
    protected Color barBackgroundColor = GlobalTheme.barBackgroundColor;
    protected Color barHandleColor = GlobalTheme.barHandleColor;
    protected float barWidth = 16;
    protected float handleHeight = 0;
    protected Rectangle hitboxesFor = null;
    protected float hitboxesForScroll = 0;
    protected float maxScroll = 0;
    protected boolean overScroll = false;
    protected ShapeRenderer renderer;
    protected float scroll = 0;
    protected boolean scrollDisabled = false;
    protected boolean scrollGrabbed = false;
    protected Hitbox scrollHitbox = null;
    protected float scrollStart = 0;
    protected boolean setUp = false;
    private Integer startX = null;
    private Integer startY = null;

    @Override
    public void onMouseDown() {
        if (overScroll) {
            scrollGrabbed = true;
            scrollStart = scroll;
        }
        children.forEach(element -> {
            if (element instanceof Interactable) ((Interactable) element).onMouseDown();
        });
    }


    @Override
    public void onMouseLeave() {
        insideHitbox.clear();
        children.forEach(element -> {
            if (element instanceof Interactable) ((Interactable) element).onMouseLeave();
        });
    }

    @Override
    public void onMouseMove(int x, int y) {
        if (scrollHitbox != null) {
            overScroll = scrollHitbox.testMouseCoordinates(x, y);
            if (scrollGrabbed) {
                if (startX == null || startY == null) {
                    startX = x;
                    startY = y;
                }
                onScrollMove(x - startX, y - startY);
            }
        }

        final HashMap<Element, Boolean> current = new HashMap<>();
        hitboxes.keySet().forEach(hitbox -> {
            if (hitbox.testMouseCoordinates(x, y)) {
                Element element = hitboxes.get(hitbox);
                if (element instanceof Interactable) {
                    if (!insideHitbox.getOrDefault(element, false)) {
                        insideHitbox.put(element, true);
                        ((Interactable) element).onMouseEnter();
                    }
                    current.put(element, true);
                    ((Interactable) element).onMouseMove(x, y);
                }
            }
        });
        insideHitbox.forEach((element, inside) -> {
            if (inside && !current.containsKey(element)) {
                if (element instanceof Interactable) {
                    ((Interactable) element).onMouseLeave();
                    insideHitbox.put(element, false);
                }
            }
        });
    }

    protected abstract void onScrollMove(int deltaX, int deltaY);

    @Override
    public void onMouseUp() {
        if (scrollGrabbed) {
            scrollGrabbed = false;
            startY = null;
            startX = null;
        }
        children.forEach(element -> {
            if (element instanceof Interactable) ((Interactable) element).onMouseUp();
        });
    }

    @Override
    public void onScroll(float x, float y) {
        insideHitbox.forEach((element, inside) -> {
            if (inside) {
                if (element instanceof Interactable) {
                    ((Interactable) element).onScroll(x, y);
                }
            }
        });
    }

    public void setScroll(float scroll) {
        this.scroll = scroll;
    }

    public void setBarWidth(float barWidth) {
        this.barWidth = barWidth;
    }

    public void setBarBackgroundColor(Color barBackgroundColor) {
        this.barBackgroundColor = barBackgroundColor;
    }

    public void setBarHandleColor(Color barHandleColor) {
        this.barHandleColor = barHandleColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void dispose() {
        children.forEach(Element::dispose);
        if (renderer != null) renderer.dispose();
    }

    @Override
    public void setup() {
        if (setUp) return;
        renderer = new ShapeRenderer();
        setUp = true;
    }

    public abstract ScrollPane addChild(@NotNull Element element, float size);

    protected abstract void updateHitboxes(float x, float y, float w, float h);
}
