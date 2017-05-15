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

import com.badlogic.gdx.math.Polyline;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class PolylineSerializer extends Serializer<Polyline> {

    @Override
    public void write (Kryo kryo, Output output, Polyline polyline) {
        output.writeInt(polyline.getVertices().length);
        output.writeFloats(polyline.getVertices());
        output.writeFloat(polyline.getX());
        output.writeFloat(polyline.getY());
        output.writeFloat(polyline.getOriginX());
        output.writeFloat(polyline.getOriginY());
        output.writeFloat(polyline.getRotation());
        output.writeFloat(polyline.getScaleX());
        output.writeFloat(polyline.getScaleY());
    }

    @Override
    public Polyline read (Kryo kryo, Input input, Class<Polyline> type) {
        int length = input.readInt();
        float[] vertices = input.readFloats(length);
        Polyline polyline = new Polyline(vertices);
        polyline.setPosition(input.readFloat(), input.readFloat());
        polyline.setOrigin(input.readFloat(), input.readFloat());
        polyline.setRotation(input.readFloat());
        polyline.setScale(input.readFloat(), input.readFloat());
        return polyline;
    }

    @Override
    public Polyline copy (Kryo kryo, Polyline original) {
        Polyline copy = new Polyline(original.getVertices());
        copy.setPosition(original.getX(), original.getY());
        copy.setOrigin(original.getOriginX(), original.getOriginY());
        copy.setRotation(original.getRotation());
        copy.setScale(original.getScaleX(), original.getScaleY());
        return copy;
    }
}
