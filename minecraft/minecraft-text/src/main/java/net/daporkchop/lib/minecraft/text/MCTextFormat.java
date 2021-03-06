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

package net.daporkchop.lib.minecraft.text;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.common.misc.string.PUnsafeStrings;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * All format+color codes from the legacy formatting system.
 * <p>
 * See https://minecraft.gamepedia.com/Formatting_codes for more information.
 *
 * @author DaPorkchop_
 * @see MCTextType#LEGACY
 */
@Getter
@Accessors(fluent = true)
public enum MCTextFormat {
    // color codes
    BLACK('0', Color.BLACK, Color.BLACK),
    DARK_BLUE('1', 0x0000AA, 0x00002A),
    DARK_GREEN('2', 0x00AA00, 0x002A00),
    DARK_AQUA('3', 0x00AAAA, 0x002A2A),
    DARK_RED('4', 0xAA0000, 0x2A0000),
    DARK_PURPLE('5', 0xAA00AA, 0x2A002A),
    GOLD('6', 0xFFAA00, 0x2A2A00),
    GRAY('7', 0xAAAAAA, 0x2A2A2A),
    DARK_GRAY('8', 0x555555, 0x151515),
    BLUE('9', 0x5555FF, 0x15153F),
    GREEN('a', 0x55FF55, 0x153F15),
    AQUA('b', 0x55FFFF, 0x153F3F),
    RED('c', 0xFF5555, 0x3F1515),
    LIGHT_PURPLE('d', 0xFF55FF, 0x3F153F),
    YELLOW('e', 0xFFFF55, 0x3F3F15),
    WHITE('f', Color.WHITE, new Color(0x3F3F3F)),
    // format codes
    OBFUSCATED('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALIC('o'),
    RESET('r'),;

    public static final  Pattern      CLEAN_PATTERN       = Pattern.compile("§[0-9a-fk-or]", Pattern.CASE_INSENSITIVE);
    private static final Ref<Matcher> CLEAN_MATCHER_CACHE = ThreadRef.soft(() -> CLEAN_PATTERN.matcher(""));

    public static final MCTextFormat[] VALUES = values();
    public static final MCTextFormat[] COLORS = Arrays.copyOfRange(VALUES, 0, WHITE.ordinal() + 1);

    private static final MCTextFormat[] CODE_LOOKUP       = new MCTextFormat['r' + 1];
    private static final MCTextFormat[] CODE_COLOR_LOOKUP = new MCTextFormat['r' + 1];

    private static final Map<String, MCTextFormat> NAME_LOOKUP       = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static final Map<String, MCTextFormat> NAME_COLOR_LOOKUP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        for (MCTextFormat format : VALUES) {
            CODE_LOOKUP[Character.toLowerCase(format.code)] = format;
            CODE_LOOKUP[Character.toUpperCase(format.code)] = format;

            NAME_LOOKUP.put(format.name(), format);

            if (format.hasColor()) {
                if (!format.hasBgColor()) {
                    throw new IllegalStateException(format.name());
                }

                CODE_COLOR_LOOKUP[Character.toLowerCase(format.code)] = format;
                CODE_COLOR_LOOKUP[Character.toUpperCase(format.code)] = format;

                NAME_COLOR_LOOKUP.put(format.name(), format);
            }
        }
    }

    /**
     * Strips all formatting codes from the given input text.
     *
     * @param input the {@link CharSequence} to clean
     * @return the cleaned text
     */
    public static String clean(@NonNull CharSequence input) {
        return CLEAN_MATCHER_CACHE.get().reset(input).replaceAll("");
    }

    /**
     * Gets the {@link MCTextFormat} whose color is most similar to the given {@link Color}.
     *
     * @param color the {@link Color} to find a match for
     * @return the {@link MCTextFormat} whose color is most similar to the given {@link Color}
     */
    public static MCTextFormat closestTo(Color color) {
        return color == null ? null : closestTo(color, color.getRGB());
    }

    /**
     * Gets the {@link MCTextFormat} whose color is most similar to the given RGB color.
     *
     * @param rgb the RGB color to find a match for
     * @return the {@link MCTextFormat} whose color is most similar to the given color
     */
    public static MCTextFormat closestTo(int rgb) {
        return closestTo(null, rgb);
    }

    private static MCTextFormat closestTo(final Color color, final int rgb) {
        MCTextFormat closest = null;
        int closestDist = 1 << 30;

        for (MCTextFormat format : COLORS) {
            if (color == format.awtColor) {
                //if the colors match at an identity level, blindly accept it
                return format;
            }

            int vR = ((rgb >>> 16) & 0xFF) - ((format.color >>> 16) & 0xFF);
            int vG = ((rgb >>> 8) & 0xFF) - ((format.color >>> 8) & 0xFF);
            int vB = (rgb & 0xFF) - (format.color & 0xFF);
            int dist = vR * vR + vG * vG + vB * vB; //distanceSq between the two colors

            if (dist < closestDist) {
                closestDist = dist;
                closest = format;
            }
        }

        return closest;
    }

    /**
     * Finds the {@link MCTextFormat} with the given formatting code.
     *
     * @param code the formatting code to search for
     * @return the {@link MCTextFormat} with the given formatting code, or {@code null} if none could be found
     */
    public static MCTextFormat lookup(char code) {
        return code <= 'r' ? CODE_LOOKUP[code] : null;
    }

    /**
     * Finds the {@link MCTextFormat} with the given color (not formatting!) code.
     *
     * @param code the color code to search for
     * @return the {@link MCTextFormat} with the given color code, or {@code null} if none could be found
     */
    public static MCTextFormat lookupColor(char code) {
        return code <= 'r' ? CODE_COLOR_LOOKUP[code] : null;
    }

    /**
     * Finds the {@link MCTextFormat} with the given name.
     *
     * @param name the name of the formatting code to search for
     * @return the {@link MCTextFormat} with the given name, or {@code null} if none could be found
     */
    public static MCTextFormat lookup(@NonNull String name) {
        MCTextFormat format;
        if (name.length() == 1 && (format = lookup(name.charAt(0))) != null)    {
            return format;
        }
        return NAME_LOOKUP.get(name);
    }

    /**
     * Finds the {@link MCTextFormat} with the given name.
     *
     * @param name the name of the color code to search for
     * @return the {@link MCTextFormat} with the given name, or {@code null} if none could be found
     */
    public static MCTextFormat lookupColor(@NonNull String name) {
        MCTextFormat format;
        if (name.length() == 1 && (format = lookupColor(name.charAt(0))) != null)    {
            return format;
        }
        return NAME_COLOR_LOOKUP.get(name);
    }

    /**
     * The in-game color of this formatting code, as an AWT {@link Color}.
     * <p>
     * If {@code null}, this formatting code has no color.
     *
     * @see #color
     */
    protected final Color awtColor;

    /**
     * The in-game background color of this formatting code, as an AWT {@link Color}.
     * <p>
     * Used for drawing the text shadow in-game.
     * <p>
     * If {@code null}, this formatting code has no background color.
     *
     * @see #bgColor
     */
    protected final Color awtBgColor;

    /**
     * The in-game ARGB color of this formatting code.
     * <p>
     * If not fully opaque (i.e. {@code (~color & 0xFF000000) != 0}, this formatting code has no color.
     */
    protected final int color;

    /**
     * The in-game ARGB background color of this formatting code.
     * <p>
     * Used for drawing the text shadow in-game.
     * <p>
     * If not fully opaque (i.e. {@code (~bgColor & 0xFF000000) != 0}, this formatting code has no background color.
     */
    protected final int bgColor;

    /**
     * The single-letter identifier for this formatting code.
     */
    protected final char code;

    MCTextFormat(char code, Color color, Color bgColor) {
        this.color = (this.awtColor = color) != null ? (color.getRGB() | 0xFF000000) : 0;
        this.bgColor = (this.awtBgColor = bgColor) != null ? (color.getRGB() | 0xFF000000) : 0;
        this.code = code;

        PUnsafeStrings.setEnumName(this, this.name().toLowerCase());
    }

    MCTextFormat(char code, int color, int bgColor) {
        this(code, new Color(color), new Color(bgColor));
    }

    MCTextFormat(char code) {
        this(code, null, null);
    }

    /**
     * @return whether or not this formatting code has a text color
     */
    public boolean hasColor() {
        return this.awtColor != null;
    }

    /**
     * @return whether or not this formatting code has a background color
     */
    public boolean hasBgColor() {
        return this.awtBgColor != null;
    }
}
