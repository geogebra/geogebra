package org.geogebra.common.kernel.arithmetic;

import java.util.Objects;

import org.geogebra.common.kernel.StringTemplate;
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
		nonRecurringLength = lengthOf(nonRecurringPart);
		recurringLength = lengthOf(recurringPart);
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

	/**
	 *
	 * @param tpl  {@link StringTemplate}
	 * @return the overlined recurring decimal string.
	 */
	public String toString(StringTemplate tpl) {

		StringBuilder sb = new StringBuilder();
		sb.append(integerPart);
		sb.append(".");
		if (nonRecurringPart != null) {
			sb.append(nonRecurringPart);
		}
		if (tpl.isLatex()) {
			sb.append("\\overline{");
			sb.append(recurringPart);
			sb.append("}");
		} else {
			String recurringString = String.valueOf(recurringPart);

			for (int i = 0; i < recurringString.length(); i++) {
				sb.append(recurringString.charAt(i));
				sb.append(Unicode.OVERLINE);

			}
		}
		return sb.toString();
	}

	private String toCasString(StringTemplate tpl) {
		int numerator = numerator();
		int denominator = denominator();
		StringBuilder sb = new StringBuilder();
		if (tpl.isLatex()) {
			sb.append("\\frac{");
			sb.append(numerator);
			sb.append("}{");
			sb.append(denominator);
			sb.append("}");
		} else {
			sb.append(numerator);
			sb.append(" / ");
			sb.append(denominator);
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
		int pL = lengthOf(recurringPart);
		int aL = isNonRecurringPartEmpty() ? 0 : lengthOf(nonRecurringPart);
		int A = isNonRecurringPartEmpty() ? 0 : nonRecurringPart;
		int iap = (int) (recurringPart + A * Math.pow(10, pL)
				+ integerPart * Math.pow(10, pL + aL));
		int ia = (int) (A + integerPart * Math.pow(10, aL));
		return iap - ia;
	}

	/**
	 *
	 * @return denominator of the fraction form.
	 */
	public int denominator() {
		int nines = recurringLength == 0 ? 1 : (int) (Math.pow(10, recurringLength) - 1);
		int tens = nonRecurringLength == 0 ? 1 : (int) (Math.pow(10, nonRecurringLength));
		return nines * tens;
	}

	private static int lengthOf(Integer number) {
		if (number == null) {
			return 0;
		}
		return number != 0 ? (int) (Math.log10(number) + 1) : 1;
	}

	public double toDouble() {
		return numerator() / (denominator() + 0.0);
	}
}
