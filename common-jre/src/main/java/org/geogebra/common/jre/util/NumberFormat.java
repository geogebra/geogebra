package org.geogebra.common.jre.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.geogebra.common.util.NumberFormatAdapter;

/** Decimal format for Desktop and android */
public class NumberFormat extends DecimalFormat implements NumberFormatAdapter {

	private static final long serialVersionUID = 1L;

	/** default format */
	public NumberFormat() {
		super();
		setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		setRoundingMode(RoundingMode.HALF_UP);
	}

	/**
	 * Format with pattern
	 * 
	 * @param pattern
	 *            format pattern
	 * @param maxDigits
	 *            maximumDigits
	 * */
	public NumberFormat(String pattern, int maxDigits) {
		super(pattern);
		setMaximumFractionDigits(maxDigits);
		setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}

}
