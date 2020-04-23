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

package net.daporkchop.lib.binary.stream.nio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.AbstractDataOut;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataOut} that can write to a {@link ByteBuffer}.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class BufferOut extends AbstractDataOut {
    @NonNull
    protected ByteBuffer delegate;

    @Override
    protected void write0(int b) throws IOException {
        this.delegate.put((byte) b);
    }

    @Override
    protected int writeSome0(@NonNull byte[] src, int start, int length) throws IOException {
        int count = min(this.delegate.remaining(), length);
        this.delegate.put(src, start, count);
        return count;
    }

    @Override
    protected long writeSome0(long addr, long length) throws IOException {
        int count = toInt(min(this.delegate.remaining(), length));
        int position = this.delegate.position();
        if (this.delegate.isDirect())   {
            PUnsafe.copyMemory(addr, PUnsafe.pork_directBufferAddress(this.delegate) + position, count);
        } else {
            PUnsafe.copyMemory(null, addr, this.delegate.array(), PUnsafe.ARRAY_BYTE_BASE_OFFSET + this.delegate.arrayOffset() + position, count);
        }
        this.delegate.position(position + count);
        return count;
    }

    @Override
    protected void writeAll0(@NonNull byte[] src, int start, int length) throws IOException {
        int count = min(this.delegate.remaining(), length);
        if (count < length) {
            throw new BufferOverflowException();
        }
        this.delegate.put(src, start, count);
    }

    @Override
    protected void writeAll0(long addr, long length) throws IOException {
        int count = toInt(min(this.delegate.remaining(), length));
        if (count < length) {
            throw new BufferOverflowException();
        }
        int position = this.delegate.position();
        if (this.delegate.isDirect())   {
            PUnsafe.copyMemory(addr, PUnsafe.pork_directBufferAddress(this.delegate) + position, count);
        } else {
            PUnsafe.copyMemory(null, addr, this.delegate.array(), PUnsafe.ARRAY_BYTE_BASE_OFFSET + this.delegate.arrayOffset() + position, count);
        }
        this.delegate.position(position + count);
    }

    @Override
    protected void flush0() throws IOException {
        //no-op
    }

    @Override
    protected void close0() throws IOException {
        this.delegate = null;
    }
}
