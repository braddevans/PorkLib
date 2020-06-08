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

package net.daporkchop.lib.minecraft.format.anvil.version.codec.chunk;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.compat.datafix.DataCodec;
import net.daporkchop.lib.compat.datafix.DataFixer;
import net.daporkchop.lib.compat.datafix.ParameterizedDataCodec;
import net.daporkchop.lib.minecraft.format.anvil.AnvilWorld;
import net.daporkchop.lib.minecraft.format.anvil.AnvilWorldStorage;
import net.daporkchop.lib.minecraft.format.anvil.storage.HeapLegacyBlockStorage;
import net.daporkchop.lib.minecraft.format.anvil.chunk.AnvilChunk;
import net.daporkchop.lib.minecraft.format.common.DefaultSection;
import net.daporkchop.lib.minecraft.format.common.storage.BlockStorage;
import net.daporkchop.lib.minecraft.format.common.nibble.HeapNibbleArray;
import net.daporkchop.lib.minecraft.format.common.nibble.NibbleArray;
import net.daporkchop.lib.minecraft.format.common.vanilla.VanillaChunk;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.WorldStorage;
import net.daporkchop.lib.nbt.tag.ByteArrayTag;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * Codec for serialization of chunks in the pre-flattening format used by Minecraft versions 1.12.2 and older.
 *
 * @author DaPorkchop_
 */
public class LegacyChunkCodec implements ParameterizedDataCodec<Chunk, CompoundTag, World> {
    public static final JavaVersion VERSION = JavaVersion.fromName("1.12.2");

    @Override
    public Chunk decode(@NonNull CompoundTag root, @NonNull World param) {
        CompoundTag level = root.getCompound("Level");
        int x = level.getInt("xPos");
        int z = level.getInt("zPos");

        Section[] sections = new Section[16];
        Chunk1_12_2 chunk = new Chunk1_12_2(param.retain(), x, z, sections);
        DataFixer<Section, CompoundTag, JavaVersion> sectionFixer = ((AnvilWorldStorage) param.storage()).sectionFixer();
        for (CompoundTag sectionTag : level.getList("Sections", CompoundTag.class)) {
            Section section = sectionFixer.decode(sectionTag, VERSION, chunk);
            sections[section.y()] = section;
        }
        return chunk;
    }

    @Override
    public CompoundTag encode(@NonNull Chunk value, @NonNull World param) {
        throw new UnsupportedOperationException(); //TODO
    }

    protected static class Chunk1_12_2 extends VanillaChunk implements AnvilChunk {
        public Chunk1_12_2(World parent, int x, int z, @NonNull Section[] sections) {
            super(parent, x, z, sections);
        }

        @Override
        protected Section createEmptySection(int y) {
            ArrayAllocator<byte[]> alloc = this.parent.parent().options().byteAlloc();
            return new DefaultSection(this, y,
                    new HeapLegacyBlockStorage(this.blockRegistry, alloc.atLeast(4096), alloc.atLeast(2048)), //TODO: decide when to use the Add layer
                    new HeapNibbleArray.YZX(alloc.atLeast(2048)),
                    new HeapNibbleArray.YZX(alloc.atLeast(2048))); //TODO: only use sunlight when needed
        }
    }
}
