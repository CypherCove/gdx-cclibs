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

import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class PlaneSerializer extends Serializer<Plane> {
    @Override
    public void write(Kryo kryo, Output output, Plane plane) {
        Vector3 normal = plane.normal;
        output.writeFloat(normal.x);
        output.writeFloat(normal.y);
        output.writeFloat(normal.z);
        output.writeFloat(plane.d);
    }

    @Override
    public Plane read(Kryo kryo, Input input, Class<Plane> type) {
        Plane plane = new Plane();
        Vector3 normal = plane.normal;
        normal.x = input.readFloat();
        normal.y = input.readFloat();
        normal.z = input.readFloat();
        plane.d = input.readFloat();
        return plane;
    }

    @Override
    public Plane copy (Kryo kryo, Plane original) {
        Plane copy = new Plane();
        copy.normal.set(original.normal); // avoid passing into constructor and risking rounding error from normalization
        copy.d = original.d;
        return copy;
    }
}
