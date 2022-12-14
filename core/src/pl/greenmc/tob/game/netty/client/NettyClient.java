package pl.greenmc.tob.game.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.FingerprintTrustManagerFactory;
import pl.greenmc.tob.game.netty.PacketReceivedHandler;

import javax.annotation.Nullable;
import javax.net.ssl.SSLException;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import static pl.greenmc.tob.game.util.Logger.log;
import static pl.greenmc.tob.game.util.Logger.warning;

/**
 * Netty client
 */
public class NettyClient {
    private final static NettyClient singleton = new NettyClient();
    private final String[] acceptedCertificates = new String[]{};
    private ClientHandler clientHandler;
    private boolean connected = false;
    private String host = "127.0.0.1";
    private int port = 21370;
    private boolean sslError = false;

    private NettyClient() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return List of accepted certificates fingerprints
     */
    public String[] getAcceptedCertificates() {
        return acceptedCertificates;
    }

    /**
     * @return Whether SSL error occurred
     */
    public boolean isSSLError() {
        return sslError;
    }

    /**
     * @return Whether client is connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * @return Client handler
     */
    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    /**
     * Connects to the server
     *
     * @param onSSLError Runnable run on SSL error
     */
    public void connect(@Nullable final Runnable onSSLError, @Nullable final Runnable onConnect, @Nullable final Runnable onDisconnect, @Nullable final Runnable onAuthenticated, @Nullable final PacketReceivedHandler packetReceivedHandler) {
        // Start the connection attempt.
        new Thread(() -> {
            EventLoopGroup group = new NioEventLoopGroup();
            final SslContext sslCtx;
            try {
                if (acceptedCertificates.length > 0)
                    sslCtx = SslContextBuilder.forClient()
                            .trustManager(
                                    FingerprintTrustManagerFactory.builder("SHA256")
                                            .fingerprints(acceptedCertificates).build()
                            ).build();
                else sslCtx = SslContextBuilder.forClient().trustManager(
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }

                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }

                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }).build();
            } catch (SSLException e) {
                e.printStackTrace();
                sslError = true;
                if (onSSLError != null) onSSLError.run();
                return;
            }
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) {
                                ChannelPipeline p = ch.pipeline();
                                clientHandler = new ClientHandler(onAuthenticated, packetReceivedHandler);
                                p.addLast();
                                p.addLast(
                                        sslCtx.newHandler(ch.alloc(), host, port),
                                        new ObjectEncoder(),
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        clientHandler);
                            }
                        });
                log("[Netty] Connecting...");
                try {
                    ChannelFuture sync = b.connect(host, port).sync();
                    log("[Netty] Connected!");
                    connected = true;
                    if (onConnect != null) onConnect.run();
                    sync.channel().closeFuture().sync();
                } catch (Exception exception) {
                    warning("[Netty] Failed to connect.");
                }
            } finally {
                connected = false;
                group.shutdownGracefully();
                warning("[Netty] Client disconnected!");
                if (onDisconnect != null) onDisconnect.run();
            }
        }).start();
        log("[Netty] Client started!");
    }

    public static NettyClient getInstance() {
        return singleton;
    }
}
