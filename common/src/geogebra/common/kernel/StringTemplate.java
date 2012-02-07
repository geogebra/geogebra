package geogebra.common.kernel;

import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.common.util.Unicode;
/**
 * StringTemplate provides a container for all settings we might need
 * when serializing ExpressionValues to screen / XML / CAS input / export.
 * @author Zbynek Konecny
 */
public class StringTemplate {
	
	/**
	 * GeoGebra string type, internationalize digits
	 */
	public static StringTemplate defaultTemplate = new StringTemplate();
	
	/**
	 * MPReduce string type, do not internationalize digits
	 */
	public static StringTemplate casTemplate = new StringTemplate();
	static {
		casTemplate.internationalizeDigits = false;
		casTemplate.setType(StringType.MPREDUCE);
	}
	/**
	 * XML string type, do not internationalize digits
	 */
	public static StringTemplate xmlTemplate = new StringTemplate();
	static {
		xmlTemplate.internationalizeDigits = false;
		xmlTemplate.setType(StringType.GEOGEBRA_XML);
	}
	/**
	 * for input bar; same as default, but increases precision to MIN_EDITING_PRINT_PRECISION
	 */
	public static StringTemplate editTemplate = new StringTemplate();
	static {
		editTemplate.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(GeoElement.MIN_EDITING_PRINT_PRECISION,20,false);
		editTemplate.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(GeoElement.MIN_EDITING_PRINT_PRECISION);
	}
	private StringType stringType;
	private boolean internationalizeDigits;
	private String casPrintFormPI;
	private ScientificFormatAdapter sf;
	private NumberFormatAdapter nf;
	/**
	 * Creates default string template
	 */
	protected StringTemplate(){
		internationalizeDigits = true;
		setType(StringType.GEOGEBRA);
	}
	/**
	 * Returns string type of resulting text
	 * @return string type
	 */
	public StringType getStringType(){
		return this.stringType;
	}
	/**
	 * Disables international digits, e.g. for CAS and XML
	 * @return true if we want to allow e.g. arabic digits in output
	 */
	public boolean internationalizeDigits(){
		return this.internationalizeDigits;
	}
	
	/**
	 * 
	 * @return string representation of PI in this template
	 */
	public String getPi(){
		return casPrintFormPI;
	}
	
	public static StringTemplate get(StringType t){
		if(t==null||t.equals(StringType.GEOGEBRA)){
			return defaultTemplate; 
		}
		StringTemplate tpl = new StringTemplate();
		tpl.setType(t);
		return tpl;
	}
	
	private void setType(StringType t) {
		stringType = t;

		switch (t) {
		case MATH_PIPER:
			casPrintFormPI = "Pi";
			break;

		case MAXIMA:
			casPrintFormPI = "%pi";
			break;

		case JASYMCA:
		case GEOGEBRA_XML:
			casPrintFormPI = "pi";
			break;

		case MPREDUCE:
			casPrintFormPI = "pi";
			break;

		case LATEX:
			casPrintFormPI = "\\pi";
			break;

		default:
			casPrintFormPI = Unicode.PI_STRING;
		}


		
	}

	public boolean useScientific(boolean defau){
		return defau;
	}
	
	public boolean hasType(StringType t){
		return stringType.equals(t);
	}

	
	/**
	 * @param type string type
	 * @param decimals number of decimal places
	 * @return template
	 */
	public static StringTemplate printDecimals(StringType type, int decimals) {
		StringTemplate tpl = new StringTemplate(){
			public boolean useScientific(boolean defau){
				return false;
			}
		};
		tpl.setType(type);
		geogebra.common.factories.FormatFactory.prototype.getNumberFormat(decimals);
		return tpl;
	}
	
	public static StringTemplate printFigures(StringType mpreduce, int decimals) {
		StringTemplate tpl = new StringTemplate(){
			public boolean useScientific(boolean defau){
				return true;
			}
		};
		tpl.setType(mpreduce);
		geogebra.common.factories.FormatFactory.prototype.getScientificFormat(decimals,20,false);
		return tpl;
	}

	public ScientificFormatAdapter getSF() {
		return sf;
	}
	
	public NumberFormatAdapter getNF() {
		return nf;
	}
}
