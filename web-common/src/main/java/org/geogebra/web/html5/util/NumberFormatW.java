package org.geogebra.web.html5.util;

import org.geogebra.common.util.NumberFormatAdapter;

import jsinterop.base.JsPropertyMap;

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
	private NumberFormat nf;

	/**
	 * @param digits
	 *            number of digits
	 */
	public NumberFormatW(String pattern, int digits) {
		maximumFractionDigits = digits;
		JsPropertyMap<Object> props = JsPropertyMap.of("maximumFractionDigits", digits);
		props.set("useGrouping", false);
		if (pattern != null && pattern.contains("E")) {
			props.set("notation", "scientific");
		}
		nf = new NumberFormat("en-US", props);
	}

	@Override
	public int getMaximumFractionDigits() {
		return maximumFractionDigits;
	}

	@Override
	public String format(double x) {
		return nf.format(x);

	}

}
