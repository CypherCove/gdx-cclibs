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

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Iterator;

public class ArrayMapSerializer extends Serializer<ArrayMap> {
    private Class keyGenericType, valueGenericType;

    public void setGenerics (Kryo kryo, Class[] generics) {
        keyGenericType = null;
        valueGenericType = null;

        if (generics != null && generics.length > 0) {
            if (generics[0] != null && kryo.isFinal(generics[0])) keyGenericType = generics[0];
            if (generics.length > 1 && generics[1] != null && kryo.isFinal(generics[1])) valueGenericType = generics[1];
        }
    }

    public void write (Kryo kryo, Output output, ArrayMap map) {
        output.writeBoolean(map.ordered);
        int length = map.size;
        output.writeVarInt(length, true);
        kryo.writeClass(output, map.keys.getClass().getComponentType());
        kryo.writeClass(output, map.values.getClass().getComponentType());

        Serializer keySerializer = null;
        if (keyGenericType != null) {
            if (keySerializer == null) keySerializer = kryo.getSerializer(keyGenericType);
            keyGenericType = null;
        }
        Serializer valueSerializer = null;
        if (valueGenericType != null) {
            if (valueSerializer == null) valueSerializer = kryo.getSerializer(valueGenericType);
            valueGenericType = null;
        }

        for (Iterator iter = map.iterator(); iter.hasNext();) {
            ObjectMap.Entry entry = (ObjectMap.Entry)iter.next();
            if (keySerializer != null) {
                kryo.writeObject(output, entry.key, keySerializer);
            } else
                kryo.writeClassAndObject(output, entry.key);
            if (valueSerializer != null) {
                kryo.writeObjectOrNull(output, entry.value, valueSerializer);
            } else
                kryo.writeClassAndObject(output, entry.value);
        }
    }

    public ArrayMap read (Kryo kryo, Input input, Class<ArrayMap> type) {
        boolean ordered = input.readBoolean();
        int length = input.readVarInt(true);
        Class knownKeyClass = kryo.readClass(input).getType();
        Class knownValueClass = kryo.readClass(input).getType();

        ArrayMap map = new ArrayMap(ordered, length, knownKeyClass, knownValueClass);

        Class keyClass = null;
        Class valueClass = null;

        Serializer keySerializer = null;
        if (keyGenericType != null) {
            keyClass = keyGenericType;
            if (keySerializer == null) keySerializer = kryo.getSerializer(keyClass);
            keyGenericType = null;
        }
        Serializer valueSerializer = null;
        if (valueGenericType != null) {
            valueClass = valueGenericType;
            if (valueSerializer == null) valueSerializer = kryo.getSerializer(valueClass);
            valueGenericType = null;
        }

        kryo.reference(map);

        for (int i = 0; i < length; i++) {
            Object key;
            if (keySerializer != null) {
                key = kryo.readObject(input, keyClass, keySerializer);
            } else
                key = kryo.readClassAndObject(input);
            Object value;
            if (valueSerializer != null) {
                value = kryo.readObjectOrNull(input, valueClass, valueSerializer);
            } else
                value = kryo.readClassAndObject(input);
            map.put(key, value);
        }
        return map;
    }

    public ArrayMap copy (Kryo kryo, ArrayMap original) {
        ArrayMap map = new ArrayMap(original);
        kryo.reference(map);
        return map;
    }
}
