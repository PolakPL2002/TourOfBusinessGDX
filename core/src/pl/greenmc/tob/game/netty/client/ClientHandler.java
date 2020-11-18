package pl.greenmc.tob.game.netty.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.Container;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.packets.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.*;
import java.util.*;

import static pl.greenmc.tob.game.util.Logger.*;

/**
 * Handler implementation for the object echo client.  It initiates the
 * ping-pong traffic between the object echo client and server by sending the
 * first message to the server.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * Packet timeout in ms
     */
    public final int TIMEOUT = 2500;
    private final Timer hbTimer = new Timer();
    private final ArrayList<SentPacket> sentPackets = new ArrayList<>();
    private final Timer timer = new Timer();
    private boolean authenticated = false;
    private ChannelHandlerContext ctx;
    private KeyPair keyPair = null;
    private final Runnable onAuthenticated;

    public ClientHandler(Runnable onAuthenticated) {
        this.onAuthenticated = onAuthenticated;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log("[Netty] Channel active!");
        this.ctx = ctx;
        hbTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendHeartbeat();
            }
        }, 1000, 1000);
        try {
            send(new HelloPacket(getClientID()), new SentPacket.Callback() {
                @Override
                public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                    if (response == null) {
                        ctx.close();
                        return;
                    }
                    try {
                        final HelloPacketResponse helloPacketResponse = HelloPacket.parseResponse(response);
                        final byte[] challengeData = helloPacketResponse.getChallengeData();
                        Signature sig = Signature.getInstance("SHA512withRSA");
                        sig.initSign(getPrivateKey());
                        sig.update(challengeData);
                        final byte[] signature = sig.sign();
                        send(new AuthenticationPacket(getClientID(), challengeData, signature), new SentPacket.Callback() {
                            @Override
                            public void success(@NotNull UUID uuid, @Nullable JsonObject response) {
                                log("[Netty] Authenticated!");
                                authenticated = true;
                                onAuthenticated.run();
                            }

                            @Override
                            public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                                ctx.close();
                            }
                        }, false);
                    } catch (InvalidPacketException | NoSuchAlgorithmException | IOException | InvalidKeyException | SignatureException | ConnectionNotAliveException e) {
                        e.printStackTrace();
                        ctx.close();
                    }
                }

                @Override
                public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                    ctx.close();
                }
            }, false);
        } catch (ConnectionNotAliveException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends heartbeat packet
     */
    public void sendHeartbeat() {
        try {
            send(new HeartbeatPacket(), null, false);
        } catch (ConnectionNotAliveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
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
                final JsonObject jsonObject = JsonParser.parseString(container.packetData)
                        .getAsJsonObject();
                final Class<?> loadClass = Packet.class.getClassLoader().loadClass(container.packetClass);
                final Constructor<?> constructor = loadClass.getConstructor(JsonObject.class);
                o = constructor.newInstance(jsonObject);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return;
            }
            if (!(o instanceof Packet)) return;
            Packet packet = (Packet) o;
            if (packet instanceof ResponsePacket) {
                final ResponsePacket responsePacket = (ResponsePacket) packet;
                SentPacket sentPacket = getPacketByUUID(responsePacket.getUUID());
                if (sentPacket != null) {
                    if (responsePacket.isSuccess())
                        sentPacket.success(responsePacket.getResponse());
                    else if (!responsePacket.isAuthenticated())
                        sentPacket.failure(SentPacket.FailureReason.NOT_AUTHENTICATED);
                    else if (sentPacket.isResendOnFailure()) {
                        try {
                            send(sentPacket.getPacket(), sentPacket.getCallback(), sentPacket.isResendOnFailure(), sentPacket.getUUID());
                        } catch (ConnectionNotAliveException e) {
                            e.printStackTrace();
                        }
                    } else sentPacket.failure(SentPacket.FailureReason.CORRUPTED_RESPONSE);
                }
            } else if (packet instanceof ConfirmationPacket) {
                final ConfirmationPacket confirmationPacket = (ConfirmationPacket) packet;
                SentPacket sentPacket = getPacketByUUID(confirmationPacket.getUUID());
                if (sentPacket != null) {
                    if (confirmationPacket.isSuccess()) sentPacket.success(null);
                    else if (!confirmationPacket.isAuthenticated())
                        sentPacket.failure(SentPacket.FailureReason.NOT_AUTHENTICATED);
                    else if (sentPacket.isResendOnFailure()) {
                        try {
                            send(sentPacket.getPacket(), sentPacket.getCallback(), sentPacket.isResendOnFailure(), sentPacket.getUUID());
                        } catch (ConnectionNotAliveException e) {
                            e.printStackTrace();
                        }
                    } else sentPacket.failure(SentPacket.FailureReason.CORRUPTED_RESPONSE);
                }
            } else {
                //TODO Raise event
            }
        } else {
            warning("[Netty] Received data that is not a container!");
        }
    }

    /**
     * Sends packet to the server
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log("[Netty] Exception caught");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        timer.cancel();
        hbTimer.cancel();
        log("[Netty] Channel inactive!");
    }

    private String getClientID() throws IOException, NoSuchAlgorithmException {
        return Base64.getEncoder().encodeToString(getPublicKey().getEncoded());
    }

    private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException {
        if (keyPair != null) return keyPair.getPrivate();
        loadKey();
        return keyPair.getPrivate();
    }

    private PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException {
        if (keyPair != null) return keyPair.getPublic();
        loadKey();
        return keyPair.getPublic();
    }

    private void loadKey() throws IOException, NoSuchAlgorithmException {
        File keyFile = new File("id.dat");
        if (!keyFile.exists()) {
            //Generate key
            generateKey();
        } else {
            //Load key
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyFile));
            try {
                Object o = ois.readObject();
                if (!(o instanceof KeyPair)) {
                    warning("Failed to load stored key! (Not an instance of KeyPair)");
                    generateKey();
                } else {
                    keyPair = (KeyPair) o;
                }
                ois.close();
            } catch (ClassNotFoundException e) {
                warning("Failed to load stored key!");
                warning(e);
                generateKey();
                ois.close();
            }
        }

    }

    private void generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        keyPair = kpg.generateKeyPair();
        File keyFile = new File("id.dat");
        if (keyFile.exists()) {
            if (!keyFile.delete()) {
                warning("Failed to save key! (Unable to delete existing file)");
                return;
            }
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(keyFile));
            oos.writeObject(keyPair);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            warning("Failed to save key! (Unable to delete existing file)");
            warning(e);
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ioException) {
                    error(e);
                }
            }
        }
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
        if (ctx != null && (authenticated || packet instanceof ConfirmationPacket || packet instanceof HelloPacket || packet instanceof AuthenticationPacket)) {
            Container container = new Container(messageUUID, packet.getClass().getCanonicalName(), packet.encode().toString());
            ctx.writeAndFlush(container);
            if (!(packet instanceof ConfirmationPacket || packet instanceof HeartbeatPacket)) {
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
        } else throw new ConnectionNotAliveException();
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

