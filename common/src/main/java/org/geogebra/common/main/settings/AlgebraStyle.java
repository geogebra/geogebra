package org.geogebra.common.main.settings;

public enum AlgebraStyle {
	Value(0), Description(1), Definition(2), DefinitionAndValue(3);

	private final int value;

	AlgebraStyle(int value) {
		this.value = value;
	}

	public static AlgebraStyle get(int index) {
		switch (index) {
			case 0:
				return Value;
			case 1:
				return Description;
			case 2:
				return Definition;
			case 3:
				return DefinitionAndValue;
		}
		throw new IndexOutOfBoundsException();
	}

	public int getValue() {
		return value;
	}
}
