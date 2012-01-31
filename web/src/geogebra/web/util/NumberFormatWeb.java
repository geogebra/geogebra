package geogebra.web.util;

import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.constants.NumberConstants;

import geogebra.common.main.AbstractApplication;
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
		return nf.format(x);
	}

	
}
