package org.geogebra.web.html5.util;

import org.geogebra.common.util.NumberFormatAdapter;

/**
 * @author gabor@geogebra.org
 *
 *         <p>
 *         GWT NumberFormat class wrapped in supertype
 *         </p>
 *
 */
public class NumberFormatW implements NumberFormatAdapter {

	private int maximumFractionDigits;
	private MyNumberFormat nf = MyNumberFormat.getDecimalFormat();

	/**
	 * @param s
	 *            format string
	 * @param digits
	 *            number of digits
	 */
	public NumberFormatW(String s, int digits) {
		maximumFractionDigits = digits;

		// Boolean forcedLatinDigits = MyNumberFormat.forcedLatinDigits();
		// if (!forcedLatinDigits) {
		// MyNumberFormat.setForcedLatinDigits(true);
		// }
		this.nf = MyNumberFormat.getFormat(s);
		// if (!forcedLatinDigits) {
		// MyNumberFormat.setForcedLatinDigits(false);
		// }
		nf.overrideFractionDigits(0, maximumFractionDigits);
	}

	@Override
	public int getMaximumFractionDigits() {
		return maximumFractionDigits;
	}

	@Override
	public String format(double x) {
		String ret = nf.format(x);

		// "0." as the format string can give eg format(0.9)="1."
		// so check for . on the end
		if (ret.endsWith(".")) {
			ret = ret.substring(0, ret.length() - 1);
		}

		// GWT uses the locale to decide . or , as decimal separator
		// we must always have .
		// not needed as Locale removed from MyNumberFormat
		return ret;

	}

}
