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

import com.badlogic.gdx.utils.ObjectSet;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ObjectSetSerializer<T extends ObjectSet> extends Serializer<T> {
    private Class genericType;

    public void setGenerics (Kryo kryo, Class[] generics) {
        genericType = null;
        if (generics != null && generics.length > 0) {
            if (kryo.isFinal(generics[0])) genericType = generics[0];
        }
    }

    @Override
    public void write(Kryo kryo, Output output, T objectSet) {
        int length = objectSet.size;
        output.writeVarInt(length, true);
        kryo.writeClass(output, Object.class); // in case future version of ObjectSet supports type awareness
        Serializer serializer = null;
        if (genericType != null) {
            if (serializer == null) serializer = kryo.getSerializer(genericType);
            genericType = null;
        }
        if (serializer != null) {
            for (Object element : objectSet)
                kryo.writeObject(output, element, serializer);
        } else {
            for (Object element : objectSet)
                kryo.writeClassAndObject(output, element);
        }
    }

    protected T create (int capacity) {
        return (T)new ObjectSet(capacity);
    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> type) {
        int length = input.readVarInt(true);
        kryo.readClass(input); // currently unused
        T objectSet = create(length);

        kryo.reference(objectSet);

        Class elementClass = null;
        Serializer serializer = null;
        if (genericType != null) {
            elementClass = genericType;
            serializer = kryo.getSerializer(genericType);
            genericType = null;
        }
        if (serializer != null) {
            for (int i = 0; i < length; i++)
                objectSet.add(kryo.readObject(input, elementClass, serializer));
        } else {
            for (int i = 0; i < length; i++)
                objectSet.add(kryo.readClassAndObject(input));
        }
        return objectSet;
    }

    @Override
    public T copy (Kryo kryo, T original) {
        T copy = create(original.size);
        kryo.reference(copy);
        copy.addAll(original);
        return copy;
    }
}
