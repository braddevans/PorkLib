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

package net.daporkchop.lib.binary.netty.buf;

import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteBuffer;

/**
 * Wraps a {@link ByteBuffer} and releases it directly, rather than releasing a slice.
 *
 * @author DaPorkchop_
 */
public final class FreeingWrappedUnpooledUnsafeDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
    private static final long DONOTFREE_OFFSET = PUnsafe.pork_getOffset(UnpooledUnsafeDirectByteBuf.class, "doNotFree");

    protected final ByteBuffer theActualBuffer;

    public FreeingWrappedUnpooledUnsafeDirectByteBuf(@NonNull ByteBuffer theActualBuffer, ByteBuffer buffer, int size) {
        super(UnpooledByteBufAllocator.DEFAULT, buffer, size);

        PUnsafe.putBoolean(this, DONOTFREE_OFFSET, false);

        this.theActualBuffer = theActualBuffer;
    }

    @Override
    protected void freeDirect(ByteBuffer buffer) {
        PorkUtil.release(this.theActualBuffer);
    }
}
