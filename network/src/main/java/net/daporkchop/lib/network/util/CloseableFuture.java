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

package net.daporkchop.lib.network.util;

import io.netty.util.concurrent.Future;

/**
 * A resource that may be closed both synchronously and asynchronously.
 *
 * @author DaPorkchop_
 */
public interface CloseableFuture {
    /**
     * Checks whether or not this resource has been closed or is currently closing.
     *
     * @return whether or not this resource has been closed or is currently closing
     */
    boolean isClosed();

    /**
     * Checks whether or not this resource is currently open.
     *
     * @return whether or not this resource is currently open
     */
    default boolean isOpen() {
        return !this.isClosed();
    }

    /**
     * Closes this resource, blocking until it is closed.
     */
    void closeNow();

    /**
     * Closes this resource at some point in the future.
     *
     * @return a future that will be completed once this resource is closed
     * @see #closeNow()
     */
    Future<Void> closeAsync();
}
