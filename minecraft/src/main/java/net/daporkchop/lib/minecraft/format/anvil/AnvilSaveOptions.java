/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.format.anvil;

import io.netty.buffer.ByteBufAllocator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.util.WriteAccess;

import java.util.concurrent.Executor;

/**
 * Extension of {@link SaveOptions} for the Anvil save format.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class AnvilSaveOptions extends SaveOptions {
    public AnvilSaveOptions(@NonNull SaveOptions other) {
        super(other);

        if (other instanceof AnvilSaveOptions) {
            this.copyFrom((AnvilSaveOptions) other);
        }
    }

    public AnvilSaveOptions(@NonNull AnvilSaveOptions other) {
        super(other);

        this.copyFrom(other);
    }

    private void copyFrom(@NonNull AnvilSaveOptions other) {
    }

    @Override
    public AnvilSaveOptions access(@NonNull WriteAccess access) {
        super.access(access);
        return this;
    }

    @Override
    public AnvilSaveOptions ioExecutor(@NonNull Executor ioExecutor) {
        super.ioExecutor(ioExecutor);
        return this;
    }

    @Override
    public AnvilSaveOptions nettyAlloc(@NonNull ByteBufAllocator nettyAlloc) {
        super.nettyAlloc(nettyAlloc);
        return this;
    }

    @Override
    public AnvilSaveOptions byteAlloc(ArrayAllocator<byte[]> byteAlloc) {
        super.byteAlloc(byteAlloc);
        return this;
    }

    @Override
    public AnvilSaveOptions intAlloc(ArrayAllocator<int[]> intAlloc) {
        super.intAlloc(intAlloc);
        return this;
    }

    @Override
    public AnvilSaveOptions longAlloc(ArrayAllocator<long[]> longAlloc) {
        super.longAlloc(longAlloc);
        return this;
    }

    @Override
    public AnvilSaveOptions clone() {
        return new AnvilSaveOptions(this);
    }
}