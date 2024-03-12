package org.geogebra.common.euclidian.measurement;

enum Legs {
	A(1),
	B(2);

	Legs(int index) {
		this.index = index;
	}

	private int index;

	public int index() {
		return index;
	}
}
