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

package net.daporkchop.lib.minecraft.tileentity.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.tileentity.TileEntityBase;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * Represents an unknown (unregistered) tile entity.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class UnknownTileEntity extends TileEntityBase {
    protected ResourceLocation id;
    protected CompoundTag data;

    @Override
    protected void doInit(@NonNull CompoundTag nbt) {
        this.data = nbt; //TODO: some way to make immutable tags? i should probably rewrite NBT lib to use interfaces for each tag type
        this.id = new ResourceLocation(nbt.getString("id"));
    }

    @Override
    protected void doDeinit() {
        this.id = null;
        this.data = null;
    }

    @Override
    public CompoundTag save() {
        return this.data;
    }

    @Override
    protected boolean isValidId(@NonNull String id) {
        //accept everything, it really doesn't matter (as long as it's not null)
        return true;
    }

    @Override
    public boolean dirty() {
        return false;
    }

    @Override
    public boolean markDirty() {
        throw new UnsupportedOperationException("markDirty");
    }

    @Override
    protected void resetDirty() {
        throw new UnsupportedOperationException("resetDirty");
    }

    @Override
    protected boolean checkAndResetDirty() {
        throw new UnsupportedOperationException("checkAndResetDirty");
    }
}
