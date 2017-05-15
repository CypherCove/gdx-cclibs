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
import com.badlogic.gdx.math.collision.Ray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class RaySerializer extends Serializer<Ray> {
    @Override
    public void write(Kryo kryo, Output output, Ray ray) {
        Vector3 origin = ray.origin;
        output.writeFloat(origin.x);
        output.writeFloat(origin.y);
        output.writeFloat(origin.z);
        Vector3 direction = ray.direction;
        output.writeFloat(direction.x);
        output.writeFloat(direction.y);
        output.writeFloat(direction.z);
    }

    @Override
    public Ray read(Kryo kryo, Input input, Class<Ray> type) {
        Ray ray = new Ray();
        Vector3 origin = ray.origin;
        origin.x = input.readFloat();
        origin.y = input.readFloat();
        origin.z = input.readFloat();
        Vector3 direction = ray.direction;
        direction.x = input.readFloat();
        direction.y = input.readFloat();
        direction.z = input.readFloat();
        return ray;
    }

    @Override
    public Ray copy (Kryo kryo, Ray original) {
        return original.cpy();
    }
}
