package pl.greenmc.tob.graphics;

import com.badlogic.gdx.math.Vector2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PolygonHitbox extends Hitbox {
    private final ArrayList<Vector2> points = new ArrayList<>();

    public PolygonHitbox(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.addPoint(x1, y1).addPoint(x2, y2).addPoint(x3, y3);
    }

    public PolygonHitbox addPoint(float x, float y) {
        points.add(new Vector2(x, y));
        return this;
    }

    public PolygonHitbox(@NotNull Vector2 v1, @NotNull Vector2 v2, @NotNull Vector2 v3, @NotNull Vector2 v4) {
        this.addPoint(v1.x, v1.y).addPoint(v2.x, v2.y).addPoint(v3.x, v3.y).addPoint(v4.x, v4.y);
    }

    @Override
    public boolean testCoordinates(float x, float y) {
        boolean ok = true;
        for (int i = 0; i < points.size(); i++) {
            final Vector2 point = points.get(i);
            final Vector2 nextPoint;
            if (i == points.size() - 1)
                nextPoint = points.get(0);
            else
                nextPoint = points.get(i + 1);
            final Vector2 dirPoint;
            if (i == points.size() - 2)
                dirPoint = points.get(0);
            else if (i == points.size() - 1)
                dirPoint = points.get(1);
            else
                dirPoint = points.get(i + 2);
            ok = testLine(point.x, point.y, nextPoint.x, nextPoint.y, dirPoint.x, dirPoint.y, x, y);
            if (!ok) break;
        }
        return ok;
    }

    private boolean testLine(float x1, float y1, float x2, float y2, float dirX, float dirY, float testX, float testY) {
        //f(x) = ax+b
        float a = (y2 - y1) / (x2 - x1);
        float b = y1 - a * x1;
        boolean above = a * dirX + b < dirY;
        if (above)
            return a * testX + b < testY;
        else
            return a * testX + b > testY;
    }
}
