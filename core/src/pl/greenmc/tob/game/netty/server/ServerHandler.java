package pl.greenmc.tob.game.netty.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.Player;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.Container;
import pl.greenmc.tob.game.netty.PacketReceivedHandler;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.packets.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.*;

import static pl.greenmc.tob.game.util.Logger.*;

/**
 * Handles incoming connections
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * Length of challenge in bytes
     */
    public final int CHALLENGE_LENGTH = 1024;
    /**
     * Heartbeat timeout
     */
    public final int HB_TIMEOUT = 2500;
    /**
     * Packet timeout in ms
     */
    public final int TIMEOUT = 2500;
    private final byte[] challengeData = new byte[CHALLENGE_LENGTH];
    private final PacketReceivedHandler packetReceivedHandler;
    private final ArrayList<SentPacket> sentPackets = new ArrayList<>();
    private final Timer timer = new Timer();
    private boolean alreadyRemoved;
    private boolean authenticated = false;
    private boolean challengeDataSent = false;
    private ChannelHandlerContext ctx;
    private Timer hbTimer = new Timer();
    private String identity;
    private Player player;

    public Player getPlayer() {
        return player;
    }

    public ServerHandler(@Nullable PacketReceivedHandler packetReceivedHandler) {
        this.packetReceivedHandler = packetReceivedHandler;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Echo back the received object to the client.
        if (msg instanceof Container) {
            Container container = (Container) msg;
            //Validate checksum
            if (!container.validateChecksum()) {
                try {
                    send(new ConfirmationPacket(container.messageUUID, false, authenticated), null, false);
                } catch (ConnectionNotAliveException e) {
                    e.printStackTrace();
                }
                return;
            }
            //Parse class
            Object o;
            try {
                final JsonObject jsonObject = JsonParser.parseString(container.packetData).getAsJsonObject();
                final Class<?> loadClass = Packet.class.getClassLoader().loadClass(container.packetClass);
                final Constructor<?> constructor = loadClass.getConstructor(JsonObject.class);
                o = constructor.newInstance(jsonObject);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return;
            }
            if (!(o instanceof Packet)) return;
            Packet packet = (Packet) o;
            if (packet instanceof ConfirmationPacket) {
                final ConfirmationPacket confirmationPacket = (ConfirmationPacket) packet;
                SentPacket sentPacket = getPacketByUUID(confirmationPacket.getUUID());
                if (sentPacket != null) {
                    if (confirmationPacket.isSuccess())
                        sentPacket.success(confirmationPacket instanceof ResponsePacket ? ((ResponsePacket) confirmationPacket).getResponse() : null);
                    else if (sentPacket.isResendOnFailure()) {
                        try {
                            send(sentPacket.getPacket(), sentPacket.getCallback(), sentPacket.isResendOnFailure(), sentPacket.getUUID());
                        } catch (ConnectionNotAliveException e) {
                            warning("Failed to resend packet.");
                            warning(e);
                        }
                    } else sentPacket.failure(SentPacket.FailureReason.CORRUPTED_RESPONSE);
                }
            } else if (packet instanceof HeartbeatPacket) {
                resetHeartbeat();
            } else {
                if (!authenticated) {
                    if (!challengeDataSent && packet instanceof HelloPacket) {
                        //First message of authentication
                        //Send challenge to the client
                        challengeDataSent = true;
                        HelloPacket helloPacket = (HelloPacket) packet;
                        identity = helloPacket.getClientID();

                        try {
                            player = NettyServer.getInstance().getDatabase().getPlayer(identity);
                        } catch (SQLException e) {
                            error("An error occurred while reading client data from database!");
                            error(e);
                            return;
                        }

                        if (player == null) {
                            //New player
                            log("New player is connecting. identity=" + identity);
                            try {
                                player = NettyServer.getInstance().getDatabase().addPlayer(identity);
                            } catch (SQLException e) {
                                error("An error occurred while adding client data to database!");
                                error(e);
                                return;
                            }
                        }

                        log("Authentication of " + player.getName() + " (unauthenticated) started.");
                        new SecureRandom().nextBytes(challengeData);
                        try {
                            send(new ResponsePacket(container.messageUUID, true, authenticated, HelloPacket.generateResponse(challengeData)), null, true);
                        } catch (ConnectionNotAliveException e) {
                            warning("Failed to send confirmation packet.");
                            warning(e);
                            return;
                        }
                        log("Challenge data sent to " + player.getName() + " (unauthenticated).");
                    } else if (challengeDataSent && packet instanceof AuthenticationPacket) {
                        AuthenticationPacket authenticationPacket = (AuthenticationPacket) packet;
                        if (!Arrays.equals(challengeData, authenticationPacket.getChallenge())) {
                            ctx.close();
                            return;
                        }
                        try {
                            Signature sig = Signature.getInstance("SHA512withRSA");
                            byte[] keyBytes = Base64.getDecoder().decode(identity);
                            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                            KeyFactory kf = KeyFactory.getInstance("RSA");
                            PublicKey publicKey = kf.generatePublic(spec);
                            sig.initVerify(publicKey);
                            sig.update(challengeData);
                            if (sig.verify(authenticationPacket.getResponse())) {
                                log("Authenticated client " + player.getName() + ".");
                                authenticated = true;
                                NettyServer.getInstance().addClient(identity, this);
                            } else {
                                warning("Authentication for client " + player.getName() + " (unauthenticated) failed.");
                            }
                            try {
                                send(new ConfirmationPacket(container.messageUUID, authenticated, authenticated), null, true);
                            } catch (ConnectionNotAliveException e) {
                                warning("Failed to send confirmation packet.");
                                warning(e);
                            }
                            if (!authenticated) ctx.close();
                        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
                            e.printStackTrace();
                            ctx.close();
                        }
                    } else {
                        warning("Received non-authentication packet before authentication from client " + player.getName() + " (unauthenticated)! (" + packet.getClass().getCanonicalName() + ")");
                        try {
                            send(new ConfirmationPacket(container.messageUUID, false, authenticated), null, true);
                        } catch (ConnectionNotAliveException e) {
                            warning("Failed to send confirmation packet.");
                            warning(e);
                        }
                        ctx.close();
                    }
                } else {
                    if (packetReceivedHandler != null)
                        packetReceivedHandler.onPacketReceived(container, packet, identity);
                }
            }
        } else {
            warning("[Netty] Received data that is not a container!");
        }
    }

    /**
     * Sends packet to the client
     *
     * @param packet          {@link Packet} to be sent
     * @param callback        Callback instance to be called on success or failure
     * @param resendOnFailure Whether packet should be retransmitted on failure
     * @throws ConnectionNotAliveException When connection is dead
     */
    public void send(Packet packet, SentPacket.Callback callback, boolean resendOnFailure) throws ConnectionNotAliveException {
        send(packet, callback, resendOnFailure, UUID.randomUUID());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
        resetHeartbeat();
        debug("Channel active!");
    }

    /**
     * Resets heartbeat timer
     */
    private void resetHeartbeat() {
        hbTimer.cancel();
        hbTimer = new Timer();
        hbTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                heartbeatTimeout();
            }
        }, HB_TIMEOUT);
    }

    /**
     * Called on timeout
     */
    private void heartbeatTimeout() {
        //TODO Raise some event
        warning("Client " + player.getName() + (authenticated ? "" : " (unauthenticated)") + " timed out!");
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        debug("Channel registered!");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        debug("Channel unregistered!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        timer.cancel();
        hbTimer.cancel();
        if (!alreadyRemoved)
            NettyServer.getInstance().removeClient(identity);
        debug("Channel inactive!");
    }

    void setAlreadyRemoved(boolean alreadyRemoved) {
        this.alreadyRemoved = alreadyRemoved;
    }

    /**
     * Sends packet to the server
     *
     * @param packet          {@link Packet} to be sent
     * @param callback        Callback instance to be called on success or failure
     * @param resendOnFailure Whether packet should be retransmitted on failure
     * @param messageUUID     UUID of message used to create container
     * @throws ConnectionNotAliveException When connection is dead
     */
    private void send(Packet packet, SentPacket.Callback callback, boolean resendOnFailure, UUID messageUUID) throws ConnectionNotAliveException {
        if (ctx != null) {
            Container container = new Container(messageUUID, packet.getClass().getCanonicalName(), packet.encode().toString());
            ctx.writeAndFlush(container);
            if (!(packet instanceof ConfirmationPacket)) {
                final SentPacket sentPacket = new SentPacket(packet, callback, resendOnFailure, container.messageUUID);
                synchronized (sentPackets) {
                    sentPackets.add(sentPacket);
                }
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                if (!sentPacket.gotResponse()) {
                                    if (sentPacket.isResendOnFailure()) {
                                        try {
                                            send(sentPacket.getPacket(), sentPacket.getCallback(), sentPacket.isResendOnFailure(), sentPacket.getUUID());
                                        } catch (ConnectionNotAliveException e) {
                                            e.printStackTrace();
                                        }
                                    } else sentPacket.failure(SentPacket.FailureReason.TIMEOUT);
                                }
                                synchronized (sentPackets) {
                                    sentPackets.remove(sentPacket);
                                }
                            }
                        }
                        , TIMEOUT);
            }
        } else {
            throw new ConnectionNotAliveException();
        }
    }

    /**
     * @param uuid Packet UUID
     * @return {@link SentPacket} or null
     */
    private @Nullable
    SentPacket getPacketByUUID(UUID uuid) {
        for (SentPacket packet : sentPackets)
            if (packet.getUUID().equals(uuid)) return packet;
        return null;
    }
}
