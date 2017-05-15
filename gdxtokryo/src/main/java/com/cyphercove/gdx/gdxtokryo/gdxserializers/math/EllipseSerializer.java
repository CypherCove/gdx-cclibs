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

import com.badlogic.gdx.math.Ellipse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class EllipseSerializer extends Serializer<Ellipse> {
    @Override
    public void write(Kryo kryo, Output output, Ellipse ellipse) {
        output.writeFloat(ellipse.x);
        output.writeFloat(ellipse.y);
        output.writeFloat(ellipse.width);
        output.writeFloat(ellipse.height);
    }

    @Override
    public Ellipse read(Kryo kryo, Input input, Class<Ellipse> type) {
        float x = input.readFloat();
        float y = input.readFloat();
        float width = input.readFloat();
        float height = input.readFloat();
        return new Ellipse(x, y, width, height);
    }

    @Override
    public Ellipse copy (Kryo kryo, Ellipse original) {
        return new Ellipse(original);
    }
}
