package pl.greenmc.tob.graphics.scenes.menus;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.Lobby;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.lobby.*;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.Menu;

import java.util.UUID;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.*;

public class LobbyMenu extends Menu {
    private final boolean create;
    private final int lobbyID;
    private Button backButton;
    private PaddingPane container;
    private Lobby lobby = null;
    private Button readyButton;

    public LobbyMenu() {
        this.create = true;
        lobbyID = 0;
    }

    public LobbyMenu(int lobbyID) {
        this.lobbyID = lobbyID;
        this.create = false;
    }

    @Override
    public void setup() {
        super.setup();
        if (create) {
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
                            getLobbyDetails();
                        } catch (InvalidPacketException e) {
                            error(e);
                            TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to create lobby!\n" + e.getMessage())));
                        }
                    }

                    @Override
                    public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                        warning("Failed to create lobby!");
                        TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to create lobby!\n" + reason.name())));
                    }
                }, false);
            } catch (ConnectionNotAliveException e) {
                error("Failed to create lobby!");
                error(e);
                TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to create lobby!\n" + e.getMessage())));
            }
        } else {
            try {
                NettyClient.getInstance().getClientHandler().send(new JoinLobbyPacket(lobbyID), new SentPacket.Callback() {
                    @Override
                    public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                        try {
                            if (response == null) throw new InvalidPacketException();
                            if (JoinLobbyPacket.parseResponse(response))
                                log("Successfully joined lobby! id=" + lobbyID);
                            else
                                log("Failed to join lobby!");
                            getLobbyDetails();
                        } catch (InvalidPacketException e) {
                            error(e);
                            TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to join lobby!\n" + e.getMessage())));
                        }
                    }

                    @Override
                    public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                        warning("Failed to join lobby!");
                        TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to join lobby!\n" + reason.name())));
                    }
                }, false);
            } catch (ConnectionNotAliveException e) {
                error("Failed to join lobby!");
                error(e);
                TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to join lobby!\n" + e.getMessage())));
            }
        }

        backButton = new Button("Opuść");
        readyButton = new Button("Gotowy");

        backButton.setFontSize(20);
        readyButton.setFontSize(20);

        backButton.setClickCallback(() -> {
            TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu()));
            try {
                NettyClient.getInstance().getClientHandler().send(new LeaveLobbyPacket(), null, false);
            } catch (ConnectionNotAliveException e) {
                e.printStackTrace();
            }
        });

        Label label = new Label("Ładowanie...\nProszę czekać", 30, false);
        label.setBackgroundColor(new Color(1, 1, 1, 0.75f));
        container = new PaddingPane(label, 0);
        setElement(
                new HSplitPane()
                        .addChild(
                                new TransparentColor(),
                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                        )
                        .addChild(
                                new VSplitPane()
                                        .addChild(
                                                new TransparentColor(),
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        )
                                        .addChild(
                                                container,
                                                new VSplitPane.ElementOptions(3, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        )
                                        .addChild(
                                                new TransparentColor(),
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        ),
                                new HSplitPane.ElementOptions(3, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                        )
                        .addChild(
                                new TransparentColor(),
                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                        )
        );
    }

    private void getLobbyDetails() {
        try {
            NettyClient.getInstance().getClientHandler().send(new GetSelfLobbyPacket(), new SentPacket.Callback() {
                @Override
                public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                    try {
                        if (response == null) throw new InvalidPacketException();
                        lobby = GetSelfLobbyPacket.parseResponse(response);
                        if (lobby != null) {
                            log("Successfully joined lobby! id=" + lobby.getID());
                            updateView();
                        } else {
                            log("Not in lobby!");
                        }
                    } catch (InvalidPacketException e) {
                        error(e);
                        TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to get lobby data!\n" + e.getMessage())));
                    }
                }

                @Override
                public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                    warning("Failed to get lobby data!");
                    TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to get lobby data!\n" + reason.name())));
                }
            }, false);
        } catch (ConnectionNotAliveException e) {
            error("Failed to get lobby data!");
            error(e);
            TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to get lobby data!\n" + e.getMessage())));
        }
    }

    private void updateView() {
        TOB.runOnGLThread(() -> {
            if (lobby == null) {
                Label label = new Label("Ładowanie...\nProszę czekać", 30, false);
                label.setBackgroundColor(new Color(1, 1, 1, 0.75f));
                container.setChild(label);
            } else {
                if (lobby.getOwner() == TOB.getGame().getSelf().getID()) {
                    backButton.setBackgroundColor(GlobalTheme.buttonNoBackgroundColor);
                    backButton.setClickColor(GlobalTheme.buttonNoClickColor);
                    backButton.setHoverColor(GlobalTheme.buttonNoHoverColor);
                    backButton.setBorderColor(GlobalTheme.buttonNoBorderColor);
                    backButton.setText("Opuść i usuń");
                }
                container.setChild(new HSplitPane()
                        .addChild(
                                new VSplitPane()
                                        .addChild(
                                                new TransparentColor(),
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        )
                                        .addChild(
                                                backButton,
                                                new VSplitPane.ElementOptions(300, VSplitPane.ElementOptions.WidthMode.FIXED)
                                        )
                                        .addChild(
                                                new TransparentColor(),
                                                new VSplitPane.ElementOptions(20, VSplitPane.ElementOptions.WidthMode.FIXED)
                                        )
                                        .addChild(
                                                readyButton,
                                                new VSplitPane.ElementOptions(300, VSplitPane.ElementOptions.WidthMode.FIXED)
                                        )
                                        .addChild(
                                                new TransparentColor(),
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        ),
                                new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED)
                        )
                        .addChild(
                                new TransparentColor(),
                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                        )
                );
            }
        });
    }
}

