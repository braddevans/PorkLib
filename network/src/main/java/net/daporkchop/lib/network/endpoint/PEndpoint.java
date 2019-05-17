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

package net.daporkchop.lib.network.endpoint;

import lombok.NonNull;
import net.daporkchop.lib.network.EndpointType;
import net.daporkchop.lib.network.protocol.Protocol;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.util.CloseableFuture;
import net.daporkchop.lib.network.util.TransportEngineHolder;

/**
 * An endpoint is one of the ends on a connection. Connections consist of two endpoints, one local one and
 * a remote one.
 *
 * @author DaPorkchop_
 */
public interface PEndpoint<Impl extends PEndpoint<Impl, S>, S extends AbstractUserSession<S>> extends CloseableFuture, TransportEngineHolder {
    /**
     * @return this endpoint's type
     */
    @NonNull
    EndpointType type();

    /**
     * Closes this endpoint, blocking until it is closed.
     * <p>
     * Closing an endpoint will result in all connections associated with it being closed.
     */
    @Override
    void closeNow();

    /**
     * @return the default protocol that will be used initially for all connections to and from this endpoint
     */
    Protocol<S> protocol();
}
