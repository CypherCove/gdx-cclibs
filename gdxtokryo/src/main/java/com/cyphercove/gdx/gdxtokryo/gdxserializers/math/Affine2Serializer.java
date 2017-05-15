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

import com.badlogic.gdx.math.Affine2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Affine2Serializer extends Serializer<Affine2> {
    @Override
    public void write(Kryo kryo, Output output, Affine2 affine2) {
        output.writeFloat(affine2.m00);
        output.writeFloat(affine2.m01);
        output.writeFloat(affine2.m02);
        output.writeFloat(affine2.m10);
        output.writeFloat(affine2.m11);
        output.writeFloat(affine2.m12);
    }

    @Override
    public Affine2 read(Kryo kryo, Input input, Class<Affine2> type) {
        Affine2 affine2 = new Affine2();
        affine2.m00 = input.readFloat();
        affine2.m01 = input.readFloat();
        affine2.m02 = input.readFloat();
        affine2.m10 = input.readFloat();
        affine2.m11 = input.readFloat();
        affine2.m12 = input.readFloat();
        return affine2;
    }

    @Override
    public Affine2 copy (Kryo kryo, Affine2 original) {
        return new Affine2(original);
    }
}
