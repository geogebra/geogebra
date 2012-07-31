package geogebra.util;

import geogebra.common.util.NumberFormatAdapter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberFormatDesktop extends DecimalFormat implements NumberFormatAdapter  {
	private static final long serialVersionUID = 1L;
	public NumberFormatDesktop(){
		super();
		this.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}
	
	public NumberFormatDesktop(String pattern, int i){
		super(pattern);
		setMaximumFractionDigits(i);
		this.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}
}
