package geogebra.web.util;

import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.constants.NumberConstants;

import geogebra.common.util.NumberFormatAdapter;

/**
 * @author gabor@geogebra.org
 *
 * <p>GWT NumberFormat class wrapped in supertype</p> 
 *
 */
public class NumberFormat extends Format implements NumberFormatAdapter {

	 
	
	

	private int maximumFractionDigits = 3;
	private int minimumFractionDigits = 0;
	private boolean groupingUsed;
	 
	public NumberFormat(String s) {
	   this.nf = com.google.gwt.i18n.client.NumberFormat.getFormat(s);
    }

	public NumberFormat() {
	    // TODO Auto-generated constructor stub
    }

	
	public int getMaximumFractionDigits() {
		return maximumFractionDigits;
	}

	
	public void setGroupingUsed(boolean b) {
		groupingUsed = b;
	}

	
	public String format(double x) {
		return nf.format(x);
	}

	
	public void setMaximumFractionDigits(int decimals) {
		 maximumFractionDigits = Math.max(0,decimals);
		 if (maximumFractionDigits < minimumFractionDigits) {
			 minimumFractionDigits = maximumFractionDigits;
		 }
	}

}
