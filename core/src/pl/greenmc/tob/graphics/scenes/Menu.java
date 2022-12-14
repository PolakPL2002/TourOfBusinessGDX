package pl.greenmc.tob.graphics.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
import static pl.greenmc.tob.game.util.Utilities.disposeObject;

public class Menu extends Scene implements Interactable {
    protected boolean disposed = false;
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

    public void setBackground(@NotNull Background background) {
        disposeObject(this.background);
        this.background = background;
        background.setup();
    }

    @Override
    public void render() {
        frameBuffer.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        background.draw(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        element.draw(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        frameBuffer.end();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        batch.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.draw(frameBuffer.getColorBufferTexture(), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void setup() {
        final Texture texture = TOB.getGame().getAssetManager().get("textures/ui/menu/background.png");
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        background = new ImageBackground(texture, Image.Align.CROP_ASPECT).setChild(new SolidBackground());
        background.setup();
        batch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Math.max(Gdx.graphics.getWidth(), 1), Math.max(Gdx.graphics.getHeight(), 1), true);
        setElement(new VSplitPane());
    }

    public void setElement(@NotNull Element element) {
        disposeObject(this.element);
        this.element = element;
        this.element.setup();
    }

    @Override
    public void resize(int width, int height) {
        disposeObject(batch);
        disposeObject(frameBuffer);

        batch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Math.max(Gdx.graphics.getWidth(), 1), Math.max(Gdx.graphics.getHeight(), 1), true);

        if (this.element != null) element.resize(width, height);
        background.resize(width, height);
    }

    @Override
    public void dispose() {
        disposed = true;
        disposeObject(batch);
        disposeObject(frameBuffer);
        disposeObject(element);
    }
}
