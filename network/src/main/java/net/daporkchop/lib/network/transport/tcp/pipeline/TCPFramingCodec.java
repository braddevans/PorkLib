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

package net.daporkchop.lib.network.transport.tcp.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.transport.ChanneledPacket;
import net.daporkchop.lib.network.transport.tcp.Framer;
import net.daporkchop.lib.network.transport.tcp.WrapperNioSocketChannel;
import net.daporkchop.lib.network.transport.tcp.endpoint.TCPEndpoint;

import java.util.List;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class TCPFramingCodec extends ByteToMessageCodec<ChanneledPacket<ByteBuf>> {
    @NonNull
    protected final WrapperNioSocketChannel session;

    @Override
    @SuppressWarnings("unchecked")
    protected void encode(ChannelHandlerContext ctx, ChanneledPacket<ByteBuf> msg, ByteBuf out) throws Exception {
        this.session.<TCPEndpoint>endpoint().transportEngine().framer().pack(msg, (AbstractUserSession) this.session.userSession(), out);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        this.session.<TCPEndpoint>endpoint().transportEngine().framer().unpack(msg, this.session.userSession(), out);
        if (msg.isReadable())  {
            msg.retain();
        }
    }
}
