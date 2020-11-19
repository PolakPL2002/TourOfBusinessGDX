package pl.greenmc.tob.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import pl.greenmc.tob.TourOfBusiness;
import pl.greenmc.tob.game.TourOfBusinessGame;

import java.awt.*;

public class DesktopLauncher {
    public static void main(String[] arg) {
        boolean server = false;
        for (String s : arg) {
            if (s.equalsIgnoreCase("--server")) {
                server = true;
                break;
            }
        }
        if (server) {
            new TourOfBusinessGame(true);
        } else {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.fullscreen = true;
            Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            config.height = dimension.height;
            config.width = dimension.width;
            config.pauseWhenMinimized = false;
            config.samples = 16;
            new LwjglApplication(new TourOfBusiness(), config);
        }
    }
}
