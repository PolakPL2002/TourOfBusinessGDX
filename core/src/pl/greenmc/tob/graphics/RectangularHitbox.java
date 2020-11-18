package pl.greenmc.tob.graphics;

public class RectangularHitbox extends Hitbox {
    private final float h;
    private final float w;
    private final float x;
    private final float y;

    public RectangularHitbox(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public float getH() {
        return h;
    }

    public float getW() {
        return w;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public boolean testCoordinates(float x, float y) {
        return x >= this.x &&
                x < this.x + this.w &&
                y >= this.y &&
                y < this.y + this.h;
    }
}
