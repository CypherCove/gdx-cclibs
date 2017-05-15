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

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ArraySerializer<T extends Array> extends Serializer<T> {
    private Class genericType;

    public void setGenerics (Kryo kryo, Class[] generics) {
        genericType = null;
        if (generics != null && generics.length > 0) {
            if (kryo.isFinal(generics[0])) genericType = generics[0];
        }
    }

    @Override
    public void write(Kryo kryo, Output output, T array) {
        output.writeVarInt(array.size, true);
        output.writeBoolean(array.ordered);
        kryo.writeClass(output, array.items.getClass().getComponentType());
        Serializer serializer = null;
        if (genericType != null) {
            if (serializer == null) serializer = kryo.getSerializer(genericType);
            genericType = null;
        }
        if (serializer != null) {
            for (Object element : array)
                kryo.writeObjectOrNull(output, element, serializer);
        } else {
            for (Object element : array)
                kryo.writeClassAndObject(output, element);
        }
    }

    protected T create (boolean ordered, int capacity, Class type) {
        return (T)(new Array(ordered, capacity, type));
    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> type) {
        int length = input.readVarInt(true);
        boolean ordered = input.readBoolean();
        Class cls = kryo.readClass(input).getType();
        T array = create(ordered, length, cls);
        kryo.reference(array);
        Class elementClass = null;
        Serializer serializer = null;
        if (genericType != null) {
            elementClass = genericType;
            serializer = kryo.getSerializer(genericType);
            genericType = null;
        }
        if (serializer != null) {
            for (int i = 0; i < length; i++)
                array.add(kryo.readObjectOrNull(input, elementClass, serializer));
        } else {
            for (int i = 0; i < length; i++)
                array.add(kryo.readClassAndObject(input));
        }
        return array;
    }

    @Override
    public T copy (Kryo kryo, T original) {
        Class cls = original.items.getClass().getComponentType();
        T copy = create(original.ordered, original.size, cls);
        kryo.reference(copy);
        copy.addAll(original);
        return copy;
    }
}
