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

package net.daporkchop.lib.network.session;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.transport.TransportEngine;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractUserSession<S extends AbstractUserSession<S>> implements UserSession<S, S> {
    private final NetSession<S> internalSession = null;

    /**
     * Fired when this session is opened (i.e. an opened event reaches the end of the pipeline).
     */
    public void onOpened() {
    }

    /**
     * Fired when this session is closed (i.e. a closed event reaches the end of the pipeline).
     */
    public void onClosed() {
    }

    /**
     * Fired when an exception is caught while updating this session.
     *
     * @param t the exception that was caught
     */
    public void onException(@NonNull Throwable t) {
        Logging.logger.alert(new RuntimeException(t));
        this.closeAsync();
    }

    /**
     * Fired if a message reaches the end of the pipeline.
     * <p>
     * Many protocol implementations will never allow packets to get this far down the pipeline, this method is here
     * mainly to serve as a sort of backup handling option.
     *
     * @param msg     the message that was received
     * @param channel the channel that the message was received on
     */
    public void onReceived(@NonNull Object msg, int channel) {
    }

    /**
     * Fired if raw binary data reaches the end of the pipeline.
     * <p>
     * Whether or not a certain message qualifies as binary or not depends on the transport engine.
     * <p>
     * Many protocol implementations will never allow packets to get this far down the pipeline, this method is here
     * mainly to serve as a sort of backup handling option.
     *
     * @param in      a {@link DataIn} to read data from
     * @param channel the channel that the data was received on
     */
    public void onBinary(@NonNull DataIn in, int channel) throws IOException {
    }

    //
    //
    // finalize implementatation methods for UserSession
    //
    //
    @Override
    public final <E extends PEndpoint<E, S>> E endpoint() {
        return this.internalSession().endpoint();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final S send(@NonNull Object packet, Reliability reliability) {
        this.internalSession().send(packet, reliability);
        return (S) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final S send(@NonNull Object packet, Reliability reliability, int channel) {
        this.internalSession().send(packet, reliability, channel);
        return (S) this;
    }

    @Override
    public final Promise sendAsync(@NonNull Object packet, Reliability reliability) {
        return this.internalSession().sendAsync(packet, reliability);
    }

    @Override
    public final Promise sendAsync(@NonNull Object packet, Reliability reliability, int channel) {
        return this.internalSession().sendAsync(packet, reliability, channel);
    }

    @Override
    public final DataOut writer() {
        return this.internalSession().writer();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final S flushBuffer() {
        this.internalSession().flushBuffer();
        return (S) this;
    }

    @Override
    public final Reliability fallbackReliability() {
        return this.internalSession().fallbackReliability();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final S fallbackReliability(@NonNull Reliability reliability) throws IllegalArgumentException {
        this.internalSession().fallbackReliability(reliability);
        return (S) this;
    }

    @Override
    public final Promise closeAsync() {
        return this.internalSession().closeAsync();
    }

    @Override
    public final TransportEngine transportEngine() {
        return this.internalSession().transportEngine();
    }
}
