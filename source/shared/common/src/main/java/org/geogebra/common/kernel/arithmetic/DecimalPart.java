/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.arithmetic;

import java.util.Objects;

import org.geogebra.editor.share.util.Unicode;

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
		this.value = value;
		this.length = length;
	}

	private int lengthWithoutLeadingZeros() {
		return value != null && value != 0 ? (int) (Math.log10(value) + 1) : 1;
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

	public void appendPlain(StringBuilder sb) {
		if (value == null) {
			return;
		}

		appendLeadingZeros(sb, "0");
		sb.append(value);
	}

	private void appendLeadingZeros(StringBuilder sb, String number) {
		int leadingZerosCount = length - lengthWithoutLeadingZeros();
		for (int i = 0; i < leadingZerosCount; i++) {
			sb.append(number);
		}
	}

	public void appendOverline(StringBuilder sb) {
		if (value == null) {
			return;
		}
		appendLeadingZeros(sb, "0" + Unicode.OVERLINE);
		String valueString = String.valueOf(value);
		for (int i = 0; i < valueString.length(); i++) {
			sb.append(valueString.charAt(i)).append(Unicode.OVERLINE);
		}
	}
}
