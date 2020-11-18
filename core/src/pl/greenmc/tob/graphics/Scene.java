package pl.greenmc.tob.graphics;

import com.badlogic.gdx.utils.Disposable;

public abstract class Scene implements Disposable {
    public abstract void render();

    public abstract void setup();
}
