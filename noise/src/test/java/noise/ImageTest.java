/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package noise;


import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.graphics.color.ColorFormatBW;
import net.daporkchop.lib.graphics.color.ColorFormatRGB;
import net.daporkchop.lib.noise.NoiseSource;

import java.awt.image.BufferedImage;

import static net.daporkchop.lib.math.primitive.PMath.*;
import static noise.NoiseTests.*;

/**
 * @author DaPorkchop_
 */
public class ImageTest {
    public static void main(String... args) {
        int size = 512;
        double scale = 0.025d;

        BufferedImage img = new BufferedImage(size << 1, size, BufferedImage.TYPE_INT_ARGB);

        for (NoiseSource src : ALL_SOURCES) {
            src = src.toRange(-1.0d, 1.0d);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    double val = src.get(x * scale, y * scale);
                    if (val < -1.0d || val > 1.0d) {
                        throw new IllegalStateException(String.format("(%d,%d) (%f,%f): %f", x, y, x * scale, y * scale, val));
                    }
                    int col = val < 0.0d
                            ? lerpI(0x00, 0xFF, -val) << 16
                            : lerpI(0x00, 0xFF, val) << 8;
                    img.setRGB(x, y, ColorFormatRGB.toARGB(col));

                    img.setRGB(size + x, y, ColorFormatBW.toARGB(lerpI(0x00, 0xFF, val * 0.5d + 0.5d)));
                }
            }
            System.out.println(src);
            PorkUtil.simpleDisplayImage(true, img);
        }
    }
}