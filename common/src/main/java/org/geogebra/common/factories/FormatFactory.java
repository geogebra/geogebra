package org.geogebra.common.factories;

import org.geogebra.common.util.DefaultTimeFormat;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.common.util.TimeFormatAdapter;

public abstract class FormatFactory {
	private static volatile FormatFactory prototype;

	private static final Object lock = new Object();

	public static FormatFactory getPrototype() {
		return prototype;
	}

	/**
	 * @param p
	 *            prototype
	 */
	public static void setPrototypeIfNull(FormatFactory p) {

		synchronized (lock) {
			if (prototype == null) {
				prototype = p;
			}
		}
	}

	public abstract ScientificFormatAdapter getScientificFormat(int sigDigit,
			int maxWidth, boolean sciNote);

	public abstract NumberFormatAdapter getNumberFormat(int digits);

	public abstract NumberFormatAdapter getNumberFormat(String pattern,
			int digits);

	public TimeFormatAdapter getTimeFormat() {
		return new DefaultTimeFormat();
	}

	/**
	 * Gets scientific format that is optimized for speed, not pretty output.
	 * 
	 * @param digits
	 *            precision
	 * @return scientific format with E notation and given precision
	 */
	public ScientificFormatAdapter getFastScientificFormat(int digits) {
		return getScientificFormat(digits, digits + 2, true);
	}

}
