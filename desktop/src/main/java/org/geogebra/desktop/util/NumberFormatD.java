package org.geogebra.desktop.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.geogebra.common.util.NumberFormatAdapter;

public class NumberFormatD extends DecimalFormat implements
		NumberFormatAdapter {
	private static final long serialVersionUID = 1L;

	public NumberFormatD() {
		super();
		this.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}

	public NumberFormatD(String pattern, int i) {
		super(pattern);
		setMaximumFractionDigits(i);
		this.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}
}
