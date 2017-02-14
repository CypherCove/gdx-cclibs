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

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Pool;
import com.cyphercove.gdx.covetools.tween.Blend.SplineBlend;

/** An action that blends between parallel values over time using a {@link Blend}.
 * <p>
 * This is based on the TemporalAction class in LibGDX, but uses Blends so it can 
 * smoothly interrupt existing tweens on the same parameters.
 * @author cypherdare */
abstract public class TweenAction extends Action {
	private float duration, time;
	private Blend interpolation;
	private SplineBlend splineInterpolation; // To avoid repeated casts. Non-null if and only if there are starting speeds
																		// and blend is a spline blend.
	protected final float[] startSpeeds;
	private boolean hasStartSpeeds;
	private final float[] tmpPercents;
	private boolean reverse, began, complete;
	
	private Object parameterTarget;

	public TweenAction (int numberOfValues) {
		this(1f, numberOfValues);
	}

	public TweenAction (float duration, int numberOfValues) {
		this(duration, Blend.linear, numberOfValues);
	}

	public TweenAction (float duration, Blend interpolation, int numberOfValues) {
		this.duration = duration;
		this.interpolation = interpolation;
		startSpeeds = new float[numberOfValues];
		tmpPercents = new float[numberOfValues];
	}
	
	/** The parameter target is the unique object that this tween is affecting. Any other tween that is
	 * currently operating on the same target will be used to get starting speed(s).
	 * @param parameterTarget
	 */
	protected void setParameterTarget (Object parameterTarget){
		this.parameterTarget = parameterTarget;
	}
	
	protected Object getParameterTarget (){
		return parameterTarget;
	}

	public boolean act (float delta) {
		if (complete) return true;
		Pool<?> pool = getPool();
		setPool(null); // Ensure this action can't be returned to the pool while executing.
		try {
			if (!began) {
				begin();
				if (hasStartSpeeds && interpolation instanceof SplineBlend)
					splineInterpolation = (SplineBlend)interpolation;
				began = true;
			}
			time += delta;
			complete = time >= duration;
			if (complete) {
				update(reverse ? 0f : 1f);
			} else if (splineInterpolation == null) {
				float percent = time / duration;
				if (interpolation != null) percent = interpolation.apply(percent);
				update(reverse ? 1 - percent : percent);
			} else {
				float percent = time / duration;
				for (int i=0; i<startSpeeds.length; i++){
					if (reverse)
						tmpPercents[i] = 1f - splineInterpolation.applyWithSpeed(startSpeeds[i], percent);
					else 
						tmpPercents[i] = splineInterpolation.applyWithSpeed(startSpeeds[i], percent);
				}
				updateIndependently(tmpPercents);
			}
			if (complete) end();
			return complete;
		} finally {
			setPool(pool);
		}
	}

	/** Called the first time {@link #act(float)} is called. This is a good place to query the {@link #actor actor's} starting
	 * state. */
	protected void begin () {
	}

	/** Called the last time {@link #act(float)} is called. */
	protected void end () {
	}

	/** Called each frame if no starting speed was specified for the Blend (which is always the case with non-SplineBlends
	 * and sometimes the case for SplineBlends).
	 * @param percent The percentage of completion for this action, growing from 0 to 1 over the duration. If
	 *           {@link #setReverse(boolean) reversed}, this will shrink from 1 to 0. */
	abstract protected void update (float percent);

	/** Called each frame if using a SplineBlend with starting speed(s). Multiple parameters may have different
	 * percentages of completion due to differing starting speeds.
	 * @param percents The percentages of completion for each of the parameters. */
	protected void updateIndependently (float[] percents) {
	};

	/** Skips to the end of the transition. */
	public void finish () {
		time = duration;
	}

	public void restart () {
		time = 0;
		began = false;
		complete = false;
	}

	public void reset () {
		super.reset();
		reverse = false;
		interpolation = null;
		splineInterpolation = null;
		hasStartSpeeds = false;
		parameterTarget = null;
	}

	/** Gets the transition time so far. */
	public float getTime () {
		return time;
	}

	/** Sets the transition time so far. */
	public void setTime (float time) {
		this.time = time;
	}

	public float getDuration () {
		return duration;
	}

	/** Sets the length of the transition in seconds. */
	public void setDuration (float duration) {
		this.duration = duration;
	}

	public Blend getBlend () {
		return interpolation;
	}

	public void setBlend (Blend interpolation) {
		this.interpolation = interpolation;
	}

	/** Call after setting start speeds by filling in {@link #startSpeeds}. They will only be used if the Blend is a SplineBlend by 
	 * the time the action begins. Speeds are expressed in terms of unit change over unit duration. Subclasses can expose a more 
	 * intuitive method. To prepare the values for {@link #startSpeeds}, you may use:
	 * <p>
	 * {@code speed = worldSpeed * duration / (totalChange)}*/
	protected void onStartSpeedsSet (int numberUsed, float startSpeed0, float startSpeed1, float startSpeed2, float startSpeed3) {
		if (interpolation instanceof SplineBlend) splineInterpolation = (SplineBlend)interpolation;
	}

	protected void clearStartSpeeds () {
		hasStartSpeeds = false;
		splineInterpolation = null;
	}

	protected float getSpeed (int index) {
		if (splineInterpolation != null) return splineInterpolation.speed(startSpeeds[index], 0, time / duration);
		if (interpolation != null) return interpolation.speed(0, 0, time / duration);
		return 1f;
	}

	public boolean isReverse () {
		return reverse;
	}

	/** When true, the action's progress will go from 100% to 0%. */
	public void setReverse (boolean reverse) {
		this.reverse = reverse;
	}
}