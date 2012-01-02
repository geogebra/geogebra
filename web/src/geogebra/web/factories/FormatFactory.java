package geogebra.web.factories;

import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.web.util.NumberFormat;

public class FormatFactory extends geogebra.common.factories.FormatFactory{
	@Override
    public NumberFormatAdapter getNumberFormat() {
	    return new NumberFormat();
    }

	@Override
    public NumberFormatAdapter getNumberFormat(String s) {
	    return new NumberFormat(s);
    }

	@Override
    public ScientificFormatAdapter getScientificFormat(int a, int b, boolean c) {
	    // TODO Auto-generated method stub
	    return null;
    }
}
