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

import com.badlogic.gdx.math.Polygon;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class PolygonSerializer extends Serializer<Polygon> {

    @Override
    public void write (Kryo kryo, Output output, Polygon polygon) {
        output.writeInt(polygon.getVertices().length);
        output.writeFloats(polygon.getVertices());
        output.writeFloat(polygon.getX());
        output.writeFloat(polygon.getY());
        output.writeFloat(polygon.getOriginX());
        output.writeFloat(polygon.getOriginY());
        output.writeFloat(polygon.getRotation());
        output.writeFloat(polygon.getScaleX());
        output.writeFloat(polygon.getScaleY());
    }

    @Override
    public Polygon read (Kryo kryo, Input input, Class<Polygon> type) {
        int length = input.readInt();
        float[] vertices = input.readFloats(length);
        Polygon polygon = new Polygon(vertices);
        polygon.setPosition(input.readFloat(), input.readFloat());
        polygon.setOrigin(input.readFloat(), input.readFloat());
        polygon.setRotation(input.readFloat());
        polygon.setScale(input.readFloat(), input.readFloat());
        return polygon;
    }

    @Override
    public Polygon copy (Kryo kryo, Polygon original) {
        Polygon copy = new Polygon(original.getVertices());
        copy.setPosition(original.getX(), original.getY());
        copy.setOrigin(original.getOriginX(), original.getOriginY());
        copy.setRotation(original.getRotation());
        copy.setScale(original.getScaleX(), original.getScaleY());
        return copy;
    }
}
