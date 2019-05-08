/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.network.transport.tcp;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.concurrent.Future;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.session.PChannel;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.session.UserSession;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.transport.WrappedPacket;

import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
public class WrapperNioSocketChannel extends NioSocketChannel implements NetSession {
    protected final PEndpoint endpoint;
    protected final DummyTCPChannel defaultChannel = new DummyTCPChannel(this, 0);
    protected final Map<Integer, DummyTCPChannel> channels = PorkUtil.newSoftCache();
    protected final UserSession userSession;

    public WrapperNioSocketChannel(@NonNull PEndpoint endpoint, @NonNull UserSession userSession) {
        super();
        this.endpoint = endpoint;
        this.userSession = userSession;
    }

    public WrapperNioSocketChannel(SelectorProvider provider, @NonNull PEndpoint endpoint, @NonNull UserSession userSession) {
        super(provider);
        this.endpoint = endpoint;
        this.userSession = userSession;
    }

    public WrapperNioSocketChannel(SocketChannel socket, @NonNull PEndpoint endpoint, @NonNull UserSession userSession) {
        super(socket);
        this.endpoint = endpoint;
        this.userSession = userSession;
    }

    public WrapperNioSocketChannel(Channel parent, SocketChannel socket, @NonNull PEndpoint endpoint, @NonNull UserSession userSession) {
        super(parent, socket);
        this.endpoint = endpoint;
        this.userSession = userSession;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends PEndpoint<E>> E endpoint() {
        return (E) this.endpoint;
    }

    @Override
    public PChannel channel(int id) {
        if (id == 0) {
            return this.defaultChannel;
        } else {
            synchronized (this.channels) {
                return this.channels.computeIfAbsent(id, i -> new DummyTCPChannel(this, i));
            }
        }
    }

    @Override
    public NetSession send(@NonNull Object packet, Reliability reliability) {
        this.write(new WrappedPacket(packet, Reliability.RELIABLE_ORDERED, 0));
        return this;
    }

    @Override
    public NetSession send(@NonNull Object packet, Reliability reliability, int channelId) {
        if (!this.channels.containsKey(channelId)) {
            throw new IllegalArgumentException(String.format("Unknown channel id: %d", channelId));
        }
        this.write(new WrappedPacket(packet, Reliability.RELIABLE_ORDERED, channelId));
        return this;
    }

    @Override
    public Future<Void> sendFuture(@NonNull Object packet, Reliability reliability) {
        return this.write(new WrappedPacket(packet, Reliability.RELIABLE_ORDERED, 0));
    }

    @Override
    public Future<Void> sendFuture(@NonNull Object packet, Reliability reliability, int channelId) {
        if (!this.channels.containsKey(channelId)) {
            throw new IllegalArgumentException(String.format("Unknown channel id: %d", channelId));
        }
        return this.write(new WrappedPacket(packet, Reliability.RELIABLE_ORDERED, channelId));
    }

    @Override
    public Reliability fallbackReliability() {
        return Reliability.RELIABLE_ORDERED;
    }

    @Override
    public NetSession fallbackReliability(@NonNull Reliability reliability) throws IllegalArgumentException {
        if (reliability != Reliability.RELIABLE_ORDERED) {
            throw new IllegalArgumentException(reliability.name());
        }
        return this;
    }

    @Override
    public boolean isClosed() {
        return !this.isOpen();
    }

    @Override
    public TransportEngine transportEngine() {
        return this.endpoint.transportEngine();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UserSession<T>> T userSession() {
        return (T) this.userSession;
    }

    @Override
    public void closeNow() {
        this.close().syncUninterruptibly();
    }

    @Override
    public Future<Void> closeAsync() {
        return this.close();
    }
}
