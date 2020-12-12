package pl.greenmc.tob.graphics.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.RectangularHitbox;

import java.util.HashMap;

import static com.badlogic.gdx.graphics.GL20.GL_SCISSOR_TEST;

public class VScrollPane extends ScrollPane {
    private final HashMap<Element, Float> heights = new HashMap<>();

    @Override
    public void onScroll(float x, float y) {
//        super.onScroll(x, y);
        if (!scrollDisabled) {
            scroll += y * 50;
            if (scroll > maxScroll) scroll = maxScroll;
            if (scroll < 0) scroll = 0;
            updateHitboxes(hitboxesFor.x, hitboxesFor.y, hitboxesFor.width, hitboxesFor.height);
        }
    }

    @Override
    protected void updateHitboxes(float x, float y, float w, float h) {
        hitboxesFor = new Rectangle(x, y, w, h);
        hitboxesForScroll = scroll;
        hitboxes.clear();

        final double[] totalFixedHeight = {0};
        children.forEach(element -> totalFixedHeight[0] += heights.get(element));

        handleHeight = (float) (h / totalFixedHeight[0] * h);

        if (handleHeight < h / 10) handleHeight = h / 10;

        maxScroll = (float) (totalFixedHeight[0] - h);
        if (maxScroll == 0) maxScroll = -1;

        if (scrollDisabled) scroll = 0;
        float s = maxScroll - scroll;

        float handleY = (y + s / maxScroll * (h - handleHeight));

        scrollHitbox = new RectangularHitbox(x + w - barWidth, handleY, barWidth, handleHeight);

        scrollDisabled = maxScroll < 0;

        if (scroll > maxScroll) scroll = maxScroll;

        final double[] currentY = {y};
        children.forEach((element) -> {
            float height = heights.get(element);
            hitboxes.put(new RectangularHitbox(x, (float) currentY[0] - s, w - ((autoHideScroll && scrollDisabled) ? 0 : barWidth), height), element);
            currentY[0] += height;
        });
    }

    @Override
    public void draw(float x, float y, float w, float h) {
        x = (int) Math.floor(x);
        y = (int) Math.floor(y);
        w = (int) Math.floor(w);
        h = (int) Math.floor(h);
        if (hitboxesFor == null ||
                hitboxesFor.height != h ||
                hitboxesFor.width != w ||
                hitboxesFor.x != x ||
                hitboxesFor.y != y ||
                children.size() != hitboxes.size() ||
                hitboxesForScroll != scroll ||
                scrollHitbox == null)
            updateHitboxes(x, y, w, h);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(backgroundColor);
        renderer.rect(x, y, w, h);

        if (!autoHideScroll || !scrollDisabled) {

            renderer.setColor(barBackgroundColor);
            renderer.rect(x + w - barWidth, y, barWidth, h);
        }
        final double[] totalFixedHeight = {0};
        synchronized (children) {
            children.forEach(element -> totalFixedHeight[0] += heights.get(element));
        }
        handleHeight = (float) (h / totalFixedHeight[0] * h);

        if (handleHeight < h / 10) handleHeight = h / 10;

        float maxScroll = (float) (totalFixedHeight[0] - h);

        if (scrollDisabled) scroll = 0;
        float s = maxScroll - scroll;

        float handleY = (y + s / maxScroll * (h - handleHeight));
        if (!scrollDisabled) {
            renderer.setColor(barHandleColor);
            renderer.rect(x + w - barWidth, handleY, barWidth, handleHeight);
        }
        renderer.end();

        if (scroll > maxScroll) scroll = maxScroll;

        final double[] currentY = {y};
        float finalX = x;
        float finalY = y;
        float finalW = w;
        float finalH = h;
        synchronized (children) {
            children.forEach((element) -> {
                Gdx.gl.glEnable(GL_SCISSOR_TEST);
                Gdx.gl.glScissor((int) finalX, (int) finalY, (int) (finalW - ((autoHideScroll && scrollDisabled) ? 0 : barWidth)), (int) finalH);
                float height = heights.get(element);
                element.draw(finalX, (float) currentY[0] - s, finalW - ((autoHideScroll && scrollDisabled) ? 0 : barWidth), height);
                currentY[0] += height;
            });
        }
        Gdx.gl.glDisable(GL_SCISSOR_TEST);
    }

    @Override
    public VScrollPane addChild(@NotNull Element element, float height) {
        synchronized (children) {
            children.add(element);
            heights.put(element, height);
        }
        element.setup();
        return this;
    }

    @Override
    protected void onScrollMove(int deltaX, int deltaY) {
        if (!scrollDisabled) {
            float unitsPerPx = maxScroll / (hitboxesFor.height - handleHeight);
            scroll = scrollStart + deltaY * unitsPerPx;
            if (Math.abs(deltaX) > 600) scroll = scrollStart;
            if (scroll > maxScroll) scroll = maxScroll;
            if (scroll < 0) scroll = 0;
        }
    }
}
