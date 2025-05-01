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

	/**
	 * @param sigDigit number of significant digits
	 * @param maxWidth maximum width
	 * @param sciNote whether to use scientific notation
	 * @return number format for specific number of significant digits
	 */
	public abstract ScientificFormatAdapter getScientificFormat(int sigDigit,
			int maxWidth, boolean sciNote);

	/**
	 * @param digits number of decimal digits
	 * @return number format with fixed number of decimal digits
	 */
	public abstract NumberFormatAdapter getNumberFormat(int digits);

	/**
	 * @param pattern in the JRE all the pattern options of DecimalFormat are supported,
	 * in GWT the emulation is very limited. TODO replace with enum?
	 * @param digits number of decimal digits
	 * @return number format with fixed number of decimal digits
	 */
	public abstract NumberFormatAdapter getNumberFormat(String pattern,
			int digits);

	public TimeFormatAdapter getTimeFormat() {
		return new DefaultTimeFormat();
	}

	/**
	 * Gets a scientific format that is optimized for speed, not pretty output.
	 * 
	 * @param digits
	 *            precision
	 * @return scientific format with E notation and given precision
	 */
	public ScientificFormatAdapter getFastScientificFormat(int digits) {
		return getScientificFormat(digits, digits + 2, true);
	}

}
