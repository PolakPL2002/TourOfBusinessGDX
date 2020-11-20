package pl.greenmc.tob.graphics;

import com.badlogic.gdx.utils.Disposable;

public abstract class Overlay implements Disposable {
    public abstract void setup();

    public abstract void draw();

    public abstract void resize(int width, int height);
}
