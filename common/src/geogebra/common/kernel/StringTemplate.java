package geogebra.common.kernel;

import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.common.util.Unicode;

public class StringTemplate {
	
	public static StringTemplate defaultTemplate = new StringTemplate();
	static {
		defaultTemplate.casPrintFormPI=Unicode.PI_STRING;
		defaultTemplate.internationalizeDigits = true;
		defaultTemplate.stringType=StringType.GEOGEBRA;
		
	}
	public static StringTemplate editTemplate = new StringTemplate();
	static {
		editTemplate.casPrintFormPI=Unicode.PI_STRING;
		editTemplate.internationalizeDigits = true;
		editTemplate.stringType=StringType.GEOGEBRA;
		editTemplate.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(GeoElement.MIN_EDITING_PRINT_PRECISION,20,false);
		editTemplate.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(GeoElement.MIN_EDITING_PRINT_PRECISION);
	}
	private StringType stringType;
	private boolean internationalizeDigits;
	private String casPrintFormPI;
	private ScientificFormatAdapter sf;
	private NumberFormatAdapter nf;
	private StringTemplate(){}
	
	public StringType getStringType(){
		return this.stringType;
	}
	public boolean internationalizeDigits(){
		return this.internationalizeDigits;
	}
	
	public String getPi(){
		return casPrintFormPI;
	}
	
	public static StringTemplate get(StringType t){
		if(t==null||t.equals(StringType.GEOGEBRA)){
			return defaultTemplate; 
		}
		StringTemplate tpl = new StringTemplate();
		tpl.stringType = t;

		switch (t) {
		case MATH_PIPER:
			tpl.casPrintFormPI = "Pi";
			break;

		case MAXIMA:
			tpl.casPrintFormPI = "%pi";
			break;

		case JASYMCA:
		case GEOGEBRA_XML:
			tpl.casPrintFormPI = "pi";
			break;

		case MPREDUCE:
			tpl.casPrintFormPI = "pi";
			break;

		case LATEX:
			tpl.casPrintFormPI = "\\pi";
			break;

		default:
			tpl.casPrintFormPI = Unicode.PI_STRING;
		}

		return tpl;
	}
	
	public boolean hasType(StringType t){
		return stringType.equals(t);
	}

	public static StringTemplate get(StringType mpreduce, boolean b) {
		StringTemplate tpl = get(mpreduce);
		tpl.internationalizeDigits=b;
		return tpl;
	}

	public ScientificFormatAdapter getSF() {
		return sf;
	}
	
	public NumberFormatAdapter getNF() {
		return nf;
	}
}
