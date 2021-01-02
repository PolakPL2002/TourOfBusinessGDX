package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import pl.greenmc.tob.game.util.Utilities;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Hitbox;
import pl.greenmc.tob.graphics.Interactable;

import java.util.ArrayList;
import java.util.HashMap;

import static pl.greenmc.tob.game.util.Utilities.disposeObject;

public abstract class SplitPane extends Element implements Interactable {
    protected final ArrayList<Element> children = new ArrayList<>();
    protected final HashMap<Hitbox, Element> hitboxes = new HashMap<>();
    protected final HashMap<Element, Boolean> insideHitbox = new HashMap<>();
    protected final HashMap<Element, ElementOptions> options = new HashMap<>();
    protected Color backgroundColor = GlobalTheme.backgroundColor;
    protected boolean drawBackground = false;
    protected Rectangle hitboxesFor = null;
    protected ShapeRenderer renderer;
    protected boolean setUp = false;

    @Override
    public void onMouseDown() {
        synchronized (children) {
            children.forEach(element -> {
                if (element instanceof Interactable) ((Interactable) element).onMouseDown();
            });
        }
    }

    @Override
    public void onMouseLeave() {
        synchronized (children) {
            insideHitbox.clear();
            children.forEach(element -> {
                if (element instanceof Interactable) ((Interactable) element).onMouseLeave();
            });
        }
    }

    @Override
    public void onMouseMove(int x, int y) {
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

    @Override
    public void onMouseUp() {
        synchronized (children) {
            children.forEach(element -> {
                if (element instanceof Interactable) ((Interactable) element).onMouseUp();
            });
        }
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

    public Element[] getChildren() {
        synchronized (children) {
            return children.toArray(new Element[0]);
        }
    }

    public void setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void dispose() {
        synchronized (children) {
            children.forEach(Utilities::disposeObject);
        }
        disposeObject(renderer);
    }

    @Override
    public void setup() {
        if (setUp) return;
        renderer = new ShapeRenderer();
        setUp = true;
    }

    @Override
    public void resize(int width, int height) {
        disposeObject(renderer);

        renderer = new ShapeRenderer();
        synchronized (children) {
            children.forEach(child -> child.resize(width, height));
        }
    }

    protected abstract void updateHitboxes(float x, float y, float w, float h);

    protected abstract static class ElementOptions {
    }
}
