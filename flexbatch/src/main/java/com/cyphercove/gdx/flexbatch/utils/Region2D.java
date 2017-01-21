
package com.cyphercove.gdx.flexbatch.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** Minimal data to define a two-dimensional region of any Texture or TextureArray. */
public class Region2D {
	public float u, u2, v, v2;
	public int layer;

	public void setFull () {
		u = v = 0f;
		u2 = v2 = 1f;
		layer = 0;
	}

	public void set (TextureRegion region) {
		u = region.getU();
		u2 = region.getU2();
		v = region.getV();
		v2 = region.getV2();
	}

	public void flip (boolean x, boolean y) {
		if (x) {
			float temp = u;
			u = u2;
			u2 = temp;
		}
		if (y) {
			float temp = v;
			v = v2;
			v2 = temp;
		}
	}
}
