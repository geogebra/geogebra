package org.geogebra.common.kernel;

import org.geogebra.common.export.MathmlTemplate;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * StringTemplate provides a container for all settings we might need when
 * serializing ExpressionValues to screen / XML / CAS input / export.
 * 
 * @author Zbynek Konecny
 */
public class StringTemplate implements ExpressionNodeConstants {

	// rounding hack, see Kernel.format()
	private static final double ROUND_HALF_UP_FACTOR = 1.0 + 1E-15;

	private final String name;

	private StringType stringType;
	private boolean internationalizeDigits;

	// form to serialise "pi" to
	private String printFormPI;

	// form to serialise sqrt(-1) to
	private String printFormImaginary;

	private ScientificFormatAdapter sf;
	private NumberFormatAdapter nf;
	private boolean forceSF;
	private boolean forceNF;
	private boolean allowMoreDigits;
	private boolean useRealLabels;

	private boolean localizeCmds;
	private boolean usePrefix;
	private boolean hideLHS = false;
	private boolean questionMarkForNaN = true;

	private boolean numeric = true;
	/**
	 * Default template, but do not localize commands
	 */
	public static final StringTemplate noLocalDefault = new StringTemplate(
			"nonLocalDefault");
	static {
		noLocalDefault.localizeCmds = false;
	}

	/**
	 * Template which prints numbers with maximal precision and adds prefix to
	 * variables (ggbtmpvar)
	 */
	public static final StringTemplate prefixedDefault = new StringTemplate(
			"prefixedDefault") {
		@Override
		public double getRoundHalfUpFactor(double abs, NumberFormatAdapter nf2,
				ScientificFormatAdapter sf2, boolean useSF) {
			return 1;
		}
	};

	/**
	 * @return whether line breaks are allowed
	 */
	public boolean isInsertLineBreaks() {
		return false;
	}

	static {
		prefixedDefault.localizeCmds = false;
		prefixedDefault.internationalizeDigits = false;
		prefixedDefault.forceNF = true;
		prefixedDefault.usePrefix = true;
		prefixedDefault.nf = FormatFactory.getPrototype()
				.getNumberFormat(15);
	}
	/**
	 * GeoGebra string type, internationalize digits
	 */
	public static final StringTemplate defaultTemplate = new StringTemplate(
			"defaultTemplate");

	/**
	 * Template which prints original construction's labels
	 */
	public static final StringTemplate realTemplate = new StringTemplate(
			"realTemplate");
	static {
		realTemplate.useRealLabels = true;
	}

	/**
	 * LaTeX string type, do not internationalize digits
	 */
	public static final StringTemplate latexTemplate = new StringTemplate(
			"latexTemplate");
	static {
		latexTemplate.setType(StringType.LATEX);
	}

	/**
	 * JLaTeXMath latex template
	 */
	public static final StringTemplate latexTemplateJLM = new StringTemplate(
			"latexTemplate") {

		@Override
		public String escapeString(String string) {
			String value = string
					.replaceAll("\\\\", "\\\\backslash ")
					.replaceAll("([&%$#{}_])", "\\\\$1")
					.replaceAll("~", "\u223C ")
					.replaceAll("\\^", "\\\\^{\\ } ");
			return value;
		}
	};
	static {
		latexTemplateJLM.setType(StringType.LATEX);
	}

	public static final StringTemplate latexTemplateCAS = new StringTemplate(
			"latexTemplate");
	static {
		latexTemplateCAS.setType(StringType.LATEX);
		latexTemplateCAS.allowPiHack = false;

	}

	/**
	 * LaTeX string type for MathQuillGGB, almost the same as latexTemplate, but
	 * uses \cdot for multiplication sign
	 */
	public static final StringTemplate latexTemplateMQ = new StringTemplate(
			"latexTemplateMQ") {
		@Override
		public boolean isMathQuill() {
			return true;
		}
		@Override
		public String multiplyString(ExpressionValue left,
				ExpressionValue right, String leftStr, String rightStr,
				boolean valueForm, Localization loc) {
			return mathQuillMultiply(left, right, leftStr, rightStr, valueForm,
					loc);
		}
	};
	static {
		latexTemplateMQ.setType(StringType.LATEX);
	}

	/**
	 * LaTeX string type for MathQuillGGB, almost the same as latexTemplate, but
	 * uses \cdot for multiplication sign
	 */
	public static final StringTemplate latexTemplateMQedit = new StringTemplate(
			"latexTemplateMQ") {
		@Override
		public boolean isMathQuill() {
			return true;
		}

		@Override
		public String multiplyString(ExpressionValue left,
				ExpressionValue right, String leftStr, String rightStr,
				boolean valueForm, Localization loc) {
			return mathQuillMultiply(left, right, leftStr, rightStr, valueForm,
					loc);
		}
	};
	static {
		latexTemplateMQedit.setType(StringType.LATEX);
		latexTemplateMQedit.allowMoreDigits = true;
		latexTemplateMQedit.sf = FormatFactory.getPrototype()
				.getScientificFormat(GeoElement.MIN_EDITING_PRINT_PRECISION,
						20, false);
		latexTemplateMQedit.nf = FormatFactory.getPrototype()
				.getNumberFormat(GeoElement.MIN_EDITING_PRINT_PRECISION);
	}

	/**
	 * MathML string type, do not internationalize digits
	 */
	public static final StringTemplate mathmlTemplate = new StringTemplate(
			"mathmlTemplate");
	static {
		mathmlTemplate.setType(StringType.CONTENT_MATHML);
	}

	/**
	 * LibreOffice string type, do not internationalize digits
	 */
	public static final StringTemplate libreofficeTemplate = new StringTemplate(
			"libreOfficeTemplate");
	static {
		libreofficeTemplate.setType(StringType.LIBRE_OFFICE);
	}

	/**
	 * giac string type, do not internationalize digits
	 */
	public static final StringTemplate giacTemplate = new StringTemplate(
			"giacTemplate");
	static {
		giacTemplate.internationalizeDigits = false;
		giacTemplate.numeric = false;
		giacTemplate.usePrefix = false;
		giacTemplate.forceNF = true;
		giacTemplate.localizeCmds = false;
		giacTemplate.setType(StringType.GIAC);
		giacTemplate.nf = FormatFactory.getPrototype()
				.getNumberFormat(15);
	}

	/**
	 * Same as giacTemplate. We check object equality to this in
	 * StringUtil.wrapInExact which is just a hack. TODO
	 */
	public static final StringTemplate giacTemplateInternal = new StringTemplate(
			"giacTemplateMini");
	static {
		giacTemplateInternal.internationalizeDigits = false;
		giacTemplateInternal.numeric = false;
		giacTemplateInternal.usePrefix = false;
		giacTemplateInternal.forceNF = true;
		giacTemplateInternal.localizeCmds = false;
		giacTemplateInternal.setType(StringType.GIAC);
		giacTemplateInternal.nf = FormatFactory.getPrototype()
				.getNumberFormat(15);
	}

	/**
	 * XML string type, do not internationalize digits
	 */
	public static final StringTemplate xmlTemplate = new StringTemplate(
			"xmlTemplate") {
		@Override
		public int getCoordStyle(int coordStyle) {
			return Kernel.COORD_STYLE_DEFAULT;
		}
	};
	static {
		xmlTemplate.forceSF = true;
		xmlTemplate.allowMoreDigits = true;
		xmlTemplate.internationalizeDigits = false;
		xmlTemplate.setType(StringType.GEOGEBRA_XML);
		xmlTemplate.localizeCmds = false;
		xmlTemplate.sf = FormatFactory.getPrototype()
				.getScientificFormat(15, 20, false);
		xmlTemplate.questionMarkForNaN = false;
	}
	/**
	 * XML string type, do not internationalize digits
	 */
	public static final StringTemplate casCopyTemplate = new StringTemplate(
			"casCopyTemplate") {
		@Override
		public int getCoordStyle(int coordStyle) {
			return Kernel.COORD_STYLE_DEFAULT;
		}
	};
	static {
		casCopyTemplate.forceSF = true;
		casCopyTemplate.allowMoreDigits = true;
		casCopyTemplate.internationalizeDigits = false;
		casCopyTemplate.setType(StringType.GEOGEBRA_XML);
		casCopyTemplate.localizeCmds = false;
		casCopyTemplate.sf = FormatFactory.getPrototype()
				.getScientificFormat(15, 20, false);
		casCopyTemplate.questionMarkForNaN = true;
	}
	/**
	 * for input bar; same as default, but increases precision to
	 * MIN_EDITING_PRINT_PRECISION
	 */
	public static final StringTemplate editTemplate = new StringTemplate(
			"editTemplate");

	/**
	 * for input bar; same as default, but adds some extra helper symbols
	 */
	public static final StringTemplate editorTemplate = new StringTemplate(
			"editorTemplate");
	/**
	 * For simplicity make this static now and see in the future whether we will
	 * need more engines in one app
	 */
	static {
		editTemplate.sf = FormatFactory.getPrototype()
				.getScientificFormat(GeoElement.MIN_EDITING_PRINT_PRECISION,
						20, false);
		editTemplate.nf = FormatFactory.getPrototype()
				.getNumberFormat(GeoElement.MIN_EDITING_PRINT_PRECISION);
		editTemplate.allowMoreDigits = true;
		editTemplate.hideLHS = true;

		editorTemplate.sf = editTemplate.sf;
		editorTemplate.nf = editTemplate.nf;
		editorTemplate.allowMoreDigits = editTemplate.allowMoreDigits;
		editorTemplate.hideLHS = editTemplate.hideLHS;

	}
	/**
	 * Template for regression: uses 6 figures or 6 sig digits based on Kernel
	 * settings, string type is XML
	 */
	public static final StringTemplate regression = new StringTemplate(
			"regression");
	static {
		regression.sf = FormatFactory.getPrototype()
				.getScientificFormat(6, 20, false);
		regression.nf = FormatFactory.getPrototype()
				.getNumberFormat(6);
		regression.forceSF = true;
		regression.setType(StringType.GEOGEBRA_XML);
	}
	/**
	 * OGP string type
	 */
	public static final StringTemplate ogpTemplate = new StringTemplate(
			"ogpTemplate");
	static {
		ogpTemplate.forceSF = false;
		ogpTemplate.internationalizeDigits = false;
		ogpTemplate.setType(StringType.OGP);
		ogpTemplate.localizeCmds = false;
		ogpTemplate.nf = FormatFactory.getPrototype()
				.getNumberFormat(0);
	}
	/**
	 * Default template, just increases precision to max
	 */
	public static final StringTemplate maxPrecision = new StringTemplate(
			"maxPrecision");
	static {
		maxPrecision.sf = FormatFactory.getPrototype()
				.getScientificFormat(15, 20, false);
		maxPrecision.allowMoreDigits = true;
		maxPrecision.forceSF = true;
		maxPrecision.localizeCmds = false;
	}
	/**
	 * Default template, just increases precision to max 13 not 15 so that when
	 * sent to Giac is treated as a double, not a multi-precision float (MPFR).
	 * #5130
	 */
	public static final StringTemplate maxPrecision13 = new StringTemplate(
			"maxPrecision13");
	static {
		maxPrecision13.sf = FormatFactory.getPrototype()
				.getScientificFormat(13, 20, false);
		maxPrecision13.allowMoreDigits = true;
		maxPrecision13.forceSF = true;
		maxPrecision13.localizeCmds = false;
		// don't want to use exact value otherwise Giac will do an exact
		// calculation when we want approx
		// eg Integral[sin(x) / (1 + a^2 - 2a cos(x)), 0, pi] in the Algebra
		// View
		// #5129, #5130

		maxPrecision13.printFormPI = "3.141592653590";
	}
	/**
	 * Default template, just allow bigger precision for Numeric command
	 */

	public static final StringTemplate numericDefault = new StringTemplate(
			"numericDefault");
	static {
		numericDefault.allowMoreDigits = true;
	}

	/**
	 * Not localized template, allow bigger precision for Numeric command
	 */

	public static final StringTemplate numericNoLocal = new StringTemplate(
			"numericNoLocal");
	static {
		numericNoLocal.allowMoreDigits = true;
		numericNoLocal.localizeCmds = false;
	}

	/**
	 * Default LaTeX template, just allow bigger precision for Numeric command
	 */
	public static final StringTemplate numericLatex = new StringTemplate(
			"numericLatex");
	static {
		numericLatex.stringType = StringType.LATEX;
		numericLatex.allowMoreDigits = true;
		numericLatex.useRealLabels = true;
	}
	/** Generic template for CAS tests */
	public static final StringTemplate testTemplate = new StringTemplate(
			"testTemplate");
	static {
		testTemplate.internationalizeDigits = false;
		testTemplate.setType(StringType.GEOGEBRA_XML);
		// testTemplate.localizeCmds = false;
		testTemplate.sf = FormatFactory.getPrototype()
				.getScientificFormat(15, 20, false);
	}
	public static final StringTemplate testTemplateJSON = new StringTemplate(
			"testTemplate");
	static {
		testTemplate.internationalizeDigits = false;
		// testTemplate.localizeCmds = false;
		testTemplate.sf = FormatFactory.getPrototype()
				.getScientificFormat(15, 20, false);
	}
	/** Template for CAS tests involving Numeric command */
	public static final StringTemplate testNumeric = new StringTemplate(
			"testNumeric");
	static {
		testNumeric.internationalizeDigits = false;
		testNumeric.setType(StringType.GEOGEBRA_XML);
		// testNumeric.localizeCmds = false;
		testNumeric.allowMoreDigits = true;
		testNumeric.sf = FormatFactory.getPrototype()
				.getScientificFormat(15, 20, false);
	}



	/**
	 * Creates default string template
	 * 
	 * @param name
	 *            name for debugging
	 */
	protected StringTemplate(String name) {
		internationalizeDigits = true;
		localizeCmds = true;
		setType(StringType.GEOGEBRA);
		this.name = name;
	}

	protected String mathQuillMultiply(ExpressionValue left,
			ExpressionValue right, String leftStr, String rightStr,
			boolean valueForm, Localization loc) {

		StringBuilder sb = new StringBuilder();
		Operation operation = Operation.MULTIPLY;
		switch (getStringType()) {

		case CONTENT_MATHML:
			MathmlTemplate.mathml(sb, "<times/>", leftStr, rightStr);
			break;
		default:
			// check for 1 at left
			if (ExpressionNode.isEqualString(left, 1, !valueForm)) {
				append(sb, rightStr, right, operation);
				break;
			}
			// check for 1 at right
			else if (ExpressionNode.isEqualString(right, 1, !valueForm)) {
				append(sb, leftStr, left, operation);
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
			else if ((rightStr.length() == 2 &&

					((rightStr.charAt(0) == Unicode.DEGREE_CHAR
							&& rightStr.charAt(1) == (loc.unicodeZero + 1))

							|| (rightStr.charAt(1) == Unicode.DEGREE_CHAR
									&& rightStr.charAt(0) == loc.unicodeZero
											+ 1)))

					|| rightStr.equals(Unicode.DEGREE)) {

				boolean rtl = loc.isRightToLeftDigits(this);

				if (rtl) {
					sb.append(Unicode.DEGREE);
				}

				if (!left.isLeaf()) {
					sb.append('('); // needed for eg (a+b)\u00b0
				}
				sb.append(leftStr);
				if (!left.isLeaf()) {
					sb.append(')'); // needed for eg (a+b)\u00b0
				}

				if (!rtl) {
					sb.append(Unicode.DEGREE);
				}

				break;
			}

		case LATEX:
		case LIBRE_OFFICE:

			boolean nounary = true;

			// vector * (matrix * vector) needs brackets; always use
			// brackets
			// for internal templates
			if (!isPrintLocalizedCommandNames()
					|| (left.evaluatesToList() && isNDvector(right))) {
				sb.append(leftBracket());
			}

			// left wing
			if (left.isLeaf()
					|| (ExpressionNode.opID(left) >= Operation.MULTIPLY
							.ordinal())) { // not
				// +,
				// -
				if (ExpressionNode.isEqualString(left, -1, !valueForm)) { // unary
																			// minus
					nounary = false;
					sb.append('-');
				} else {
					if (leftStr.startsWith(Unicode.RightToLeftUnaryMinusSign)) {
						// brackets needed for eg Arabic digits
						sb.append(Unicode.RightToLeftMark);
						sb.append(leftBracket());
						sb.append(leftStr);
						sb.append(rightBracket());
						sb.append(Unicode.RightToLeftMark);
					} else {
						sb.append(leftStr);
					}
				}
			} else {
				sb.append(leftBracket());
				sb.append(leftStr);
				sb.append(rightBracket());
			}

			// right wing
			int opIDright = ExpressionNode.opID(right);
			if (right.isLeaf() || (opIDright >= Operation.MULTIPLY.ordinal())) { // not
				// +,
				// -
				boolean showMultiplicationSign = true;
				boolean multiplicationSpaceNeeded = false;
				if (nounary) {
					switch (getStringType()) {
					
					case LATEX:
						// check if we need a multiplication sign, see #414
						// digit-digit, e.g. 3 * 5
						// digit-fraction, e.g. 3 * \frac{5}{2}
						showMultiplicationSign = !(right instanceof MySpecialDouble && Unicode.DEGREE
								.equals(right.toString(defaultTemplate)));
						// left is digit or ends with }, e.g. exponent,
						// fraction

						multiplicationSpaceNeeded = !(right instanceof MySpecialDouble && Unicode.DEGREE
								.equals(right.toString(defaultTemplate)));
						break;


					}

					if (getStringType().equals(StringType.LATEX)
							&& isInsertLineBreaks()) {
						sb.append("\\-");
					}

					if (showMultiplicationSign) {
						sb.append(multiplicationSign());
					} else if (multiplicationSpaceNeeded) {
						// space instead of multiplication sign
						sb.append("\\space");
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
						) // 3 (5)
				{
					if (rtlMinus) {
						sb.append(Unicode.RightToLeftMark);
					}
					sb.append(leftBracket());
					sb.append(rightStr);
					sb.append(rightBracket());
					if (rtlMinus) {
						sb.append(Unicode.RightToLeftMark);
					}
				} else {
					// -1.0 * 5 becomes "-5"
					sb.append(rightStr);
				}
			} else { // right is + or - tree
				if (nounary) {
					
						// space instead of multiplication sign
						sb.append("\\space");
					
				}
				sb.append(leftBracket());
				sb.append(rightStr);
				sb.append(rightBracket());
			}

			// vector * (matrix * vector) needs brackets; always use
			// brackets
			// for internal templates
			if (!isPrintLocalizedCommandNames()
					|| (left.evaluatesToList() && isNDvector(right))) {
				sb.append(rightBracket());
			}

			break;

		case GIAC:

			// Log.debug(left.getClass()+" "+right.getClass());
			// Log.debug(leftStr+" "+rightStr);


			break;

		}
		return sb.toString();
	}

	/**
	 * Returns string type of resulting text
	 * 
	 * @return string type
	 */
	public StringType getStringType() {
		return this.stringType;
	}

	/**
	 * Disables international digits, e.g. for CAS and XML
	 * 
	 * @return true if we want to allow e.g. arabic digits in output
	 */
	public boolean internationalizeDigits() {
		return this.internationalizeDigits;
	}

	/**
	 * 
	 * @return string representation of PI in this template
	 */
	public String getPi() {
		return printFormPI;
	}

	/**
	 * 
	 * @return string representation of PI in this template
	 */
	public String getImaginary() {
		return printFormImaginary;
	}

	/**
	 * Creates new string template with given type
	 * 
	 * @param t
	 *            string type
	 * @return template
	 */
	public static StringTemplate get(StringType t) {
		if (t == null || t.equals(StringType.GEOGEBRA)) {
			return defaultTemplate;
		}
		StringTemplate tpl = new StringTemplate("TemplateFor:" + t);
		tpl.setType(t);
		return tpl;
	}

	private void setType(StringType t) {
		stringType = t;

		switch (t) {
		case GIAC:
			printFormPI = "%pi";
			printFormImaginary = "i";
			break;

		case GEOGEBRA_XML:
			printFormPI = "pi";
			printFormImaginary = Unicode.IMAGINARY;
			break;

		case LATEX:
			printFormPI = "\\pi";
			printFormImaginary = "i";
			break;

		case LIBRE_OFFICE:
			printFormPI = "%pi";
			printFormImaginary = "i";
			break;

		default:
			// #5129
			// #5130
			printFormPI = Unicode.PI_STRING;
			printFormImaginary = Unicode.IMAGINARY;
		}

	}

	/**
	 * Returns whether scientific format (sig digits) should be used (default
	 * templates return the input)
	 * 
	 * @param kernelUsesSF
	 *            kernel setting of SF
	 * @return whether scientific format (sig digits) should be used
	 */
	public boolean useScientific(boolean kernelUsesSF) {
		return forceSF || (kernelUsesSF && !forceNF);
	}

	/**
	 * Convenience method instead of getStringType().equals()
	 * 
	 * @param t
	 *            string type
	 * @return true if this template uses given type equals
	 */
	public boolean hasType(StringType t) {
		return stringType.equals(t);
	}

	/**
	 * @param type
	 *            string type
	 * @param decimals
	 *            number of decimal places
	 * @param allowMore
	 *            true to use kernel's precision, if it's higher
	 * @return template
	 */
	public static StringTemplate printDecimals(StringType type, int decimals,
			boolean allowMore) {
		StringTemplate tpl = new StringTemplate("TemplateFor:" + type
				+ ",Decimals:" + decimals + "," + allowMore);
		tpl.forceNF = true;
		tpl.allowMoreDigits = allowMore;
		tpl.setType(type);
		tpl.nf = FormatFactory.getPrototype()
				.getNumberFormat(decimals);
		return tpl;
	}

	/**
	 * @param type
	 *            string type
	 * @param decimals
	 *            figures
	 * @param allowMore
	 *            true to use kernel's precision, if it's higher
	 * @return template with given parameters
	 */
	public static StringTemplate printFigures(StringType type, int decimals,
			boolean allowMore) {
		StringTemplate tpl = new StringTemplate("TemplateFor:" + type
				+ ",Figures:" + decimals + "," + allowMore);
		tpl.forceSF = true;
		tpl.allowMoreDigits = allowMore;
		tpl.setType(type);
		tpl.sf = FormatFactory.getPrototype()
				.getScientificFormat(decimals, 20, false);
		return tpl;
	}

	/**
	 * Prints the number to full double precision without using E notation
	 * 
	 * @param type
	 *            string type
	 * @return template with given parameters
	 */
	public static StringTemplate fullFigures(StringType type) {
		StringTemplate tpl = new StringTemplate("FullFiguresFor:" + type);
		tpl.forceSF = true;
		tpl.allowMoreDigits = true;
		tpl.setType(type);
		// 308 doesn't seem to work for 1E-300, 350 seems OK
		tpl.sf = FormatFactory.getPrototype()
				.getScientificFormat(16, 350, false);
		return tpl;
	}

	/**
	 * Scientific Notation (eg 2.3 * 4 ^ 5)
	 * 
	 * @param type
	 *            string type
	 * @param decimals
	 *            figures
	 * @param allowMore
	 *            true to use kernel's precision, if it's higher
	 * @return template with given parameters
	 */
	public static StringTemplate printScientific(StringType type, int decimals,
			boolean allowMore) {
		StringTemplate tpl = new StringTemplate("TemplateForScientific:" + type
				+ ",Decimals:" + decimals + "," + allowMore);
		tpl.forceSF = true;
		tpl.allowMoreDigits = allowMore;
		tpl.setType(type);
		tpl.sf = FormatFactory.getPrototype()
				.getScientificFormat(decimals, 20, true);
		return tpl;
	}

	/**
	 * Receives default SF and returns SF to be used
	 * 
	 * @param sfk
	 *            default
	 * @return SF to be used
	 */
	public ScientificFormatAdapter getSF(ScientificFormatAdapter sfk) {
		return sf == null
				|| (allowMoreDigits && sfk.getSigDigits() > sf.getSigDigits()) ? sfk
				: sf;
	}

	/**
	 * Receives default NF and returns NF to be used
	 * 
	 * @param nfk
	 *            default
	 * @return NF to be used
	 */
	public NumberFormatAdapter getNF(NumberFormatAdapter nfk) {
		return nf == null
				|| (allowMoreDigits && nfk.getMaximumFractionDigits() > nf
						.getMaximumFractionDigits()) ? nfk : nf;
	}

	/**
	 * Returns whether we need to localize commands
	 * 
	 * @return true for localized, false for internal
	 */
	public boolean isPrintLocalizedCommandNames() {
		return localizeCmds;
	}

	/**
	 * Receives default style and returns style that should be actually used
	 * 
	 * @param coordStyle
	 *            Kernel.COORD_STYLE_*
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
	 * 
	 * @param abs
	 *            absolute value of number
	 * @param nf2
	 *            kernel's number format
	 * @param sf2
	 *            kernel's scientific format
	 * @param useSF
	 *            round to significant figuress or decimal places
	 * @return factor to multiply (either 1 or 1+1E-15)
	 */
	public double getRoundHalfUpFactor(double abs, NumberFormatAdapter nf2,
			ScientificFormatAdapter sf2, boolean useSF) {

		int digits = useSF ? sf2.getSigDigits() : nf2
				.getMaximumFractionDigits();

		// eg make sure 1.2 not displayed as 1.2000000000001 when rounding set
		// to 15sf
		if (digits >= 15) {
			return 1;
		}

		if (abs < 1000) {
			return ROUND_HALF_UP_FACTOR;
		}
		if (abs > 10E7) {
			return 1;
		}

		if (useSF) {
			if (getSF(sf2) != null && getSF(sf2).getSigDigits() < 10) {
				return ROUND_HALF_UP_FACTOR;
			}
		} else {
			if (getNF(nf2) != null
					&& getNF(nf2).getMaximumFractionDigits() < 10) {
				return ROUND_HALF_UP_FACTOR;
			}

		}

		return 1;

	}

	/**
	 * @return true if more digits than what is set by this template are allowed
	 *         in output
	 */
	public boolean allowMoreDigits() {
		return allowMoreDigits;
	}

	private static final double[] precisions = new double[] { 1, 1E-1, 1E-2,
			1E-3, 1E-4,
			1E-5, 1E-6, 1E-7, 1E-8, 1E-9, 1E-10, 1E-11, 1E-12, 1E-13, 1E-14,
			1E-15, 1E-16 };

	private boolean allowPiHack = true;

	private boolean supportsFractions = true;

	/**
	 * Least positive number with given precision
	 * 
	 * @param nf2
	 *            kernel's number format
	 * @return 10^(-number of digits)
	 */
	public double getPrecision(NumberFormatAdapter nf2) {
		int digits = getNF(nf2).getMaximumFractionDigits();
		return digits <= 16 ? precisions[digits] : Math.pow(10, -digits);
	}

	/**
	 * Objects in macros have two different labels.
	 * 
	 * @return whether label within original (true) or current (false)
	 *         construction should be used
	 */
	public boolean isUseRealLabels() {
		return useRealLabels;
	}

	/**
	 * @return copy of this template that prints real labels
	 */
	public StringTemplate deriveReal() {
		StringTemplate copy = copy();
		copy.useRealLabels = true;
		return copy;
	}

	private StringTemplate copy() {
		StringTemplate result = new StringTemplate("CopyOf:" + name);
		result.stringType = stringType;
		result.nf = nf;
		result.sf = sf;
		result.usePrefix = usePrefix;
		result.allowMoreDigits = allowMoreDigits;
		result.printFormPI = printFormPI;
		result.internationalizeDigits = internationalizeDigits;
		result.useRealLabels = useRealLabels;
		result.localizeCmds = localizeCmds;
		result.forceNF = forceNF;
		result.forceSF = forceSF;
		result.supportsFractions = supportsFractions;
		return result;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns the label depending on the current print form. When sending
	 * variables to the underlying CAS, we need to make sure that we don't
	 * overwrite variable names there, so we add the prefix
	 * ExpressionNodeConstants.GGBCAS_VARIABLE_PREFIX.
	 * 
	 * @param label
	 *            raw label without prefixes
	 * @return label depending on given string type
	 */
	public String printVariableName(final String label) {
		String ret;
		if (isUseTempVariablePrefix()) {
			ret = addTempVariablePrefix(label);
		}
		ret = printVariableName(getStringType(), label);

		if (ret != null && ret.length() == 1 && ret.equals("l")
				&& hasType(StringType.LATEX)) {
			ret = "\\ell";
		}

		return ret;
	}

	final private String printVariableName(final StringType printForm,
			final String label) {
		switch (printForm) {
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
	 * 
	 * important eg i -> ggbtmpvari, e -> ggbtmpvare so that they aren't
	 * confused with the constants
	 */
	private String addTempVariablePrefix(final String label) {

		// keep x, y, z so that x^2+y^2=1 works in Giac
		if (getStringType().isGiac()
				&& ("x".equals(label) || "y".equals(label)
						|| "y'".equals(label) || "y''".equals(label) || "z"
							.equals(label))) {
			return label;
		}

		StringBuilder sb = new StringBuilder();
		// TMP_VARIABLE_PREFIX + label
		sb.append(Kernel.TMP_VARIABLE_PREFIX);

		// make sure gbtmpvarp' not interpreted as derivative
		// #3607
		sb.append(label.replaceAll("'", "unicode39u"));

		return sb.toString();
	}

	/**
	 * @return copy of this, with string type set to StringType.MATHML
	 */
	public StringTemplate deriveMathMLTemplate() {

		if (stringType.equals(StringType.CONTENT_MATHML)) {
			return this;
		}

		StringTemplate ret = this.copy();

		ret.setType(StringType.CONTENT_MATHML);

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
	 * @return copy of this, with isNumeric() returning true
	 */
	public StringTemplate deriveNumericGiac() {

		if (this.numeric) {
			return this;
		}

		StringTemplate ret = this.copy();

		ret.numeric = true;

		return ret;
	}

	/**
	 * @return whether stringType is for a CAS (ie Giac)
	 */
	public boolean hasCASType() {
		return stringType.isGiac();
	}

	protected boolean isNDvector(ExpressionValue v) {
		return v.evaluatesToNonComplex2DVector() || v.evaluatesTo3DVector();
	}

	public String plusString(ExpressionValue l, ExpressionValue r,
			String leftStr, String rightStr, boolean valueForm) {
		StringBuilder sb = new StringBuilder();

		// make sure A:=(1,2) B:=(3,4) A+B works
		// MyVecNode wrapped in ExpressionNode
		ExpressionValue left = l.unwrap();
		ExpressionValue right = r.unwrap();

		final Operation operation = Operation.PLUS;
		switch (stringType) {
		case CONTENT_MATHML:
			MathmlTemplate.mathml(sb, "<plus/>", leftStr, rightStr);
			break;
		case GIAC:
			// don't use isNumberValue(), isListValue as those lead to an
			// evaluate()
			if (left.evaluatesToList()
					&& (right.evaluatesToNumber(false) || right instanceof NumberValue)) {
				// eg {1,2,3} + 10
				sb.append("map(");
				sb.append(leftStr);
				sb.append(",ggx->ggx+(");
				sb.append(rightStr);
				sb.append("))");

				// don't use isNumberValue(), isListValue as those lead to an
				// evaluate()
			} else if ((left.evaluatesToNumber(false) || left instanceof NumberValue)
					&& right.evaluatesToList()) {
				// eg 10 + {1,2,3}
				sb.append("map(");
				sb.append(rightStr);
				sb.append(",ggx->ggx+(");
				sb.append(leftStr);
				sb.append("))");

				// instanceof VectorValue rather than isVectorValue() as
				// ExpressionNode can return true
				// don't use isNumberValue(), isListValue as those lead to an
				// evaluate()
			} else if ((left.evaluatesToNumber(false) || left instanceof NumberValue)
					&& right.evaluatesToNonComplex2DVector()) {

				// Log.debug(leftStr+" "+left.getClass());
				// Log.debug(rightStr+" "+right.getClass());
				// eg 10 + (1,2)
				sb.append("point(real(");
				sb.append(rightStr);
				sb.append("[1])+");
				sb.append(leftStr);
				sb.append(",im(");
				sb.append(rightStr);
				sb.append("[1])+");
				sb.append(leftStr);
				sb.append(')');

				// instanceof VectorValue rather than isVectorValue() as
				// ExpressionNode can return true
			} else if ((right.evaluatesToNumber(false) || right instanceof NumberValue)
					&& left.evaluatesToNonComplex2DVector()) {
				// Log.debug(left.getClass()+" "+right.getClass());
				// eg (1,2) + 10
				sb.append("point(real(");
				sb.append(leftStr);
				sb.append("[1])+");
				sb.append(rightStr);
				sb.append(",im(");
				sb.append(leftStr);
				sb.append("[1])+");
				sb.append(rightStr);
				sb.append(')');

				// don't use isNumberValue() as that leads to an evaluate()
			} else if ((left.evaluatesToNumber(false) || left instanceof NumberValue)
					&& right.evaluatesTo3DVector()) {
				// Log.debug(left.getClass()+" "+right.getClass());
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
			} else if (left.evaluatesTo3DVector()
					&& (right.evaluatesToNumber(false) || right instanceof NumberValue)) {
				// Log.debug(left.getClass()+" "+right.getClass());
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

			} else if (left.evaluatesToVectorNotPoint()
					&& right.evaluatesToVectorNotPoint()) {

				// Log.debug(left.getClass()+" "+right.getClass());
				// eg vectors (1,2)+(3,4)
				sb.append(leftStr);
				sb.append("+");
				sb.append(rightStr);

			} else if (right instanceof MyVecNDNode
					&& left instanceof MyVecNDNode) {

				MyVecNDNode leftVN = (MyVecNDNode) left;
				MyVecNDNode rightVN = (MyVecNDNode) right;

				// Log.debug(left.getClass()+" "+right.getClass());

				boolean leftIsVector = leftVN.isCASVector();
				boolean rightIsVector = rightVN.isCASVector();

				if (leftIsVector && rightIsVector) {
					// Vector + Vector
					sb.append(leftStr);
					sb.append("+");
					sb.append(rightStr);

				} else if (!leftIsVector && !rightIsVector) {
					// Point + Point
					sb.append("point(");
					sb.append(leftStr);
					sb.append("+");
					sb.append(rightStr);
					sb.append(")");
				} else {
					if (leftVN.getDimension() == 3
							|| rightVN.getDimension() == 3) {
						sb.append("point(");
						sb.append("xcoord(");
						sb.append(leftStr);
						sb.append(')');
						sb.append("+");
						sb.append("xcoord(");
						sb.append(rightStr);
						sb.append("),");
						sb.append("ycoord(");
						sb.append(leftStr);
						sb.append(')');
						sb.append("+");
						sb.append("ycoord(");
						sb.append(rightStr);
						sb.append("),");
						sb.append("zcoord(");
						sb.append(leftStr);
						sb.append(')');
						sb.append("+");
						sb.append("zcoord(");
						sb.append(rightStr);
						sb.append(")");
						sb.append(")");
					} else {
						sb.append("point(");
						sb.append(leftStr);
						sb.append("+");
						sb.append(rightStr);
						sb.append(")");

					}
				}

			} else if (right.evaluatesToNonComplex2DVector()
					&& left.evaluatesToNonComplex2DVector()) {
				// eg f: (x, y) = (3, 2) + t (5, 1)
				sb.append("point(");
				sb.append(leftStr);
				sb.append("+");
				sb.append(rightStr);
				sb.append(')');

			} else if (isNDvector(right) && isNDvector(left)) {

				// eg Evaluate[(1,2,3)+Vector[(10,20,30)]]
				// eg f: (x, y, z) = (3, 2, 1) + t (5, 1, -3)
				sb.append("point(");
				sb.append("xcoord(");
				sb.append(leftStr);
				sb.append(')');
				sb.append("+");
				sb.append("xcoord(");
				sb.append(rightStr);
				sb.append("),");
				sb.append("ycoord(");
				sb.append(leftStr);
				sb.append(')');
				sb.append("+");
				sb.append("ycoord(");
				sb.append(rightStr);
				sb.append("),");
				sb.append("zcoord(");
				sb.append(leftStr);
				sb.append(')');
				sb.append("+");
				sb.append("zcoord(");
				sb.append(rightStr);
				sb.append(")");
				sb.append(")");

			} else {
				// Log.debug("default method" +
				// left.getClass()+" "+right.getClass());

				sb.append('(');
				sb.append(leftStr);
				sb.append(")+(");
				sb.append(rightStr);
				sb.append(')');
			}
			break;

		default:
			// check for 0
			if (valueForm) {
				if (ExpressionNode.isEqualString(left, 0, !valueForm)) {
					append(sb, rightStr, right, operation);
					break;
				} else if (ExpressionNode.isEqualString(right, 0, !valueForm)) {
					append(sb, leftStr, left, operation);
					break;
				}
			}
			int leftop = ExpressionNode.opID(left);
			if (left instanceof Equation
					|| (leftop >= 0 && leftop < Operation.PLUS.ordinal())) {
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
				if (stringType.equals(StringType.LATEX) && isInsertLineBreaks()) {
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
							&& isInsertLineBreaks()) {
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
							&& isInsertLineBreaks()) {
						sb.append(" \\-- ");
					} else {
						sb.append(" - ");
					}
					append(sb, rightStr.substring(3), right, Operation.PLUS);
				} else {
					if (stringType.equals(StringType.LATEX)
							&& isInsertLineBreaks()) {
						sb.append(" \\-+ ");
					} else {
						sb.append(" + ");
					}
					append(sb, rightStr, right, Operation.PLUS);
				}
			}
			break;
		}
		return sb.toString();

	}

	public String leftBracket() {
		/*
		 * if (stringType.equals(StringType.LATEX)) return " \\left( "; else if
		 * (stringType.equals(StringType.LIBRE_OFFICE)) return " left ( "; else
		 * return "(";
		 */
		return left() + "(";
	}

	public String rightBracket() {
		/*
		 * if (stringType.equals(StringType.LATEX)) return " \\right)"; else if
		 * (stringType.equals(StringType.LIBRE_OFFICE)) return " right )"; else
		 * return ")";
		 */
		return right() + ")";
	}

	public String leftSquareBracket() {
		return left() + "[";
	}

	public String rightSquareBracket() {
		return right() + "]";
	}

	private String right() {
		if (stringType.equals(StringType.LATEX))
			return " \\right";
		else if (stringType.equals(StringType.LIBRE_OFFICE))
			return " right ";
		else
			return "";
	}

	private String left() {
		if (stringType.equals(StringType.LATEX))
			return " \\left";
		else if (stringType.equals(StringType.LIBRE_OFFICE))
			return " left ";
		else
			return "";
	}

	public String minusString(ExpressionValue l, ExpressionValue r,
			String leftStr, String rightStr, boolean valueForm, Localization loc) {

		// make sure A:=(1,2) B:=(3,4) A-B works
		// MyVecNode wrapped in ExpressionNode
		ExpressionValue left = l.unwrap();
		ExpressionValue right = r.unwrap();

		StringBuilder sb = new StringBuilder();
		switch (stringType) {
		case CONTENT_MATHML:
			MathmlTemplate.mathml(sb, "<minus/>", leftStr, rightStr);
			break;
		case GIAC:
			if (left.evaluatesToList()
					&& (right.evaluatesToNumber(false) || right instanceof NumberValue)) {
				// eg {1,2,3} + 10
				sb.append("map(");
				sb.append(leftStr);
				sb.append(",ggx->ggx-(");
				sb.append(rightStr);
				sb.append("))");

			} else if ((left.evaluatesToNumber(false) || left instanceof NumberValue)
					&& right.evaluatesToList()) {
				// eg 10 + {1,2,3}
				sb.append("map(");
				sb.append(rightStr);
				sb.append(",ggx->");
				sb.append(leftStr);
				sb.append("-ggx)");

			} else if ((left.evaluatesToNumber(false) || left instanceof NumberValue)
					&& right.evaluatesToNonComplex2DVector()) {
				// eg 10 - (1,2)
				sb.append("point(");
				sb.append(leftStr);
				sb.append("-real(");
				sb.append(rightStr);
				sb.append("[1])");
				sb.append(",");
				sb.append(leftStr);
				sb.append("-im(");
				sb.append(rightStr);
				sb.append("[1]))");

			} else if ((right.evaluatesToNumber(false) || right instanceof NumberValue)
					&& left.evaluatesToNonComplex2DVector()) {
				// eg (1,2) - 10
				sb.append("point(real(");
				sb.append(leftStr);
				sb.append("[1])-(");
				sb.append(rightStr);
				sb.append("),im(");
				sb.append(leftStr);
				sb.append("[1])-(");
				sb.append(rightStr);
				sb.append("))");

			} else if ((left.evaluatesToNumber(false) || left instanceof NumberValue)
					&& right.evaluatesTo3DVector()) {
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

				// don't use isNumberValue(), isListValue as those lead to an
				// evaluate()
			} else if (left.evaluatesTo3DVector()
					&& (right.evaluatesToNumber(false) || right instanceof NumberValue)) {
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

			} else if (left.evaluatesToVectorNotPoint()
					&& right.evaluatesToVectorNotPoint()) {
				// Log.debug(left.getClass()+" "+right.getClass());
				// eg Vectors (1,2)-(3,4)
				sb.append('(');
				sb.append(leftStr);
				sb.append("-(");
				sb.append(rightStr);
				sb.append("))");

			} else if (right instanceof MyVecNDNode
					&& left instanceof MyVecNDNode) {

				MyVecNDNode leftVN = (MyVecNDNode) left;
				MyVecNDNode rightVN = (MyVecNDNode) right;

				// Log.debug(left.getClass()+" "+right.getClass());

				boolean leftIsVector = leftVN.isCASVector();
				boolean rightIsVector = rightVN.isCASVector();

				if (leftIsVector && rightIsVector) {
					// Vector - Vector
					sb.append('(');
					sb.append(leftStr);
					sb.append("-(");
					sb.append(rightStr);
					sb.append("))");

				} else if (!leftIsVector && !rightIsVector) {
					// Point + Point
					sb.append("point(");
					sb.append(leftStr);
					sb.append("-");
					sb.append(rightStr);
					sb.append(")");
				} else {
					if (leftVN.getDimension() == 3
							|| rightVN.getDimension() == 3) {
						sb.append("point(");
						sb.append("xcoord(");
						sb.append(leftStr);
						sb.append(')');
						sb.append("-");
						sb.append("xcoord(");
						sb.append(rightStr);
						sb.append("),");
						sb.append("ycoord(");
						sb.append(leftStr);
						sb.append(')');
						sb.append("-");
						sb.append("ycoord(");
						sb.append(rightStr);
						sb.append("),");
						sb.append("zcoord(");
						sb.append(leftStr);
						sb.append(')');
						sb.append("-");
						sb.append("zcoord(");
						sb.append(rightStr);
						sb.append(")");
						sb.append(")");
					} else {
						sb.append("point(");
						sb.append(leftStr);
						sb.append("-");
						sb.append(rightStr);
						sb.append(")");

					}
				}

			} else if (right.evaluatesToNonComplex2DVector()
					&& left.evaluatesToNonComplex2DVector()) {
				// eg f: (x, y) = (3, 2) - t (5, 1)
				sb.append("point(");
				sb.append(leftStr);
				sb.append("-");
				sb.append(rightStr);
				sb.append(')');

			} else if (isNDvector(right) && isNDvector(left)) {
				// Log.debug(left.getClass()+" "+right.getClass());
				// eg (1,2)-(3,4)
				// eg f: (x, y, z) = (3, 2, 1) - t (5, 1, -3)
				sb.append("point(");
				sb.append("xcoord(");
				sb.append(leftStr);
				sb.append(')');
				sb.append("-");
				sb.append("xcoord(");
				sb.append(rightStr);
				sb.append("),");
				sb.append("ycoord(");
				sb.append(leftStr);
				sb.append(')');
				sb.append("-");
				sb.append("ycoord(");
				sb.append(rightStr);
				sb.append("),");
				sb.append("zcoord(");
				sb.append(leftStr);
				sb.append(')');
				sb.append("-");
				sb.append("zcoord(");
				sb.append(rightStr);
				sb.append(")");
				sb.append(")");

			} else {

				sb.append('(');
				sb.append(leftStr);
				sb.append(")-(");
				sb.append(rightStr);
				sb.append(')');
			}
			break;

		default:
			if (left instanceof Equation) {
				sb.append(leftBracket());
				sb.append(leftStr);
				sb.append(rightBracket());
			} else {
				sb.append(leftStr);
			}

			// check for 0 at right
			if (valueForm && rightStr.equals(loc.unicodeZero + "")) {
				break;
			}

			if (right.isLeaf()
					|| (ExpressionNode.opID(right) >= Operation.MULTIPLY
							.ordinal())) { // not
				// +,
				// -

				if (rightStr.charAt(0) == '-') { // convert - - to +
					if (stringType.equals(StringType.LATEX)
							&& isInsertLineBreaks()) {
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
							&& isInsertLineBreaks()) {
						sb.append(" \\-+ ");
					} else {
						sb.append(" + ");
					}
					sb.append(rightStr.substring(3));
				} else {
					if (stringType.equals(StringType.LATEX)
							&& isInsertLineBreaks()) {
						sb.append(" \\-- ");
					} else {
						sb.append(" - ");
					}
					sb.append(rightStr);
				}
			} else {
				// fix for changing height in Algebra View plus / minus
				if (stringType.equals(StringType.LATEX) && isInsertLineBreaks()) {
					sb.append(" \\-- ");
				} else {
					sb.append(" - ");
				}
				sb.append(leftBracket());
				sb.append(rightStr);
				sb.append(rightBracket());
			}
			break;
		}
		return sb.toString();
	}

	public String multiplyString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr, boolean valueForm, Localization loc) {
		StringBuilder sb = new StringBuilder();
		Operation operation = Operation.MULTIPLY;
		switch (stringType) {

		case CONTENT_MATHML:
			MathmlTemplate.mathml(sb, "<times/>", leftStr, rightStr);
			break;
		default:
			// check for 1 at left
			if (ExpressionNode.isEqualString(left, 1, !valueForm)) {
				append(sb, rightStr, right, operation);
				break;
			}
			// check for 1 at right
			else if (ExpressionNode.isEqualString(right, 1, !valueForm)) {
				append(sb, leftStr, left, operation);
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
			else if ((rightStr.length() == 2 &&

					((rightStr.charAt(0) == Unicode.DEGREE_CHAR
							&& rightStr.charAt(1) == (loc.unicodeZero + 1))

							|| (rightStr.charAt(1) == Unicode.DEGREE_CHAR
									&& rightStr.charAt(0) == loc.unicodeZero
											+ 1)))

					|| rightStr.equals(Unicode.DEGREE)) {

				boolean rtl = loc.isRightToLeftDigits(this);

				if (rtl) {
					sb.append(Unicode.DEGREE);
				}

				if (!left.isLeaf()) {
					sb.append('('); // needed for eg (a+b)\u00b0
				}
				sb.append(leftStr);
				if (!left.isLeaf()) {
					sb.append(')'); // needed for eg (a+b)\u00b0
				}

				if (!rtl) {
					sb.append(Unicode.DEGREE);
				}

				break;
			}

		case LATEX:
		case LIBRE_OFFICE:

			boolean nounary = true;

			// vector * (matrix * vector) needs brackets; always use brackets
			// for internal templates
			if (!isPrintLocalizedCommandNames()
					|| (left.evaluatesToList() && isNDvector(right))) {
				sb.append(leftBracket());
			}

			// left wing
			if (left.isLeaf()
					|| (ExpressionNode.opID(left) >= Operation.MULTIPLY
							.ordinal())) { // not
				// +,
				// -
				if (ExpressionNode.isEqualString(left, -1, !valueForm)) { // unary
																			// minus
					nounary = false;
					sb.append('-');
				} else {
					if (leftStr.startsWith(Unicode.RightToLeftUnaryMinusSign)) {
						// brackets needed for eg Arabic digits
						sb.append(Unicode.RightToLeftMark);
						sb.append(leftBracket());
						sb.append(leftStr);
						sb.append(rightBracket());
						sb.append(Unicode.RightToLeftMark);
					} else {
						sb.append(leftStr);
					}
				}
			} else {
				sb.append(leftBracket());
				sb.append(leftStr);
				sb.append(rightBracket());
			}

			// right wing
			int opIDright = ExpressionNode.opID(right);
			if (right.isLeaf() || (opIDright >= Operation.MULTIPLY.ordinal())) { // not
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
						char lastLeft = leftStr.charAt(leftStr.length() - 1);
						char firstRight = rightStr.charAt(0);
						showMultiplicationSign =
						// left is digit or ends with }, e.g. exponent,
						// fraction
						(StringUtil.isDigit(lastLeft) || (lastLeft == '}')) &&
						// right is digit or fraction
								(StringUtil.isDigit(firstRight) || rightStr
										.startsWith("\\frac"));
						multiplicationSpaceNeeded = !(right instanceof MySpecialDouble && Unicode.DEGREE
								.equals(right.toString(defaultTemplate)));
						break;

					default: // GeoGebra syntax
						char firstLeft = leftStr.charAt(0);
						lastLeft = leftStr.charAt(leftStr.length() - 1);
						firstRight = rightStr.charAt(0);
						// check if we need a multiplication sign, see #414
						// digit-digit, e.g. 3 * 5
						showMultiplicationSign = Character.isDigit(lastLeft)
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
							&& isInsertLineBreaks()) {
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
					sb.append(leftBracket());
					sb.append(rightStr);
					sb.append(rightBracket());
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
				sb.append(leftBracket());
				sb.append(rightStr);
				sb.append(rightBracket());
			}

			// vector * (matrix * vector) needs brackets; always use brackets
			// for internal templates
			if (!isPrintLocalizedCommandNames()
					|| (left.evaluatesToList() && isNDvector(right))) {
				sb.append(rightBracket());
			}

			break;

		case GIAC:

			// Log.debug(left.getClass()+" "+right.getClass());
			// Log.debug(leftStr+" "+rightStr);

			if (right instanceof ExpressionNode
					&& ((ExpressionNode) right).getOperation().isInequality()
					&& left.evaluatesToNumber(false)) {
				// eg 3(x<4)
				// MySpecialDouble shouldn't be negative, but just in case:
				boolean reverse = left.evaluateDouble() < 0;

				sb.append('(');
				sb.append(leftStr);
				sb.append(")*(");
				sb.append(expToString(((ExpressionNode) right).getLeft(),
						valueForm));
				sb.append(')');
				sb.append(op((ExpressionNode) right, reverse));
				sb.append('(');
				sb.append(leftStr);
				sb.append(")*(");
				sb.append(expToString(((ExpressionNode) right).getRight(),
						valueForm));
				sb.append(')');
			} else if (left instanceof ExpressionNode
					&& ((ExpressionNode) left).getOperation().isInequality()
					&& right.evaluatesToNumber(false)) {
				// eg 3(x<4)
				// MySpecialDouble shouldn't be negative, but just in case:
				boolean reverse = right.evaluateDouble() < 0;

				sb.append('(');
				sb.append(rightStr);
				sb.append(")*(");
				sb.append(expToString(((ExpressionNode) left).getLeft(),
						valueForm));
				sb.append(')');
				sb.append(op((ExpressionNode) left, reverse));
				sb.append('(');
				sb.append(rightStr);
				sb.append(")*(");
				sb.append(expToString(((ExpressionNode) left).getRight(),
						valueForm));
				sb.append(')');
			} else if (ExpressionNode.isEqualString(left, -1, !valueForm)) {
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

	protected String expToString(ExpressionValue v, boolean valueMode) {
		return valueMode ? v.toValueString(this) : v.toString(this);
	}

	private static String op(ExpressionNode right, boolean reverse) {

		switch (right.getOperation()) {
		case LESS:
			return reverse ? ">" : "<";
		case LESS_EQUAL:
			return reverse ? ">=" : "<=";
		case GREATER_EQUAL:
			return reverse ? "<=" : ">=";
		case GREATER:
			return reverse ? "<" : ">";
		}

		return null;

	}

	protected String multiplicationSign() {
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

	protected String multiplicationSpace() {
		// wide space for multiplicatoin space in LaTeX
		return (stringType.equals(StringType.LATEX)) ? " \\; " : " ";
	}

	public void append(StringBuilder sb, String str, ExpressionValue ev,
			Operation op) {
		if (ev.isLeaf()
				|| (ExpressionNode.opID(ev) >= op.ordinal())
				&& (!ExpressionNode.chainedBooleanOp(op) || !ExpressionNode
						.chainedBooleanOp(ev.wrap().getOperation()))) {
			sb.append(str);
		} else {
			sb.append(leftBracket());
			sb.append(str);
			sb.append(rightBracket());
		}

	}

	public String divideString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr, boolean valueForm) {
		StringBuilder sb = new StringBuilder();
		switch (stringType) {
		case CONTENT_MATHML:
			MathmlTemplate.mathml(sb, "<divide/>", leftStr, rightStr);
			break;
		case LATEX:
			if ((leftStr.charAt(0) == '-')
					&& (left.isLeaf() || (left instanceof ExpressionNode && ExpressionNode
							.isMultiplyOrDivide((ExpressionNode) left)))) {
				sb.append("-\\frac{");
				sb.append(leftStr.substring(1));
				sb.append("}{");
				sb.append(rightStr);
				sb.append("}");
			} else {

				sb.append("\\frac{");
				sb.append(leftStr);
				sb.append("}{");
				sb.append(rightStr);
				sb.append("}");
			}
			break;
		case LIBRE_OFFICE:
			sb.append("{ ");
			sb.append(leftStr);
			sb.append(" } over { ");
			sb.append(rightStr);
			sb.append(" }");
			break;

		case GIAC:
			sb.append("(");
			sb.append(leftStr);
			sb.append(")/(");
			sb.append(rightStr);
			sb.append(')');
			break;

		default:
			// check for 1 in denominator
			// #5396
			if (left.isLeaf()
					&& ExpressionNode.isEqualString(right, 1, !valueForm)) {
				sb.append(leftStr);
				break;
			}

			// left wing
			// put parentheses around +, -, *
			if (left.isExpressionNode() && ((ExpressionNode) left).getOperation() == Operation.MULTIPLY
					&& !((ExpressionNode) left).hasBrackets()
					&& ExpressionNode.isConstantDouble(
							((ExpressionNode) left).getRight(),
					Math.PI)) {
				sb.append(leftStr);
			} else {
				append(sb, leftStr, left, Operation.DIVIDE);
			}
			sb.append(" / ");


			// right wing
			append(sb, rightStr, right, Operation.POWER); // not


			// +,
			// -,
			// *,
			// /
		}
		return sb.toString();
	}

	public String notString(ExpressionValue left, String leftStr) {
		StringBuilder sb = new StringBuilder();

		if (stringType.equals(StringType.CONTENT_MATHML)) {
			MathmlTemplate.mathml(sb, "<not/>", leftStr, null);
		} else {

			switch (stringType) {
			case CONTENT_MATHML:

				break;
			case LATEX:
				sb.append("\\neg ");
				break;

			case LIBRE_OFFICE:
				sb.append("neg ");
				break;

			default:
				sb.append(strNOT);
			}
			if (left.isLeaf()) {
				sb.append(leftStr);
			} else {
				sb.append(leftBracket());
				sb.append(leftStr);
				sb.append(rightBracket());
			}
		}
		return sb.toString();
	}

	public static void appendOp(StringBuilder sb, String string,
			String leftStr, String rightStr) {
		sb.append(string);
		sb.append('(');
		sb.append(leftStr);
		sb.append(',');
		sb.append(rightStr);
		sb.append(')');

	}

	public String orString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr) {
		StringBuilder sb = new StringBuilder();

		if (stringType.equals(StringType.CONTENT_MATHML)) {
			MathmlTemplate.mathml(sb, "<or/>", leftStr, rightStr);
		} else {
			append(sb, leftStr, left, Operation.OR);
			sb.append(' ');

			switch (stringType) {
			case LATEX:
				if (isInsertLineBreaks()) {
					sb.append("\\-");
				}
				sb.append("\\vee");
				break;
			case LIBRE_OFFICE:
				sb.append("or");
				break;

			case GIAC:
				sb.append("||");
				break;

			default:
				sb.append(strOR);
			}

			sb.append(' ');
			append(sb, rightStr, right, Operation.OR);
			// sb.append(rightStr);
		}
		return sb.toString();
	}

	public String geqSign() {
		switch (getStringType()) {
		case LATEX:
			if (isInsertLineBreaks()) {
				return "\\-\\geq";
			}
			return "\\geq";
		case LIBRE_OFFICE:
		case GIAC:
			return ">=";
		default:
			return strGREATER_EQUAL;
		}
	}

	public String leqSign() {
		switch (getStringType()) {
		case LATEX:
			if (isInsertLineBreaks()) {
				return "\\-\\leq";
			}
			return "\\leq";
		case LIBRE_OFFICE:
		case GIAC:
			return "<=";
		default:
			return strLESS_EQUAL;
		}
	}

	public String greaterSign() {
		if (hasType(StringType.LATEX) && isInsertLineBreaks()) {
			return "\\->";
		}
		return ">";
	}

	public String lessSign() {
		if (hasType(StringType.LATEX) && isInsertLineBreaks()) {
			return "\\-<";
		}
		return " < ";
	}

	public String strictSubsetSign() {
		switch (getStringType()) {
		case LATEX:
			if (isInsertLineBreaks()) {
				return "\\-\\subset";
			}
			return "\\subset";
		case LIBRE_OFFICE:
			return "subset";
		default:
			return strIS_SUBSET_OF_STRICT;
		}
	}

	public String subsetSign() {
		switch (getStringType()) {
		case LATEX:
			if (isInsertLineBreaks()) {
				return "\\-\\subseteq";
			}
			return "\\subseteq";
		case LIBRE_OFFICE:
			return "subseteq";
		default:
			return strIS_SUBSET_OF;
		}
	}

	public String notEqualSign() {

		switch (getStringType()) {
		case LATEX:
			if (isInsertLineBreaks()) {
				return "\\-\\neq";
			}
			return "\\neq";
		case LIBRE_OFFICE:
			return "<>";
		case GIAC:
			return "!=";

		default:
			return strNOT_EQUAL;
		}

	}

	public String equalSign() {

		switch (getStringType()) {
		case LATEX:
			if (isInsertLineBreaks()) {

				if (isMathQuill()) {
					return "\\-\\questeq ";
				}
				return "\\-\\stackrel{ \\small ?}{=} ";
			}
			// #4068 changed from \stackrel{ \small ?}{=}
			if (isMathQuill()) {
				return "\\questeq ";
			}
			return "\\stackrel{ \\small ?}{=} ";

		case LIBRE_OFFICE:
		case GIAC:
			return "=";
		default:
			return strEQUAL_BOOLEAN;
		}

	}


	public String perpSign() {

		switch (getStringType()) {
		case LATEX:
			if (isInsertLineBreaks()) {
				return "\\-\\perp";
			}
			return "\\perp";
		case LIBRE_OFFICE:
			return "ortho";
		default:
			return strPERPENDICULAR;
		}
	}

	public String parallelSign() {
		switch (getStringType()) {
		case LATEX:
			if (isInsertLineBreaks()) {
				return "\\-\\parallel";
			}
			return "\\parallel";
		case LIBRE_OFFICE:
			return "parallel";
		default:
			return strPARALLEL;
		}
	}

	public void infixBinary(StringBuilder sb, ExpressionValue left,
			ExpressionValue right, Operation operation, String leftStr,
			String rightStr, StringTemplate tpl, String operationString) {

		tpl.append(sb, leftStr, left, operation);
		sb.append(' ');
		sb.append(operationString);
		sb.append(' ');
		tpl.append(sb, rightStr, right, operation);

	}

	public String andIntervalString(ExpressionValue left,
			ExpressionValue right, String leftStr, String rightStr,
			boolean valueForm) {
		StringBuilder sb = new StringBuilder();
		if (stringType.equals(StringType.CONTENT_MATHML)
 || stringType.isGiac()) {
			return andString(left, right, leftStr, rightStr);
		}
		if (right.isExpressionNode()) {
			sb.append(left.wrap().getCASstring(this, !valueForm));
			sb.append(' ');
			switch (((ExpressionNode) right).getOperation()) {
			case LESS:
				sb.append(lessSign());
				break;
			case LESS_EQUAL:
				sb.append(leqSign());
				break;
			case GREATER:
				sb.append(greaterSign());
				break;
			case EQUAL_BOOLEAN:
				sb.append(equalSign());
				break;
			case NOT_EQUAL:
				sb.append(notEqualSign());
				break;
			case GREATER_EQUAL:
				sb.append(geqSign());
				break;
			case IS_SUBSET_OF:
				sb.append(subsetSign());
				break;
			case IS_SUBSET_OF_STRICT:
				sb.append(strictSubsetSign());
				break;
			case PARALLEL:
				sb.append(parallelSign());
				break;
			case PERPENDICULAR:
				sb.append(perpSign());
				break;
			default:
				Log.debug(((ExpressionNode) right).getOperation()
						+ " invalid in chain");
			}
			sb.append(' ');
			sb.append(((ExpressionNode) right).getRightTree().getCASstring(
					this, !valueForm));
			return sb.toString();
		}
		return andString(left, right, leftStr, rightStr);
	}

	public String andString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr) {
		StringBuilder sb = new StringBuilder();
		if (stringType.equals(StringType.CONTENT_MATHML)) {
			MathmlTemplate.mathml(sb, "<and/>", leftStr, rightStr);
		} else if (stringType.isGiac()) {
			sb.append('(');
			sb.append(leftStr);
			sb.append(" && ");
			sb.append(rightStr);
			sb.append(')');
		} else {
			append(sb, leftStr, left, Operation.AND);

			sb.append(' ');
			switch (stringType) {
			case LATEX:
				if (isInsertLineBreaks()) {
					sb.append("\\-");
				}
				sb.append("\\wedge");
				break;

			case LIBRE_OFFICE:
				sb.append("and");
				break;

			case GIAC:
				sb.append("&&");
				break;

			default:
				sb.append(strAND);
			}
			sb.append(' ');

			append(sb, rightStr, right, Operation.AND);
		}
		return sb.toString();
	}

	@SuppressFBWarnings({ "SF_SWITCH_FALLTHROUGH",
			"missing break is deliberate" })
	public String powerString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr, boolean valueForm) {
		StringBuilder sb = new StringBuilder();

		/*
		 * support for sin^2(x) for display, too slow and hacky if
		 * (STRING_TYPE.equals(StringType.GEOGEBRA &&
		 * leftStr.startsWith("sin(")) { //&& rightStr.equals("2")) { int index;
		 * try { index = Integer.parseInt(rightStr); } catch
		 * (NumberFormatException nfe) { index = Integer.MAX_VALUE; }
		 * 
		 * if (index > 0 && index != Integer.MAX_VALUE) { sb.append("sin");
		 * sb.append(Unicode.numberToIndex(index));
		 * sb.append(leftStr.substring(3)); // everying except the "sin" break;
		 * }
		 * 
		 * }//
		 */

		if (stringType.equals(StringType.CONTENT_MATHML)) {
			MathmlTemplate.mathml(sb, "<power/>", leftStr, rightStr);
		} else {

			// everything else

			boolean finished = false;

			// support for sin^2(x) for LaTeX, eg FormulaText[]
			if (stringType.equals(StringType.LATEX) && left.isExpressionNode()) {
				switch (((ExpressionNode) left).getOperation()) {
				// #1592
				case SIN:
				case COS:
				case TAN:
				case SEC:
				case CSC:
				case COT:
				case SINH:
				case COSH:
				case TANH:
				case SECH:
				case CSCH:
				case COTH:
					
					double indexD = right.evaluateDouble();
					int index = (int) Math.round(indexD);

					// only positive integers
					// sin^-1(x) is arcsin
					// sin^-2(x) not standard notation
					if (!(Double.isInfinite(indexD) || Double.isNaN(indexD))
							&& (index > 0)) {

						String leftStrTrimmed = leftStr.trim();
						
						int spaceIndex = leftStrTrimmed.trim().indexOf(' ');
						sb.append(leftStrTrimmed.substring(0, spaceIndex));

						sb.append(" ^{");
						sb.append(rightStr);
						sb.append("}");

						// alternative using Unicode
						// sb.append(Unicode.numberToIndex(index));

						// everything except the "\\sin "
						sb.append(leftStrTrimmed.substring(spaceIndex + 1));

						finished = true;

						break;
					}

				default:
					// fall through
				}

				if (finished) {
					return sb.toString();
				}

			}

			switch (stringType) {

			case GIAC:

				// if user types e^(ln(4.93)/1.14)
				// ie not Unicode.EULER_STRING
				// then it's ggbtmpvare here
				// Unicode.EULER_STRING is changed to just e

				// check for Unicode.EULER_STRING just in case

				if ("e".equals(leftStr) || Unicode.EULER_STRING.equals(leftStr)) {
					sb.append("exp(");
					sb.append(rightStr);
					sb.append(")");
					break;
				}

				if (right.isExpressionNode()
						&& ((ExpressionNode) right).getOperation() == Operation.DIVIDE) {
					ExpressionNode enR = (ExpressionNode) right;

					// was simplify(surd, causes problems
					// GGB-321
					sb.append("surd(");
					sb.append(leftStr);
					sb.append(',');
					// #4186: make sure we send value string to CAS
					sb.append(expToString(enR.getRight(), valueForm));
					sb.append(")");
					sb.append("^(");
					sb.append(expToString(enR.getLeft(), valueForm));
					sb.append(")");

				} else {

					sb.append("(");
					sb.append(leftStr);
					// Log.debug(left.evaluatesToList());
					// Log.debug(left instanceof ListValue);
					// Log.debug(((ListValue)left).getListElement(0).evaluatesToList());

					// if list && !matrix
					if (left.evaluatesToList() && left.getListDepth() != 2) {
						// make sure {1,2,3}^2 gives {1,4,9} rather than 14
						sb.append(").^(");
					} else {
						sb.append(")^(");
					}

					sb.append(rightStr);
					sb.append(")");
				}

				break;

			case LATEX:

				// checks if the basis is leaf and if so
				// omits the brackets
				if (left.isLeaf() && (leftStr.charAt(0) != '-')) {
					sb.append(leftStr);
					break;
				}
				// else fall through
			case LIBRE_OFFICE:
			default:

				/*
				 * removed Michael Borcherds 2009-02-08 doesn't work eg m=1 g(x)
				 * = (x - 1)^m (x - 3)
				 * 
				 * 
				 * // check for 1 in exponent if (isEqualString(right, 1,
				 * !valueForm)) { sb.append(leftStr); break; } //
				 */

				// left wing
				if ((leftStr.charAt(0) != '-')
						&& // no unary
						(left.isLeaf() || ((ExpressionNode.opID(left) > Operation.POWER
								.ordinal()) && (ExpressionNode.opID(left) != Operation.EXP
								.ordinal())))) { // not +, -, *, /, ^,
					// e^x

					// we might need more brackets here #4764
					sb.append(leftStr);
				} else {
					sb.append(leftBracket());
					sb.append(leftStr);
					sb.append(rightBracket());
				}
				break;
			}

			// right wing
			switch (stringType) {
			case LATEX:
			case LIBRE_OFFICE:
				// print x^1 as x
				if ("1".equals(rightStr)) {
					break;
				}
				sb.append('^');

				// add brackets for eg a^b^c -> a^(b^c)
				boolean addParentheses = (right.isExpressionNode() && ((ExpressionNode) right)
						.getOperation().equals(Operation.POWER));

				sb.append('{');
				if (addParentheses) {
					sb.append(leftBracket());
				}
				sb.append(rightStr);
				if (addParentheses) {
					sb.append(rightBracket());
				}
				sb.append('}');
				break;
			// rightStr already done in Giac
			case GIAC:
				break;
			case PSTRICKS:
			case PGF:
			case GEOGEBRA_XML:
				sb.append('^');
				sb.append('(');
				sb.append(rightStr);
				sb.append(')');
				break;

			default:
				if (right.isLeaf()
						|| ((ExpressionNode.opID(right) > Operation.POWER
								.ordinal()) && (ExpressionNode.opID(right) != Operation.EXP
								.ordinal()))) { // not
					// +,
					// -,
					// *,
					// /,
					// ^,
					// e^x
					// Michael Borcherds 2008-05-14
					// display powers over 9 as unicode superscript
					try {
						int i = Integer.parseInt(rightStr);
						String index = "";
						if (i < 0) {
							sb.append('\u207B'); // superscript minus sign
							i = -i;
						}

						if (i == 0) {
							sb.append('\u2070'); // zero
						} else {
							while (i > 0) {
								switch (i % 10) {
								case 0:
									index = "\u2070" + index;
									break;
								case 1:
									index = "\u00b9" + index;
									break;
								case 2:
									index = "\u00b2" + index;
									break;
								case 3:
									index = "\u00b3" + index;
									break;
								case 4:
									index = "\u2074" + index;
									break;
								case 5:
									index = "\u2075" + index;
									break;
								case 6:
									index = "\u2076" + index;
									break;
								case 7:
									index = "\u2077" + index;
									break;
								case 8:
									index = "\u2078" + index;
									break;
								case 9:
									index = "\u2079" + index;
									break;

								}
								i = i / 10;
							}
						}

						sb.append(index);
					} catch (Exception e) {
						sb.append('^');
						sb.append(rightStr);
					}

				} else {
					sb.append('^');
					sb.append(leftBracket());
					sb.append(rightStr);
					sb.append(rightBracket());
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Converts 5.1E-20 to 5.1*10^(-20) or 5.1 \cdot 10^{-20} depending on
	 * current print form
	 * 
	 * @param scientificStr
	 *            string in scientific notation
	 * @param tpl
	 *            string template for output
	 * @return formated string in scientific notation (except for Giac)
	 */
	public String convertScientificNotation(String scientificStr) {

		// for Giac, don't want 3E3 or 3*10^3
		if (hasCASType()) {
			return convertScientificNotationGiac(scientificStr);
		}
		// in XML we write the original to avoid brackets and priority problems
		// #4764
		if (hasType(StringType.GEOGEBRA_XML)) {
			return scientificStr;
		}

		StringBuilder sb = new StringBuilder(scientificStr.length() * 2);
		boolean Efound = false;
		for (int i = 0; i < scientificStr.length(); i++) {
			char ch = scientificStr.charAt(i);
			if (ch == 'E') {
				if (hasType(StringType.LATEX)) {
					sb.append(" \\cdot 10^{");
				} else {
					sb.append("*10^(");
				}
				Efound = true;
			} else if (ch != '+') {
				sb.append(ch);
			}
		}
		if (Efound) {
			if (hasType(StringType.LATEX)) {
				sb.append("}");
			} else {
				sb.append(")");
			}
		}
		if (Efound && !this.isPrintLocalizedCommandNames()) {
			sb.insert(0, '(');
			sb.append(')');
		}

		return sb.toString();
	}

	/*
	 * convert 3E3 to 3000 convert 3.33 to 333/100 convert 3E-3 to 3/1000
	 */
	public String convertScientificNotationGiac(String originalString) {

		if (isNumeric()) {
			return originalString.replace('E', 'e');
		}

		if (originalString.indexOf("E-") > -1) {

			String[] s = originalString.split("E-");

			int i = Integer.parseInt(s[1]);

			int dotIndex = s[0].indexOf('.');

			if (dotIndex > -1) {
				// eg 2.22E-100
				i += s[0].length() - dotIndex - 1;
				s[0] = s[0].replace(".", "");
			}

			// brackets just in case
			// 2^2.2E-1 is different to 2^22/100
			return "(" + s[0] + "/1" + StringUtil.repeat('0', i) + ")";

		} else if (originalString.indexOf("E") > -1) {
			String[] s = originalString.split("E");

			int i = Integer.parseInt(s[1]);

			int dotIndex = s[0].indexOf('.');

			if (dotIndex > -1) {
				// eg 2.22E100 need i=98
				i -= s[0].length() - dotIndex - 1;
				s[0] = s[0].replace(".", "");
			}
			// c: -5116.91572736879x^2 - 15556.1551078899x y - 11496.6010564053y^2
			// - 2234610.47543873x - 3369532.76964123y = 243297252.338397
			// d: -19182.5685338018x^2 - 7781.50649444574x y - 639.272043575625y^2
			// - 5784784.13901330x - 1154843.72376044y = 435372862.870553
			// Intersect[c, d]
			// eg 4.35372862870553E8
			// need to add decimal point back in
			if (i < 0) {
				return s[0].substring(0, s[0].length() + i) + "."
						+ s[0].substring(s[0].length() + i);
			}

			if (i == 0) {
				return s[0];
			}

			return s[0] + StringUtil.repeat('0', i);
		}

		int dotIndex = originalString.indexOf('.');

		if (dotIndex > -1) {

			// eg 4.4%
			if (originalString.endsWith("%")) {
				return "("
						+ originalString.substring(0,
								originalString.length() - 1).replace(".", "")
						+ "/1"
						+ StringUtil.repeat('0', originalString.length()
								- dotIndex) + ")";
			}

			// eg 2.22 -> (222/100) or 02.22 -> (222/100)
			return "("
					+ (originalString.replace(".", "")).replaceFirst(
							"^0+(?!$)", "")
					+ "/1"
					+ StringUtil.repeat('0', originalString.length() - dotIndex
							- 1) + ")";
		}

		// #5500 %], %%), %%%) have special meanings in Giac, eg [[a:=3%],a][1]
		// doesn't work
		// so wrap in brackets with a space just to make sure
		if (originalString.endsWith("%")) {
			return "(" + originalString + " )";
		}
		// simple integer, no need to change
		return originalString;
	}

	public boolean isHideLHS() {
		return this.hideLHS;
	}

	public boolean allowPiHack() {
		return this.allowPiHack;
	}

	public static String[] printLimitedWidth(double decimal, Kernel kernel,
			String[] parts) {
		if (Math.abs(decimal) < 1E4
				&& (Math.abs(decimal) > 1E-4 || Kernel.isZero(decimal))) {
			parts[0] = kernel.format(decimal, defaultTemplate);
			parts[1] = null;
			return parts;
		}
		StringTemplate stl = StringTemplate.printScientific(
				StringType.GEOGEBRA, 2, false);

		// returns string like 3456E-7
		String str = kernel.format(decimal, stl);

		String[] strs = str.split("E");
		return strs;
	}

	public boolean isMathQuill() {
		return false;
	}

	public String escapeString(String string) {
		return string;
	}

	public boolean hasQuestionMarkForNaN() {
		return this.questionMarkForNaN;
	}

	public void leftCurlyBracket(StringBuilder sb) {
		if (hasType(StringType.LATEX)) {
			sb.append("\\left\\{");
		} else {
			sb.append("{");
		}

	}

	public void rightCurlyBracket(StringBuilder sb) {
		if (hasType(StringType.LATEX)) {
			sb.append("\\right\\}");
		} else {
			sb.append("}");
		}

	}

	/**
	 * @return true if numeric rather than exact is required eg 1.23 for
	 *         NSolve[] in the CAS View rather than changing to 123/100 for most
	 *         other commands
	 */
	public boolean isNumeric() {
		return numeric;
	}

	public boolean supportsFractions() {
		return supportsFractions;
	}

	public StringTemplate deriveWithFractions(boolean fractions) {
		if (supportsFractions == fractions) {
			return this;
		}

		StringTemplate ret = this.copy();

		ret.supportsFractions = fractions;

		return ret;
	}

	/**
	 * 
	 * GGB-1106
	 * 
	 * @param s
	 *            number to pad
	 * @param phantom
	 *            whether to make extra digits invisible (just for padding)
	 * @param defaultDigits
	 *            default if not set explicitly for the GeoText
	 * @param suffix
	 *            string suffix eg %
	 * @return number padded, eg 1 padded to 1.00 (2dp) and wrapped in \texttt
	 *         (monospace font)
	 */
	public String padZerosAfterDecimalPoint(String s, boolean phantom,
			int defaultDigits, String suffix) {

		if (!StringUtil.isNumber(s)) {
			if (!phantom) {
				return wrapInTexttt(s);
			}
			return wrapInTexttt(s + wrapInPhantom(
					"." + StringUtil.string("0", defaultDigits), ""));
		}

		int length = s.length();
		int pointPos = length - s.indexOf('.') - 1;

		int digits = nf == null ? defaultDigits : nf.getMaximumFractionDigits();
		int zerosToAdd = digits - (pointPos);

		if (pointPos >= length) {

			if (digits == 0) {
				return wrapInTexttt(s + suffix);
			}

			if (phantom) {
				return wrapInTexttt(
						s + wrapInPhantom(
								"." + StringUtil.string("0", digits), suffix));
			}
			return wrapInTexttt(
					s + "." + StringUtil.string("0", digits) + suffix);

		}

		if (zerosToAdd == 0) {
			return wrapInTexttt(s + suffix);
		}

		if (zerosToAdd < 0) {
			Log.error("problem in TableText[] " + s);
		}

		if (phantom) {
			return wrapInTexttt(
					s + wrapInPhantom(StringUtil.string("0", zerosToAdd),
							suffix));
		}

		return wrapInTexttt(s + StringUtil.string("0", zerosToAdd) + suffix);

	}

	/**
	 * @param s
	 *            input
	 * @return input wrapped in texttt{}
	 */
	private static String wrapInTexttt(String s) {
		return "\\texttt{" + s + "}";
	}

	/**
	 * GGB-1106 \texttt{\phantom{}} doesn't seem to work,
	 * \texttt{\phantom{\texttt{}}} seems OK
	 * 
	 * @param s
	 *            string to wrap
	 * @return wrapped string
	 */
	private static String wrapInPhantom(String s, String prefix) {
		return prefix + "\\phantom{\\texttt{" + s + "}}";
	}

}
