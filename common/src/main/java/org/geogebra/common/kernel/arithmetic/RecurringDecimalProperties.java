package org.geogebra.common.kernel.arithmetic;

import java.util.Objects;

import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

public class RecurringDecimalProperties {
	private boolean percent = false;
	double doubleValue;
	int integerPart;
	Integer nonRecurringPart = null; // Optional, so must be an object.
	int recurringPart;
	int nonRecurringLength;
	int recurringLength;

	/**
	 * Constructor with no non-recurring part.
	 *
	 * @param integerPart of the recurring decimal number
	 * @param recurringPart of the recurring decimal number.
	 */
	public RecurringDecimalProperties(int integerPart, int recurringPart) {
		this(integerPart, null, recurringPart);
	}

	/**
	 * Constructor
	 * @param integerPart of the recurring decimal number
	 * @param nonRecurringPart of the recurring decimal number. It can be null.
	 * @param recurringPart of the recurring decimal number.
	 */
	public RecurringDecimalProperties(int integerPart, Integer nonRecurringPart,
			int recurringPart) {
		this.integerPart = integerPart;
		this.nonRecurringPart = nonRecurringPart;
		this.recurringPart = recurringPart;
	}

	public RecurringDecimalProperties(boolean percent) {
		this.percent = percent;
	}

	/**
	 * Parse RecurringDecimalProperties from the string representation of RecurringDecimal.
	 *
	 * @param representation of the recurring decimal as a string.
	 * @param percent if the value is meant in percent
	 * @return the parsed RecurringDecimalProperties object.
	 */
	public static RecurringDecimalProperties parse(String representation, boolean percent) {
		RecurringDecimalProperties properies = new RecurringDecimalProperties(percent);
		int point = representation.indexOf('.');
		int overline = representation.indexOf(Unicode.OVERLINE);
		properies.integerPart = (int) StringUtil.parseDouble(representation.substring(0, point));
		if (overline > point + 2) {
			String nonRec = representation.substring(point + 1, overline - 1);
			properies.nonRecurringLength = nonRec.length();
			properies.nonRecurringPart = Integer.parseInt(nonRec);
		} else {
			properies.nonRecurringPart = null;
			properies.nonRecurringLength = 0;
		}

		String rec = representation.substring(overline - 1)
				.replaceAll(Unicode.OVERLINE + "", "");
		properies.recurringLength = rec.length();
		properies.recurringPart = Integer.parseInt(rec);
		return properies;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof RecurringDecimalProperties)) {
			return false;
		}

		RecurringDecimalProperties that = (RecurringDecimalProperties) o;
		return integerPart == that.integerPart
				&& ((isNonRecurringPartEmpty() && that.isNonRecurringPartEmpty())
						 || (nonRecurringPart.equals(that.nonRecurringPart)))
				&& recurringPart == that.recurringPart;
	}

	private boolean isNonRecurringPartEmpty() {
		return nonRecurringPart == null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(doubleValue, integerPart, nonRecurringPart, recurringPart,
				nonRecurringLength, recurringLength);
	}

	@Override
	public String toString() {
		return "RecurringDecimalProperties{"
				+ ", integerPart=" + integerPart
				+ ", nonRecurringPart=" + nonRecurringPart
				+ ", recurringPart=" + recurringPart
				+ '}';
	}

	public boolean isPercent() {
		return percent;
	}
}
