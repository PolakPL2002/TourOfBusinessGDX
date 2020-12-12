package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.RectangularHitbox;

import static com.badlogic.gdx.graphics.GL20.GL_SCISSOR_TEST;

public class HSplitPane extends SplitPane {
    public HSplitPane addChild(@NotNull Element element, @NotNull ElementOptions elementOptions) {
        synchronized (children) {
            children.add(element);
            options.put(element, elementOptions);
            element.setup();
        }
        return this;
    }

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

        final double[] totalFixedHeight = {0};
        final double[] totalVariableWeight = {0};
        synchronized (children) {
            children.forEach(element -> {
                ElementOptions elementOptions = (ElementOptions) options.get(element);
                if (elementOptions.mode == ElementOptions.HeightMode.FIXED)
                    totalFixedHeight[0] += elementOptions.height;
                else if (elementOptions.mode == ElementOptions.HeightMode.VARIABLE)
                    totalVariableWeight[0] += elementOptions.height;
            });
        }
        if (totalVariableWeight[0] == 0) totalVariableWeight[0] = 1;
        double pxPerWeight = (h - totalFixedHeight[0]) / totalVariableWeight[0];
        final double[] currentY = {y};
        float finalX = x;
        float finalY = y;
        float finalW = w;
        float finalH = h;
        synchronized (children) {
            children.forEach((element) -> {
                Gdx.gl.glEnable(GL_SCISSOR_TEST);
                Gdx.gl.glScissor((int) finalX, (int) finalY, (int) finalW, (int) finalH);
                ElementOptions elementOptions = (ElementOptions) options.get(element);
                float height = (float) (elementOptions.mode == ElementOptions.HeightMode.FIXED ?
                        elementOptions.height :
                        elementOptions.height * pxPerWeight);
                element.draw(finalX, (float) currentY[0], finalW, height);
                currentY[0] += height;
            });
        }
        Gdx.gl.glDisable(GL_SCISSOR_TEST);
    }

    @Override
    protected void updateHitboxes(float x, float y, float w, float h) {
        hitboxes.clear();
        final double[] totalFixedHeight = {0};
        final double[] totalVariableWeight = {0};
        synchronized (children) {
            children.forEach(element -> {
                ElementOptions elementOptions = (ElementOptions) options.get(element);
                if (elementOptions.mode == ElementOptions.HeightMode.FIXED)
                    totalFixedHeight[0] += elementOptions.height;
                else if (elementOptions.mode == ElementOptions.HeightMode.VARIABLE)
                    totalVariableWeight[0] += elementOptions.height;
            });
        }
        if (totalVariableWeight[0] == 0) totalVariableWeight[0] = 1;
        double pxPerWeight = (h - totalFixedHeight[0]) / totalVariableWeight[0];
        final double[] currentY = {y};
        synchronized (children) {
            children.forEach((element) -> {
                ElementOptions elementOptions = (ElementOptions) options.get(element);
                float height = (float) (elementOptions.mode == ElementOptions.HeightMode.FIXED ?
                        elementOptions.height :
                        elementOptions.height * pxPerWeight);
                hitboxes.put(new RectangularHitbox(x, (float) currentY[0], w, height), element);
                currentY[0] += height;
            });
        }
        hitboxesFor = new Rectangle(x, y, w, h);
        onMouseMove(Gdx.input.getX(), Gdx.input.getY());
    }

    public static class ElementOptions extends SplitPane.ElementOptions {
        private final double height;
        private final HeightMode mode;

        public ElementOptions(double height, HeightMode mode) {
            this.height = height;
            this.mode = mode;
        }

        public double getHeight() {
            return height;
        }

        public HeightMode getMode() {
            return mode;
        }

        public enum HeightMode {
            FIXED,
            VARIABLE
        }
    }
}
