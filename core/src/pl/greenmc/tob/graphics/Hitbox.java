package pl.greenmc.tob.graphics;

import com.badlogic.gdx.Gdx;

public abstract class Hitbox {
    public boolean testMouseCoordinates(int x, int y) {
        return testCoordinates(x, Gdx.graphics.getHeight() - y - 1);
    }

    public abstract boolean testCoordinates(float x, float y);
}
