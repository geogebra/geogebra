package org.geogebra.common.gui;

public class EdgeInsets {
	private final int left;
	private final int top;
	private final int right;
	private final int bottom;

	public EdgeInsets() {
		this(0, 0, 0, 0);
	}

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
