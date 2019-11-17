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

package net.daporkchop.lib.network.nettycommon.eventloopgroup.factory;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import lombok.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * Provides instances of {@link EventLoopGroup}.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface EventLoopGroupFactory {
    default EventLoopGroup create(int threads) {
        return this.create(threads, new ThreadPerTaskExecutor(Thread::new));
    }

    default EventLoopGroup create(int threads, @NonNull ThreadFactory threadFactory) {
        return this.create(threads, new ThreadPerTaskExecutor(threadFactory));
    }

    /**
     * Creates a new {@link EventLoopGroup}.
     *
     * @param threads  the number of threads that the new {@link EventLoopGroup} will use
     * @param executor the executor that the threads will be run on
     * @return a new {@link EventLoopGroup}
     */
    EventLoopGroup create(int threads, Executor executor);
}