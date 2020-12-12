package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.RectangularHitbox;

import static com.badlogic.gdx.graphics.GL20.GL_SCISSOR_TEST;

public class VSplitPane extends SplitPane {
    @Override
    public void draw(float x, float y, float w, float h) {
        x = (int) Math.floor(x);
        y = (int) Math.floor(y);
        w = (int) Math.floor(w);
        h = (int) Math.floor(h);
        synchronized (children) {
            if (hitboxesFor == null ||
                    hitboxesFor.height != h ||
                    hitboxesFor.width != w ||
                    hitboxesFor.x != x ||
                    hitboxesFor.y != y
                    || children.size() != hitboxes.size())
                updateHitboxes(x, y, w, h);
        }
        if (drawBackground) {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(backgroundColor);
            renderer.rect(x, y, w, h);
            renderer.end();
        }

        final double[] totalFixedWidth = {0};
        final double[] totalVariableWeight = {0};
        synchronized (children) {
            children.forEach(element -> {
                ElementOptions elementOptions = (ElementOptions) options.get(element);
                if (elementOptions.mode == ElementOptions.WidthMode.FIXED)
                    totalFixedWidth[0] += elementOptions.width;
                else if (elementOptions.mode == ElementOptions.WidthMode.VARIABLE)
                    totalVariableWeight[0] += elementOptions.width;
            });
        }
        if (totalVariableWeight[0] == 0) totalVariableWeight[0] = 1;
        double pxPerWeight = (w - totalFixedWidth[0]) / totalVariableWeight[0];
        final double[] currentX = {x};
        float finalX = x;
        float finalY = y;
        float finalW = w;
        float finalH = h;
        synchronized (children) {
            children.forEach((element) -> {
                Gdx.gl.glEnable(GL_SCISSOR_TEST);
                Gdx.gl.glScissor((int) finalX, (int) finalY, (int) finalW, (int) finalH);
                ElementOptions elementOptions = (ElementOptions) options.get(element);
                float width = (float) (elementOptions.mode == ElementOptions.WidthMode.FIXED ?
                        elementOptions.width :
                        elementOptions.width * pxPerWeight);
                element.draw((float) currentX[0], finalY, width, finalH);
                currentX[0] += width;
            });
        }
        Gdx.gl.glDisable(GL_SCISSOR_TEST);
    }

    @Override
    protected void updateHitboxes(float x, float y, float w, float h) {
        hitboxes.clear();
        final double[] totalFixedWidth = {0};
        final double[] totalVariableWeight = {0};
        synchronized (children) {
            children.forEach(element -> {
                ElementOptions elementOptions = (ElementOptions) options.get(element);
                if (elementOptions.mode == ElementOptions.WidthMode.FIXED)
                    totalFixedWidth[0] += elementOptions.width;
                else if (elementOptions.mode == ElementOptions.WidthMode.VARIABLE)
                    totalVariableWeight[0] += elementOptions.width;
            });
        }
        if (totalVariableWeight[0] == 0) totalVariableWeight[0] = 1;
        double pxPerWeight = (w - totalFixedWidth[0]) / totalVariableWeight[0];
        final double[] currentX = {x};
        synchronized (children) {
            children.forEach((element) -> {
                ElementOptions elementOptions = (ElementOptions) options.get(element);
                float width = (float) (elementOptions.mode == ElementOptions.WidthMode.FIXED ?
                        elementOptions.width :
                        elementOptions.width * pxPerWeight);
                hitboxes.put(new RectangularHitbox((float) currentX[0], y, width, h), element);
                currentX[0] += width;
            });
        }
        hitboxesFor = new Rectangle(x, y, w, h);
        onMouseMove(Gdx.input.getX(), Gdx.input.getY());
    }

    public VSplitPane addChild(@NotNull Element element, @NotNull ElementOptions elementOptions) {
        synchronized (children) {
            children.add(element);
            options.put(element, elementOptions);
            element.setup();
        }
        return this;
    }

    public static class ElementOptions extends SplitPane.ElementOptions {
        private final WidthMode mode;
        private final double width;

        public ElementOptions(double width, WidthMode mode) {
            this.width = width;
            this.mode = mode;
        }

        public double getWidth() {
            return width;
        }

        public WidthMode getMode() {
            return mode;
        }

        public enum WidthMode {
            FIXED,
            VARIABLE
        }
    }
}
