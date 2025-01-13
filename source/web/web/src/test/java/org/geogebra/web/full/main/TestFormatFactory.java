package org.geogebra.web.full.main;

import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.web.html5.factories.FormatFactoryW;

class TestFormatFactory extends FormatFactoryW {

	@Override
	public NumberFormatAdapter getNumberFormat(String pattern, int digits) {
		return new NumberFormatAdapter() {
			@Override
			public int getMaximumFractionDigits() {
				return digits;
			}

			@Override
			public String format(double x) {
				return String.valueOf(x);
			}
		};
	}
}
