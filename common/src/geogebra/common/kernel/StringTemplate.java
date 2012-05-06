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
	 * Default template, but do not localize commands
	 */
	public static final StringTemplate noLocalDefault = new StringTemplate();
	static{
		noLocalDefault.localizeCmds = false;
	}
	
	/**
	 * Template which prints numbers with maximal precision and adds prefix to 
	 * variables (ggbtmpvar)
	 */
	public static final StringTemplate prefixedDefault = new StringTemplate(){
		@Override
		public boolean allowsRoundHack(double abs, NumberFormatAdapter nf2,ScientificFormatAdapter sf2){
			return false;
		}
	};
	static {
		prefixedDefault.forceSF = true;
		prefixedDefault.usePrefix = true;
		prefixedDefault.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(15,20,false);
	}
	/**
	 * GeoGebra string type, internationalize digits
	 */
	public static StringTemplate defaultTemplate = new StringTemplate();
	
	/**
	 * MPReduce string type, do not internationalize digits
	 */
	public static StringTemplate latexTemplate = new StringTemplate();
	static {
		latexTemplate.setType(StringType.LATEX);
	}
	
	/**
	 * MPReduce string type, do not internationalize digits
	 */
	public static StringTemplate casTemplate = new StringTemplate();
	static {
		casTemplate.internationalizeDigits = false;
		casTemplate.usePrefix = false;
		casTemplate.forceSF = true;
		casTemplate.localizeCmds = false;
		casTemplate.setType(StringType.MPREDUCE);
		casTemplate.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(15,20,false);
	}
	
	/**
	 * XML string type, do not internationalize digits
	 */
	public static StringTemplate xmlTemplate = new StringTemplate(){
		@Override
		public int getCoordStyle(int coordStyle) {
			return Kernel.COORD_STYLE_DEFAULT;
		}
	};
	static {
		xmlTemplate.forceSF=true;
		xmlTemplate.internationalizeDigits = false;
		xmlTemplate.setType(StringType.GEOGEBRA_XML);
		xmlTemplate.localizeCmds = false;
		xmlTemplate.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(15,20,false);
	}
	/**
	 * for input bar; same as default, but increases precision to MIN_EDITING_PRINT_PRECISION
	 */
	public static StringTemplate editTemplate = new StringTemplate();
	static {
		editTemplate.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(GeoElement.MIN_EDITING_PRINT_PRECISION,20,false);
		editTemplate.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(GeoElement.MIN_EDITING_PRINT_PRECISION);
		editTemplate.allowMoreDigits = true;
	}
	/**
	 * Template for regression: uses 6 figures or 6 sig digits based on Kernel settings,
	 * string type is XML
	 */
	public static StringTemplate regression = new StringTemplate();
	static {
		regression.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(6,20,false);
		regression.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(6);
		regression.setType(StringType.GEOGEBRA_XML);
	}
	/**
	 * OGP string type
	 */
	public static StringTemplate ogpTemplate = new StringTemplate();
	static {
		ogpTemplate.forceSF=true;
		ogpTemplate.internationalizeDigits = false;
		ogpTemplate.setType(StringType.OGP);
		ogpTemplate.localizeCmds = false;
		ogpTemplate.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(15,20,false);
	}
	/**
	 * Default template, just inccreases precision to max
	 */
	public static StringTemplate maxPrecision = new StringTemplate();
	static {
		maxPrecision.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(15,20,false);
	}
	/**
	 * Default template, just better output of arbitrary constants in CAS
	 */
	public static StringTemplate casCellTemplate = new StringTemplate();

	private boolean symbolicArbConst;
	
	static{
			casCellTemplate.symbolicArbConst = true;
	}
		
	private StringType stringType;
	private boolean internationalizeDigits;
	private String casPrintFormPI;
	private ScientificFormatAdapter sf;
	private NumberFormatAdapter nf;
	private boolean forceSF;
	private boolean forceNF;
	private boolean allowMoreDigits;

	private boolean localizeCmds;
	private boolean usePrefix;
	/**
	 * Creates default string template
	 */
	protected StringTemplate(){
		internationalizeDigits = true;
		localizeCmds = true;
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
	
	/**
	 * Creates new string template with given type
	 * @param t string type
	 * @return template
	 */
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
	/**
	 * Returns whether scientific format (sig digits) should be used
	 * (default templates return the input)
	 * @param kernelUsesSF kernel setting of SF
	 * @return  whether scientific format (sig digits) should be used
	 */
	public boolean useScientific(boolean kernelUsesSF){
		return forceSF || (kernelUsesSF && !forceNF);
	}
	
	/**
	 * Convenience method instead of getStringType().equals()
	 * @param t string type
	 * @return true if this template uses given type equals
	 */
	public boolean hasType(StringType t){
		return stringType.equals(t);
	}

	
	/**
	 * @param type string type
	 * @param decimals number of decimal places
	 * @param allowMore  true to use kernel's precision, if it's higher 
	 * @return template
	 */
	public static StringTemplate printDecimals(StringType type, int decimals,boolean allowMore) {
		StringTemplate tpl = new StringTemplate();
		tpl.forceNF = true;
		tpl.allowMoreDigits = allowMore;
		tpl.setType(type);
		tpl.nf=geogebra.common.factories.FormatFactory.prototype.getNumberFormat(decimals);
		return tpl;
	}
	
	/**
	 * @param type string type
	 * @param decimals figures
	 * @param allowMore true to use kernel's precision, if it's higher
	 * @return template with given parameters
	 */
	public static StringTemplate printFigures(StringType type, int decimals,boolean allowMore) {
		StringTemplate tpl = new StringTemplate();
		tpl.forceSF = true;
		tpl.allowMoreDigits = allowMore;
		tpl.setType(type);
		tpl.sf=geogebra.common.factories.FormatFactory.prototype.getScientificFormat(decimals,20,false);
		return tpl;
	}

	/**
	 * Receives default SF and returns SF to be used
	 * @param sfk default
	 * @return SF to be used
	 */
	public ScientificFormatAdapter getSF(ScientificFormatAdapter sfk) {
		return sf==null || (allowMoreDigits && sfk.getSigDigits()>sf.getSigDigits())?sfk:sf;
	}
	
	/**
	 * Receives default NF and returns NF to be used
	 * @param nfk default
	 * @return NF to be used
	 */
	public NumberFormatAdapter getNF(NumberFormatAdapter nfk) {
		return nf==null|| (allowMoreDigits && nfk.getMaximumFractionDigits()>nf.getMaximumFractionDigits())?nfk:nf;
	}
	/**
	 * Returns whether we need to localize commands
	 * @return true for localized, false for internal
	 */
	public boolean isPrintLocalizedCommandNames() {
		return localizeCmds;
	}
	/**
	 * Receives default style and returns style that should be actually used
	 * @param coordStyle Kernel.COORD_STYLE_*
	 * @return new style
	 */
	public int getCoordStyle(int coordStyle) {
		return coordStyle;
	}
	/**
	 * @return true if variable prefix should be used
	 */
	public boolean isUseTempVariablePrefix() {
		return usePrefix;
	}
	/**
	 * @return true if arbConst can be written as name string
	 */
	public boolean isSymbolicArbConst() {
		return symbolicArbConst;
	}
	
	/**
	 * Returns whether round hack is allowed for given number
	 * @param abs absolute value of number
	 * @param nf2 kernel's number format
	 * @param sf2 kernel's scientific format
	 * @return whether round hack is allowed for given number
	 */
	public boolean allowsRoundHack(double abs, NumberFormatAdapter nf2,ScientificFormatAdapter sf2) {
		if(abs < 1000)
			return true;
		if(abs > 10E7)
			return false;
		return (getNF(nf2)!=null && getNF(nf2).getMaximumFractionDigits() < 10)
			|| (getSF(sf2)!=null && getSF(sf2).getSigDigits() < 10);
	}
	
}
