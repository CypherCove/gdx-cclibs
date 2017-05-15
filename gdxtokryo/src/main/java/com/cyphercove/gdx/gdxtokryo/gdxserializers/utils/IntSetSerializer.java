/*******************************************************************************
 * Copyright 2017 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.cyphercove.gdx.gdxtokryo.gdxserializers.utils;

import com.badlogic.gdx.utils.IntSet;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class IntSetSerializer extends Serializer<IntSet> {

    @Override
    public void write(Kryo kryo, Output output, IntSet set) {
        output.writeVarInt(set.size, true);
        IntSet.IntSetIterator iter = set.iterator();
        while (iter.hasNext) {
            output.writeInt(iter.next());
        }
    }

    @Override
    public IntSet read(Kryo kryo, Input input, Class<IntSet> type) {
        int length = input.readVarInt(true);
        IntSet array = new IntSet(length);
        for (int i = 0; i < length; i++) {
            array.add(input.readInt());
        }
        return array;
    }

    @Override
    public IntSet copy (Kryo kryo, IntSet original) {
        IntSet copy = new IntSet(original.size);
        copy.addAll(original);
        return copy;
    }
}
