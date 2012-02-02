package geogebra.common.factories;

import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;

public abstract class FormatFactory {
	public static FormatFactory prototype;
	public abstract ScientificFormatAdapter getScientificFormat(int a, int b, boolean c);
    public abstract NumberFormatAdapter getNumberFormat(int digits);
    public abstract NumberFormatAdapter getNumberFormat(String s, int digits);

}
