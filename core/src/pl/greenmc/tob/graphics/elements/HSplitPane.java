package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.RectangularHitbox;

public class HSplitPane extends SplitPane {
    public HSplitPane addChild(@NotNull Element element, @NotNull ElementOptions elementOptions) {
        children.add(element);
        options.put(element, elementOptions);
        element.setup();
        return this;
    }

    @Override
    public void draw(float x, float y, float w, float h) {
        if (hitboxesFor == null ||
                hitboxesFor.height != h ||
                hitboxesFor.width != w ||
                hitboxesFor.x != x ||
                hitboxesFor.y != y
                || children.size() != hitboxes.size())
            updateHitboxes(x, y, w, h);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(backgroundColor);
        renderer.rect(x, y, w, h);
        renderer.end();

        final double[] totalFixedHeight = {0};
        final double[] totalVariableWeight = {0};
        children.forEach(element -> {
            ElementOptions elementOptions = (ElementOptions) options.get(element);
            if (elementOptions.mode == ElementOptions.HeightMode.FIXED)
                totalFixedHeight[0] += elementOptions.height;
            else if (elementOptions.mode == ElementOptions.HeightMode.VARIABLE)
                totalVariableWeight[0] += elementOptions.height;
        });
        if (totalVariableWeight[0] == 0) totalVariableWeight[0] = 1;
        double pxPerWeight = (h - totalFixedHeight[0]) / totalVariableWeight[0];
        final double[] currentY = {y};
        children.forEach((element) -> {
            ElementOptions elementOptions = (ElementOptions) options.get(element);
            float height = (float) (elementOptions.mode == ElementOptions.HeightMode.FIXED ?
                    elementOptions.height :
                    elementOptions.height * pxPerWeight);
            element.draw(x, (float) currentY[0], w, height);
            currentY[0] += height;
        });
    }

    @Override
    protected void updateHitboxes(float x, float y, float w, float h) {
        hitboxes.clear();
        final double[] totalFixedHeight = {0};
        final double[] totalVariableWeight = {0};
        children.forEach(element -> {
            ElementOptions elementOptions = (ElementOptions) options.get(element);
            if (elementOptions.mode == ElementOptions.HeightMode.FIXED)
                totalFixedHeight[0] += elementOptions.height;
            else if (elementOptions.mode == ElementOptions.HeightMode.VARIABLE)
                totalVariableWeight[0] += elementOptions.height;
        });
        if (totalVariableWeight[0] == 0) totalVariableWeight[0] = 1;
        double pxPerWeight = (h - totalFixedHeight[0]) / totalVariableWeight[0];
        final double[] currentY = {y};
        children.forEach((element) -> {
            ElementOptions elementOptions = (ElementOptions) options.get(element);
            float height = (float) (elementOptions.mode == ElementOptions.HeightMode.FIXED ?
                    elementOptions.height :
                    elementOptions.height * pxPerWeight);
            hitboxes.put(new RectangularHitbox(x, (float) currentY[0], w, height), element);
            currentY[0] += height;
        });
        hitboxesFor = new Rectangle(x, y, w, h);
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
