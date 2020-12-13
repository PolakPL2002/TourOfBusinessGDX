package pl.greenmc.tob.game.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.netty.PacketReceivedHandler;
import pl.greenmc.tob.game.server.Database;

import javax.net.ssl.SSLException;
import java.io.File;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

import static pl.greenmc.tob.game.util.Logger.*;

/**
 * Server
 */
public class NettyServer {
    private final static NettyServer singleton = new NettyServer();
    private final File certChainFile = new File("certificate/cert.crt");
    private final File certKeyFile = new File("certificate/cert.key");
    private final HashMap<String, ServerHandler> clients = new HashMap<>();
    private Database database;
    private int port = 21370;
    private SslContext sslCtx = null;

    private NettyServer() {

    }

    public ServerHandler[] getClients() {
        return clients.values().toArray(new ServerHandler[0]);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Database getDatabase() {
        return database;
    }

    /**
     * Starts the server
     *
     * @param onInterrupted Called when server is interrupted
     */
    public void start(@Nullable Runnable onInterrupted, @Nullable final PacketReceivedHandler packetReceivedHandler) {
        if (database == null) database = new Database();
        if (sslCtx == null) loadCertificate();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(
                                    sslCtx.newHandler(ch.alloc()),
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ServerHandler(packetReceivedHandler));
                        }
                    });
            // Bind and start to accept incoming connections.
            try {
                log("Starting server...");
                b.bind(port).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                if (onInterrupted != null) onInterrupted.run();
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void loadCertificate() {
        if (!certKeyFile.exists() || !certChainFile.exists()) {
            try {
                generateSelfSignedCert();
            } catch (NoSuchProviderException | OperatorCreationException | CertificateException | SSLException | NoSuchAlgorithmException e) {
                error("Failed to generate self-signed certificate!");
                fatal(e);
            }
        } else {
            try {
                log("Loading certificate from file...");
                sslCtx = SslContextBuilder.forServer(certChainFile, certKeyFile).build();
                log("Certificate loaded!");
            } catch (SSLException e) {
                error("Failed to load certificate!");
                error(e);
                try {
                    generateSelfSignedCert();
                } catch (NoSuchProviderException | OperatorCreationException | CertificateException | SSLException | NoSuchAlgorithmException e2) {
                    error("Failed to generate self-signed certificate!");
                    fatal(e2);
                }
            }
        }
    }

    private void generateSelfSignedCert() throws NoSuchProviderException, NoSuchAlgorithmException, OperatorCreationException, CertificateException, SSLException {
        log("Generating self-signed certificate...");
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
            Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        X500Name dnName = new X500Name("CN=TOB");
        BigInteger certSerialNumber = BigInteger.valueOf(System.currentTimeMillis());
        String signatureAlgorithm = "SHA256WithRSA";
        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm)
                .build(keyPair.getPrivate());
        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(2 * 365, ChronoUnit.DAYS);
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                dnName, certSerialNumber, Date.from(startDate), Date.from(endDate), dnName,
                keyPair.getPublic());
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(certBuilder.build(contentSigner));
        sslCtx = SslContextBuilder.forServer(keyPair.getPrivate(), certificate).build();
        log("Generated self-signed certificate!");
    }

    /**
     * @param ID  Client ID
     * @param ctx Client context
     */
    public void addClient(String ID, ServerHandler ctx) {
        synchronized (clients) {
            if (clients.containsKey(ID)) {
                warning("Duplicate connection for client " + ID + ". Dropping older one.");
                final ServerHandler client = getClient(ID);
                if (client != null) {
                    client.getCtx().close();
                    client.setAlreadyRemoved(true);
                    removeClient(ID);
                }
            }
            clients.put(ID, ctx);
            log("Added client " + ID + ".");
        }
    }

    /**
     * @param ID Client ID
     */
    public void removeClient(String ID) {
        synchronized (clients) {
            clients.remove(ID);
            log("Removed client " + ID + ".");
        }
    }

    /**
     * @param ID Client ID
     * @return Client or null if not found
     */
    @Nullable
    public ServerHandler getClient(String ID) {
        synchronized (clients) {
            return clients.get(ID);
        }
    }

    public static NettyServer getInstance() {
        return singleton;
    }
}
