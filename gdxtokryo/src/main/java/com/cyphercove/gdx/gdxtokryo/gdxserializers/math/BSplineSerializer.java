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

import com.badlogic.gdx.math.BSpline;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class BSplineSerializer extends Serializer<BSpline> {

    @Override
    public void write(Kryo kryo, Output output, BSpline bSpline) {
        Class<? extends Vector> vectorType = null;
        if (bSpline.controlPoints != null && bSpline.controlPoints.length > 0)
            vectorType = bSpline.controlPoints[0].getClass();
        else if (bSpline.knots != null && bSpline.knots.size > 0)
            vectorType = ((Vector)bSpline.knots.first()).getClass();
        kryo.writeClass(output, vectorType);
        output.writeInt(bSpline.controlPoints != null ? bSpline.controlPoints.length : -1); // -1 for null array
        if (bSpline.controlPoints != null){
            for (int i = 0; i < bSpline.controlPoints.length; i++) {
                kryo.writeObjectOrNull(output, bSpline.controlPoints[i], vectorType);
            }
        }
        kryo.writeObjectOrNull(output, bSpline.knots, Array.class);
        output.writeInt(bSpline.degree, true);
        output.writeBoolean(bSpline.continuous);
        output.writeInt(bSpline.spanCount, true);
    }

    @Override
    public BSpline read(Kryo kryo, Input input, Class<BSpline> type) {
        BSpline bSpline = new BSpline();
        Class<? extends Vector> vectorType = kryo.readClass(input).getType();
        int controlPointsLength = input.readInt();
        if (controlPointsLength >= 0){
            Vector<?>[] controlPoints = (Vector<?>[])java.lang.reflect.Array.newInstance(vectorType, controlPointsLength);
            for (int i = 0; i < controlPointsLength; i++) {
                controlPoints[i] = kryo.readObjectOrNull(input, vectorType); // vector type won't be null if control points had a length
            }
            bSpline.controlPoints = controlPoints;
        }
        bSpline.knots = kryo.readObjectOrNull(input, Array.class);
        bSpline.degree = input.readInt(true);
        bSpline.continuous = input.readBoolean();
        bSpline.spanCount = input.readInt(true);
        return bSpline;
    }

    @Override
    public BSpline copy (Kryo kryo, BSpline original) {
        BSpline copy = new BSpline();
        if (original.controlPoints != null){
            copy.controlPoints = new Vector[original.controlPoints.length];
            for (int i = 0; i < copy.controlPoints.length; i++) {
                copy.controlPoints[i] = original.controlPoints[i].cpy();
            }
        }
        if (original.knots != null){
            copy.knots = new Array(original.knots.size);
            for (int i = 0; i < original.knots.size; i++) {
                Vector<?> vector = (Vector)original.knots.get(i);
                copy.knots.add(vector.cpy());
            }
        }
        copy.degree = original.degree;
        copy.continuous = original.continuous;
        copy.spanCount = original.spanCount;
        return copy;
    }
}
