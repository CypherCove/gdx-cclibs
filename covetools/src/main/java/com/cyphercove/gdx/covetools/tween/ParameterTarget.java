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
package com.cyphercove.gdx.covetools.tween;

/** Use for objects that have multiple types of tween parameters that should not interrupt
 * each other (for instance, position and size).
 * <p>
 * If a TweenAction's parameter target implements this, it can be used to distinguish between
 * parameters of the same target object when determining whether an interruption is occurring. 
 * If this interface is not used, simple `==` comparison is done to determine interruption.
 * @author cypherdare
 */
public interface ParameterTarget<T> {
	public boolean matches (T other);
}
