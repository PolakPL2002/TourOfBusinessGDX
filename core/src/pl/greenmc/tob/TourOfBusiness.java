package pl.greenmc.tob;

import com.badlogic.gdx.ApplicationAdapter;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.TourOfBusinessGame;
import pl.greenmc.tob.graphics.Overlay;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.overlays.FPSCounter;
import pl.greenmc.tob.graphics.scenes.LoadingScene;

import java.util.ArrayList;

public class TourOfBusiness extends ApplicationAdapter {
    public static TourOfBusiness TOB;
    private final ArrayList<Overlay> overlays = new ArrayList<>();
    private final ArrayList<Runnable> tasksToExecute = new ArrayList<>();
    private Scene currentScene = null;
    private TourOfBusinessGame game;

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
    }

    public void changeScene(@NotNull Scene scene) {
        if (currentScene != null) currentScene.dispose();
        currentScene = scene;
        scene.setup();
    }

    @Override
    public void render() {
        synchronized (tasksToExecute) {
            while (tasksToExecute.size() > 0) {
                tasksToExecute.remove(0).run();
            }
        }
        if (currentScene != null) currentScene.render();
        overlays.forEach(Overlay::draw);
    }

    @Override
    public void dispose() {
        if (currentScene != null) currentScene.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}
