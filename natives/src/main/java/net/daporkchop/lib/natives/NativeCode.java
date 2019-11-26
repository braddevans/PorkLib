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

package net.daporkchop.lib.natives;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.function.Supplier;

/**
 * A wrapper around multiple distinct implementations of something.
 *
 * @param <T> the type of the feature to be implemented
 * @author DaPorkchop_
 */
public final class NativeCode<T> implements Supplier<T> {
    private final Impl<T> implementation;

    @SafeVarargs
    public NativeCode(@NonNull Supplier<Impl<T>>... implementationFactories)    {
        for (Supplier<Impl<T>> implementationFactory : implementationFactories) {
            Impl<T> implementation = implementationFactory.get();
            if (implementation.available()) {
                this.implementation = implementation;
                return;
            }
        }

        throw new IllegalStateException("No implementations found!");
    }

    @Override
    public T get() {
        return this.implementation.get();
    }

    /**
     * An implementation for use by {@link NativeCode}.
     *
     * @param <T> the type of the feature to be implemented
     */
    @Getter
    @Accessors(fluent = true)
    public static abstract class Impl<T> implements Supplier<T> {
        protected final boolean available = this._available();

        @Override
        public T get() {
            if (this.available) {
                return this._get();
            } else {
                throw new IllegalStateException("Not available!");
            }
        }

        protected abstract T _get();

        protected abstract boolean _available();
    }
}
