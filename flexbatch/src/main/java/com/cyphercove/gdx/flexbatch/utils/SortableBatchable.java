
package com.cyphercove.gdx.flexbatch.utils;

import com.badlogic.gdx.math.Vector3;
import com.cyphercove.gdx.flexbatch.Batchable;

/** A 3D Batchable that can be sorted by {@link BatchableSorter}.
 * @param <T> The type must match the class that implements this. */
public interface SortableBatchable<T extends Batchable> {
	/** @return Whether this Batchable is opaque. */
	boolean isOpaque ();

	/** @return The squared distance from the given camera position. */
	float calculateDistanceSquared (Vector3 camPosition);

	/** @param other Another instance of the same class type as this one, for comparison.
	 * @return Whether this Batchable and the other have the same texture configuration such that they could be drawn sequentially
	 *         without forcing the FlexBatch to flush in between. */
	public abstract boolean hasEquivalentTextures (T other);
}
