package org.geogebra.common.kernel.arithmetic;

import java.util.Objects;

class DecimalPart {
	Integer value;
	int length;

	/**
	 * Default constructor
	 */
	public DecimalPart() {
		value = null;
		length = 0;
	}

	DecimalPart(String description) {
		this(Integer.parseInt(description), description.length());
	}

	DecimalPart(Integer value, int length) {
		this(value);
		this.length = length;
	}

	DecimalPart(Integer value) {
		this.value = value;
		length = lengthWithoutLeadingZeros();
	}

	private int lengthWithoutLeadingZeros() {
		return value != null && value != 0 ? (int) (Math.log10(value) + 1) : 1;
	}

	int leadingZeroCount() {
		return length - lengthWithoutLeadingZeros();
	}

	void appendWithLeadingZeros(StringBuilder sb) {
		if (value == null) {
			return;
		}
		for (int i = 0; i < leadingZeroCount(); i++) {
			sb.append("0");
		}
		sb.append(value);
	}

	@Override
	public String toString() {
		return value != null ? "DecimalPart{"
				+ "value=" + value
				+ ", length=" + length
				+ '}' : "";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DecimalPart)) {
			return false;
		}
		DecimalPart that = (DecimalPart) o;
		return length == that.length && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, length);
	}

	 int value() {
		return value != null ? value : 0;
	}
}
