package org.geogebra.common.factories;

import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;

public abstract class FormatFactory {
	public static FormatFactory prototype;

	public abstract ScientificFormatAdapter getScientificFormat(int sigDigit,
			int maxWidth, boolean sciNote);

	public abstract NumberFormatAdapter getNumberFormat(int digits);

	public abstract NumberFormatAdapter getNumberFormat(String pattern,
			int digits);

}
