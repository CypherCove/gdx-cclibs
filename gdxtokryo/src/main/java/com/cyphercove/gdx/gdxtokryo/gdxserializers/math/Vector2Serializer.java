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

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Vector2Serializer extends Serializer<Vector2> {
    @Override
    public void write(Kryo kryo, Output output, Vector2 vector2) {
        output.writeFloat(vector2.x);
        output.writeFloat(vector2.y);
    }

    @Override
    public Vector2 read(Kryo kryo, Input input, Class<Vector2> type) {
        float x = input.readFloat();
        float y = input.readFloat();
        return new Vector2(x, y);
    }

    @Override
    public Vector2 copy (Kryo kryo, Vector2 original) {
        return new Vector2(original);
    }
}
