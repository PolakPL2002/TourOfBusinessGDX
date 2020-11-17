package pl.greenmc.tob;

import com.badlogic.gdx.ApplicationAdapter;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.Overlay;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.overlays.FPSCounter;
import pl.greenmc.tob.graphics.scenes.IntroScene;

import java.util.ArrayList;

public class TourOfBusiness extends ApplicationAdapter {
    private final ArrayList<Overlay> overlays = new ArrayList<>();
    private Scene currentScene = null;

    @Override
    public void create() {
        changeScene(new IntroScene());
        addOverlay(new FPSCounter(16, 60));
    }

    public void addOverlay(@NotNull Overlay overlay) {
        overlays.add(overlay);
    }

    public void changeScene(@NotNull Scene scene) {
        if (currentScene != null) currentScene.dispose();
        currentScene = scene;
        scene.setup();
    }

    @Override
    public void render() {
        if (currentScene != null) currentScene.render();
        overlays.forEach(Overlay::draw);
    }

    @Override
    public void dispose() {
        if (currentScene != null) currentScene.dispose();
    }
}
