package org.geogebra.common.gui;

import com.google.j2objc.annotations.ObjectiveCName;

@ObjectiveCName("GGBEdgeInsets")
public class EdgeInsets {
	private final int left;
	private final int top;
	private final int right;
	private final int bottom;

	/**
	 * Creates an empty edge inset
	 */
	public EdgeInsets() {
		this(0);
	}

	/**
	 * Construct an edge inset object.
	 * @param all inset from all directions
	 */
	public EdgeInsets(int all) {
		this(all, all, all, all);
	}

	/**
	 * Construct an edge inset object.
	 * @param left left inset
	 * @param top top inset
	 * @param right right inset
	 * @param bottom bottom inset
	 */
	public EdgeInsets(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public int getRight() {
		return right;
	}

	public int getBottom() {
		return bottom;
	}
}
