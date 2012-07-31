package geogebra.web.util;

import geogebra.common.util.NumberFormatAdapter;

/**
 * @author gabor@geogebra.org
 *
 * <p>GWT NumberFormat class wrapped in supertype</p> 
 *
 */
public class NumberFormatWeb extends Format implements NumberFormatAdapter {

	private int maximumFractionDigits;
	 
	public NumberFormatWeb(String s, int digits) {
		maximumFractionDigits = digits;
	   this.nf = com.google.gwt.i18n.client.NumberFormat.getFormat(s);
    }
	
	public int getMaximumFractionDigits() {
		return maximumFractionDigits;
	}
	
	public String format(double x) {
		String ret = nf.format(x);
		
		// "0." as the format string can give eg format(0.9)="1."
		// so check for . on the end
		if (ret.endsWith(".")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		
		// GWT uses the locale to decide . or , as decimal separator
		// we must always have .
		return ret.replace(',', '.');
		
	}

	
}
