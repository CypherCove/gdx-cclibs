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

import com.badlogic.gdx.math.WindowedMean;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class WindowedMeanSerializer extends Serializer<WindowedMean> {

    @Override
    public void write(Kryo kryo, Output output, WindowedMean windowedMean) {
        int size = windowedMean.getWindowSize();
        float[] values = windowedMean.getWindowValues();
        int count = values.length;
        output.writeVarInt(size, true);
        output.writeVarInt(count, true);
        output.writeFloats(values);
    }

    @Override
    public WindowedMean read(Kryo kryo, Input input, Class<WindowedMean> type) {
        int size = input.readVarInt(true);
        int count = input.readVarInt(true);
        float[] values = input.readFloats(count);
        WindowedMean windowedMean = new WindowedMean(size);
        for (int i = 0; i < values.length; i++) {
            windowedMean.addValue(values[i]);
        }
        return windowedMean;
    }

    @Override
    public WindowedMean copy (Kryo kryo, WindowedMean original) {
        WindowedMean windowedMean = new WindowedMean(original.getWindowSize());
        float[] values = original.getWindowValues();
        for (int i = 0; i < values.length; i++) {
            windowedMean.addValue(values[i]);
        }
        return windowedMean;
    }
}
