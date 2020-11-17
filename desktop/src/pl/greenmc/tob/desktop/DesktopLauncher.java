package pl.greenmc.tob.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import pl.greenmc.tob.TourOfBusiness;

import java.awt.*;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.fullscreen = true;
        Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        config.height = dimension.height;
        config.width = dimension.width;
        new LwjglApplication(new TourOfBusiness(), config);
    }
}
