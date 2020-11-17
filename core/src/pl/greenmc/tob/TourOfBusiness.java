package pl.greenmc.tob;

import com.badlogic.gdx.ApplicationAdapter;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.overlays.FPSCounter;
import pl.greenmc.tob.graphics.scenes.IntroScene;

public class TourOfBusiness extends ApplicationAdapter {
    private Scene currentScene = null;
    private FPSCounter fpsCounter;

    @Override
    public void create() {
        currentScene = new IntroScene();
        currentScene.setup();
        fpsCounter = new FPSCounter(16, 60);
    }

    @Override
    public void render() {
        if (currentScene != null) currentScene.render();
        fpsCounter.draw();
    }

    @Override
    public void dispose() {
        if (currentScene != null) currentScene.dispose();
    }
}
