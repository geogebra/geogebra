package org.geogebra.common.factories;

import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;

public abstract class FormatFactory {
	private static volatile FormatFactory prototype;

	private static final Object lock = new Object();

	public static FormatFactory getPrototype() {
		return prototype;
	}

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

}
