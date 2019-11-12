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

package net.daporkchop.lib.http.request;

import io.netty.util.concurrent.Future;
import net.daporkchop.lib.http.response.Response;

/**
 * An HTTP request.
 *
 * @param <V> the type of the return value of the request
 * @author DaPorkchop_
 */
public interface Request<V> {
    /**
     * This future is updated once the remote server has responded with a status code and headers.
     *
     * @return a {@link Future} that will be notified when headers have been received
     */
    Future<Response> response();

    /**
     * This future is updated once the request has been completed with the final value obtained from the request, or marked as completed exceptionally if
     * an exception occurred while processing the request.
     *
     * @return a {@link Future} that will be notified when the request is complete
     */
    Future<V> complete();

    /**
     * Attempts to close the HTTP request.
     * <p>
     * If the request has already been completed, this method does nothing.
     *
     * @return the same {@link Future} instance as {@link #complete()}
     */
    Future<V> close();
}
