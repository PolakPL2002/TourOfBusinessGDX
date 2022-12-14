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
import pl.greenmc.tob.game.netty.packets.game.lobby.GetLobbiesPacket;
import pl.greenmc.tob.game.netty.packets.game.lobby.GetLobbiesResponse;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.Menu;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.*;

public class JoinGameMenu extends Menu {

    private final Object reloadLock = new Object();
    private Button button1;
    private VScrollPane element;
    private Lobby[] lobbies = new Lobby[0];
    private PaddingPane pane;
    private Player[] players = new Player[0];
    private boolean reloadInProgress = false;
    private boolean reloadScheduled = false;

    public void onGameStarted(int lobbyID) {
        synchronized (reloadLock) {
            if (!reloadInProgress)
                reloadLobbies();
            else
                reloadScheduled = true;
        }
    }

    public void onLobbyCreated(int lobbyID) {
        synchronized (reloadLock) {
            if (!reloadInProgress)
                reloadLobbies();
            else
                reloadScheduled = true;
        }
    }

    public void onLobbyRemoved(int lobbyID) {
        synchronized (reloadLock) {
            if (!reloadInProgress)
                reloadLobbies();
            else
                reloadScheduled = true;
        }
    }

    @Override
    public void setup() {
        super.setup();
        button1 = new Button("Wr????");

        button1.setClickCallback(this::onBack);

        Label label = new Label("??adowanie...\nProsz?? czeka??", (int) (TOB.getFontBase() / 4), false);
        label.setBackgroundColor(new Color(1, 1, 1, 0.75f));
        pane = new PaddingPane(label, 0);
        reloadLobbies();

        HSplitPane menu = new HSplitPane()
                .addChild(
                        button1,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                ).addChild(pane, new HSplitPane.ElementOptions(526, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        setElement(
                new VSplitPane()
                        .addChild(
                                new TransparentColor(),
                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
                        .addChild(
                                new HSplitPane()
                                        .addChild(
                                                new TransparentColor(),
                                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        )
                                        .addChild(
                                                menu,
                                                new HSplitPane.ElementOptions(8, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        )
                                        .addChild(
                                                new TransparentColor(),
                                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        ),
                                new VSplitPane.ElementOptions(2, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
                        .addChild(
                                new TransparentColor(),
                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
        );
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        updateSizes();
    }

    private void updateSizes() {
        if (button1 != null) button1.setFontSize((int) (TOB.getFontBase() / 6));
    }

    private void reloadLobbies() {
        synchronized (reloadLock) {
            reloadInProgress = true;
        }
        try {
            NettyClient.getInstance().getClientHandler().send(new GetLobbiesPacket(), new SentPacket.Callback() {
                @Override
                public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                    try {
                        if (response == null) throw new InvalidPacketException();
                        GetLobbiesResponse getLobbiesResponse = GetLobbiesPacket.parseResponse(response);
                        synchronized (reloadLock) {
                            lobbies = getLobbiesResponse.getLobbies();
                            players = getLobbiesResponse.getPlayers();
                            log("Got " + lobbies.length + " lobbies and " + players.length + " players from server.");
                        }
                        TOB.runOnGLThread(() -> {
                            if (element == null) {
                                element = new VScrollPane();
                                pane.setChild(element);
                            }
                            element.clearChildren();
                            synchronized (reloadLock) {
                                List<Lobby> lobbies = Arrays.asList(JoinGameMenu.this.lobbies);
                                Collections.reverse(lobbies);
                                boolean first = true;
                                for (Lobby lobby : lobbies) {
                                    Player owner = getPlayerByID(lobby.getOwner());
                                    String name;
                                    if (owner != null)
                                        name = "Lobby gracza " + owner.getName();
                                    else name = "Lobby nieznanego gracza";
                                    HSplitPane lobbyElement = new HSplitPane();

                                    VSplitPane topRow = new VSplitPane();
                                    VSplitPane bottomRow = new VSplitPane();

                                    lobbyElement.addChild(bottomRow, new HSplitPane.ElementOptions(60, HSplitPane.ElementOptions.HeightMode.FIXED));
                                    lobbyElement.addChild(topRow, new HSplitPane.ElementOptions(40, HSplitPane.ElementOptions.HeightMode.FIXED));

                                    topRow.addChild(new Label(name, 24, false), new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));

                                    Button button = new Button("Do????cz");

                                    if (lobby.getLobbyState() != Lobby.LobbyState.CONFIGURING || lobby.getPlayers().length > 6)
                                        button.applyDisabledTheme();
                                    else {
                                        button.applyYesTheme();
                                        button.setClickCallback(() -> joinLobby(lobby));
                                    }

                                    bottomRow.addChild(new Label("Stan: " + lobby.getLobbyState().name() + "\nGraczy: " + (lobby.getPlayers().length + 1) + "/8", 20, false, true), new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));
                                    bottomRow.addChild(button, new VSplitPane.ElementOptions(100, VSplitPane.ElementOptions.WidthMode.FIXED));

                                    lobbyElement.setDrawBackground(true);
                                    lobbyElement.setBackgroundColor(first ? GlobalTheme.scheme.color200() : GlobalTheme.scheme.color300());
                                    first = !first;

                                    JoinGameMenu.this.element.addChild(lobbyElement, 100);
                                }
                            }
                        });
                        reloadFinished();
                    } catch (InvalidPacketException e) {
                        error(e);
                        TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to get lobbies!\n" + e.getMessage())));
                        reloadFinished();
                    }
                }

                @Override
                public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                    warning("Failed to get lobbies!");
                    TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to get lobbies!\n" + reason.name())));
                    reloadFinished();
                }
            }, false);
        } catch (ConnectionNotAliveException e) {
            error("Failed to get lobbies!");
            error(e);
            TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu("Failed to get lobbies!\n" + e.getMessage())));
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
        if (doReload) reloadLobbies();
    }

    private void joinLobby(Lobby lobby) {
        TOB.runOnGLThread(() -> TOB.changeScene(new LobbyMenu(lobby.getID())));
    }

    @Nullable
    private Player getPlayerByID(int playerID) {
        synchronized (reloadLock) {
            for (Player player : players)
                if (player.getID() == playerID)
                    return player;
            return null;
        }
    }

    private void onBack() {
        TOB.runOnGLThread(() -> TOB.changeScene(new GameMenu()));
    }
}
