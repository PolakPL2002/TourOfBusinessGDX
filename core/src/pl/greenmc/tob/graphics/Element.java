package pl.greenmc.tob.graphics;

public abstract class Element {
    public abstract void draw(float x, float y, float w, float h);

    public abstract void dispose();

    public abstract void setup();
}
