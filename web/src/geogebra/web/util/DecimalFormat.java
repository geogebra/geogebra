package geogebra.web.util;

import geogebra.common.main.AbstractApplication;

/**
 * @author gabor@geogebra.org
 * <p>
 * Dummy class for DecimalFormat
 * </p>
 *
 */
public class DecimalFormat extends Format {
	
	String val = "";
	
	public DecimalFormat(String string) {
	    val = string;	
    }

	public String format(double d) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return nf.format(d);
    }

}
