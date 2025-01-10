package org.geogebra.common.kernel.arithmetic;

import java.util.Objects;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.util.StringUtil;

public class RecurringDecimalModel {
	protected int integerPart;
	protected DecimalPart nonRecurring;
	protected DecimalPart recurring;

	/**
	 * Constructor
	 * @param integerPart of the recurring decimal number
	 * @param nonRecurringPart of the recurring decimal number.
	 * @param recurringPart of the recurring decimal number.
	 */
	public RecurringDecimalModel(int integerPart, DecimalPart nonRecurringPart,
			DecimalPart recurringPart) {
		this.integerPart = integerPart;
		this.nonRecurring = nonRecurringPart;
		this.recurring = recurringPart;
	}

	/**
	 * Parse RecurringDecimalModel from the string representation of RecurringDecimal.
	 *
	 * @param preperiod of the recurring decimal as a string.
	 * @param recurringString the recurring digits, without unicode overlines
	 * @return the parsed model.
	 */
	public static RecurringDecimalModel parse(String preperiod, String recurringString) {
		int point = preperiod.indexOf('.');
		if (point < 0) {
			throw new NumberFormatException("Missing . in recurring decimal");
		}
		// integer part of .3 is 0, for xyz.3 it's xyz
		int integerPart = point == 0 ? 0
				: (int) StringUtil.parseDouble(preperiod.substring(0, point));
		DecimalPart nonRecurring;
		if (preperiod.length() > point + 1) {
			String nonRec = preperiod.substring(point + 1);
			nonRecurring = new DecimalPart(nonRec);
		} else {
			nonRecurring = new DecimalPart();
		}

		DecimalPart recurring = new DecimalPart(recurringString);
		return new RecurringDecimalModel(integerPart, nonRecurring, recurring);
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
		nonRecurring.appendPlain(sb);
		if (tpl.isLatex()) {
			sb.append("\\overline{");
			recurring.appendPlain(sb);
			sb.append("}");
		} else {
			recurring.appendOverline(sb);
		}

		return sb.toString();
	}

	/**
	 *
	 * @return numerator of the fraction form.
	 */
	public int numerator() {
		// variable naming follows https://en.wikipedia.org/wiki/Repeating_decimal#In_compressed_form
		int ia = (int) (nonRecurring.value() + integerPart * Math.pow(10, nonRecurring.length));
		int iap = (int) (recurring.value() + ia * Math.pow(10, recurring.length));
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
		return numerator() / ((double) denominator());
	}
}
