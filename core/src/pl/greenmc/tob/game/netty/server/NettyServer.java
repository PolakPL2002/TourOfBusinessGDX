package pl.greenmc.tob.game.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
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
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static pl.greenmc.tob.game.util.Logger.log;
import static pl.greenmc.tob.game.util.Logger.warning;

/**
 * Server
 */
public class NettyServer {
    private final static NettyServer singleton = new NettyServer();
    private final int PORT = 2137;
    private final HashMap<String, ChannelHandlerContext> clients = new HashMap<>();

    private NettyServer() {
    }

    /**
     * Starts the server
     *
     * @param onInterrupted Called when server is interrupted
     * @throws IOException On SSL cert loading error
     */
    public void start(Runnable onInterrupted) throws IOException {
        final SslContext sslCtx;
        sslCtx = SslContextBuilder.forServer(new File("certificate/cert.crt"), new File("certificate/cert.key")).build();
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
                                    new ServerHandler());
                        }
                    });
            // Bind and start to accept incoming connections.
            try {
                log("Starting server...");
                b.bind(PORT).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                if (onInterrupted != null) onInterrupted.run();
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * @param ID  Client ID
     * @param ctx Client context
     */
    public void addClient(String ID, ChannelHandlerContext ctx) {
        if (clients.containsKey(ID)) {
            warning("Duplicate connection for client " + ID + ". Dropping older one.");
            final ChannelHandlerContext client = getClient(ID);
            if (client != null) client.close();
            removeClient(ID);
        }
        clients.put(ID, ctx);
        log("Added client " + ID + ".");
    }

    /**
     * @param ID Client ID
     */
    public void removeClient(String ID) {
        clients.remove(ID);
        log("Removed client " + ID + ".");
    }

    /**
     * @param ID Client ID
     * @return Client or null if not found
     */
    @Nullable
    public ChannelHandlerContext getClient(String ID) {
        return clients.get(ID);
    }

    public static NettyServer getInstance() {
        return singleton;
    }
}
