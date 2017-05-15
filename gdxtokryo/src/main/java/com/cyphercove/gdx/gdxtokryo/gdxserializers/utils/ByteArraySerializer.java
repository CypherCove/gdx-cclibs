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

import com.badlogic.gdx.utils.ByteArray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ByteArraySerializer extends Serializer<ByteArray> {

    @Override
    public void write(Kryo kryo, Output output, ByteArray array) {
        output.writeVarInt(array.size, true);
        output.writeBoolean(array.ordered);
        for (int i = 0; i < array.size; i++) {
            output.writeByte(array.get(i));
        }
    }

    @Override
    public ByteArray read(Kryo kryo, Input input, Class<ByteArray> type) {
        int length = input.readVarInt(true);
        boolean ordered = input.readBoolean();
        ByteArray array = new ByteArray(ordered, length);
        for (int i = 0; i < length; i++) {
            array.add(input.readByte());
        }
        return array;
    }

    @Override
    public ByteArray copy (Kryo kryo, ByteArray original) {
        ByteArray copy = new ByteArray(original.ordered, original.size);
        kryo.reference(copy);
        copy.addAll(original);
        return copy;
    }
}
