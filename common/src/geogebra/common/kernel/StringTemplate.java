package geogebra.common.kernel;

import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.ListValue;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.plugin.Operation;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
/**
 * StringTemplate provides a container for all settings we might need
 * when serializing ExpressionValues to screen / XML / CAS input / export.
 * @author Zbynek Konecny
 */
public class StringTemplate {
	
	private final String name;
	/**
	 * Default template, but do not localize commands
	 */
	public static final StringTemplate noLocalDefault = new StringTemplate("nonLocalDefault");
	static{
		noLocalDefault.localizeCmds = false;
	}
	
	/**
	 * Template which prints numbers with maximal precision and adds prefix to 
	 * variables (ggbtmpvar)
	 */
	public static final StringTemplate prefixedDefault = new StringTemplate("prefixedDefault"){
		@Override
		public boolean allowsRoundHack(double abs, NumberFormatAdapter nf2,ScientificFormatAdapter sf2){
			return false;
		}
	};
	
	/**
	 * @return whether line breaks are allowed
	 */
	public boolean isInsertLineBreaks(){
		return false;
	}
	
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
	public static final StringTemplate defaultTemplate = new StringTemplate("defaultTempplate");
	
	/**
	 * Template which prints original construction's labels
	 */
	public static final StringTemplate realTemplate = new StringTemplate("realTemplate");
	static {
		realTemplate.useRealLabels = true;
	}
	
	/**
	 * LaTeX string type, do not internationalize digits
	 */
	public static final StringTemplate latexTemplate = new StringTemplate("latexTemplate");
	static {
		latexTemplate.setType(StringType.LATEX);
	}
	
	/**
	 * MathML string type, do not internationalize digits
	 */
	public static final StringTemplate mathmlTemplate = new StringTemplate("mathmlTemplate");
	static {
		mathmlTemplate.setType(StringType.MATHML);
	}
	
	/**
	 * LibreOffice string type, do not internationalize digits
	 */
	public static final StringTemplate libreofficeTemplate = new StringTemplate("libreOfficeTemplate");
	static {
		libreofficeTemplate.setType(StringType.LIBRE_OFFICE);
	}
	
	/**
	 * MPReduce string type, do not internationalize digits
	 */
	public static final StringTemplate casTemplate = new StringTemplate("casTemplate");
	static {
		casTemplate.internationalizeDigits = false;
		casTemplate.usePrefix = false;
		casTemplate.forceNF = true;
		casTemplate.localizeCmds = false;
		casTemplate.setType(StringType.MPREDUCE);
		casTemplate.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(15);
	}
	
	/**
	 * giac string type, do not internationalize digits
	 */
	public static final StringTemplate giacTemplate = new StringTemplate("giacTemplate");
	static {
		giacTemplate.internationalizeDigits = false;
		giacTemplate.usePrefix = false;
		giacTemplate.forceNF = true;
		giacTemplate.localizeCmds = false;
		giacTemplate.setType(StringType.GIAC);
		giacTemplate.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(15);
	}
	
	/**
	 * XML string type, do not internationalize digits
	 */
	public static final StringTemplate xmlTemplate = new StringTemplate("xmlTemplate"){
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
	public static StringTemplate editTemplate = new StringTemplate("editTemplate");
	/**
	 * For simplicity make this static now and see in the future whether we will need more engines in one app
	 */
	public static boolean latexIsMathQuill = true;
	static {
		editTemplate.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(GeoElement.MIN_EDITING_PRINT_PRECISION,20,false);
		editTemplate.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(GeoElement.MIN_EDITING_PRINT_PRECISION);
		editTemplate.allowMoreDigits = true;
	}
	/**
	 * Template for regression: uses 6 figures or 6 sig digits based on Kernel settings,
	 * string type is XML
	 */
	public static final StringTemplate regression = new StringTemplate("regression");
	static {
		regression.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(6,20,false);
		regression.nf = geogebra.common.factories.FormatFactory.prototype.getNumberFormat(6);
		regression.forceSF = true;
		regression.setType(StringType.GEOGEBRA_XML);
	}
	/**
	 * OGP string type
	 */
	public static final StringTemplate ogpTemplate = new StringTemplate("ogpTemplate");
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
	public static final StringTemplate maxPrecision = new StringTemplate("maxPrecision");
	static {
		maxPrecision.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(15,20,false);
		maxPrecision.allowMoreDigits = true;
		maxPrecision.forceSF = true;
	}
	/**
	 * Default template, just allow bigger precision for Numeric command
	 */

	public static final StringTemplate numericDefault = new StringTemplate("numericDefault");
	static{
		numericDefault.allowMoreDigits = true;
	}
	
	/**
	 * Not localized template, allow bigger precision for Numeric command
	 */

	public static final StringTemplate numericNoLocal = new StringTemplate("numericNoLocal");
	static{
		numericNoLocal.allowMoreDigits = true;
		numericNoLocal.localizeCmds = false;
	}
	
	/**
	 * Default LaTeX template, just allow bigger precision for Numeric command
	 */
	public static final StringTemplate numericLatex = new StringTemplate("numericLatex");
	static{
		numericLatex.stringType = StringType.LATEX;
		numericLatex.allowMoreDigits = true;
		numericLatex.useRealLabels = true;
	}
	/** Generic template for CAS tests */
	public static final StringTemplate testTemplate = new StringTemplate("testTemplate");
	static {
		testTemplate.internationalizeDigits = false;
		testTemplate.setType(StringType.GEOGEBRA_XML);
		//testTemplate.localizeCmds = false;
		testTemplate.sf = geogebra.common.factories.FormatFactory.prototype.getScientificFormat(15,20,false);
	}
	/** Template for CAS tests involving Numeric command*/
	public static final StringTemplate testNumeric = new StringTemplate("testNumeric");
	static {
		testNumeric.internationalizeDigits = false;
		testNumeric.setType(StringType.GEOGEBRA_XML);
		//testNumeric.localizeCmds = false;
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
	 * @param name name for debugging
	 */
	protected StringTemplate(String name){
		internationalizeDigits = true;
		localizeCmds = true;
		setType(StringType.GEOGEBRA);
		this.name = name;
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
		StringTemplate tpl = new StringTemplate("TemplateFor:"+t);
		tpl.setType(t);
		return tpl;
	}
	
	private void setType(StringType t) {
		stringType = t;

		switch (t) {
		case GIAC:
			casPrintFormPI = "%pi";
			break;

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
		StringTemplate tpl = new StringTemplate("TemplateFor:"+type+",Decimals:"+decimals+","+allowMore);
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
		StringTemplate tpl = new StringTemplate("TemplateFor:"+type+",Figures:"+decimals+","+allowMore);
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
		StringTemplate tpl = new StringTemplate("FullFiguresFor:"+type); 
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
		StringTemplate tpl = new StringTemplate("TemplateForScientific:"+type+",Decimals:"+decimals+","+allowMore);
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
	/**
	 * Objects in macros have two different labels.
	 * @return whether label within original (true) or current (false) construction should be used
	 */
	public boolean isUseRealLabels() {
		return useRealLabels;
	}

	/**
	 * @return copy of this  template that prints real labels
	 */
	public StringTemplate deriveReal() {
		StringTemplate copy = copy();
		copy.useRealLabels = true;
		return copy;
	}
	private StringTemplate copy() {
		StringTemplate result = new StringTemplate("CopyOf:"+name);
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
	
	@Override
	public String toString(){
		return name;
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
		case GIAC:
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
	
	/**
	 * @return copy of this, with string type set to StringType.MATHML
	 */
	public StringTemplate deriveMathMLTemplate() {

		if (stringType.equals(StringType.MATHML)) {
			return this;
		}
		
		StringTemplate ret = this.copy();
		
		ret.setType(StringType.MATHML);
		
		return ret;
	}
		
	/**
	 * @return copy of this, with string type set to StringType.LATEX
	 */
	public StringTemplate deriveLaTeXTemplate() {
		
		if (stringType.equals(StringType.LATEX)) {
			return this;
		}
		
		StringTemplate ret = this.copy();
		
		ret.setType(StringType.LATEX);
		
		return ret;
	}

	/**
	 * @return whether stringType is for a CAS (Giac, MPReduce, MathPiper)
	 */
	public boolean hasCASType() {
		return stringType.equals(StringType.MPREDUCE)
				|| stringType.equals(StringType.GIAC);
	}
	
	/**
	 * @return whether this is using MathQuill or not, may return rubbish for non-LaTeX templates
	 */
	public boolean isMathQuill() {
		return StringTemplate.latexIsMathQuill;
	}
	
	public String plusString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr, boolean valueForm, StringTemplate tpl){
		StringBuilder sb = new StringBuilder();
		Operation operation = Operation.PLUS;
		switch (stringType) {
		case MATHML:
			ExpressionNode.mathml(sb, "<plus/>", leftStr, rightStr);
			break;
		case GIAC:
			// don't use isNumberValue(), isListValue as those lead to an evaluate()
			if (left instanceof ListValue && right instanceof NumberValue) {
				//App.debug(left.getClass()+" "+right.getClass());
				// eg {1,2,3} + 10
				sb.append("seq((");
				sb.append(leftStr);
				sb.append(")[j]+");
				sb.append(rightStr);
				sb.append(",j,0,");
				sb.append(((ListValue)left).size()-1);
				sb.append(')');

			// don't use isNumberValue(), isListValue as those lead to an evaluate()
			} else if ((left instanceof NumberValue) && right instanceof ListValue) {
				App.debug(left.getClass()+" "+right.getClass());
				// eg 10 + {1,2,3}
				sb.append("seq((");
				sb.append(rightStr);
				sb.append(")[j]+");
				sb.append(leftStr);
				sb.append(",j,0,");
				sb.append(((ListValue)right).size()-1);
				sb.append(')');
				
			// instanceof VectorValue rather than isVectorValue() as ExpressionNode can return true
				// don't use isNumberValue(), isListValue as those lead to an evaluate()
			} else if (left instanceof NumberValue && right instanceof VectorValue && ((VectorValue)right).getMode() != Kernel.COORD_COMPLEX) {
				
				//App.debug(leftStr+" "+left.getClass());
				//App.debug(rightStr+" "+right.getClass());
				// eg 10 + (1,2)
				sb.append("((");
				sb.append(rightStr);
				sb.append(")[0]+");
				sb.append(leftStr);
				sb.append(",(");
				sb.append(rightStr);
				sb.append(")[1]+");
				sb.append(leftStr);
				sb.append(')');

			// instanceof VectorValue rather than isVectorValue() as ExpressionNode can return true
			} else if (left instanceof VectorValue && (right instanceof NumberValue) && ((VectorValue)left).getMode() != Kernel.COORD_COMPLEX) {
				//App.debug(left.getClass()+" "+right.getClass());
				// eg (1,2) + 10
				sb.append("((");
				sb.append(leftStr);
				sb.append(")[0]+");
				sb.append(rightStr);
				sb.append(",(");
				sb.append(leftStr);
				sb.append(")[1]+");
				sb.append(rightStr);
				sb.append(')');

			// don't use isNumberValue() as that leads to an evaluate()
			} else if ((left instanceof NumberValue) && right.isVector3DValue()) {
				//App.debug(left.getClass()+" "+right.getClass());
				// eg 10 + (1,2,3)
				sb.append("((");
				sb.append(rightStr);
				sb.append(")[0]+");
				sb.append(leftStr);
				sb.append(",(");
				sb.append(rightStr);
				sb.append(")[1]+");
				sb.append(leftStr);
				sb.append(",(");
				sb.append(rightStr);
				sb.append(")[2]+");
				sb.append(leftStr);
				sb.append(')');

				// don't use isNumberValue() as that leads to an evaluate()
			} else if (left.isVector3DValue() && right instanceof NumberValue) {
				//App.debug(left.getClass()+" "+right.getClass());
				// eg (1,2,3) + 10
				sb.append("((");
				sb.append(leftStr);
				sb.append(")[0]+");
				sb.append(rightStr);
				sb.append(",(");
				sb.append(leftStr);
				sb.append(")[1]+");
				sb.append(rightStr);
				sb.append(",(");
				sb.append(leftStr);
				sb.append(")[2]+");
				sb.append(rightStr);
				sb.append(')');

			} else {
				//App.debug(left.getClass()+" "+right.getClass());

				sb.append('(');
				sb.append(leftStr);
				sb.append(")+(");
				sb.append(rightStr);
				sb.append(')');
			}
			break;

		case MPREDUCE:
			sb.append("addition(");
			sb.append(leftStr);
			sb.append(',');
			sb.append(rightStr);
			sb.append(')');
			break;

		default:
			// check for 0
			if (valueForm) {
				if (ExpressionNode.isEqualString(left, 0, !valueForm)) {						
					ExpressionNode.append(sb, rightStr, right, operation, this);
					break;
				} else if (ExpressionNode.isEqualString(right, 0, !valueForm)) {
					ExpressionNode.append(sb, leftStr, left, operation, this);
					break;
				}
			}

			if (left instanceof Equation) {
				sb.append(leftBracket());
				sb.append(leftStr);
				sb.append(rightBracket());
			} else {
				sb.append(leftStr);
			}

			// we need parantheses around right text
			// if right is not a leaf expression or
			// it is a leaf GeoElement without a label (i.e. it is
			// calculated somehow)
			if (left.evaluatesToText()
					&& (!right.isLeaf() || (right.isGeoElement() && !((GeoElement) right)
							.isLabelSet()))) {
				if (stringType.equals(StringType.LATEX)
						&& tpl.isInsertLineBreaks()) {
					sb.append(" \\-+ ");
				} else {
					sb.append(" + ");
				}
				sb.append(leftBracket());
				sb.append(rightStr);
				sb.append(rightBracket());
			} else {
				if (rightStr.charAt(0) == '-') { // convert + - to -
					if (stringType.equals(StringType.LATEX)
							&& tpl.isInsertLineBreaks()) {
						sb.append(" \\-- ");
					} else {
						sb.append(" - ");
					}
					sb.append(rightStr.substring(1));
				} else if (rightStr
						.startsWith(Unicode.RightToLeftUnaryMinusSign)) { // Arabic
					// convert
					// +
					// -
					// to
					// -
					if (stringType.equals(StringType.LATEX)
							&& tpl.isInsertLineBreaks()) {
						sb.append(" \\-- ");
					} else {
						sb.append(" - ");
					}
					sb.append(rightStr.substring(3));
				} else {
					if (stringType.equals(StringType.LATEX)
							&& tpl.isInsertLineBreaks()) {
						sb.append(" \\-+ ");
					} else {
						sb.append(" + ");
					}
					sb.append(rightStr);
				}
			}
			break;
		}
		return sb.toString();

	}
	
	public String leftBracket() {
		//return (type.equals(StringType.LATEX)) ? " \\left( " : "(";

		if (stringType.equals(StringType.LATEX))
			return " \\left( ";
		else if (stringType.equals(StringType.LIBRE_OFFICE))
			return " left ( ";
		else 
			return "(";
	}

	public String rightBracket() {
		//return (type.equals(StringType.LATEX)) ? " \\right) " : ")";

		if (stringType.equals(StringType.LATEX))
			return " \\right)";
		else if (stringType.equals(StringType.LIBRE_OFFICE))
			return " right )";
		else 
			return ")";
	}
	public String minusString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr, boolean valueForm,
			StringTemplate tpl) {
		StringBuilder sb = new StringBuilder(); 
		switch (stringType) {
		case MATHML:
			ExpressionNode.mathml(sb, "<minus/>", leftStr, rightStr);
			break;
		case GIAC:
			// don't use isNumberValue(), isListValue as those lead to an evaluate()
			if (left instanceof ListValue && right instanceof NumberValue) {
				// eg {1,2,3} - 10
				sb.append("seq((");
				sb.append(leftStr);
				sb.append(")[j]-(");
				sb.append(rightStr);
				sb.append("),j,0,");
				sb.append(((ListValue)left).size()-1);
				sb.append(')');

				// don't use isNumberValue(), isListValue as those lead to an evaluate()
			} else if (left instanceof NumberValue && right instanceof ListValue) {
				// eg 10 - {1,2,3}
				sb.append("seq(");
				sb.append(leftStr);
				sb.append("-(");
				sb.append(rightStr);
				sb.append(")[j]");
				sb.append(",j,0,");
				sb.append(((ListValue)right).size()-1);
				sb.append(')');
			
			// instanceof VectorValue rather than isVectorValue() as ExpressionNode can return true
				// don't use isNumberValue(), isListValue as those lead to an evaluate()
			} else if (left instanceof NumberValue && right instanceof VectorValue && ((VectorValue)right).getMode() != Kernel.COORD_COMPLEX) {
				// eg 10 - (1,2)
				sb.append("(");
				sb.append(leftStr);
				sb.append("-(");
				sb.append(rightStr);
				sb.append(")[0]");
				sb.append(",");
				sb.append(leftStr);
				sb.append("-(");
				sb.append(rightStr);
				sb.append(")[1])");

			// instanceof VectorValue rather than isVectorValue() as ExpressionNode can return true
				// don't use isNumberValue(), isListValue as those lead to an evaluate()
			} else if (left instanceof VectorValue && right instanceof NumberValue && ((VectorValue)left).getMode() != Kernel.COORD_COMPLEX) {
				// eg (1,2) - 10
				sb.append("((");
				sb.append(leftStr);
				sb.append(")[0]-(");
				sb.append(rightStr);
				sb.append("),(");
				sb.append(leftStr);
				sb.append(")[1]-(");
				sb.append(rightStr);
				sb.append("))");

				// don't use isNumberValue(), isListValue as those lead to an evaluate()
			} else if (left instanceof NumberValue && right.isVector3DValue()) {
				// eg 10 - (1,2,3)
				sb.append("(");
				sb.append(leftStr);
				sb.append("-(");
				sb.append(rightStr);
				sb.append(")[0]");
				sb.append(",");
				sb.append(leftStr);
				sb.append("-(");
				sb.append(rightStr);
				sb.append(")[1]");
				sb.append(",");
				sb.append(leftStr);
				sb.append("-(");
				sb.append(rightStr);
				sb.append(")[2])");

				// don't use isNumberValue(), isListValue as those lead to an evaluate()
			} else if (left.isVector3DValue() && right instanceof NumberValue) {
				// eg (1,2,3) - 10
				sb.append("((");
				sb.append(leftStr);
				sb.append(")[0]-(");
				sb.append(rightStr);
				sb.append("),(");
				sb.append(leftStr);
				sb.append(")[1]-(");
				sb.append(rightStr);
				sb.append("),(");
				sb.append(leftStr);
				sb.append(")[2]-(");
				sb.append(rightStr);
				sb.append("))");


			} else {

				sb.append('(');
				sb.append(leftStr);
				sb.append(")-(");
				sb.append(rightStr);
				sb.append(')');
			}
			break;

		case MPREDUCE:
			sb.append("subtraction(");
			sb.append(leftStr);
			sb.append(',');
			sb.append(rightStr);
			sb.append(')');
			break;

		default:
			if (left instanceof Equation) {
				sb.append(tpl.leftBracket());
				sb.append(leftStr);
				sb.append(tpl.rightBracket());
			} else {
				sb.append(leftStr);
			}

			// check for 0 at right
			if (valueForm
					&& rightStr
					.equals(left.getKernel().getLocalization().unicodeZero + "")) {
				break;
			}

			if (right.isLeaf()
					|| (ExpressionNode.opID(right) >= Operation.MULTIPLY.ordinal())) { // not
				// +,
				// -

				if (rightStr.charAt(0) == '-') { // convert - - to +
					if (stringType.equals(StringType.LATEX)
							&& tpl.isInsertLineBreaks()) {
						sb.append(" \\-+ ");
					} else {
						sb.append(" + ");
					}
					sb.append(rightStr.substring(1));
				} else if (rightStr
						.startsWith(Unicode.RightToLeftUnaryMinusSign)) { // Arabic
					// convert
					// -
					// -
					// to
					// +
					if (stringType.equals(StringType.LATEX)
							&& tpl.isInsertLineBreaks()) {
						sb.append(" \\-+ ");
					} else {
						sb.append(" + ");
					}
					sb.append(rightStr.substring(3));
				} else {
					if (stringType.equals(StringType.LATEX)
							&& tpl.isInsertLineBreaks()) {
						sb.append(" \\-- ");
					} else {
						sb.append(" - ");
					}
					sb.append(rightStr);
				}
			} else {
				// fix for changing height in Algebra View plus / minus
				if (stringType.equals(StringType.LATEX)
						&& tpl.isInsertLineBreaks()) {
					sb.append(" \\-- ");
				} else {
					sb.append(" - ");
				}
				sb.append(tpl.leftBracket());
				sb.append(rightStr);
				sb.append(tpl.rightBracket());
			}
			break;
		}
		return sb.toString();
	}
	public String multiplyString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr, boolean valueForm,
			StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		Operation operation = Operation.MULTIPLY;
		Localization loc = left.getKernel().getLocalization();
		switch (stringType) {

		case MATHML:
			ExpressionNode.mathml(sb, "<times/>", leftStr, rightStr);
			break;
		default:
			// check for 1 at left
			if (ExpressionNode.isEqualString(left, 1, !valueForm)) {
				ExpressionNode.append(sb, rightStr, right, operation, tpl);
				break;
			}
			// check for 1 at right
			else if (ExpressionNode.isEqualString(right, 1, !valueForm)) {
				ExpressionNode.append(sb, leftStr, left, operation, tpl);
				break;
			}

			// removed 0 handling due to problems with functions,
			// e.g 0 * x + 1 becomes 0 + 1 and no longer is a function
			// // check for 0 at left
			// else if (valueForm && isEqualString(left, 0, !valueForm)) {
			// sb.append("0");
			// break;
			// }
			// // check for 0 at right
			// else if (valueForm && isEqualString(right, 0, !valueForm)) {
			// sb.append("0");
			// break;
			// }

			// check for degree sign or 1degree or degree1 (eg for Arabic)
			else if (((rightStr.length() == 2) && (((rightStr.charAt(0) == Unicode.degreeChar) && (rightStr
					.charAt(1) == (loc.unicodeZero + 1))) || ((rightStr
							.charAt(0) == Unicode.degreeChar) && (rightStr
									.charAt(1) == (loc.unicodeZero + 1)))))
									|| rightStr.equals(Unicode.degree)) {

				boolean rtl = loc.isRightToLeftDigits(tpl);

				if (rtl) {
					sb.append(Unicode.degree);
				}

				if (!left.isLeaf()) {
					sb.append('('); // needed for eg (a+b)\u00b0
				}
				sb.append(leftStr);
				if (!left.isLeaf()) {
					sb.append(')'); // needed for eg (a+b)\u00b0
				}

				if (!rtl) {
					sb.append(Unicode.degree);
				}

				break;
			}

		case LATEX:
		case LIBRE_OFFICE:

			boolean nounary = true;

			// vector * (matrix * vector) needs brackets; always use brackets for internal templates
			if (!tpl.isPrintLocalizedCommandNames() || (left.isListValue() && right.isVectorValue())) {
				sb.append(tpl.leftBracket());
			}

			// left wing
			if (left.isLeaf()
					|| (ExpressionNode.opID(left) >= Operation.MULTIPLY.ordinal())) { // not
				// +,
				// -
				if (ExpressionNode.isEqualString(left, -1, !valueForm)) { // unary minus
					nounary = false;
					sb.append('-');
				} else {
					if (leftStr
							.startsWith(Unicode.RightToLeftUnaryMinusSign)) {
						// brackets needed for eg Arabic digits
						sb.append(Unicode.RightToLeftMark);
						sb.append(tpl.leftBracket());
						sb.append(leftStr);
						sb.append(tpl.rightBracket());
						sb.append(Unicode.RightToLeftMark);
					} else {
						sb.append(leftStr);
					}
				}
			} else {
				sb.append(tpl.leftBracket());
				sb.append(leftStr);
				sb.append(tpl.rightBracket());
			}

			// right wing
			int opIDright = ExpressionNode.opID(right);
			if (right.isLeaf()
					|| (opIDright >= Operation.MULTIPLY.ordinal())) { // not
				// +,
				// -
				boolean showMultiplicationSign = false;
				boolean multiplicationSpaceNeeded = true;
				if (nounary) {
					switch (stringType) {
					case PGF:
					case PSTRICKS:
					case GEOGEBRA_XML:
					case GIAC:
						showMultiplicationSign = true;
						break;

					case LIBRE_OFFICE:
					case LATEX:
						// check if we need a multiplication sign, see #414
						// digit-digit, e.g. 3 * 5
						// digit-fraction, e.g. 3 * \frac{5}{2}
						char lastLeft = leftStr
						.charAt(leftStr.length() - 1);
						char firstRight = rightStr.charAt(0);
						showMultiplicationSign =
								// left is digit or ends with }, e.g. exponent,
								// fraction
								(StringUtil.isDigit(lastLeft) || (lastLeft == '}'))
								&&
								// right is digit or fraction
								(StringUtil.isDigit(firstRight) || rightStr
										.startsWith("\\frac"));
						break;

					default: // GeoGebra syntax
						char firstLeft = leftStr.charAt(0);
						lastLeft = leftStr.charAt(leftStr.length() - 1);
						firstRight = rightStr.charAt(0);
						// check if we need a multiplication sign, see #414
						// digit-digit, e.g. 3 * 5
						showMultiplicationSign = Character
								.isDigit(lastLeft)
								&& (StringUtil.isDigit(firstRight)
										// 3*E23AB can't be written 3E23AB
										|| (rightStr.charAt(0) == 'E'));
						// check if we need a multiplication space:
						multiplicationSpaceNeeded = showMultiplicationSign;
						if (!multiplicationSpaceNeeded) {
							// check if we need a multiplication space:
							// it's needed except for number * character,
							// e.g. 23x
							// need to check start and end for eg A1 * A2
							boolean leftIsNumber = left.isLeaf()
									&& (StringUtil.isDigit(firstLeft) || (firstLeft == '-'))
									&& StringUtil.isDigit(lastLeft);

							// check if we need a multiplication space:
							// all cases except number * character, e.g. 3x
							multiplicationSpaceNeeded = showMultiplicationSign
									|| !(leftIsNumber && !Character
											.isDigit(firstRight));
						}
					}

					if (stringType.equals(StringType.LATEX)
							&& tpl.isInsertLineBreaks()) {
						sb.append("\\-");
					}

					if (showMultiplicationSign) {
						sb.append(multiplicationSign());
					} else if (multiplicationSpaceNeeded) {
						// space instead of multiplication sign
						sb.append(multiplicationSpace());
					}
				}

				boolean rtlMinus;
				// show parentheses around these cases
				if (((rtlMinus = rightStr
						.startsWith(Unicode.RightToLeftUnaryMinusSign)) || (rightStr
								.charAt(0) == '-')) // 2 (-5) or -(-5)
								|| (!nounary && !right.isLeaf() && (opIDright <= Operation.DIVIDE
								.ordinal() // -(x * a) or -(x / a)
										))
										|| (showMultiplicationSign && stringType
												.equals(StringType.GEOGEBRA))) // 3 (5)
				{
					if (rtlMinus) {
						sb.append(Unicode.RightToLeftMark);
					}
					sb.append(tpl.leftBracket());
					sb.append(rightStr);
					sb.append(tpl.rightBracket());
					if (rtlMinus) {
						sb.append(Unicode.RightToLeftMark);
					}
				} else {
					// -1.0 * 5 becomes "-5"
					sb.append(rightStr);
				}
			} else { // right is + or - tree
				if (nounary) {
					switch (stringType) {
					case PGF:
					case PSTRICKS:
					case GEOGEBRA_XML:
					case GIAC:
						sb.append(multiplicationSign());
						break;

					default:
						// space instead of multiplication sign
						sb.append(multiplicationSpace());
					}
				}
				sb.append(tpl.leftBracket());
				sb.append(rightStr);
				sb.append(tpl.rightBracket());
			}
			
			// vector * (matrix * vector) needs brackets; always use brackets for internal templates
			if (!tpl.isPrintLocalizedCommandNames() || (left.isListValue() && right.isVectorValue())) {
				sb.append(tpl.rightBracket());
			}

			break;

		case MPREDUCE:

			if (ExpressionNode.isEqualString(left, -1, !valueForm)) {
				sb.append("-(");
				sb.append(rightStr);
				sb.append(')');
			} else {
				sb.append("multiplication(");
				sb.append(leftStr);
				sb.append(",");
				sb.append(rightStr);
				sb.append(")");
				break;
			}
			break;

		case GIAC:

			if (ExpressionNode.isEqualString(left, -1, !valueForm)) {
				sb.append("-(");
				sb.append(rightStr);
				sb.append(')');
			} else {
				sb.append("(");
				sb.append(leftStr);
				sb.append(")*(");
				sb.append(rightStr);
				sb.append(")");
				break;
			}
			break;


		}
		return sb.toString();

	}
	
	private String multiplicationSign() {
		switch (stringType) {
		case LATEX:
			return " \\cdot ";

		case LIBRE_OFFICE:
			return " cdot ";

		case GEOGEBRA:
			return " "; // space for multiplication

		default:
			return " * ";
		}
	}
	
	private String multiplicationSpace() {
		// wide space for multiplicatoin space in LaTeX
		return (stringType.equals(StringType.LATEX)) ? " \\; " : " ";
	}
	
	
}
