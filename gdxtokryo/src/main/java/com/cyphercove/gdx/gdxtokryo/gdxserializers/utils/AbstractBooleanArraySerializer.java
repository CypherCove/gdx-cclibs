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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public abstract class AbstractBooleanArraySerializer<T> extends Serializer<T> {

    protected abstract int numBits(T bitSource);
    protected abstract boolean get(T bitSource, int index);
    protected abstract T createObject (int numBits);
    protected abstract void set (T bitDest, int index);

    @Override
    public void write(Kryo kryo, Output output, T bitSource) {
        int numBits = numBits(bitSource);
        output.writeInt(numBits, true);
        long[] array = new long[(numBits >>> 6) + 1];
        for (int i = 0; i < numBits; i++) {
            if (get(bitSource, i)) {
                final int word = i >>> 6;
                array[word] |= 1L << (i & 0x3F);
            }
        }
        output.writeLongs(array);
    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> type) {
        int numBits = input.readInt(true);
        int length = (numBits >>> 6) + 1;
        long[] array = input.readLongs(length);
        T object = createObject(numBits);
        for (int i = 0; i < numBits; i++) {
            final int word = i >>> 6;
            if ((array[word] & (1L << (i & 0x3F))) != 0L){
                set(object, i);
            }
        }
        return object;
    }

}
