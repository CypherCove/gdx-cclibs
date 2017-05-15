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
package com.cyphercove.gdx.gdxtokryo.gdxserializers.math;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class BezierSerializer extends Serializer<Bezier> {

    @Override
    public void write(Kryo kryo, Output output, Bezier bezier) {
        kryo.writeObjectOrNull(output, bezier.points, Array.class);
    }

    @Override
    public Bezier read(Kryo kryo, Input input, Class<Bezier> type) {
        Bezier bezier = new Bezier();
        bezier.points = kryo.readObjectOrNull(input, Array.class);
        return bezier;
    }

    @Override
    public Bezier copy (Kryo kryo, Bezier original) {
        Bezier copy = new Bezier();
        if (original.points != null){
            copy.points = new Array(original.points.size);
            for (int i = 0; i < original.points.size; i++) {
                Vector<?> vector = (Vector)original.points.get(i);
                copy.points.add(vector.cpy());
            }
        }
        return copy;
    }
}
