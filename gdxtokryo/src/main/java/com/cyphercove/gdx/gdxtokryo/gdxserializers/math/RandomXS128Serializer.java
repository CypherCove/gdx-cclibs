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

import com.badlogic.gdx.math.RandomXS128;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class RandomXS128Serializer extends Serializer<RandomXS128> {
    @Override
    public void write(Kryo kryo, Output output, RandomXS128 random) {
        output.writeLong(random.getState(0));
        output.writeLong(random.getState(1));
    }

    @Override
    public RandomXS128 read(Kryo kryo, Input input, Class<RandomXS128> type) {
        long seed0 = input.readLong();
        long seed1 = input.readLong();
        return new RandomXS128(seed0, seed1);
    }

    @Override
    public RandomXS128 copy (Kryo kryo, RandomXS128 original) {
        return new RandomXS128(original.getState(0), original.getState(1));
    }
}
