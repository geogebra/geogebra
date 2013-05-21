package geogebra.html5.factories;

import geogebra.common.factories.FormatFactory;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.html5.util.NumberFormatWeb;
import geogebra.html5.util.ScientificFormat;

public class FormatFactoryW extends FormatFactory{
	@Override
    public NumberFormatAdapter getNumberFormat(int digits) {
		switch (digits) {
		case 0: return new NumberFormatWeb("0.", digits);
		case 1: return new NumberFormatWeb("0.#", digits);
		case 2: return new NumberFormatWeb("0.##", digits);
		case 3: return new NumberFormatWeb("0.###", digits);
		case 4: return new NumberFormatWeb("0.####", digits);
		case 5: return new NumberFormatWeb("0.#####", digits);
		case 6: return new NumberFormatWeb("0.######", digits);
		case 7: return new NumberFormatWeb("0.#######", digits);
		case 8: return new NumberFormatWeb("0.########", digits);
		case 9: return new NumberFormatWeb("0.#########", digits);
		case 10: return new NumberFormatWeb("0.##########", digits);
		case 11: return new NumberFormatWeb("0.###########", digits);
		case 12: return new NumberFormatWeb("0.############", digits);
		case 13: return new NumberFormatWeb("0.#############", digits);
		case 14: return new NumberFormatWeb("0.##############", digits);
		default: return new NumberFormatWeb("0.###############", digits);
		}
    }

	@Override
    public NumberFormatAdapter getNumberFormat(String s, int d) {
	    return new NumberFormatWeb(s, d);
    }

	@Override
    public ScientificFormatAdapter getScientificFormat(int a, int b, boolean c) {
	    return new ScientificFormat(a,b,c);
    }
}
