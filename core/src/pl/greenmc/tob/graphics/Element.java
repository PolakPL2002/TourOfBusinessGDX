package pl.greenmc.tob.graphics;

import com.badlogic.gdx.utils.Disposable;

public abstract class Element implements Disposable {
    public abstract void draw(float x, float y, float w, float h);

    public abstract void setup();
}
