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

import com.badlogic.gdx.math.GridPoint3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class GridPoint3Serializer extends Serializer<GridPoint3> {
    @Override
    public void write(Kryo kryo, Output output, GridPoint3 gridPoint3) {
        output.writeInt(gridPoint3.x);
        output.writeInt(gridPoint3.y);
        output.writeInt(gridPoint3.z);
    }

    @Override
    public GridPoint3 read(Kryo kryo, Input input, Class<GridPoint3> type) {
        int x = input.readInt();
        int y = input.readInt();
        int z = input.readInt();
        return new GridPoint3(x, y, z);
    }

    @Override
    public GridPoint3 copy (Kryo kryo, GridPoint3 original) {
        return new GridPoint3(original);
    }
}
