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
package com.cyphercove.gdx.gdxtokryo.gdxserializers.utils;

import com.badlogic.gdx.utils.Bits;
import com.esotericsoftware.kryo.Kryo;

public class BitsSerializer extends AbstractBooleanArraySerializer<Bits> {

    @Override
    protected int numBits(Bits bitSource) {
        return bitSource.numBits();
    }

    @Override
    protected boolean get(Bits bitSource, int index) {
        return bitSource.get(index);
    }

    @Override
    protected Bits createObject(int numBits) {
        return new Bits(numBits);
    }

    @Override
    protected void set(Bits bitDest, int index) {
        bitDest.set(index);
    }

    @Override
    public Bits copy (Kryo kryo, Bits original) {
        Bits copy = new Bits(original.numBits());
        copy.or(original);
        return copy;
    }
}
