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

import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class RectangleSerializer extends Serializer<Rectangle> {
    @Override
    public void write(Kryo kryo, Output output, Rectangle rectangle) {
        output.writeFloat(rectangle.x);
        output.writeFloat(rectangle.y);
        output.writeFloat(rectangle.width);
        output.writeFloat(rectangle.height);
    }

    @Override
    public Rectangle read(Kryo kryo, Input input, Class<Rectangle> type) {
        float x = input.readFloat();
        float y = input.readFloat();
        float width = input.readFloat();
        float height = input.readFloat();
        return new Rectangle(x, y, width, height);
    }

    @Override
    public Rectangle copy (Kryo kryo, Rectangle original) {
        return new Rectangle(original);
    }
}
