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

import com.badlogic.gdx.math.FloatCounter;
import com.badlogic.gdx.math.WindowedMean;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class FloatCounterSerializer extends Serializer<FloatCounter> {
    @Override
    public void write(Kryo kryo, Output output, FloatCounter floatCounter) {
        kryo.writeObjectOrNull(output, floatCounter.mean, WindowedMean.class);
        output.writeInt(floatCounter.count);
        output.writeFloat(floatCounter.total);
        output.writeFloat(floatCounter.min);
        output.writeFloat(floatCounter.max);
        output.writeFloat(floatCounter.average);
        output.writeFloat(floatCounter.latest);
        output.writeFloat(floatCounter.value);
    }

    @Override
    public FloatCounter read(Kryo kryo, Input input, Class<FloatCounter> type) {
        WindowedMean windowedMean = kryo.readObjectOrNull(input, WindowedMean.class);
        FloatCounter floatCounter = new FloatCounter(windowedMean == null ? 0 : windowedMean.getWindowSize());
        if (windowedMean != null) {
            float[] windowedMeanValues = windowedMean.getWindowValues();
            for (float f : windowedMeanValues) { // restores the private WindowedMean
                floatCounter.put(f);
            }
        }
        floatCounter.count = input.readInt();
        floatCounter.total = input.readFloat();
        floatCounter.min = input.readFloat();
        floatCounter.max = input.readFloat();
        floatCounter.average = input.readFloat();
        floatCounter.latest = input.readFloat();
        floatCounter.value = input.readFloat();
        return floatCounter;
    }

    @Override
    public FloatCounter copy (Kryo kryo, FloatCounter original) {
        WindowedMean windowedMean = original.mean;
        FloatCounter floatCounter = new FloatCounter(windowedMean == null ? 0 : windowedMean.getWindowSize());
        if (windowedMean != null) {
            float[] windowedMeanValues = windowedMean.getWindowValues();
            for (float f : windowedMeanValues) { // restores the private WindowedMean
                floatCounter.put(f);
            }
        }
        floatCounter.count = original.count;
        floatCounter.total = original.total;
        floatCounter.min = original.min;
        floatCounter.max = original.max;
        floatCounter.average = original.average;
        floatCounter.latest = original.latest;
        floatCounter.value = original.value;
        return floatCounter;
    }
}
