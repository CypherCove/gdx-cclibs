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

import com.badlogic.gdx.utils.IntArray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class IntArraySerializer extends Serializer<IntArray> {

    @Override
    public void write(Kryo kryo, Output output, IntArray array) {
        output.writeVarInt(array.size, true);
        output.writeBoolean(array.ordered);
        for (int i = 0; i < array.size; i++) {
            output.writeInt(array.get(i));
        }
    }

    @Override
    public IntArray read(Kryo kryo, Input input, Class<IntArray> type) {
        int length = input.readVarInt(true);
        boolean ordered = input.readBoolean();
        IntArray array = new IntArray(ordered, length);
        for (int i = 0; i < length; i++) {
            array.add(input.readInt());
        }
        return array;
    }

    @Override
    public IntArray copy (Kryo kryo, IntArray original) {
        IntArray copy = new IntArray(original.ordered, original.size);
        kryo.reference(copy);
        copy.addAll(original);
        return copy;
    }
}
