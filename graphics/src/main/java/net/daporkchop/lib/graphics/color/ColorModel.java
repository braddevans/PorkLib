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

package net.daporkchop.lib.graphics.color;

/**
 * A method of encoding colors.
 *
 * @author DaPorkchop_
 */
public interface ColorModel {
    ColorModelRGB RGB = new ColorModelRGB();
    ColorModelARGB ARGB = new ColorModelARGB();
    ColorModelBW BW = new ColorModelBW();
    ColorModelABW ABW = new ColorModelABW();

    /**
     * Decodes the given color into ARGB.
     * <p>
     * If this {@link ColorModel} doesn't contain an alpha channel, the alpha value should be set to {@code 0xFF} (fully opaque).
     *
     * @param color the color to decode
     * @return the color as a standard ARGB color
     */
    int decode(long color);

    /**
     * Encodes the given ARGB color into this color model's format.
     * <p>
     * If this {@link ColorModel} doesn't contain an alpha channel, implementations are permitted to silently discard the input
     * value's alpha level.
     *
     * @param argb the ARGB color to encode
     * @return the encoded color
     */
    long encode(int argb);

    /**
     * @return the number of bits that are used by this color model
     */
    int encodedBits();

    /**
     * @return whether or not this {@link ColorModel} contains an alpha channel
     */
    boolean alpha();
}
