package org.geogebra.common.kernel.arithmetic;

import java.util.Objects;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

public class RecurringDecimalProperties {
	private boolean percent = false;
	int integerPart;
	private DecimalPart nonRecurring;
	private DecimalPart recurring;

	static class DecimalPart {
		Integer value;
		int length;

		public DecimalPart(String description) {
			this(Integer.parseInt(description), description.length());
		}

		public DecimalPart(Integer value, int length) {
			this(value);
			this.length = length;
		}

		public DecimalPart(Integer value) {
			this.value = value;
			length = lengthWithoutLeadingZeros();
		}

		private int lengthWithoutLeadingZeros() {
			return value != 0 ? (int) (Math.log10(value) + 1) : 1;
		}

		int leadingZeroCount() {
			return length - lengthWithoutLeadingZeros();
		}

		public void appendWithLeadingZeros(StringBuilder sb) {
			for (int i = 0; i < leadingZeroCount(); i++) {
				sb.append("0");
			}
			sb.append(value);
		}

		@Override
		public String toString() {
			return "DecimalPart{" +
					"value=" + value +
					", length=" + length +
					'}';
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
	}

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
		this.nonRecurring = nonRecurringPart != null ? new DecimalPart(nonRecurringPart) : null;
		this.recurring = new DecimalPart(recurringPart);
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
			properies.nonRecurring = new DecimalPart(nonRec);
		} else {
			properies.nonRecurring = null;
		}

		String overlined = representation.substring(overline - 1);
		if (isOverlineStringValid(overlined)) {
			String recurrinString = overlined.replaceAll(Unicode.OVERLINE + "", "");
			properies.recurring = new DecimalPart(recurrinString);
			return properies;
		}

		throw new NumberFormatException("Invalid recurring number format");
	}

	private static boolean isOverlineStringValid(String overlined) {
		if (overlined.length() % 2 == 1) {
			return false;
		}

		for (int i = 0; i < overlined.length() / 2; i++) {
			if (!StringUtil.isDigit(overlined.charAt(2 * i))
					|| overlined.charAt(2 * i + 1) != Unicode.OVERLINE) {
				return false;
			}
		}
		return true;
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
		return integerPart == that.integerPart && (
				(nonRecurring == null && that.nonRecurring == null)
						|| (nonRecurring.equals(that.nonRecurring)))
				&& recurring.equals(that.recurring);
	}

	@Override
	public int hashCode() {
		return Objects.hash(integerPart, nonRecurring, recurring);
	}

	@Override
	public String toString() {
		return "RecurringDecimalProperties{"
				+ " integerPart=" + integerPart
				+ ", nonRecurringPart=" + nonRecurring
				+ ", recurringPart=" + recurring
				+ '}';
	}

	/**
	 *
	 * @param tpl  {@link StringTemplate}
	 * @return the overlined recurring decimal string.
	 */
	public String toString(StringTemplate tpl) {

		StringBuilder sb = new StringBuilder();
		sb.append(integerPart);
		sb.append(".");
		if (nonRecurring != null) {
			nonRecurring.appendWithLeadingZeros(sb);
		}
		if (tpl.isLatex()) {
			sb.append("\\overline{");
			sb.append(recurring.value);
			sb.append("}");
		} else {
			for (int i=0; i < recurring.leadingZeroCount(); i++) {
				sb.append("0");
				sb.append(Unicode.OVERLINE);
			}

			String recurringString = String.valueOf(recurring.value);
			for (int i = 0; i < recurringString.length(); i++) {
				sb.append(recurringString.charAt(i));
				sb.append(Unicode.OVERLINE);

			}
		}
		return sb.toString();
	}

	public boolean isPercent() {
		return percent;
	}

	/**
	 *
	 * @return numerator of the fraction form.
	 */
	public int numerator() {
		int pL = recurring.length;
		int aL = nonRecurring == null ? 0 : nonRecurring.length;
		int A = nonRecurring == null ? 0 : nonRecurring.value;
		int iap = (int) (recurring.value + A * Math.pow(10, pL)
				+ integerPart * Math.pow(10, pL + aL));
		int ia = (int) (A + integerPart * Math.pow(10, aL));
		return iap - ia;
	}

	/**
	 *
	 * @return denominator of the fraction form.
	 */
	public int denominator() {
		int nines = recurring.length == 0 ? 1 : (int) (Math.pow(10, recurring.length) - 1);
		int tens = nonRecurring == null ||
				nonRecurring.length == 0 ? 1 : (int) (Math.pow(10, nonRecurring.length));
		return nines * tens;
	}

	public double toDouble() {
		return numerator() / (denominator() + 0.0);
	}
}
