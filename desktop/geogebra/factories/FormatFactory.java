package geogebra.factories;

import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.util.NumberFormatDesktop;
import geogebra.util.ScientificFormat;

public class FormatFactory extends geogebra.common.factories.FormatFactory{
	@Override
	public ScientificFormatAdapter getScientificFormat(int a, int b, boolean c) {
		return new ScientificFormat(a,b,c);
	}

	//@Override
	public NumberFormatAdapter getNumberFormat() {
		return new NumberFormatDesktop();
	}

	public NumberFormatAdapter getNumberFormat(int digits) {
		NumberFormatDesktop ret = new NumberFormatDesktop();
		ret.setMaximumFractionDigits(digits);
		ret.setGroupingUsed(false);
		return ret;
	}

	@Override
	public NumberFormatAdapter getNumberFormat(String s, int i) {
		return new NumberFormatDesktop(s, i);
	}
}
