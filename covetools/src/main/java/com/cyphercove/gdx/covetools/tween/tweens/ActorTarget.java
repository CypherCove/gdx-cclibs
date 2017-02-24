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
package com.cyphercove.gdx.covetools.tween.tweens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.cyphercove.gdx.covetools.tween.tweens.ParameterTarget;

/** Wrapper for tween parameter target that is an Actor, so the various parameters can be distinguished
 * for interruption purposes.
 * 
 * @author cypherdare
 */
public abstract class ActorTarget implements ParameterTarget<ActorTarget> {
	Actor actor;
	private Parameter parameter;
	
	public enum Parameter {
		Position, Scale, Rotation, Origin, Size, Bounds, Color
	}
	
	public void applyX (float rotation){
		actor.setRotation(rotation);
	}
	
	public void applyXY (float x, float y){
		switch (parameter) {
		case Position:
			actor.setPosition(x, y);
			break;
		case Scale:
			actor.setScale(x, y);
			break;
		case Size:
			actor.setSize(x, y);
			break;
		case Origin:
			actor.setOrigin(x, y);
			break;
		default:
			break;
		}
	}
	
	public void applyXYZW (float x, float y, float z, float w){
		switch (parameter) {
		case Color:
			actor.setColor(x, y, z, w);
			break;
		case Bounds:
			actor.setBounds(x, y, z, w);
			break;
		default:
			break;
		}
	}
	
	public boolean matches (ActorTarget other){
		return other.actor == actor && other.parameter == parameter;
	}
}
