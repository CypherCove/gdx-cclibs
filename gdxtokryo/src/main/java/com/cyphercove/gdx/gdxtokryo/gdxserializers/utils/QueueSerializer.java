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

import com.badlogic.gdx.utils.Queue;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.lang.reflect.Field;

public class QueueSerializer extends Serializer<Queue> {
    private Class genericType;

    public void setGenerics (Kryo kryo, Class[] generics) {
        genericType = null;
        if (generics != null && generics.length > 0) {
            if (kryo.isFinal(generics[0])) genericType = generics[0];
        }
    }

    @Override
    public void write(Kryo kryo, Output output, Queue queue) {
        output.writeVarInt(queue.size, true);
        Class type = Object.class;
        try { //TODO if Queue is updated accordingly, avoid reflection
            Field valueArrayField = Queue.class.getField("values");
            type = valueArrayField.getClass().getComponentType();
        } catch (NoSuchFieldException e) {}
        kryo.writeClass(output, type);
        Serializer serializer = null;
        if (genericType != null) {
            if (serializer == null) serializer = kryo.getSerializer(genericType);
            genericType = null;
        }
        if (serializer != null) {
            for (Object element : queue)
                kryo.writeObjectOrNull(output, element, serializer);
        } else {
            for (Object element : queue)
                kryo.writeClassAndObject(output, element);
        }
    }

    @Override
    public Queue read(Kryo kryo, Input input, Class<Queue> type) {
        int length = input.readVarInt(true);
        Class cls = kryo.readClass(input).getType();
        Queue queue = new Queue(length, cls);
        kryo.reference(queue);
        Class elementClass = null;
        Serializer serializer = null;
        if (genericType != null) {
            elementClass = genericType;
            serializer = kryo.getSerializer(genericType);
            genericType = null;
        }
        if (serializer != null) {
            for (int i = 0; i < length; i++)
                queue.addLast(kryo.readObjectOrNull(input, elementClass, serializer));
        } else {
            for (int i = 0; i < length; i++)
                queue.addLast(kryo.readClassAndObject(input));
        }
        return queue;
    }

    @Override
    public Queue copy (Kryo kryo, Queue original) {
        Class type = Object.class;
        try { //TODO if Queue is updated accordingly, avoid reflection
            Field valueArrayField = Queue.class.getField("values");
            type = valueArrayField.getClass().getComponentType();
        } catch (NoSuchFieldException e) {}
        Queue copy = new Queue(original.size, type);
        kryo.reference(copy);
        for (Object obj : original)
            copy.addLast(obj);
        return copy;
    }
}
