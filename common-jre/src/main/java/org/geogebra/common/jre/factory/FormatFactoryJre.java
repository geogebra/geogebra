package org.geogebra.common.jre.factory;

import org.geogebra.common.jre.util.NumberFormat;
import org.geogebra.common.jre.util.ScientificFormat;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;

public class FormatFactoryJre extends org.geogebra.common.factories.FormatFactory {

	@Override
	public ScientificFormatAdapter getScientificFormat(int sigDigit,
			int maxWidth, boolean sciNote) {
		return new ScientificFormat(sigDigit, maxWidth, sciNote);
	}

	@Override
	public NumberFormatAdapter getNumberFormat(int digits) {
		NumberFormat ret = new NumberFormat();
		ret.setMaximumFractionDigits(digits);
		ret.setGroupingUsed(false);
		return ret;
	}

	@Override
	public NumberFormatAdapter getNumberFormat(String s, int i) {
		return new NumberFormat(s, i);
	}

}
