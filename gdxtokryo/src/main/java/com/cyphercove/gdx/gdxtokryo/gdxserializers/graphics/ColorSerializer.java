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
package com.cyphercove.gdx.gdxtokryo.gdxserializers.graphics;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.cyphercove.gdx.gdxtokryo.GraphHeader;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ColorSerializer extends Serializer<Color> {

    private boolean isCompactDefault = true;

    /** Sets whether compact encoding should be used or not in the absence of a {@link GraphHeader}. True by default.*/
    public void setCompactDefault(boolean compactDefault) {
        isCompactDefault = compactDefault;
    }

    @Override
    public void write(Kryo kryo, Output output, Color color) {
        Boolean compact = GraphHeader.isUseCompactColor(kryo);
        if (compact == null) compact = isCompactDefault;
        if (compact){
            output.writeInt(Color.rgba8888(color));
        } else {
            output.writeFloat(color.r);
            output.writeFloat(color.g);
            output.writeFloat(color.b);
            output.writeFloat(color.a);
        }
    }

    @Override
    public Color read(Kryo kryo, Input input, Class<Color> type) {
        Boolean compact = GraphHeader.isUseCompactColor(kryo);
        if (compact == null) compact = isCompactDefault;
        if (compact){
            Color color = new Color();
            Color.rgba8888ToColor(color, input.readInt());
            return color;
        } else {
            float r = input.readFloat();
            float g = input.readFloat();
            float b = input.readFloat();
            float a = input.readFloat();
            return new Color(r, g, b, a);
        }

    }

    @Override
    public Color copy (Kryo kryo, Color original) {
        return new Color(original);
    }
}
