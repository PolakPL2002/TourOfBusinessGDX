package pl.greenmc.tob.graphics.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.Element;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Interactable;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.elements.VSplitPane;

public class Menu extends Scene implements Interactable {
    private Color backgroundColor = GlobalTheme.backgroundColor;
    private Element element = null;

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

    @Override
    public void render() {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        element.draw(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void setup() {
        setElement(new VSplitPane());
    }

    public void setElement(@NotNull Element element) {
        if (this.element != null) this.element.dispose();
        this.element = element;
        this.element.setup();
    }

    @Override
    public void dispose() {
        if (this.element != null) element.dispose();
    }
}
