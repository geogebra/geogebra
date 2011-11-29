package geogebra.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import geogebra.common.util.NumberFormatAdapter;

public class NumberFormatDesktop extends DecimalFormat implements NumberFormatAdapter  {
	public NumberFormatDesktop(){
		super();
		this.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}
}
