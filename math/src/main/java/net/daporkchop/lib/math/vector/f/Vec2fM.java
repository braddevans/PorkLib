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

package net.daporkchop.lib.math.vector.f;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * A 2-dimensional vector.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@Setter
public final class Vec2fM implements FloatVector2 {
    protected float x;
    protected float y;

    @Override
    public FloatVector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public FloatVector2 subtract(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    @Override
    public FloatVector2 multiply(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    @Override
    public FloatVector2 divide(float x, float y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FloatVector2)) {
            return false;
        }

        FloatVector2 vec = (FloatVector2) obj;
        return this.x == vec.getX() && this.y == vec.getY();
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.x) * 926373397 + Float.floatToIntBits(this.y);
    }

    @Override
    public String toString() {
        return String.format("Vec2fM(%f,%f)", this.x, this.y);
    }
}
