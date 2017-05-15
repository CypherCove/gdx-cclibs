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

import com.badlogic.gdx.utils.SortedIntList;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Iterator;

public class SortedIntListSerializer extends Serializer<SortedIntList> {
    private Class genericType;

    public void setGenerics (Kryo kryo, Class[] generics) {
        genericType = null;
        if (generics != null && generics.length > 0) {
            if (kryo.isFinal(generics[0])) genericType = generics[0];
        }
    }

    @Override
    public void write(Kryo kryo, Output output, SortedIntList list) {
        output.writeVarInt(list.size(), true);
        Serializer serializer = null;
        if (genericType != null) {
            if (serializer == null) serializer = kryo.getSerializer(genericType);
            genericType = null;
        }
        for (Iterator<SortedIntList.Node> iter = list.iterator(); iter.hasNext();){
            SortedIntList.Node node = iter.next();
            output.writeInt(node.index);
            if (serializer != null) {
                kryo.writeObjectOrNull(output, node.value, serializer);
            } else {
                kryo.writeClassAndObject(output, node.value);
            }
        }
    }

    @Override
    public SortedIntList read(Kryo kryo, Input input, Class<SortedIntList> type) {
        int length = input.readVarInt(true);
        SortedIntList list = new SortedIntList();
        kryo.reference(list);
        Class elementClass = null;
        Serializer serializer = null;
        if (genericType != null) {
            elementClass = genericType;
            serializer = kryo.getSerializer(genericType);
            genericType = null;
        }
        for (int i = 0; i < length; i++) {
            int index = input.readInt();
            Object value = serializer != null ?
                    kryo.readObjectOrNull(input, elementClass, serializer) : kryo.readClassAndObject(input);
            list.insert(index, value);
        }
        return list;
    }

    @Override
    public SortedIntList copy (Kryo kryo, SortedIntList original) {
        SortedIntList copy = new SortedIntList();
        for (Iterator<SortedIntList.Node> iter = original.iterator(); iter.hasNext();){
            SortedIntList.Node node = iter.next();
            copy.insert(node.index, node.value);
        }
        return copy;
    }
}
