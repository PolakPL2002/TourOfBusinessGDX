package pl.greenmc.tob.game;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.Container;
import pl.greenmc.tob.game.netty.PacketReceivedHandler;
import pl.greenmc.tob.game.netty.packets.ConfirmationPacket;
import pl.greenmc.tob.game.netty.packets.Packet;
import pl.greenmc.tob.game.netty.packets.ResponsePacket;
import pl.greenmc.tob.game.netty.packets.game.GetPlayerPacket;
import pl.greenmc.tob.game.netty.packets.game.GetSelfPacket;
import pl.greenmc.tob.game.netty.packets.game.events.lobby.*;
import pl.greenmc.tob.game.netty.packets.game.lobby.*;
import pl.greenmc.tob.game.netty.server.NettyServer;
import pl.greenmc.tob.game.netty.server.ServerHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static pl.greenmc.tob.game.util.Logger.log;
import static pl.greenmc.tob.game.util.Logger.warning;

public class TourOfBusinessServer {
    private final int LOBBY_MAX_PLAYERS = 7;
    private final ArrayList<Lobby> lobbies = new ArrayList<>();

    public TourOfBusinessServer() {
        NettyServer.getInstance().start(null, new PacketReceivedHandler() {
            @Override
            public void onPacketReceived(Container container, Packet packet, @Nullable String identity) {
                log("Packet received: " + packet + " from " + identity);
                if (packet instanceof GetSelfPacket) {
                    try {
                        Player player = NettyServer.getInstance().getDatabase().getPlayer(identity);
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null)
                            client.send(new ResponsePacket(container.messageUUID, true, true, GetSelfPacket.generateResponse(player)), null, false);
                    } catch (SQLException e) {
                        warning("Failed to get player data.");
                        warning(e);
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    }
                } else if (packet instanceof GetPlayerPacket) {
                    try {
                        Player player = NettyServer.getInstance().getDatabase().getPlayer(((GetPlayerPacket) packet).getPlayerID());
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null)
                            client.send(new ResponsePacket(container.messageUUID, true, true, GetPlayerPacket.generateResponse(player)), null, false);
                    } catch (SQLException e) {
                        warning("Failed to get player data.");
                        warning(e);
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    }
                } else if (packet instanceof GetLobbiesPacket) {
                    try {
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null) {
                            Lobby[] lobbies = TourOfBusinessServer.this.lobbies.toArray(new Lobby[0]);
                            client.send(new ResponsePacket(container.messageUUID, true, true, GetLobbiesPacket.generateResponse(lobbies, getLobbiesPlayers(lobbies))), null, false);
                        }
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    } catch (SQLException e) {
                        warning("Failed to get player data.");
                        warning(e);
                    }
                } else if (packet instanceof GetLobbyPacket) {
                    try {
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null) {
                            Lobby lobby = getLobbyByID(((GetLobbyPacket) packet).getLobbyID());
                            client.send(new ResponsePacket(container.messageUUID, true, true, GetLobbyPacket.generateResponse(lobby, getLobbyPlayers(lobby))), null, false);
                        }
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    } catch (SQLException e) {
                        warning("Failed to get player data.");
                        warning(e);
                    }
                } else if (packet instanceof CreateLobbyPacket) {
                    try {
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null) {
                            Player player = getPlayerFromHandler(client);
                            if (player == null) return;
                            Lobby lobby = getLobbyByOwner(player.getID());
                            boolean success = true;
                            if (lobby != null)
                                success = false;
                            else
                                createLobby(player);
                            lobby = getLobbyByOwner(player.getID());
                            if (lobby != null)
                                client.send(new ResponsePacket(container.messageUUID, true, true, CreateLobbyPacket.generateResponse(success, lobby.getID())), null, false);
                        }
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    } catch (SQLException e) {
                        warning("Failed to get player from database.");
                        warning(e);
                    }
                } else if (packet instanceof JoinLobbyPacket) {
                    try {
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null) {
                            Player player = getPlayerFromHandler(client);
                            if (player == null) return;
                            Lobby lobby = getLobbyByID(((JoinLobbyPacket) packet).getLobbyID());
                            boolean success = true;
                            if (lobby == null)
                                success = false;
                            else {
                                final Integer[] players = lobby.getPlayers();

                                boolean playerInLobby = false;
                                for (int pl : players)
                                    if (pl == player.getID()) {
                                        playerInLobby = true;
                                        break;
                                    }

                                if (players.length >= LOBBY_MAX_PLAYERS || playerInLobby)
                                    success = false;
                                else
                                    lobby.addPlayer(player.getID());
                            }
                            if (success) {
                                log("Player " + player.getName() + " has joined lobby#" + lobby.getID() + " of player#" + lobby.getOwner());
                                onPlayerJoinedLobby(lobby, player);
                            } else {
                                if (lobby == null)
                                    log("Player " + player.getName() + " has failed to join unknown lobby.");
                                else
                                    log("Player " + player.getName() + " has failed to join lobby#" + lobby.getID() + " of player#" + lobby.getOwner());
                            }
                            client.send(new ResponsePacket(container.messageUUID, true, true, JoinLobbyPacket.generateResponse(success)), null, false);
                        }
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    } catch (SQLException e) {
                        warning("Failed to get player from database.");
                        warning(e);
                    }
                } else if (packet instanceof LeaveLobbyPacket) {
                    try {
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null) {
                            Player player = getPlayerFromHandler(client);
                            if (player == null) return;
                            Lobby lobby = getLobbyByPlayer(player.getID());
                            boolean success = true;
                            if (lobby == null)
                                success = false;
                            else {
                                if (lobby.getOwner() == player.getID())
                                    removeLobby(lobby);
                                else
                                    lobby.removePlayer(player.getID());
                            }
                            if (success) {
                                log("Player " + player.getName() + " has left lobby#" + lobby.getID() + " of player#" + lobby.getOwner());
                                onPlayerLeftLobby(lobby, player);
                            } else {
                                log("Player " + player.getName() + " has failed to leave unknown lobby.");
                            }
                            client.send(new ConfirmationPacket(container.messageUUID, true, true), null, false);
                        }
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    } catch (SQLException e) {
                        warning("Failed to get player from database.");
                        warning(e);
                    }
                } else if (packet instanceof GetSelfLobbyPacket) {
                    try {
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null) {
                            Player player = getPlayerFromHandler(client);
                            if (player == null) return;
                            Lobby lobby = getLobbyByPlayer(player.getID());

                            client.send(new ResponsePacket(container.messageUUID, true, true, GetLobbyPacket.generateResponse(lobby, getLobbyPlayers(lobby))), null, false);
                        }
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    } catch (SQLException e) {
                        warning("Failed to get player from database.");
                        warning(e);
                    }
                } else if (packet instanceof SetSelfReadyPacket) {
                    try {
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null) {
                            Player player = getPlayerFromHandler(client);
                            if (player == null) return;
                            Lobby lobby = getLobbyByPlayer(player.getID());
                            if (lobby != null) {
                                lobby.setPlayerReady(player.getID(), ((SetSelfReadyPacket) packet).isReady());
                                onPlayerReadyStateChanged(lobby, player);
                            }
                            client.send(new ConfirmationPacket(container.messageUUID, true, true), null, false);
                        }
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    } catch (SQLException e) {
                        warning("Failed to get player from database.");
                        warning(e);
                    }
                } else if (packet instanceof StartGamePacket) {
                    try {
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null) {
                            Player player = getPlayerFromHandler(client);
                            if (player == null) return;
                            Lobby lobby = getLobbyByOwner(player.getID());
                            if (lobby != null &&
                                    lobby.getLobbyState() == Lobby.LobbyState.CONFIGURING &&
                                    lobby.allReady() &&
                                    lobby.getPlayers().length > 0) {
                                lobby.setLobbyState(Lobby.LobbyState.IN_GAME);
                                onGameStarted(lobby);
                            }
                            client.send(new ConfirmationPacket(container.messageUUID, true, true), null, false);
                        }
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    } catch (SQLException e) {
                        warning("Failed to get player from database.");
                        warning(e);
                    }
                } else
                    try {
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null)
                            client.send(new ConfirmationPacket(container.messageUUID, true, true), null, false);
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send confirmation packet.");
                        warning(e);
                    }
            }
        });
    }

    private void onGameStarted(Lobby lobby) {
        for (ServerHandler client : NettyServer.getInstance().getClients()) {
            try {
                client.send(new GameStartedPacket(lobby.getID()), null, false);
            } catch (ConnectionNotAliveException e) {
                warning("Failed to send event packet.");
                warning(e);
            }
        }
    }

    @NotNull
    private Player[] getLobbiesPlayers(@NotNull Lobby[] lobbies) throws SQLException {
        HashSet<Player> out = new HashSet<>();
        for (Lobby lobby : lobbies) {
            Player[] newPlayers = getLobbyPlayers(lobby);
            out.addAll(Arrays.asList(newPlayers));

            Player owner = NettyServer.getInstance().getDatabase().getPlayer(lobby.getOwner());
            out.add(owner);
        }
        return out.toArray(new Player[0]);
    }

    @Nullable
    @Contract("null -> null; !null -> new")
    private Player[] getLobbyPlayers(@Nullable Lobby lobby) throws SQLException {
        if (lobby == null) return null;
        Integer[] lobbyPlayers = lobby.getPlayers();
        Player[] players = new Player[lobbyPlayers.length + 1];
        int i = 0;
        for (int player : lobbyPlayers) {
            Player playerByID = NettyServer.getInstance().getDatabase().getPlayer(player);
            if (playerByID == null) {
                lobby.removePlayer(player);
                //Event not raised because player is unknown
                warning("Removed unknown player#" + player + " from lobby#" + lobby.getID());
                return getLobbyPlayers(lobby);
            }
            players[i] = playerByID;
            i++;
        }
        Player playerByID = NettyServer.getInstance().getDatabase().getPlayer(lobby.getOwner());
        if (playerByID == null) {
            removeLobby(lobby);
            warning("Removed lobby#" + lobby.getID() + ". Owner unknown.");
            return null;
        }
        players[i] = playerByID;
        return players;
    }

    private void onPlayerJoinedLobby(@NotNull Lobby lobby, @NotNull Player player) {
        for (int p : lobby.getPlayers()) {
            if (p != player.getID()) {
                sendPacketToPlayerByID(new PlayerJoinedPacket(player.getID()), p);
            }
        }
        sendPacketToPlayerByID(new PlayerJoinedPacket(player.getID()), lobby.getOwner());
    }

    private void onPlayerReadyStateChanged(@NotNull Lobby lobby, @NotNull Player player) {
        for (int p : lobby.getPlayers()) {
            if (p != player.getID()) {
                sendPacketToPlayerByID(new PlayerReadyStateChanged(player.getID(), lobby.isPlayerReady(player.getID())), p);
            }
        }
        sendPacketToPlayerByID(new PlayerReadyStateChanged(player.getID(), lobby.isPlayerReady(player.getID())), lobby.getOwner());
    }

    private void onPlayerLeftLobby(@NotNull Lobby lobby, @NotNull Player player) {
        for (int p : lobby.getPlayers()) {
            if (p != player.getID()) {
                sendPacketToPlayerByID(new PlayerLeftPacket(player.getID()), p);
            }
        }
        sendPacketToPlayerByID(new PlayerLeftPacket(player.getID()), lobby.getOwner());
    }

    private void sendPacketToPlayerByID(@NotNull Packet packet, int playerID) {
        try {
            Player player1 = NettyServer.getInstance().getDatabase().getPlayer(playerID);
            if (player1 != null) {
                ServerHandler client = NettyServer.getInstance().getClient(player1.getIdentity());
                if (client != null) {
                    client.send(packet, null, false);
                }
            }
        } catch (ConnectionNotAliveException e) {
            warning("Failed to send event packet.");
            warning(e);
        } catch (SQLException e) {
            warning("Failed to get player from database.");
            warning(e);
        }
    }

    @Nullable
    private Player getPlayerFromHandler(@NotNull ServerHandler client) throws SQLException {
        Player player = client.getPlayer();
        if (player == null)
            player = NettyServer.getInstance().getDatabase().getPlayer(client.getIdentity());
        if (player == null) {
            warning("Can't find player for identity=" + client.getIdentity());
            client.getCtx().close();
            return null;
        }
        return player;
    }

    @Nullable
    private Lobby getLobbyByPlayer(int playerID) {
        for (Lobby lobby : lobbies) {
            if (lobby.getOwner() == playerID) return lobby;
            for (Integer id : lobby.getPlayers())
                if (id == playerID) return lobby;
        }
        return null;
    }

    private void createLobby(@NotNull Player owner) {
        Lobby lobby = getLobbyByOwner(owner.getID());
        if (lobby != null) {
            warning("Given owner already has a lobby. Removing existing one...");
            removeLobby(lobby);
        }
        lobby = new Lobby(owner.getID(), owner.getID());
        lobbies.add(lobby);
        log("Created lobby#" + owner.getID() + " for player " + owner.getName());
        onLobbyCreated(lobby);
    }

    private void onLobbyCreated(@NotNull Lobby lobby) {
        for (ServerHandler client : NettyServer.getInstance().getClients()) {
            try {
                client.send(new LobbyCreatedPacket(lobby.getID()), null, false);
            } catch (ConnectionNotAliveException e) {
                warning("Failed to send event packet.");
                warning(e);
            }
        }
    }

    private void removeLobby(@NotNull Lobby lobby) {
        onLobbyRemoved(lobby);
        lobbies.remove(lobby);
        log("Removed lobby#" + lobby.getID() + " of player#" + lobby.getOwner());
    }

    private void onLobbyRemoved(@NotNull Lobby lobby) {
        for (ServerHandler client : NettyServer.getInstance().getClients()) {
            try {
                client.send(new LobbyRemovedPacket(lobby.getID()), null, false);
            } catch (ConnectionNotAliveException e) {
                warning("Failed to send event packet.");
                warning(e);
            }
        }
    }

    @Nullable
    private Lobby getLobbyByID(int id) {
        for (Lobby lobby : lobbies)
            if (lobby.getID() == id) return lobby;
        return null;
    }

    @Nullable
    private Lobby getLobbyByOwner(int owner) {
        for (Lobby lobby : lobbies)
            if (lobby.getOwner() == owner) return lobby;
        return null;
    }
}
