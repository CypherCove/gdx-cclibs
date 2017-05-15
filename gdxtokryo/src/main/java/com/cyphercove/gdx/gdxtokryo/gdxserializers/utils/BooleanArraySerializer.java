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

import com.badlogic.gdx.utils.BooleanArray;
import com.esotericsoftware.kryo.Kryo;

public class BooleanArraySerializer extends AbstractBooleanArraySerializer<BooleanArray> {

    @Override
    protected int numBits(BooleanArray bitSource) {
        return bitSource.size;
    }

    @Override
    protected boolean get(BooleanArray bitSource, int index) {
        return bitSource.get(index);
    }

    @Override
    protected BooleanArray createObject(int numBits) {
        BooleanArray array = new BooleanArray(numBits);
        array.setSize(numBits);
        return array;
    }

    @Override
    protected void set(BooleanArray bitDest, int index) {
        bitDest.set(index, true);
    }

    @Override
    public BooleanArray copy (Kryo kryo, BooleanArray original) {
        return new BooleanArray(original);
    }
}
