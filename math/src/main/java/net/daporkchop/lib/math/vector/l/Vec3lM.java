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

package net.daporkchop.lib.math.vector.l;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A 3-dimensional vector.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@Setter
public final class Vec3lM implements LongVector3 {
    protected long x;
    protected long y;
    protected long z;

    @Override
    public LongVector3 add(long x, long y, long z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    public LongVector3 subtract(long x, long y, long z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    @Override
    public LongVector3 multiply(long x, long y, long z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    @Override
    public LongVector3 divide(long x, long y, long z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LongVector3)) {
            return false;
        }

        LongVector3 vec = (LongVector3) obj;
        return this.x == vec.getX() && this.y == vec.getY() && this.z == vec.getZ();
    }

    @Override
    public int hashCode() {
        long l = (this.x * 611573530454211019L + this.y) * 32185023686116541L + this.z;
        return (int) (l ^ (l >>> 32L));
    }

    @Override
    public String toString() {
        return String.format("Vec3lM(%d,%d,%d)", this.x, this.y, this.z);
    }
}
