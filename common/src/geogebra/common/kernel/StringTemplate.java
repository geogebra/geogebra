package geogebra.common.kernel;

import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.util.Unicode;

public class StringTemplate {
	
	private static StringTemplate defaultTemplate = new StringTemplate();
	static {
		defaultTemplate.casPrintFormPI=Unicode.PI_STRING;
		defaultTemplate.internationalizeDigits = true;
		defaultTemplate.stringType=StringType.GEOGEBRA;
	}
	private StringType stringType;
	private boolean internationalizeDigits;
	private String casPrintFormPI;
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
}
