package pl.greenmc.tob.graphics.scenes.menus;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.Lobby;
import pl.greenmc.tob.game.Player;
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
import static pl.greenmc.tob.graphics.GlobalTheme.playerColors;

public class LobbyMenu extends Menu {
    private final boolean create;
    private final int lobbyID;
    private final Object reloadLock = new Object();
    private PaddingPane container;
    private Lobby lobby = null;
    private Player[] players;
    private boolean reloadInProgress = false;
    private boolean reloadScheduled = false;
    private boolean removed;

    public LobbyMenu() {
        this.create = true;
        lobbyID = 0;
    }

    public LobbyMenu(int lobbyID) {
        this.lobbyID = lobbyID;
        this.create = false;
    }

    public void onLobbyRemoved(int lobbyID) {
        if (lobby != null && lobbyID == lobby.getID()) {
            removed = true;
            TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Lobby zostało usunięte")));
        }
    }

    public void onPlayerJoined(int playerID) {
        synchronized (reloadLock) {
            if (!reloadInProgress)
                getLobbyDetails();
            else
                reloadScheduled = true;
        }
        //TODO Some notification
    }

    public void onPlayerLeft(int playerID) {
        synchronized (reloadLock) {
            if (!reloadInProgress)
                getLobbyDetails();
            else
                reloadScheduled = true;
        }
        //TODO Some notification
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
        synchronized (reloadLock) {
            reloadInProgress = true;
        }
        try {
            NettyClient.getInstance().getClientHandler().send(new GetSelfLobbyPacket(), new SentPacket.Callback() {
                @Override
                public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                    try {
                        if (response == null) throw new InvalidPacketException();

                        GetLobbyResponse getLobbyResponse = GetLobbyPacket.parseResponse(response);
                        lobby = getLobbyResponse.getLobby();
                        players = getLobbyResponse.getPlayers();
                        if (lobby != null) {
                            log("Successfully joined lobby! id=" + lobby.getID());
                            updateView();
                        } else if (!removed) {
                            log("Not in lobby!");
                            TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Not in lobby!")));
                        }
                        reloadFinished();
                    } catch (InvalidPacketException e) {
                        error(e);
                        TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to get lobby data!\n" + e.getMessage())));
                        reloadFinished();
                    }
                }

                @Override
                public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                    warning("Failed to get lobby data!");
                    TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to get lobby data!\n" + reason.name())));
                    reloadFinished();
                }
            }, false);
        } catch (ConnectionNotAliveException e) {
            error("Failed to get lobby data!");
            error(e);
            TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to get lobby data!\n" + e.getMessage())));
            reloadFinished();
        }
    }

    private void reloadFinished() {
        boolean doReload = false;
        synchronized (reloadLock) {
            reloadInProgress = false;
            if (reloadScheduled) {
                doReload = true;
                reloadScheduled = false;
            }
        }
        if (doReload) getLobbyDetails();
    }

    private void updateView() {
        TOB.runOnGLThread(() -> {
            if (lobby == null) {
                Label label = new Label("Ładowanie...\nProszę czekać", 30, false);
                label.setBackgroundColor(new Color(1, 1, 1, 0.75f));
                container.setChild(label);
            } else {
                Button backButton = new Button("Opuść");
                Button readyButton = new Button("Gotowy");

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

                if (lobby.getOwner() == TOB.getGame().getSelf().getID()) {
                    backButton.setBackgroundColor(GlobalTheme.buttonNoBackgroundColor);
                    backButton.setClickColor(GlobalTheme.buttonNoClickColor);
                    backButton.setHoverColor(GlobalTheme.buttonNoHoverColor);
                    backButton.setBorderColor(GlobalTheme.buttonNoBorderColor);
                    backButton.setText("Opuść i usuń");
                }


                Label[] players = new Label[8]; //TODO Get max players from somewhere
                for (int i = 0; i < 8; i++) {
                    players[i] = new Label("<Wolne miejsce>", 18, false);
                    players[i].setBackgroundColor(playerColors[i]);
                }

                Player player;
                player = getPlayerByID(lobby.getOwner());
                if (player != null)
                    players[0].setText(player.getName());
                else
                    players[0].setText("<Unknown player>");
                int i = 1;
                for (int playerID : lobby.getPlayers()) {
                    player = getPlayerByID(playerID);
                    if (player != null)
                        players[i].setText(player.getName());
                    else
                        players[i].setText("<Unknown player>");
                    i++;
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
                                new HSplitPane.ElementOptions(20, HSplitPane.ElementOptions.HeightMode.FIXED)
                        )
                        .addChild(
                                new VSplitPane()
                                        .addChild(
                                                players[4],
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        )
                                        .addChild(
                                                players[5],
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        )
                                        .addChild(
                                                players[6],
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        )
                                        .addChild(
                                                players[7],
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        ),
                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                        .addChild(
                                new VSplitPane()
                                        .addChild(
                                                players[0],
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        )
                                        .addChild(
                                                players[1],
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        )
                                        .addChild(
                                                players[2],
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        )
                                        .addChild(
                                                players[3],
                                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                                        ),
                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                        )
                );
            }
        });
    }

    @Nullable
    private Player getPlayerByID(int playerID) {
        for (Player player : players)
            if (player.getID() == playerID)
                return player;
        return null;
    }
}

