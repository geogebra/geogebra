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
		prefixedDefault.localizeCmds = false;
		prefixedDefault.internationalizeDigits = false;
		prefixedDefault.forceNF = true;
		prefixedDefault.usePrefix = true;
		prefixedDefault.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(15);
	}
	/**
	 * GeoGebra string type, internationalize digits
	 */
	public static final StringTemplate defaultTemplate = new StringTemplate();
	
	public static final StringTemplate realTemplate = new StringTemplate();
	static {
		realTemplate.useRealLabels = true;
	}
	
	/**
	 * LaTeX string type, do not internationalize digits
	 */
	public static final StringTemplate latexTemplate = new StringTemplate();
	static {
		latexTemplate.setType(StringType.LATEX);
	}
	
	/**
	 * MathML string type, do not internationalize digits
	 */
	public static final StringTemplate mathmlTemplate = new StringTemplate();
	static {
		mathmlTemplate.setType(StringType.MATHML);
	}
	
	/**
	 * LibreOffice string type, do not internationalize digits
	 */
	public static final StringTemplate libreofficeTemplate = new StringTemplate();
	static {
		libreofficeTemplate.setType(StringType.LIBRE_OFFICE);
	}
	
	/**
	 * MPReduce string type, do not internationalize digits
	 */
	public static final StringTemplate casTemplate = new StringTemplate();
	static {
		casTemplate.internationalizeDigits = false;
		casTemplate.usePrefix = false;
		casTemplate.forceNF = true;
		casTemplate.localizeCmds = false;
		casTemplate.setType(StringType.MPREDUCE);
		casTemplate.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(15);
	}
	
	/**
	 * XML string type, do not internationalize digits
	 */
	public static final StringTemplate xmlTemplate = new StringTemplate(){
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
	public static final StringTemplate regression = new StringTemplate();
	static {
		regression.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(6,20,false);
		regression.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(6);
		regression.forceSF = true;
		regression.setType(StringType.GEOGEBRA_XML);
	}
	/**
	 * OGP string type
	 */
	public static final StringTemplate ogpTemplate = new StringTemplate();
	static {
		ogpTemplate.forceSF=false;
		ogpTemplate.internationalizeDigits = false;
		ogpTemplate.setType(StringType.OGP);
		ogpTemplate.localizeCmds = false;
		ogpTemplate.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(0);
	}
	/**
	 * Default template, just increases precision to max
	 */
	public static final StringTemplate maxPrecision = new StringTemplate();
	static {
		maxPrecision.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(15,20,false);
		maxPrecision.allowMoreDigits = true;
		maxPrecision.forceSF = true;
	}
	/**
	 * Default template, just allow bigger precision for Numeric command
	 */

	public static final StringTemplate numericDefault = new StringTemplate();
	static{
		numericDefault.allowMoreDigits = true;
	}
	
	/**
	 * Not localized template, allow bigger precision for Numeric command
	 */

	public static final StringTemplate numericNoLocal = new StringTemplate();
	static{
		numericNoLocal.allowMoreDigits = true;
		numericNoLocal.localizeCmds = false;
	}
	
	/**
	 * Default LaTeX template, just allow bigger precision for Numeric command
	 */
	public static final StringTemplate numericLatex = new StringTemplate();
	static{
		numericLatex.stringType = StringType.LATEX;
		numericLatex.allowMoreDigits = true;
		numericLatex.useRealLabels = true;
	}
	/** Generic template for CAS tests */
	public static final StringTemplate testTemplate = new StringTemplate();
	static {
		testTemplate.internationalizeDigits = false;
		testTemplate.setType(StringType.GEOGEBRA_XML);
		testTemplate.localizeCmds = false;
		testTemplate.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(15,20,false);
	}
	/** Template for CAS tests involving Numeric command*/
	public static final StringTemplate testNumeric = new StringTemplate();
	static {
		testNumeric.internationalizeDigits = false;
		testNumeric.setType(StringType.GEOGEBRA_XML);
		testNumeric.localizeCmds = false;
		testNumeric.allowMoreDigits = true;
		testNumeric.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(15,20,false);
	}
		
	private StringType stringType;
	private boolean internationalizeDigits;
	private String casPrintFormPI;
	private ScientificFormatAdapter sf;
	private NumberFormatAdapter nf;
	private boolean forceSF;
	private boolean forceNF;
	private boolean allowMoreDigits;
	private boolean useRealLabels;

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
			
		case LIBRE_OFFICE:
			casPrintFormPI = "%pi";
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
	 * Prints the number to full double precision without using E notation 
	 *  
	 * @param type string type 
	 * @return template with given parameters 
	 */ 
	public static StringTemplate fullFigures(StringType type) { 
		StringTemplate tpl = new StringTemplate(); 
		tpl.forceSF = true; 
		tpl.allowMoreDigits = true; 
		tpl.setType(type); 
		// 308 doesn't seem to work for 1E-300, 350 seems OK 
		tpl.sf=geogebra.common.factories.FormatFactory.prototype.getScientificFormat(16,350,false); 
		return tpl; 
	} 	

	/**
	 * Scientific Notation (eg 2.3 * 4 ^ 5)
	 * 
	 * @param type string type
	 * @param decimals figures
	 * @param allowMore true to use kernel's precision, if it's higher
	 * @return template with given parameters
	 */
	public static StringTemplate printScientific(StringType type, int decimals,boolean allowMore) {
		StringTemplate tpl = new StringTemplate();
		tpl.forceSF = true;
		tpl.allowMoreDigits = allowMore;
		tpl.setType(type);
		tpl.sf=geogebra.common.factories.FormatFactory.prototype.getScientificFormat(decimals,20,true);
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
	/**
	 * @return true if more digits than what is set by this template are allowed in output
	 */
	public boolean allowMoreDigits() {
		return allowMoreDigits;
	}
	
	private double[] precisions = new double[]{1,1E-1,1E-2,1E-3,1E-4,1E-5,1E-6,1E-7,
			1E-8,1E-9,1E-10,1E-11,1E-12,1E-13,1E-14,1E-15,1E-16};
	/**
	 * Least positive number with given precision
	 * @param nf2 kernel's number format
	 * @return 10^(-number of digits)
	 */
	public double getPrecision(NumberFormatAdapter nf2) {
		int digits = getNF(nf2).getMaximumFractionDigits();
		return digits <=16 ? precisions[digits] : Math.pow(10, -digits);
	}
	public boolean isUseRealLabels() {
		return useRealLabels;
	}
	
	public StringTemplate deriveReal() {
		StringTemplate copy = copy();
		copy.useRealLabels = true;
		return copy;
	}
	private StringTemplate copy() {
		StringTemplate result = new StringTemplate();
		result.stringType = stringType;
		result.nf = nf;
		result.sf = sf;
		result.usePrefix = usePrefix;
		result.allowMoreDigits = allowMoreDigits;
		result.casPrintFormPI = casPrintFormPI;
		result.internationalizeDigits = internationalizeDigits;
		result.useRealLabels = useRealLabels;
		result.localizeCmds = localizeCmds;
		result.forceNF = forceNF;
		result.forceSF = forceSF;
		
		return result;
	}
	
	/**
	 * Returns the label depending on the current print form. When sending
	 * variables to the underlying CAS, we need to make sure that we don't
	 * overwrite variable names there, so we add the prefix
	 * ExpressionNodeConstants.GGBCAS_VARIABLE_PREFIX.

	 * @param label raw label without prefixes
	 * @return label depending on given string type
	 */
	public String printVariableName(final String label) {
		String ret;
		if (isUseTempVariablePrefix()) {
			ret = addTempVariablePrefix(label);
		}
		ret = printVariableName(getStringType(), label);
		
		 if (ret.length() == 1 && ret.equals("l") && hasType(StringType.LATEX)) {
			   ret = "\\ell";
		 }
		
		return ret;
	}
	

	final private static String printVariableName(final StringType printForm, final String label) {
		switch (printForm) {
		case MPREDUCE:
		case MAXIMA:
			// make sure we don't interfer with reserved names
			// or command names in the underlying CAS
			// see http://www.geogebra.org/trac/ticket/1051
			return addTempVariablePrefix(label.replace("$", ""));

		default:
			// standard case
			return label;
		}
	}
	
	/**
	 * Returns ExpressionNodeConstants.TMP_VARIABLE_PREFIX + label.
	 */
	private static String addTempVariablePrefix(final String label) {
		StringBuilder sb = new StringBuilder();
		// TMP_VARIABLE_PREFIX + label
		sb.append(Kernel.TMP_VARIABLE_PREFIX);
		sb.append(label);
		return sb.toString();
	}
		
	
	
}
