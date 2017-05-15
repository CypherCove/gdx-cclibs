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

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class CatmullRomSplineSerializer extends Serializer<CatmullRomSpline> {

    @Override
    public void write(Kryo kryo, Output output, CatmullRomSpline catmullRomSpline) {
        Class<? extends Vector> vectorType = null;
        if (catmullRomSpline.controlPoints != null && catmullRomSpline.controlPoints.length > 0)
            vectorType = catmullRomSpline.controlPoints[0].getClass();
        kryo.writeClass(output, vectorType);
        output.writeInt(catmullRomSpline.controlPoints != null ? catmullRomSpline.controlPoints.length : -1); // -1 for null array
        if (catmullRomSpline.controlPoints != null){
            for (int i = 0; i < catmullRomSpline.controlPoints.length; i++) {
                kryo.writeObjectOrNull(output, catmullRomSpline.controlPoints[i], vectorType);
            }
        }
        output.writeBoolean(catmullRomSpline.continuous);
        output.writeInt(catmullRomSpline.spanCount, true);
    }

    @Override
    public CatmullRomSpline read(Kryo kryo, Input input, Class<CatmullRomSpline> type) {
        CatmullRomSpline catmullRomSpline = new CatmullRomSpline();
        Class<? extends Vector> vectorType = kryo.readClass(input).getType();
        int controlPointsLength = input.readInt();
        if (controlPointsLength >= 0){
            Vector<?>[] controlPoints = (Vector<?>[])java.lang.reflect.Array.newInstance(vectorType, controlPointsLength);
            for (int i = 0; i < controlPointsLength; i++) {
                controlPoints[i] = kryo.readObjectOrNull(input, vectorType); // vector type won't be null if control points had a length
            }
            catmullRomSpline.controlPoints = controlPoints;
        }
        catmullRomSpline.continuous = input.readBoolean();
        catmullRomSpline.spanCount = input.readInt(true);
        return catmullRomSpline;
    }

    @Override
    public CatmullRomSpline copy (Kryo kryo, CatmullRomSpline original) {
        CatmullRomSpline copy = new CatmullRomSpline();
        if (original.controlPoints != null){
            copy.controlPoints = new Vector[original.controlPoints.length];
            for (int i = 0; i < copy.controlPoints.length; i++) {
                copy.controlPoints[i] = original.controlPoints[i].cpy();
            }
        }
        copy.continuous = original.continuous;
        copy.spanCount = original.spanCount;
        return copy;
    }
}
