package org.geogebra.common.spy.factory;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;

public class FormatFactorySpy extends FormatFactory {

	@Override
	public ScientificFormatAdapter getScientificFormat(int sigDigit, int maxWidth, boolean sciNote) {
		return null;
	}

	@Override
	public NumberFormatAdapter getNumberFormat(int digits) {
		return null;
	}

	@Override
	public NumberFormatAdapter getNumberFormat(String pattern, int digits) {
		return null;
	}
}
