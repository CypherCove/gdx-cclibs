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
package com.cyphercove.gdx.gdxtokryo.gdxserializers.graphics;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class PerspectiveCameraSerializer extends Serializer<PerspectiveCamera> {

    @Override
    public void write (Kryo kryo, Output output, PerspectiveCamera camera) {
        kryo.writeObject(output, camera.position);
        kryo.writeObject(output, camera.direction);
        kryo.writeObject(output, camera.up);
        output.writeFloat(camera.near);
        output.writeFloat(camera.far);
        output.writeFloat(camera.viewportWidth);
        output.writeFloat(camera.viewportHeight);
        output.writeFloat(camera.fieldOfView);
    }

    @Override
    public PerspectiveCamera read (Kryo kryo, Input input, Class<PerspectiveCamera> type) {
        PerspectiveCamera camera = new PerspectiveCamera();
        Vector3 position = kryo.readObject(input, Vector3.class);
        Vector3 direction = kryo.readObject(input, Vector3.class);
        Vector3 up = kryo.readObject(input, Vector3.class);
        camera.position.set(position);
        camera.direction.set(direction);
        camera.up.set(up);
        camera.near = input.readFloat();
        camera.far = input.readFloat();
        camera.viewportWidth = input.readFloat();
        camera.viewportHeight = input.readFloat();
        camera.fieldOfView = input.readFloat();
        camera.update();
        return camera;
    }

    @Override
    public PerspectiveCamera copy (Kryo kryo, PerspectiveCamera original) {
        PerspectiveCamera camera = new PerspectiveCamera();
        camera.position.set(original.position);
        camera.direction.set(original.direction);
        camera.up.set(original.up);
        camera.near = original.near;
        camera.far = original.far;
        camera.viewportWidth = original.viewportWidth;
        camera.viewportHeight = original.viewportHeight;
        camera.fieldOfView = original.fieldOfView;
        camera.update();
        return camera;
    }
}
