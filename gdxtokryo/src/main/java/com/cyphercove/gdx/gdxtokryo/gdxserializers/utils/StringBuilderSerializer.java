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

import com.badlogic.gdx.utils.StringBuilder;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class StringBuilderSerializer extends Serializer<StringBuilder> {

    @Override
    public void write(Kryo kryo, Output output, StringBuilder stringBuilder) {
        output.writeVarInt(stringBuilder.length, true);
        for (int i = 0; i < stringBuilder.length(); i++) {
            output.writeChar(stringBuilder.charAt(i));
        }
    }

    @Override
    public StringBuilder read(Kryo kryo, Input input, Class<StringBuilder> type) {
        int length = input.readVarInt(true);
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(input.readChar());
        }
        return stringBuilder;
    }

    @Override
    public StringBuilder copy (Kryo kryo, StringBuilder original) {
        return new StringBuilder(original);
    }
}
