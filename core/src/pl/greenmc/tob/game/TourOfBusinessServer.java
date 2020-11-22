package pl.greenmc.tob.game;

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
import pl.greenmc.tob.game.netty.packets.game.lobby.CreateLobbyPacket;
import pl.greenmc.tob.game.netty.packets.game.lobby.GetLobbiesPacket;
import pl.greenmc.tob.game.netty.packets.game.lobby.GetLobbyPacket;
import pl.greenmc.tob.game.netty.packets.game.lobby.JoinLobbyPacket;
import pl.greenmc.tob.game.netty.server.NettyServer;
import pl.greenmc.tob.game.netty.server.ServerHandler;

import java.sql.SQLException;
import java.util.ArrayList;

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
                        if (client != null)
                            client.send(new ResponsePacket(container.messageUUID, true, true, GetLobbiesPacket.generateResponse(lobbies.toArray(new Lobby[0]))), null, false);
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    }
                } else if (packet instanceof GetLobbyPacket) {
                    try {
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null)
                            client.send(new ResponsePacket(container.messageUUID, true, true, GetLobbyPacket.generateResponse(getLobbyByID(((GetLobbyPacket) packet).getLobbyID()))), null, false);
                    } catch (ConnectionNotAliveException e) {
                        warning("Failed to send response packet.");
                        warning(e);
                    }
                } else if (packet instanceof CreateLobbyPacket) {
                    try {
                        final ServerHandler client = NettyServer.getInstance().getClient(identity);
                        if (client != null) {
                            Player player = client.getPlayer();
                            if (player == null)
                                player = NettyServer.getInstance().getDatabase().getPlayer(identity);
                            if (player == null) {
                                warning("Can't find player for identity=" + identity);
                                client.getCtx().close();
                                return;
                            }
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
                            Player player = client.getPlayer();
                            if (player == null)
                                player = NettyServer.getInstance().getDatabase().getPlayer(identity);
                            if (player == null) {
                                warning("Can't find player for identity=" + identity);
                                client.getCtx().close();
                                return;
                            }
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
                            if (success)
                                log("Player " + player.getName() + " has joined lobby#" + lobby.getID() + " of player#" + lobby.getOwner());
                                //TODO Event?
                            else {
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

    private void createLobby(@NotNull Player owner) {
        final Lobby lobby = getLobbyByOwner(owner.getID());
        if (lobby != null) {
            warning("Given owner already has a lobby. Removing existing one...");
            removeLobby(lobby);
        }
        lobbies.add(new Lobby(owner.getID(), owner.getID()));
        log("Created lobby#" + owner.getID() + " for player " + owner.getName());
        //TODO Event?
    }

    private void removeLobby(@NotNull Lobby lobby) {
        //TODO Remove lobby when owner leaves
        lobbies.remove(lobby);
        log("Removed lobby#" + lobby.getID() + " of player#" + lobby.getOwner());
        //TODO Event?
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
