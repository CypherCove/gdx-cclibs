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

import com.badlogic.gdx.utils.IntFloatMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Iterator;

public class IntFloatMapSerializer extends Serializer<IntFloatMap> {

    public void write (Kryo kryo, Output output, IntFloatMap map) {
        int length = map.size;
        output.writeVarInt(length, true);
        for (Iterator iter = map.iterator(); iter.hasNext();) {
            IntFloatMap.Entry entry = (IntFloatMap.Entry)iter.next();
            output.writeInt(entry.key);
            output.writeFloat(entry.value);
        }
    }

    public IntFloatMap read (Kryo kryo, Input input, Class<IntFloatMap> type) {
        int length = input.readVarInt(true);
        IntFloatMap map = new IntFloatMap(length);

        for (int i = 0; i < length; i++) {
            int key = input.readInt();
            float value = input.readFloat();
            map.put(key, value);
        }
        return map;
    }

    public IntFloatMap copy (Kryo kryo, IntFloatMap original) {
        IntFloatMap copy = new IntFloatMap(original.size);
        kryo.reference(copy);
        copy.putAll(original);
        return copy;
    }
}
