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

import com.badlogic.gdx.utils.IdentityMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Iterator;

public class IdentityMapSerializer extends Serializer<IdentityMap> {
    private Class keyGenericType, valueGenericType;

    public void setGenerics (Kryo kryo, Class[] generics) {
        keyGenericType = null;
        valueGenericType = null;

        if (generics != null && generics.length > 0) {
            if (generics[0] != null && kryo.isFinal(generics[0])) keyGenericType = generics[0];
            if (generics.length > 1 && generics[1] != null && kryo.isFinal(generics[1])) valueGenericType = generics[1];
        }
    }

    public void write (Kryo kryo, Output output, IdentityMap map) {
        int length = map.size;
        output.writeVarInt(length, true);
        kryo.writeClass(output, Object.class); // in case future version of IdentityMap supports type awareness
        kryo.writeClass(output, Object.class); // in case future version of IdentityMap supports type awareness

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
            IdentityMap.Entry entry = (IdentityMap.Entry)iter.next();
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

    public IdentityMap read (Kryo kryo, Input input, Class<IdentityMap> type) {
        int length = input.readVarInt(true);
        kryo.readClass(input); // currently unused
        kryo.readClass(input); // currently unused
        IdentityMap map = new IdentityMap(length);

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

    public IdentityMap copy (Kryo kryo, IdentityMap original) {
        IdentityMap copy = new IdentityMap(original.size);
        kryo.reference(copy);
        for (Object obj : original){
            IdentityMap.Entry entry = (IdentityMap.Entry)obj;
            copy.put(entry.key, entry.value);
        }
        return copy;
    }
}
