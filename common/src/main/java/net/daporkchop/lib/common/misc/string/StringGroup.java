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

package net.daporkchop.lib.common.misc.string;

import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;

/**
 * Allows applying bulk operations to a number of strings at the same time.
 *
 * @author DaPorkchop_
 */
public final class StringGroup {
    protected final char[][] values;
    protected final int      totalLength;

    StringGroup(@NonNull char[][] values) {
        this.values = values;

        int totalLength = 0;
        for (char[] arr : values) {
            if ((totalLength += arr.length) < 0) {
                throw new IllegalArgumentException("integer overflow");
            }
        }
        this.totalLength = totalLength;
    }

    /**
     * Applies title formatting to each of the strings in this {@link StringGroup}.
     *
     * @see PUnsafeStrings#titleFormat(String)
     */
    public StringGroup titleFormat() {
        for (char[] arr : this.values) {
            PUnsafeStrings.titleFormat(arr);
        }
        return this;
    }

    /**
     * Joins the individual strings in this {@link StringGroup} together using the given character as a delimiter.
     *
     * @param glue the {@code char} to put between the strings
     */
    public String join(char glue) {
        if (this.values.length == 0) {
            return "";
        } else if (this.values.length == 1) {
            return PorkUtil.wrap(this.values[0]);
        } else if (this.totalLength + this.values.length < 0) {
            throw new IllegalArgumentException("integer overflow");
        }

        char[] dst = new char[this.totalLength + this.values.length - 1];
        int i = this.values[0].length;

        System.arraycopy(this.values[0], 0, dst, 0, i);
        for (int j = 1, length = this.values.length; j < length; j++) {
            char[] arr = this.values[j];
            dst[i++] = glue;
            System.arraycopy(arr, 0, dst, i, arr.length);
            i += arr.length;
        }

        return PorkUtil.wrap(dst);
    }
}
