package org.geogebra.common.kernel.arithmetic;

import java.util.Objects;

import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

public class RecurringDecimalProperties {
	private boolean percent = false;
	double doubleValue;
	int integerPart;
	Integer nonRecurringPart = null;
	int recurringPart;
	int nonRecurringLength;
	int recurringLength;

	public RecurringDecimalProperties(int integerPart, int recurringPart) {
		this(integerPart, null, recurringPart);
	}

	public RecurringDecimalProperties(int integerPart, Integer nonRecurringPart, int recurringPart) {
		this.integerPart = integerPart;
		this.nonRecurringPart = nonRecurringPart;
		this.recurringPart = recurringPart;
	}

	public RecurringDecimalProperties(boolean percent) {
		this.percent = percent;
	}

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

		String rec = representation.substring(overline - 1).replaceAll(Unicode.OVERLINE+"", "");
		properies.recurringLength = rec.length();
		properies.recurringPart = Integer.parseInt(rec);
		return properies;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RecurringDecimalProperties)) return false;
		RecurringDecimalProperties that = (RecurringDecimalProperties) o;
		return integerPart == that.integerPart
				&& (nonRecurringPart == that.nonRecurringPart)
				&& recurringPart == that.recurringPart;
	}

	@Override
	public int hashCode() {
		return Objects.hash(doubleValue, integerPart, nonRecurringPart, recurringPart,
				nonRecurringLength, recurringLength);
	}

	@Override
	public String toString() {
		return "RecurringDecimalProperties{" +
				", integerPart=" + integerPart +
				", nonRecurringPart=" + nonRecurringPart +
				", recurringPart=" + recurringPart +
				'}';
	}

	public boolean isPercent() {
		return percent;
	}
}
