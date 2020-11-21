package pl.greenmc.tob;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.TourOfBusinessGame;
import pl.greenmc.tob.game.util.Logger;
import pl.greenmc.tob.graphics.Interactable;
import pl.greenmc.tob.graphics.Overlay;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.overlays.FPSCounter;
import pl.greenmc.tob.graphics.scenes.LoadingScene;

import java.util.ArrayList;

import static com.badlogic.gdx.graphics.GL20.*;
import static pl.greenmc.tob.game.util.Logger.log;

public class TourOfBusiness extends ApplicationAdapter implements InputProcessor {
    public static TourOfBusiness TOB;
    private final int GL_MULTISAMPLE = 32925;
    private final ArrayList<Overlay> overlays = new ArrayList<>();
    private final ArrayList<Runnable> tasksToExecute = new ArrayList<>();
    private boolean LMBPressed = false;
    private Scene currentScene = null;
    private TourOfBusinessGame game;
    private Vector2 lastMousePosition = null;

    public Scene getScene() {
        return currentScene;
    }

    public void runOnGLThread(@NotNull Runnable runnable) {
        synchronized (tasksToExecute) {
            tasksToExecute.add(runnable);
        }
    }

    @Override
    public void create() {
        TOB = this;
        changeScene(new LoadingScene());
        addOverlay(new FPSCounter(16, 60));
        game = new TourOfBusinessGame(false);
        Gdx.input.setInputProcessor(this);
//        File original = new File("C:\\Users\\Szymon\\IdeaProjects\\TourOfBusinessGDX\\core\\assets\\x.wav");
//        for (int i = 100; i <110; i++) {
//            File copied = new File(
//                    "C:\\Users\\Szymon\\IdeaProjects\\TourOfBusinessGDX\\core\\assets\\" + i + ".wav");
//            try {
//                FileUtils.copyFile(original, copied);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }

    public void addOverlay(@NotNull Overlay overlay) {
        overlays.add(overlay);
        overlay.setup();
    }

    public void changeScene(@NotNull Scene scene) {
        if (currentScene != null) currentScene.dispose();
        currentScene = scene;
        scene.setup();

        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        if (currentScene != null && currentScene instanceof Interactable)
            ((Interactable) currentScene).onMouseMove(x, y);
        lastMousePosition = new Vector2(x, y);
    }

    public TourOfBusinessGame getGame() {
        return game;
    }

    @Override
    public void render() {
        synchronized (tasksToExecute) {
            while (tasksToExecute.size() > 0) {
                tasksToExecute.remove(0).run();
            }
        }
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        if (lastMousePosition == null ||
                lastMousePosition.x != x ||
                lastMousePosition.y != y) {
            if (currentScene != null && currentScene instanceof Interactable)
                ((Interactable) currentScene).onMouseMove(x, y);
            lastMousePosition = new Vector2(x, y);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (currentScene != null && currentScene instanceof Interactable)
                ((Interactable) currentScene).onMouseDown();
            LMBPressed = true;
        } else if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LMBPressed) {
            if (currentScene != null && currentScene instanceof Interactable)
                ((Interactable) currentScene).onMouseUp();
            LMBPressed = false;
        }
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if (currentScene != null) currentScene.render();
        overlays.forEach(Overlay::draw);
    }

    @Override
    public void dispose() {
        if (currentScene != null) currentScene.dispose();
        overlays.forEach(Overlay::dispose);
        Logger.flushLog();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (currentScene != null) currentScene.resize(width, height);
        overlays.forEach(overlay -> overlay.resize(width, height));
        log("Viewport resized to " + width + "x" + height);
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float x, float y) {
        if (currentScene != null && currentScene instanceof Interactable)
            ((Interactable) currentScene).onScroll(x, y);
        return true;
    }
}
