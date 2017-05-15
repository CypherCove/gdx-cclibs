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
import com.badlogic.gdx.math.collision.Segment;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SegmentSerializer extends Serializer<Segment> {
    @Override
    public void write(Kryo kryo, Output output, Segment segment) {
        Vector3 a = segment.a;
        output.writeFloat(a.x);
        output.writeFloat(a.y);
        output.writeFloat(a.z);
        Vector3 b = segment.b;
        output.writeFloat(b.x);
        output.writeFloat(b.y);
        output.writeFloat(b.z);
    }

    @Override
    public Segment read(Kryo kryo, Input input, Class<Segment> type) {
        Vector3 a = new Vector3();
        a.x = input.readFloat();
        a.y = input.readFloat();
        a.z = input.readFloat();
        Vector3 b = new Vector3();
        b.x = input.readFloat();
        b.y = input.readFloat();
        b.z = input.readFloat();
        return new Segment(a, b);
    }

    @Override
    public Segment copy (Kryo kryo, Segment original) {
        return new Segment(original.a, original.b);
    }
}
