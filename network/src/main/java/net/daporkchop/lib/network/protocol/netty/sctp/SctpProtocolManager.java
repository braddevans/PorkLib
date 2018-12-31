/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.network.protocol.netty.sctp;

import com.sun.nio.sctp.SctpStandardSocketOptions;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.sctp.SctpChannelOption;
import io.netty.handler.codec.sctp.SctpMessageCompletionHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.builder.AbstractBuilder;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.endpoint.client.Client;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.packet.PacketRegistry;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.pork.packet.DisconnectPacket;
import net.daporkchop.lib.network.protocol.api.EndpointManager;
import net.daporkchop.lib.network.protocol.api.ProtocolManager;
import net.daporkchop.lib.network.protocol.netty.NettyServerChannel;

import java.util.function.Consumer;

/**
 * An implementation of {@link ProtocolManager} for the SCTP transport protocol.
 * <p>
 * SCTP provides an unlimited* number of independent reliable (and optionally ordered) channels. Unlike TCP,
 * which is stream-based, SCTP is message-based (like UDP) which gives better performance for the direct packet-based networking that
 * PorkLib network is designed for.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SctpProtocolManager implements ProtocolManager {
    public static final SctpProtocolManager INSTANCE = new SctpProtocolManager();
    private static final SctpStandardSocketOptions.InitMaxStreams MAX_STREAMS = SctpStandardSocketOptions.InitMaxStreams.create(0xFFFF, 0xFFFF);

    @Override
    public EndpointManager.ServerEndpointManager createServerManager() {
        return new SctpServerManager();
    }

    @Override
    public EndpointManager.ClientEndpointManager createClientManager() {
        return new SctpClientManager();
    }

    private abstract static class SctpEndpointManager<E extends Endpoint, B extends AbstractBuilder<E, B>> implements EndpointManager<E, B> {
        protected Channel channel;
        protected EventLoopGroup workerGroup;

        @Override
        public void close() {
            if (this.isClosed()) {
                throw new IllegalStateException("already closed!");
            }
            this.channel.flush();
            this.channel.close().syncUninterruptibly();
        }

        @Override
        public boolean isRunning() {
            return this.channel != null && this.channel.isActive();
        }
    }

    private static class SctpServerManager extends SctpEndpointManager<Server, ServerBuilder> implements EndpointManager.ServerEndpointManager {
        private ChannelGroup channels;
        @Getter
        private SctpServerChannel channel;

        @Override
        public void start(@NonNull ServerBuilder builder, @NonNull Server server) {
            this.workerGroup = builder.getEventGroup();
            this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(this.workerGroup);
                bootstrap.channelFactory(() -> new WrapperNioSctpServerChannel(server));
                bootstrap.childHandler(new SctpChannelInitializer(server, this.channels::add, this.channels::remove));
                bootstrap.option(SctpChannelOption.SCTP_INIT_MAXSTREAMS, MAX_STREAMS);
                bootstrap.childOption(SctpChannelOption.SCTP_NODELAY, true);
                bootstrap.childOption(SctpChannelOption.SCTP_INIT_MAXSTREAMS, MAX_STREAMS);

                super.channel = bootstrap.bind(builder.getAddress()).syncUninterruptibly().channel();
                this.channel = new SctpServerChannel(this.channels, server);
            } catch (Throwable t) {
                this.channels.close();
                throw new RuntimeException(t);
            }
        }

        @Override
        public void close(String reason) {
            this.channel.broadcast(new DisconnectPacket(reason), false);
            this.close();
        }

        @Override
        public void close() {
            this.channels.close();
            super.close();
        }

        private class SctpServerChannel extends NettyServerChannel {
            private SctpServerChannel(ChannelGroup channels, Server server) {
                super(channels, server);
            }

            @Override
            public void close(String reason) {
                SctpServerManager.this.close(reason);
            }

            @Override
            public void broadcast(@NonNull Object message, boolean blocking) {
                int id = this.server.getPacketRegistry().getId(message.getClass());
                super.broadcast(new UnencodedSctpPacket(message, UnderlyingNetworkConnection.ID_DEFAULT_CHANNEL, id, true), blocking);
            }

            @Override
            public <C extends UserConnection> void broadcast(@NonNull ByteBuf data, short id, @NonNull Class<? extends UserProtocol<C>> protocolClass) {
                SctpPacketWrapper wrapper = new SctpPacketWrapper(
                        data,
                        UnderlyingNetworkConnection.ID_DEFAULT_CHANNEL,
                        PacketRegistry.combine(this.server.getPacketRegistry().getProtocolId(protocolClass), id),
                        true
                );
                this.channels.writeAndFlush(wrapper);
            }
        }
    }

    private static class SctpClientManager extends SctpEndpointManager<Client, ClientBuilder> implements EndpointManager.ClientEndpointManager {
        @Override
        public void start(@NonNull ClientBuilder builder, @NonNull Client client) {
            this.workerGroup = builder.getEventGroup();

            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(this.workerGroup);
                bootstrap.channelFactory(() -> new WrapperNioSctpChannel(client));
                bootstrap.handler(new SctpChannelInitializer(client));
                bootstrap.option(SctpChannelOption.SCTP_NODELAY, true);
                bootstrap.option(SctpChannelOption.SCTP_INIT_MAXSTREAMS, MAX_STREAMS);

                this.channel = bootstrap.connect(builder.getAddress()).syncUninterruptibly().channel();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @Override
        public <C extends UserConnection> C getConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
            return ((WrapperNioSctpChannel) this.channel).getUserConnection(protocolClass);
        }

        @Override
        public void send(@NonNull Object message, boolean blocking, Void callback) {
            ((WrapperNioSctpChannel) this.channel).send(message, blocking, callback);
        }
    }

    private static class SctpChannelInitializer extends ChannelInitializer<Channel> {
        @NonNull
        private final Endpoint endpoint;
        private final Consumer<Channel> registerHook;
        private final Consumer<Channel> unRegisterHook;

        private SctpChannelInitializer(@NonNull Endpoint endpoint) {
            this(endpoint, c -> {
            }, c -> {
            });
        }

        private SctpChannelInitializer(@NonNull Endpoint endpoint, @NonNull Consumer<Channel> registerHook, @NonNull Consumer<Channel> unRegisterHook) {
            this.endpoint = endpoint;
            this.registerHook = registerHook;
            this.unRegisterHook = unRegisterHook;
        }

        @Override
        protected void initChannel(Channel c) throws Exception {
            c.pipeline().addLast(new SctpMessageCompletionHandler());
            c.pipeline().addLast(new SctpPacketCodec(this.endpoint));
            c.pipeline().addLast(new SctpPacketEncodingFilter(this.endpoint.getPacketRegistry()));
            c.pipeline().addLast(new SctpHandler(this.endpoint));
            this.registerHook.accept(c);

            WrapperNioSctpChannel realConnection = (WrapperNioSctpChannel) c;
            this.endpoint.getPacketRegistry().getProtocols().forEach(protocol -> realConnection.putUserConnection(protocol.getClass(), protocol.newConnection()));
            realConnection.registerTheUnderlyingConnection();
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
            this.unRegisterHook.accept(ctx.channel());
        }
    }
}