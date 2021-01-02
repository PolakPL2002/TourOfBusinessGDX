package pl.greenmc.tob.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import pl.greenmc.tob.TourOfBusiness;
import pl.greenmc.tob.game.TourOfBusinessGame;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.server.NettyServer;

import java.awt.*;

import static pl.greenmc.tob.game.util.Logger.log;
import static pl.greenmc.tob.game.util.Logger.warning;

public class DesktopLauncher {
    public static void main(String[] arg) {
        boolean server = false, nextIP = false, nextPort = false, windowed = false, networkActivity = false, fps = false;
        String IP = null, port = null;
        for (String s : arg) {
            if (nextIP) {
                IP = s;
                nextIP = false;
                continue;
            } else if (nextPort) {
                port = s;
                nextPort = false;
                continue;
            }
            if (s.equalsIgnoreCase("--server")) {
                server = true;
            } else if (s.equalsIgnoreCase("--windowed")) {
                windowed = true;
            } else if (s.equalsIgnoreCase("--ip")) {
                nextIP = true;
            } else if (s.equalsIgnoreCase("--port")) {
                nextPort = true;
            } else if (s.equalsIgnoreCase("--network-activity")) {
                networkActivity = true;
            } else if (s.equalsIgnoreCase("--fps")) {
                fps = true;
            }
        }
        if (IP != null) {
            log("Setting IP to " + IP);
            NettyClient.getInstance().setHost(IP);
        }
        if (port != null) {
            try {
                int portNum = Integer.parseInt(port);
                if (portNum < 1 || portNum > 65535) throw new NumberFormatException();
                log("Setting port to " + IP);
                NettyClient.getInstance().setPort(portNum);
                NettyServer.getInstance().setPort(portNum);
            } catch (NumberFormatException e) {
                warning("Invalid port provided. Falling back to default.");
            }
        }
        if (server) {
            log("Starting server...");
            new TourOfBusinessGame(true);
        } else {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            if (!windowed) {
                config.fullscreen = true;
                Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                config.height = dimension.height;
                config.width = dimension.width;
                log("Setting resolution to " + dimension.width + "x" + dimension.height);
            } else {
                config.height = 720;
                config.width = 1280;
            }
            config.pauseWhenMinimized = false;
            config.samples = 16;
            new LwjglApplication(new TourOfBusiness(fps, networkActivity), config);
        }
    }
}
