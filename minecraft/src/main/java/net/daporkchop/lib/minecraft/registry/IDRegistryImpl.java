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

package net.daporkchop.lib.minecraft.registry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.primitive.lambda.ObjIntConsumer;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A simple implementation of {@link IDRegistry}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class IDRegistryImpl implements IDRegistry {
    @Getter(AccessLevel.NONE)
    protected final ResourceLocation[] contents;
    protected final ResourceLocation name;

    protected final int size;

    public IDRegistryImpl(@NonNull ResourceLocation[] contents, ResourceLocation name)  {
        this.contents = contents;
        this.name = name;

        this.size = Arrays.stream(contents).filter(Objects::nonNull).mapToInt(resourceLocation -> 1).sum();
    }

    @Override
    public void forEach(@NonNull Consumer<ResourceLocation> callback) {
        for (int i = 0, size = this.size; i < size; i++)  {
            ResourceLocation location = this.contents[i];
            if (location != null)   {
                callback.accept(location);
            }
        }
    }

    @Override
    public void forEach(@NonNull ObjIntConsumer<ResourceLocation> callback) {
        for (int i = 0, size = this.size; i < size; i++)  {
            ResourceLocation location = this.contents[i];
            if (location != null)   {
                callback.accept(location, i);
            }
        }
    }

    @Override
    public ResourceLocation lookup(int id) {
        return id >= 0 && id < this.size ? this.contents[id] : null;
    }

    @Override
    public int lookup(@NonNull ResourceLocation name) {
        int hashCode = name.hashCode();
        for (int i = 0, size = this.size; i < size; i++)  {
            ResourceLocation location = this.contents[i];
            if (location != null && location.hashCode() == hashCode && location.equals(name))   {
                return i;
            }
        }
        return -1;
    }
}
