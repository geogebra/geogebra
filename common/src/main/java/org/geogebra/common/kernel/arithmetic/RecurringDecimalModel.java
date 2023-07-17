package org.geogebra.common.kernel.arithmetic;

import java.util.Objects;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

public class RecurringDecimalModel {
	private boolean percent = false;
	int integerPart;
	private DecimalPart nonRecurring;
	private DecimalPart recurring;

	/**
	 * Constructor with no non-recurring part.
	 *
	 * @param integerPart of the recurring decimal number
	 * @param recurringPart of the recurring decimal number.
	 */
	public RecurringDecimalModel(int integerPart, int recurringPart) {
		this(integerPart, null, recurringPart);
	}

	/**
	 * Constructor
	 * @param integerPart of the recurring decimal number
	 * @param nonRecurringPart of the recurring decimal number. It can be null.
	 * @param recurringPart of the recurring decimal number.
	 */
	public RecurringDecimalModel(int integerPart, Integer nonRecurringPart,
			int recurringPart) {
		this.integerPart = integerPart;
		this.nonRecurring = nonRecurringPart != null ? new DecimalPart(nonRecurringPart)
				: new DecimalPart();
		this.recurring = new DecimalPart(recurringPart);
	}

	public RecurringDecimalModel(boolean percent) {
		this.percent = percent;
	}

	/**
	 * Parse RecurringDecimalModel from the string representation of RecurringDecimal.
	 *
	 * @param representation of the recurring decimal as a string.
	 * @param percent if the value is meant in percent
	 * @return the parsed model.
	 */
	public static RecurringDecimalModel parse(String representation, boolean percent) {
		RecurringDecimalModel properies = new RecurringDecimalModel(percent);
		int point = representation.indexOf('.');
		int overline = representation.indexOf(Unicode.OVERLINE);
		properies.integerPart = (int) StringUtil.parseDouble(representation.substring(0, point));
		if (overline > point + 2) {
			String nonRec = representation.substring(point + 1, overline - 1);
			properies.nonRecurring = new DecimalPart(nonRec);
		} else {
			properies.nonRecurring = new DecimalPart();
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
		if ((overlined.length() & 1) == 1) {
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

		if (!(o instanceof RecurringDecimalModel)) {
			return false;
		}

		RecurringDecimalModel that = (RecurringDecimalModel) o;
		return integerPart == that.integerPart && (
				(nonRecurring == null && that.nonRecurring == null)
						|| (nonRecurring != null && nonRecurring.equals(that.nonRecurring)))
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
		nonRecurring.append(sb);
		if (tpl.isLatex()) {
			sb.append("\\overline{");
			recurring.append(sb);
			sb.append("}");
		} else {
			recurring.append(sb, Unicode.OVERLINE);
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
		int iap = (int) (recurring.value() + nonRecurring.value() * Math.pow(10, recurring.length)
				+ integerPart * Math.pow(10, recurring.length + nonRecurring.length));
		int ia = (int) (nonRecurring.value() + integerPart * Math.pow(10, nonRecurring.length));
		return iap - ia;
	}

	/**
	 *
	 * @return denominator of the fraction form.
	 */
	public int denominator() {
		int nines = recurring.length == 0 ? 1 : (int) (Math.pow(10, recurring.length) - 1);
		int tens = nonRecurring.length == 0 ? 1 : (int) (Math.pow(10, nonRecurring.length));
		return nines * tens;
	}

	public double toDouble() {
		return numerator() / (denominator() + 0.0);
	}
}
