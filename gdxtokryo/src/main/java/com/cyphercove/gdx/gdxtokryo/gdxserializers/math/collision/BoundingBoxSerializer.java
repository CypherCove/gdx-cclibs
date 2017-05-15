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
import com.badlogic.gdx.math.collision.BoundingBox;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class BoundingBoxSerializer extends Serializer<BoundingBox> {
    @Override
    public void write(Kryo kryo, Output output, BoundingBox boundingBox) {
        Vector3 min = boundingBox.min;
        output.writeFloat(min.x);
        output.writeFloat(min.y);
        output.writeFloat(min.z);
        Vector3 max = boundingBox.max;
        output.writeFloat(max.x);
        output.writeFloat(max.y);
        output.writeFloat(max.z);
    }

    @Override
    public BoundingBox read(Kryo kryo, Input input, Class<BoundingBox> type) {
        Vector3 min = new Vector3();
        min.x = input.readFloat();
        min.y = input.readFloat();
        min.z = input.readFloat();
        Vector3 max = new Vector3();
        max.x = input.readFloat();
        max.y = input.readFloat();
        max.z = input.readFloat();
        return new BoundingBox(min, max);
    }

    @Override
    public BoundingBox copy (Kryo kryo, BoundingBox original) {
        return new BoundingBox(original.min, original.max);
    }
}
