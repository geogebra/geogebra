package org.geogebra.common.jre.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.geogebra.common.util.NumberFormatAdapter;

public class NumberFormat extends DecimalFormat implements NumberFormatAdapter {

	private static final long serialVersionUID = 1L;

	public NumberFormat() {
		super();
		setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}

	public NumberFormat(String pattern, int i) {
		super(pattern);
		setMaximumFractionDigits(i);
		setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}

}
