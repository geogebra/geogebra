package geogebra.web.util;

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
	    // TODO Auto-generated method stub
	    return nf.format(d);
    }

}
