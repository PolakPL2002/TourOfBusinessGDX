package pl.greenmc.tob.graphics.scenes.menus;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.lobby.CreateLobbyPacket;
import pl.greenmc.tob.game.netty.packets.game.lobby.CreateLobbyResponse;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.Menu;

import java.util.UUID;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.*;

public class GameMenu extends Menu {
    @Override
    public void setup() {
        super.setup();
        Button button1 = new Button("Stwórz nową grę");
        Button button2 = new Button("Dołącz do istniejącej gry");
        Button button3 = new Button("Wróć");
        button1.setFontSize(20);
        button2.setFontSize(20);
        button3.setFontSize(20);

        button1.setClickCallback(this::onNew);
        button2.setClickCallback(this::onJoin);
        button3.setClickCallback(this::onBack);

        HSplitPane menu = new HSplitPane()
                .addChild(
                        button3,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED)
                )
                .addChild(
                        new TransparentColor(),
                        new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                )
                .addChild(
                        button2,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED)
                )
                .addChild(
                        new TransparentColor(),
                        new HSplitPane.ElementOptions(10, HSplitPane.ElementOptions.HeightMode.FIXED)
                )
                .addChild(
                        button1,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED)
                );
        menu.setBackgroundColor(new Color(0, 0, 0, 0));
        PaddingPane menuPadding = new PaddingPane(
                menu,
                10
        );
        menuPadding.setColor(GlobalTheme.menuBackgroundColor);
        setElement(
                new VSplitPane()
                        .addChild(
                                new TransparentColor(),
                                new VSplitPane.ElementOptions(2, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
                        .addChild(
                                new HSplitPane()
                                        .addChild(
                                                new TransparentColor(),
                                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        )
                                        .addChild(
                                                new PaddingPane(
                                                        menuPadding,
                                                        3
                                                ),
                                                new HSplitPane.ElementOptions(300, HSplitPane.ElementOptions.HeightMode.FIXED)
                                        )
                                        .addChild(
                                                new TransparentColor(),
                                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        ),
                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
                        .addChild(
                                new TransparentColor(),
                                new VSplitPane.ElementOptions(2, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
        );
    }

    private void onBack() {
        TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu()));
    }

    private void onNew() {
        try {
            NettyClient.getInstance().getClientHandler().send(new CreateLobbyPacket(), new SentPacket.Callback() {
                @Override
                public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                    try {
                        if (response == null) throw new InvalidPacketException();
                        final CreateLobbyResponse createLobbyResponse = CreateLobbyPacket.parseResponse(response);
                        if (createLobbyResponse.isSuccess())
                            log("Successfully created lobby! id=" + createLobbyResponse.getLobbyID());
                        else
                            log("Failed to create lobby!");
                    } catch (InvalidPacketException e) {
                        error(e);
                    }
                }

                @Override
                public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                    warning("Failed to create lobby!");
                }
            }, false);
        } catch (ConnectionNotAliveException e) {
            error("Failed to create lobby");
            error(e);
        }
//        TOB.runOnGLThread(() -> TOB.changeScene(new GameScene(new DefaultMap())));
    }

    private void onJoin() {
        TOB.runOnGLThread(() -> TOB.changeScene(new JoinGameMenu()));
    }
}
