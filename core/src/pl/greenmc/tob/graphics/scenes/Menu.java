package pl.greenmc.tob.graphics.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.Background;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.Interactable;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.backgrounds.ImageBackground;
import pl.greenmc.tob.graphics.backgrounds.SolidBackground;
import pl.greenmc.tob.graphics.elements.Image;
import pl.greenmc.tob.graphics.elements.VSplitPane;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;
import static pl.greenmc.tob.TourOfBusiness.TOB;

public class Menu extends Scene implements Interactable {
    private Background background;
    private SpriteBatch batch;
    private Element element = null;
    private FrameBuffer frameBuffer;

    @Override
    public void onMouseDown() {
        if (element != null && element instanceof Interactable) {
            ((Interactable) element).onMouseDown();
        }
    }

    @Override
    public void onMouseMove(int x, int y) {
        if (element != null && element instanceof Interactable) {
            ((Interactable) element).onMouseMove(x, y);
        }
    }

    @Override
    public void onMouseUp() {
        if (element != null && element instanceof Interactable) {
            ((Interactable) element).onMouseUp();
        }
    }

    @Override
    public void onScroll(float x, float y) {
        if (element != null && element instanceof Interactable) {
            ((Interactable) element).onScroll(x, y);
        }
    }

    public void setBackground(Background background) {
        if (this.background != null) this.background.dispose();
        this.background = background;
        background.setup();
    }

    @Override
    public void render() {
        frameBuffer.begin();
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        background.draw(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        element.draw(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        frameBuffer.end();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        batch.begin();
        batch.draw(frameBuffer.getColorBufferTexture(), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void setup() {
        background = new ImageBackground(TOB.getGame().getAssetManager().get("textures/ui/menu/background.png"), Image.Align.CROP_ASPECT).setChild(new SolidBackground());
        background.setup();
        batch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        setElement(new VSplitPane());
    }

    public void setElement(@NotNull Element element) {
        if (this.element != null) this.element.dispose();
        this.element = element;
        this.element.setup();
    }

    @Override
    public void resize(int width, int height) {
        batch.dispose();
        frameBuffer.dispose();

        batch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        if (this.element != null) element.resize(width, height);
        background.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        frameBuffer.dispose();
        if (this.element != null) element.dispose();
    }
}
