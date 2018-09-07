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

package net.daporkchop.lib.gdxnetwork.endpoint.server;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.ByteBufferOutputStream;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.gdxnetwork.endpoint.Endpoint;
import net.daporkchop.lib.gdxnetwork.packet.Packet;
import net.daporkchop.lib.gdxnetwork.protocol.PacketProtocol;
import net.daporkchop.lib.gdxnetwork.protocol.encapsulated.EncapsulatedPacket;
import net.daporkchop.lib.gdxnetwork.protocol.encapsulated.WrappedPacket;
import net.daporkchop.lib.gdxnetwork.session.Session;
import net.daporkchop.lib.gdxnetwork.util.CryptHelper;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @author DaPorkchop_
 */
@Getter
public class SessionServer extends Session {
    private final NetServer server;
    private final WebSocket webSocket;

    public SessionServer(CryptHelper cryptHelper, PacketProtocol protocol, @NonNull NetServer server, @NonNull WebSocket webSocket) {
        super(cryptHelper, protocol);
        this.server = server;
        this.webSocket = webSocket;
    }

    @Override
    public Endpoint getEndpoint() {
        return this.server;
    }

    @Override
    public void send(@NonNull Packet packet) {
        try {
            if (!(packet instanceof EncapsulatedPacket)) {
                packet = new WrappedPacket(packet);
            }
            ByteBuffer buffer = ByteBuffer.allocate(packet.getDataLength() + 1);
            OutputStream os = new ByteBufferOutputStream(buffer);
            os = this.getCryptHelper().wrap(os);
            os.write(packet.getId());
            DataOut dataOut = new DataOut(os);
            packet.encode(dataOut);
            dataOut.close();
            this.webSocket.send(buffer.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.webSocket.getRemoteSocketAddress();
    }

    @Override
    public void disconnect(String reason) {
        this.webSocket.close(CloseFrame.NORMAL, reason);
    }

    @Override
    public boolean isConnected() {
        return this.webSocket.isOpen();
    }
}
