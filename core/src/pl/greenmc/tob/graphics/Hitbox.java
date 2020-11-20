package pl.greenmc.tob.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public abstract class Hitbox {
    public boolean testMouseCoordinates(int x, int y) {
        return testCoordinates(x, Gdx.graphics.getHeight() - y - 1);
    }

    public abstract boolean testCoordinates(float x, float y);

    /**
     * This function leave a framebuffer behind. DO NOT USE IN PRODUCTION
     *
     * @param x x
     * @param y y
     * @param w width
     * @param h height
     * @return texture
     */
    public Texture getHitboxOverlay(int x, int y, int w, int h) {
//        int okPoints = 0;
        final FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        final ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 1);
        for (int x1 = x; x1 < x + w; x1++) {
            for (int y1 = y; y1 < y + h; y1++) {
                if (testCoordinates(x1, y1)) {
//                    okPoints++;
                    shapeRenderer.circle(x1, y1, 1);
                }
            }
        }
        shapeRenderer.end();
        frameBuffer.end();
        Texture out = frameBuffer.getColorBufferTexture();
        shapeRenderer.dispose();
//        frameBuffer.dispose();
//        log(okPoints + "/" + w * h);
        return out;
    }
}
