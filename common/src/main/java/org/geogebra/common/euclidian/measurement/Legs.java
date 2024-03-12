package org.geogebra.common.euclidian.measurement;

enum Legs {
	A(1),
	B(2);

	private int index;

	Legs(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}
}
