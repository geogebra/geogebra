package org.geogebra.common.euclidian.draw.dropdown;

enum DropDownScrollMode {
	NONE(0), UP(-1), DOWN(1);

	private final int direction;

	DropDownScrollMode(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}
}
