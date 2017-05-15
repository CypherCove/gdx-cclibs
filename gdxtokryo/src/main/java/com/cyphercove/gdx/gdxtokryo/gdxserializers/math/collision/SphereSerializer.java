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
package com.cyphercove.gdx.gdxtokryo.gdxserializers.math.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Sphere;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SphereSerializer extends Serializer<Sphere> {
    @Override
    public void write(Kryo kryo, Output output, Sphere sphere) {
        Vector3 center = sphere.center;
        output.writeFloat(center.x);
        output.writeFloat(center.y);
        output.writeFloat(center.z);
        output.writeFloat(sphere.radius);
    }

    @Override
    public Sphere read(Kryo kryo, Input input, Class<Sphere> type) {
        Vector3 center = new Vector3();
        center.x = input.readFloat();
        center.y = input.readFloat();
        center.z = input.readFloat();
        float radius = input.readFloat();
        return new Sphere(center, radius);
    }

    @Override
    public Sphere copy (Kryo kryo, Sphere original) {
        return new Sphere(original.center, original.radius);
    }
}
