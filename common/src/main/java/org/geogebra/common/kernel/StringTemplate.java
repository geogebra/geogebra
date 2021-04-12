package org.geogebra.common.kernel;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.export.MathmlTemplate;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MinusOne;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * StringTemplate provides a container for all settings we might need when
 * serializing ExpressionValues to screen / XML / CAS input / export.
 *
 * @author Zbynek Konecny
 */
public class StringTemplate implements ExpressionNodeConstants {

	// rounding hack, see Kernel.format()
	private static final double ROUND_HALF_UP_FACTOR = 1.0 + 1E-15;
	private static final String RAD = "rad";

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

	private boolean changeArcTrig = true;

	private boolean localizeCmds;
	private boolean usePrefix;
	private boolean questionMarkForNaN = true;

	private boolean numeric = true;

	private boolean niceQuotes = false;

	private boolean shouldPrintMethodsWithParenthesis;
	private boolean forEditorParser = false;
	private boolean allowShortLhs = true;

	/**
	 * Default template, but do not localize commands
	 */
	public static final StringTemplate noLocalDefault = new StringTemplate(
			"nonLocalDefault");

	static {
		noLocalDefault.localizeCmds = false;
	}

	private static final double[] precisions = new double[] { 1, 1E-1, 1E-2,
			1E-3, 1E-4, 1E-5, 1E-6, 1E-7, 1E-8, 1E-9, 1E-10, 1E-11, 1E-12,
			1E-13, 1E-14, 1E-15, 1E-16 };

	private boolean allowPiHack = true;

	private boolean supportsFractions = true;

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

	static {
		prefixedDefault.localizeCmds = false;
		prefixedDefault.internationalizeDigits = false;
		prefixedDefault.usePrefix = true;
		prefixedDefault.allowMoreDigits = true;
		prefixedDefault.sf = FormatFactory.getPrototype()
				.getScientificFormat(15, 20, false);
	}

	/**
	 * Template which prints numbers with maximal precision and adds prefix to
	 * variables (ggbtmpvar)
	 */
	public static final StringTemplate prefixedDefaultSF = new StringTemplate(
			"prefixedDefaultSF") {
		@Override
		public double getRoundHalfUpFactor(double abs, NumberFormatAdapter nf2,
				ScientificFormatAdapter sf2, boolean useSF) {
			return 1;
		}
	};

	static {
		prefixedDefaultSF.localizeCmds = false;
		prefixedDefaultSF.internationalizeDigits = false;
		prefixedDefaultSF.usePrefix = true;
		prefixedDefaultSF.forceSF = true;
		prefixedDefaultSF.sf = FormatFactory.getPrototype()
				.getScientificFormat(15, 20, false);
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
	 * Template which prints original construction's labels
	 */
	public static final StringTemplate algebraTemplate = new StringTemplate(
			"algebraTemplate");

	static {
		algebraTemplate.niceQuotes = true;
		algebraTemplate.allowPiHack = false;
	}

	/**
	 * LaTeX string type, do not internationalize digits
	 */
	public static final StringTemplate latexTemplate = new StringTemplate(
			"latexTemplate");

	static {
		latexTemplate.setType(StringType.LATEX);
		latexTemplate.allowPiHack = false;
	}

	/**
	 * JLaTeXMath latex template
	 */
	public static final StringTemplate latexTemplateJLM = new StringTemplate(
			"latexTemplate") {

		@Override
		public String escapeString(String string) {
			return string.replaceAll("\\\\", "\\\\backslash ")
					.replaceAll("([&%$#{}_])", "\\\\$1")
					.replaceAll("~", "\u223C ")
					.replaceAll("\\^", "\\\\^{\\ } ");
		}
	};

	static {
		latexTemplateJLM.setType(StringType.LATEX);
	}

	/**
	 * LaTeX template for CAS; like LaTeX template but do not substitute
	 * 3.1415926535 by pi
	 */
	public static final StringTemplate latexTemplateCAS = new StringTemplate(
			"latexTemplate");

	static {
		latexTemplateCAS.setType(StringType.LATEX);
		latexTemplateCAS.allowPiHack = false;
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
	 * For printing CAS Expressions nicely
	 */
	public static final StringTemplate casPrintTemplate = new StringTemplate(
			"casPrintTemplate");

	static {
		casPrintTemplate.internationalizeDigits = true;
		casPrintTemplate.allowPiHack = false;
		casPrintTemplate.numeric = false;
		casPrintTemplate.usePrefix = false;
		casPrintTemplate.forceNF = true;
		casPrintTemplate.localizeCmds = true;
		casPrintTemplate.setType(StringType.GEOGEBRA);
		casPrintTemplate.nf = FormatFactory.getPrototype().getNumberFormat(15);
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
		giacTemplate.nf = FormatFactory.getPrototype().getNumberFormat(15);

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
		xmlTemplate.sf = FormatFactory.getPrototype().getScientificFormat(15,
				20, false);
		xmlTemplate.questionMarkForNaN = false;
		xmlTemplate.changeArcTrig = false;
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
		casCopyTemplate.changeArcTrig = false;
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
		initForEditing(editTemplate);
		editTemplate.changeArcTrig = false;
		initForEditing(editorTemplate);
		editorTemplate.forEditorParser = true;
	}

	/**
	 * Template for regression: uses 6 figures or 6 sig digits based on Kernel
	 * settings, string type is XML
	 */
	public static final StringTemplate regression = new StringTemplate(
			"regression");

	static {
		regression.sf = FormatFactory.getPrototype().getScientificFormat(6, 20,
				false);
		regression.nf = FormatFactory.getPrototype().getNumberFormat(6);
		regression.forceSF = true;
		regression.setType(StringType.GEOGEBRA_XML);
		regression.changeArcTrig = false;
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
		ogpTemplate.nf = FormatFactory.getPrototype().getNumberFormat(0);
	}

	/**
	 * Default template, just increases precision to max
	 */
	public static final StringTemplate maxPrecision = new StringTemplate(
			"maxPrecision");

	static {
		maxPrecision.sf = FormatFactory.getPrototype().getScientificFormat(15,
				20, false);
		maxPrecision.allowMoreDigits = true;
		maxPrecision.forceSF = true;
		maxPrecision.localizeCmds = false;
	}

	/**
	 * GGB-2454
	 */
	public static final StringTemplate screenReader = new StringTemplate(
			"screenReader");

	static {
		screenReader.setType(StringType.SCREEN_READER);
		screenReader.localizeCmds = true;
	}

	/**
	 * High precision, fixed decimal places (15)
	 */
	public static final StringTemplate maxDecimals = new StringTemplate(
			"maxDecimals");

	static {
		maxDecimals.nf = FormatFactory.getPrototype().getNumberFormat(15);
		maxDecimals.allowMoreDigits = false;
		maxDecimals.forceNF = true;
		maxDecimals.localizeCmds = false;
	}

	public static final StringTemplate casCompare = new StringTemplate(
			"casCompare");

	static {
		casCompare.nf = FormatFactory.getPrototype().getNumberFormat(10);
		casCompare.allowMoreDigits = false;
		casCompare.forceNF = true;
		casCompare.localizeCmds = false;
		casCompare.allowShortLhs = false;
	}

	/**
	 * Just used for tests
	 */
	public static final StringTemplate maxPrecision13 = new StringTemplate(
			"maxPrecision13");

	static {
		maxPrecision13.sf = FormatFactory.getPrototype().getScientificFormat(13,
				20, false);
		maxPrecision13.allowMoreDigits = true;
		maxPrecision13.forceSF = true;
		maxPrecision13.localizeCmds = false;
		maxPrecision13.printFormPI = "3.141592653590";
	}

	/**
	 * Default template, just increases precision to max 13 not 15 so that when
	 * sent to Giac is treated as a double, not a multi-precision float (MPFR).
	 * #5130
	 */
	public static final StringTemplate giacNumeric13 = new StringTemplate(
			"giacNumeric13");

	static {
		giacNumeric13.sf = FormatFactory.getPrototype().getScientificFormat(13,
				20, false);
		giacNumeric13.allowMoreDigits = true;
		giacNumeric13.forceSF = true;
		giacNumeric13.localizeCmds = false;
		giacNumeric13.setType(StringType.GIAC);

		// don't want to use exact value otherwise Giac will do an exact
		// calculation when we want approx
		// eg Integral[sin(x) / (1 + a^2 - 2a cos(x)), 0, pi] in the Algebra
		// View
		// #5129, #5130

		giacNumeric13.printFormPI = "3.141592653590";
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
			"numericNoLocal") {

		@Override
		public double getRoundHalfUpFactor(double abs, NumberFormatAdapter nf2,
				ScientificFormatAdapter sf2, boolean useSF) {
			return 1;
		}
	};

	static {
		numericNoLocal.allowMoreDigits = true;
		numericNoLocal.localizeCmds = false;
		numericNoLocal.forceSF = true;
		numericNoLocal.sf =
				FormatFactory.getPrototype()
						.getScientificFormat(15, 20, false);
	}

	/**
	 * Default LaTeX template, just allow bigger precision for Numeric command
	 */
	public static final StringTemplate numericLatex = new StringTemplate(
			"numericLatex");

	static {
		numericLatex.stringType = StringType.LATEX;
		numericLatex.allowMoreDigits = true;
		numericLatex.allowPiHack = false;
		numericLatex.useRealLabels = true;
	}

	/** Generic template for CAS tests */
	public static final StringTemplate testTemplate = new StringTemplate(
			"testTemplate");

	static {
		testTemplate.internationalizeDigits = false;
		testTemplate.setType(StringType.GEOGEBRA_XML);
		// testTemplate.localizeCmds = false;
		testTemplate.sf = FormatFactory.getPrototype().getScientificFormat(15,
				20, false);
		testTemplate.changeArcTrig = false;
	}

	/**
	 * No localized digits, max precision
	 */
	public static final StringTemplate testTemplateJSON = new StringTemplate(
			"testTemplate");

	static {
		testTemplateJSON.internationalizeDigits = false;
		// testTemplate.localizeCmds = false;
		testTemplateJSON.sf = FormatFactory.getPrototype().getScientificFormat(
				15,
				20, false);
		testTemplateJSON.changeArcTrig = false;
	}

	/** Template for CAS tests involving Numeric command */
	public static final StringTemplate testNumeric = new StringTemplate(
			"testNumeric");

	static {
		testNumeric.internationalizeDigits = false;
		testNumeric.setType(StringType.GEOGEBRA_XML);
		// testNumeric.localizeCmds = false;
		testNumeric.allowMoreDigits = true;
		testNumeric.sf = FormatFactory.getPrototype().getScientificFormat(15,
				20, false);
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

	private static void initForEditing(StringTemplate template) {
		template.sf = FormatFactory.getPrototype().getScientificFormat(
				GeoElement.MIN_EDITING_PRINT_PRECISION, 20, false);
		template.nf = FormatFactory.getPrototype()
				.getNumberFormat(GeoElement.MIN_EDITING_PRINT_PRECISION);
		template.allowMoreDigits = true;
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
			printFormImaginary = Unicode.IMAGINARY + "";
			break;

		case LATEX:
			printFormPI = "\\pi ";
			printFormImaginary = "i";
			break;

		case LIBRE_OFFICE:
			printFormPI = "%pi";
			printFormImaginary = "i";
			break;

		case SCREEN_READER:
			printFormPI = " pi ";
			printFormImaginary = " i ";
			break;

		default:
			// #5129
			// #5130
			printFormPI = Unicode.PI_STRING;
			printFormImaginary = Unicode.IMAGINARY + "";
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
		tpl.nf = FormatFactory.getPrototype().getNumberFormat(decimals);
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
		tpl.sf = FormatFactory.getPrototype().getScientificFormat(decimals, 20,
				false);
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
		tpl.sf = FormatFactory.getPrototype().getScientificFormat(16, 350,
				false);
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
		tpl.sf = FormatFactory.getPrototype().getScientificFormat(decimals, 20,
				true);
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
				|| (allowMoreDigits && sfk.getSigDigits() > sf.getSigDigits())
						? sfk : sf;
	}

	/**
	 * Receives default NF and returns NF to be used
	 *
	 * @param nfk
	 *            default
	 * @return NF to be used
	 */
	public NumberFormatAdapter getNF(NumberFormatAdapter nfk) {
		return nf == null || (allowMoreDigits && nfk
				.getMaximumFractionDigits() > nf.getMaximumFractionDigits())
						? nfk : nf;
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

		int digits = useSF ? sf2.getSigDigits()
				: nf2.getMaximumFractionDigits();

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
		result.printFormImaginary = printFormImaginary;
		result.internationalizeDigits = internationalizeDigits;
		result.useRealLabels = useRealLabels;
		result.localizeCmds = localizeCmds;
		result.forceNF = forceNF;
		result.forceSF = forceSF;
		result.supportsFractions = supportsFractions;
		result.questionMarkForNaN = questionMarkForNaN;
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

		switch (getStringType()) {
		case GIAC:
			// make sure we don't interfere with reserved names
			// or command names in the underlying CAS
			// see TRAC-793
			return addTempVariablePrefix(label.replace("$", ""));

		case LATEX:
			if ("l".equals(label)) {
				return "\\ell";
			}
			
			// eg $1 in "Keep Input" mode
			return label.replace("$", "\\$");
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
	private static String addTempVariablePrefix(final String label) {

		// keep x, y, z so that x^2+y^2=1 works in Giac
		if (!GeoGebraCAS.needsTmpPrefix(label)) {
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

	/**
	 * @param v
	 *            expression value
	 * @return whether it's a vector (2D or 3D)
	 */
	protected boolean isNDvector(ExpressionValue v) {
		return v.evaluatesToNonComplex2DVector() || v.evaluatesTo3DVector();
	}

	/**
	 * @return whether line breaks are allowed
	 */
	public boolean isInsertLineBreaks() {
		return false;
	}

	/**
	 * @param l
	 *            left subtree
	 * @param r
	 *            right subtree
	 * @param leftStr
	 *            left subtree as string
	 * @param rightStr
	 *            right subtree as string
	 * @param valueForm
	 *            whether to show values rather than names
	 * @param loc
	 *            localization
	 * @return l+r as string
	 */
	public String plusString(ExpressionValue l, ExpressionValue r,
			String leftStr, String rightStr, boolean valueForm,
			Localization loc) {
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
			if (left.evaluatesToList() && (right.evaluatesToNumber(false)
					|| right instanceof NumberValue)) {
				// eg {1,2,3} + 10
				sb.append("map(");
				sb.append(leftStr);
				sb.append(",ggx->ggx+(");
				sb.append(rightStr);
				sb.append("))");

				// don't use isNumberValue(), isListValue as those lead to an
				// evaluate()
			} else if ((left.evaluatesToNumber(false)
					|| left instanceof NumberValue)
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
			} else if ((left.evaluatesToNumber(false)
					|| left instanceof NumberValue)
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
			} else if ((right.evaluatesToNumber(false)
					|| right instanceof NumberValue)
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
			} else if ((left.evaluatesToNumber(false)
					|| left instanceof NumberValue)
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
					&& (right.evaluatesToNumber(false)
							|| right instanceof NumberValue)) {
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
						sb.append("point(xcoord(");
						sb.append(leftStr);
						sb.append(')');
						sb.append("+xcoord(");
						sb.append(rightStr);
						sb.append("),ycoord(");
						sb.append(leftStr);
						sb.append(")+ycoord(");
						sb.append(rightStr);
						sb.append("),zcoord(");
						sb.append(leftStr);
						sb.append(")+zcoord(");
						sb.append(rightStr);
						sb.append("))");
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
				sb.append("point(xcoord(");
				sb.append(leftStr);
				sb.append(")+xcoord(");
				sb.append(rightStr);
				sb.append("),ycoord(");
				sb.append(leftStr);
				sb.append(")+ycoord(");
				sb.append(rightStr);
				sb.append("),zcoord(");
				sb.append(leftStr);
				sb.append(")+zcoord(");
				sb.append(rightStr);
				sb.append("))");

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
				appendWithBrackets(sb, leftStr);
			} else {
				sb.append(leftStr);
			}

			// we need parantheses around right text
			// if right is not a leaf expression or
			// it is a leaf GeoElement without a label (i.e. it is
			// calculated somehow)
			if (left.evaluatesToText()
					&& (!right.isLeaf() || (right.isGeoElement()
							&& !((GeoElement) right).isLabelSet()))) {
				if (stringType.equals(StringType.LATEX)
						&& isInsertLineBreaks()) {
					sb.append(" \\-+ ");
				} else {
					getPlus(sb, loc);
				}
				appendWithBrackets(sb, rightStr);
			} else {
				if (rightStr.charAt(0) == '-') { // convert + - to -
					if (stringType.equals(StringType.LATEX)
							&& isInsertLineBreaks()) {
						sb.append(" \\-- ");
					} else {
						getMinus(sb, loc);
					}
					sb.append(rightStr.substring(1));
				} else if (rightStr
						.startsWith(Unicode.RIGHT_TO_LEFT_UNARY_MINUS_SIGN)) { // Arabic
					// convert
					// +
					// -
					// to
					// -
					if (stringType.equals(StringType.LATEX)
							&& isInsertLineBreaks()) {
						sb.append(" \\-- ");
					} else {
						getMinus(sb, loc);
					}
					append(sb, rightStr.substring(3), right, Operation.PLUS);
				} else {
					if (stringType.equals(StringType.LATEX)
							&& isInsertLineBreaks()) {
						sb.append(" \\-+ ");
					} else {
						getPlus(sb, loc);
					}
					append(sb, rightStr, right, Operation.PLUS);
				}
			}
			break;
		}
		return sb.toString();

	}

	/**
	 * Appends localized + to a StringBUilder
	 * @param sb builder
	 * @param loc localization
	 */
	public void getPlus(StringBuilder sb, Localization loc) {
		if (stringType == StringType.SCREEN_READER) {
			sb.append(ScreenReader.getPlus(loc));
		} else {
			appendOptionalSpace(sb);
			sb.append('+');
			appendOptionalSpace(sb);
		}
	}

	/**
	 * Appends localized - to a StringBUilder
	 * @param sb builder
	 * @param loc localization
	 */
	public void getMinus(StringBuilder sb, Localization loc) {
		if (stringType == StringType.SCREEN_READER) {
			sb.append(ScreenReader.getMinus(loc));
		} else {
			appendOptionalSpace(sb);
			sb.append("-");
			appendOptionalSpace(sb);
		}
	}

	/**
	 * @return ( or \left(
	 */
	public String leftBracket() {
		if (stringType == StringType.SCREEN_READER) {
			return ScreenReader.getOpenParenthesis();
		}
		return left() + "(";
	}

	/**
	 * @return ) or \right)
	 */
	public String rightBracket() {
		if (stringType == StringType.SCREEN_READER) {
			return ScreenReader.getCloseParenthesis();
		}
		return right() + ")";
	}

	/**
	 * @return [ or \left[
	 */
	public String leftSquareBracket() {
		return left() + "[";
	}

	/**
	 * @return ] or \right]
	 */
	public String rightSquareBracket() {
		return right() + "]";
	}

	/**
	 * Used for French and Hungarian open intervals (StepByStep)
	 *
	 * @return left ]
	 */
	public String invertedLeftSquareBracket() {
		return left() + "]";
	}

	/**
	 * Used for French and Hungarian open intervals (StepByStep)
	 *
	 * @return right [
	 */
	public String invertedRightSquareBracket() {
		return right() + "[";
	}

	/**
	 * Used for Czech closed intervals (StepByStep)
	 *
	 * @return left <
	 */
	public String leftAngleBracket() {
		if (stringType.equals(StringType.LATEX)) {
			return " \\left \\langle";
		}

		return "\u3008";
	}

	/**
	 * Used for Czech closed intervals (StepByStep)
	 *
	 * @return right >
	 */
	public String rightAngleBracket() {
		if (stringType.equals(StringType.LATEX)) {
			return " \\right \\rangle";
		}

		return "\u3009";
	}

	private String right() {
		if (stringType.equals(StringType.LATEX)) {
			return " \\right";
		} else if (stringType.equals(StringType.LIBRE_OFFICE)) {
			return " right ";
		} else {
			return "";
		}
	}

	private String left() {
		if (stringType.equals(StringType.LATEX)) {
			return "\\left";
		} else if (stringType.equals(StringType.LIBRE_OFFICE)) {
			return " left ";
		} else {
			return "";
		}
	}

	/**
	 * @param l
	 *            left expression
	 * @param r
	 *            right expression
	 * @param leftStr
	 *            serialized left expression
	 * @param rightStr
	 *            serialized right expression
	 * @param valueForm
	 *            whether to substitute variables
	 * @param loc
	 *            localization
	 * @return l-r with appropriate brackets
	 */
	public String minusString(ExpressionValue l, ExpressionValue r,
			String leftStr, String rightStr, boolean valueForm,
			Localization loc) {
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
			if (left.evaluatesToList() && (right.evaluatesToNumber(false)
					|| right instanceof NumberValue)) {
				// eg {1,2,3} + 10
				sb.append("map(");
				sb.append(leftStr);
				sb.append(",ggx->ggx-(");
				sb.append(rightStr);
				sb.append("))");

			} else if ((left.evaluatesToNumber(false)
					|| left instanceof NumberValue)
					&& right.evaluatesToList()) {
				// eg 10 + {1,2,3}
				sb.append("map(");
				sb.append(rightStr);
				sb.append(",ggx->");
				sb.append(leftStr);
				sb.append("-ggx)");

			} else if ((left.evaluatesToNumber(false)
					|| left instanceof NumberValue)
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

			} else if ((right.evaluatesToNumber(false)
					|| right instanceof NumberValue)
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

			} else if ((left.evaluatesToNumber(false)
					|| left instanceof NumberValue)
					&& right.evaluatesTo3DVector()) {
				// eg 10 - (1,2,3)
				sb.append("(");
				sb.append(leftStr);
				sb.append("-(");
				sb.append(rightStr);
				sb.append(")[0],");
				sb.append(leftStr);
				sb.append("-(");
				sb.append(rightStr);
				sb.append(")[1],");
				sb.append(leftStr);
				sb.append("-(");
				sb.append(rightStr);
				sb.append(")[2])");

				// don't use isNumberValue(), isListValue as those lead to an
				// evaluate()
			} else if (left.evaluatesTo3DVector()
					&& (right.evaluatesToNumber(false)
							|| right instanceof NumberValue)) {
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
						sb.append("point(xcoord(");
						sb.append(leftStr);
						sb.append(")-xcoord(");
						sb.append(rightStr);
						sb.append("),ycoord(");
						sb.append(leftStr);
						sb.append(")-ycoord(");
						sb.append(rightStr);
						sb.append("),zcoord(");
						sb.append(leftStr);
						sb.append(")-zcoord(");
						sb.append(rightStr);
						sb.append("))");
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
				sb.append("point(xcoord(");
				sb.append(leftStr);
				sb.append(")-xcoord(");
				sb.append(rightStr);
				sb.append("),ycoord(");
				sb.append(leftStr);
				sb.append(")-ycoord(");
				sb.append(rightStr);
				sb.append("),zcoord(");
				sb.append(leftStr);
				sb.append(")-zcoord(");
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

		default:
			if (left instanceof Equation) {
				appendWithBrackets(sb, leftStr);
			} else {
				append(sb, leftStr, left, Operation.PLUS);
			}

			// check for 0 at right
			if (valueForm && rightStr.equals(loc.getZero() + "")) {
				break;
			}

			if (right.isLeaf() || (ExpressionNode
					.opID(right) >= Operation.MULTIPLY.ordinal())) { // not
				// +,
				// -

				if (rightStr.charAt(0) == '-') { // convert - - to +
					if (stringType.equals(StringType.LATEX)
							&& isInsertLineBreaks()) {
						sb.append(" \\-+ ");
					} else {
						getPlus(sb, loc);
					}
					sb.append(rightStr.substring(1));
				} else if (rightStr
						.startsWith(Unicode.RIGHT_TO_LEFT_UNARY_MINUS_SIGN)) { // Arabic
					// convert
					// -
					// -
					// to
					// +
					if (stringType.equals(StringType.LATEX)
							&& isInsertLineBreaks()) {
						sb.append(" \\-+ ");
					} else {
						getPlus(sb, loc);
					}
					sb.append(rightStr.substring(3));
				} else {
					if (stringType.equals(StringType.LATEX)
							&& isInsertLineBreaks()) {
						sb.append(" \\-- ");
					} else {
						getMinus(sb, loc);
					}
					sb.append(rightStr);
				}
			} else {
				// fix for changing height in Algebra View plus / minus
				if (stringType.equals(StringType.LATEX)
						&& isInsertLineBreaks()) {
					sb.append(" \\-- ");
				} else {
					getMinus(sb, loc);
				}
				appendWithBrackets(sb, rightStr);
			}
			break;
		}
		return sb.toString();
	}

	/**
	 * @param left
	 *            left expression
	 * @param right
	 *            right expression
	 * @param leftStr
	 *            serialized left expression
	 * @param rightStr
	 *            serialized right expression
	 * @param valueForm
	 *            whether to substitute variables
	 * @param loc
	 *            localization
	 * @return left + right with appropriate brackets
	 */
	public String multiplyString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr, boolean valueForm,
			Localization loc) {
		StringBuilder sb = new StringBuilder();
		switch (stringType) {

		case CONTENT_MATHML:
			MathmlTemplate.mathml(sb, "<times/>", leftStr, rightStr);
			break;
		case GIAC:
			appendGiacMultiplication(sb, left, right, leftStr, rightStr, valueForm);
			break;
		default:
			appendMultiplySpecial(sb, leftStr, rightStr, left, loc);
			if (sb.length() > 0) {
				break;
			}

		case LATEX:
		case LIBRE_OFFICE:

			boolean nounary = true;

			// vector * (matrix * vector) needs brackets; always use brackets
			// for internal templates
			if (useExtensiveBrackets()
					|| (left.evaluatesToList() && isNDvector(right))) {
				sb.append(leftBracket());
			}

			// left wing
			if (left.isLeaf() || (ExpressionNode
					.opID(left) >= Operation.MULTIPLY.ordinal())) { // not +, -
				if (left instanceof MinusOne) { // unary minus
					nounary = false;
					sb.append('-');
				} else {
					if (leftStr.startsWith(
							Unicode.RIGHT_TO_LEFT_UNARY_MINUS_SIGN)) {
						// brackets needed for eg Arabic digits
						sb.append(Unicode.RIGHT_TO_LEFT_MARK);
						appendWithBrackets(sb, leftStr);
						sb.append(Unicode.RIGHT_TO_LEFT_MARK);
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
			if (opIDright == Operation.DIVIDE.ordinal() && !nounary
					&& stringType == StringType.LATEX) {
				sb.append(rightStr);
			} else  if (right.isLeaf() || (opIDright >= Operation.MULTIPLY.ordinal())) { // not
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
					case SCREEN_READER:
						showMultiplicationSign = true;
						break;

					case LIBRE_OFFICE:
					case LATEX:
						// check if we need a multiplication sign, see #414
						// digit-digit, e.g. 3 * 5
						// digit-fraction, e.g. 3 * \frac{5}{2}
						if (!leftStr.isEmpty() && !rightStr.isEmpty()) {
							char lastLeft = leftStr.charAt(leftStr.length() - 1);
							char firstRight = rightStr.charAt(0);
							showMultiplicationSign =
									StringUtil.isDigit(firstRight)
											// left is digit or ends with }, e.g. exponent,
											// fraction
											|| (StringUtil.isDigit(lastLeft)
											|| lastLeft == '}')
											&& rightStr
											.startsWith("\\frac");
							multiplicationSpaceNeeded = !isDegree(right);
						}
						break;

					default: // GeoGebra syntax
						if (!leftStr.isEmpty() && !rightStr.isEmpty()) {
							char lastLeft = leftStr.charAt(leftStr.length() - 1);
							char firstRight = rightStr.charAt(0);
							// check if we need a multiplication sign, see #414
							// digit-digit, e.g. 3 * 5
							showMultiplicationSign =
									(Character.isDigit(lastLeft) || lastLeft == ')')
											&& (StringUtil.isDigit(firstRight)
											// 3*E23AB can't be written 3E23AB
											|| (firstRight == 'E'))
											|| StringUtil.isDigit(firstRight);
							// check if we need a multiplication space:
							multiplicationSpaceNeeded = showMultiplicationSign;
							if (!multiplicationSpaceNeeded) {
								// check if we need a multiplication space:
								// it's needed except for number * character,
								// e.g. 23x
								// need to check start and end for eg A1 * A2
								boolean leftIsNumber = left.wrap()
										.endsInNumber(valueForm);

								// check if we need a multiplication space:
								// all cases except number * character, e.g. 3x
								// pi*x DOES need a multiply
								multiplicationSpaceNeeded =
										!leftIsNumber
												|| Character.isDigit(firstRight)
												|| rightStr.equals(RAD)
												|| (forEditorParser && !isDegree(right));
							}
						}
					}

					if (stringType.equals(StringType.LATEX)
							&& isInsertLineBreaks()) {
						sb.append("\\-");
					}

					if (showMultiplicationSign) {
						sb.append(multiplicationSign(loc));
					} else if (multiplicationSpaceNeeded) {
						// space instead of multiplication sign
						sb.append(multiplicationSpace());
					}
				}

				boolean rtlMinus;
				// show parentheses around these cases
				if (((rtlMinus = rightStr
						.startsWith(Unicode.RIGHT_TO_LEFT_UNARY_MINUS_SIGN))
						|| (rightStr.charAt(0) == '-')) // 2 (-5) or -(-5)
						|| (!nounary && !right.isLeaf() // -(x*a) or -(x/a)
								&& (opIDright <= Operation.DIVIDE.ordinal()))) {
					if (rtlMinus) {
						sb.append(Unicode.RIGHT_TO_LEFT_MARK);
					}
					appendWithBrackets(sb, rightStr);
					if (rtlMinus) {
						sb.append(Unicode.RIGHT_TO_LEFT_MARK);
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
					case SCREEN_READER:
						sb.append(multiplicationSign(loc));
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
			if (useExtensiveBrackets()
					|| (left.evaluatesToList() && isNDvector(right))) {
				sb.append(rightBracket());
			}

			break;
		}
		return sb.toString();
	}

	private boolean isDegree(ExpressionValue right) {
		return right instanceof MySpecialDouble
				&& Unicode.DEGREE_STRING.equals(
				right.toString(defaultTemplate));
	}

	private void appendGiacMultiplication(StringBuilder sb, ExpressionValue left,
				ExpressionValue right, String leftStr, String rightStr, boolean valueForm) {
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
		}
	}

	private void appendMultiplySpecial(StringBuilder sb, String leftStr, String rightStr,
					ExpressionValue left, Localization loc) {
		// no chceck for 0: we need 0x + 1 to be a function, not number

		// check for degree sign or 1degree or degree1 (eg for Arabic)
		if ((rightStr.length() == 2
				&& ((rightStr.charAt(0) == Unicode.DEGREE_CHAR
				&& rightStr.charAt(1) == (loc.getZero() + 1))
				|| (rightStr.charAt(1) == Unicode.DEGREE_CHAR
				&& rightStr.charAt(0) == loc.getZero()
				+ 1)))

				|| rightStr.equals(Unicode.DEGREE_STRING)) {

			boolean rtl = loc.isRightToLeftDigits(this);

			if (rtl) {
				sb.append(Unicode.DEGREE_STRING);
			}

			if (!left.isLeaf()) {
				sb.append('('); // needed for eg (a+b)\u00b0
			}
			sb.append(leftStr);
			if (!left.isLeaf()) {
				sb.append(')'); // needed for eg (a+b)\u00b0
			}

			if (!rtl) {
				sb.append(Unicode.DEGREE_STRING);
			}
		}
	}

	/**
	 * To be safe add more brackets for XML, giac, PSTRICKS, ... but not to
	 * human readable formats (localized or GGB syntax in scientific calculator)
	 *
	 * @return whether to add extra brackets around multiplication
	 */
	private boolean useExtensiveBrackets() {
		return !localizeCmds && stringType != StringType.GEOGEBRA && stringType != StringType.LATEX;
	}

	/**
	 * @param v
	 *            expression
	 * @param valueMode
	 *            whether to print value rather than definition
	 * @return serialized expression
	 */
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

	/**
	 * @param loc
	 *            localization
	 * @return multiplication sign
	 */
	protected String multiplicationSign(Localization loc) {
		switch (stringType) {
		case LATEX:
			return " \\cdot ";

		case LIBRE_OFFICE:
			return " cdot ";

		case GEOGEBRA:
			// space for multiplication
			return !forEditorParser ? " * " : "*";

		case SCREEN_READER:
			return ScreenReader.getTimes(loc);

		default:
			return " * ";
		}
	}

	/**
	 * Appends space to string builder if needed (for operators)
	 * @param sb string builder
	 */
	public void appendOptionalSpace(StringBuilder sb) {
		if (!forEditorParser) {
			sb.append(" ");
		}
	}

	public String getOptionalSpace() {
		return !forEditorParser ? " " : "";
	}

	/**
	 * @return space denoting multiplication
	 */
	public String multiplicationSpace() {
		// wide space for multiplication space in LaTeX
		return (stringType.equals(StringType.LATEX)) ? " \\; " : " ";
	}

	/**
	 * Append expression to the string builder, add brackets if needed. Special
	 * handling for symbolic fractions.
	 *
	 * @param sb
	 *            builder
	 * @param str
	 *            serialized expression
	 * @param ev
	 *            expression
	 * @param op
	 *            parent node operation
	 */
	public void append(StringBuilder sb, String str, ExpressionValue ev,
			Operation op) {
		if (isFraction(ev)) {
			append(sb, str, ((GeoElement) ev).getDefinition(), op);
			return;
		}
		if (ev.isLeaf() || (ExpressionNode.opID(ev) >= op.ordinal())
				&& (!ExpressionNode.chainedBooleanOp(op) || !ExpressionNode
						.chainedBooleanOp(ev.wrap().getOperation()))) {
			sb.append(str);
		} else {
			appendWithBrackets(sb, str);
		}
	}

	/**
	 * @param ev
	 *            expression
	 * @return whether expression is a GeoNumeric that needs to be written as a
	 *         fraction
	 */
	public static boolean isFraction(ExpressionValue ev) {
		return ev.isGeoElement() && ((GeoElement) ev).isGeoNumeric()
				&& ((GeoNumeric) ev).isSymbolicMode() && ((GeoElement) ev).getDefinition() != null
				&& ((GeoElement) ev).getDefinition().isFraction();
	}

	/**
	 * @param left
	 *            left expression
	 * @param right
	 *            right expression
	 * @param leftStr
	 *            serialized left expression
	 * @param rightStr
	 *            serialized right expression
	 * @param valueForm
	 *            whether to substitute variables
	 * @param loc
	 *            localization
	 * @return left / right with appropriate brackets
	 */
	public String divideString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr, boolean valueForm,
			Localization loc) {
		StringBuilder sb = new StringBuilder();
		switch (stringType) {
		case SCREEN_READER:
			ScreenReader.fraction(sb, leftStr, rightStr, loc);

			break;
		case CONTENT_MATHML:
			MathmlTemplate.mathml(sb, "<divide/>", leftStr, rightStr);
			break;
		case LATEX:
			sb.append("\\frac{");
			sb.append(leftStr);
			sb.append("}{");
			sb.append(rightStr);
			sb.append("}");
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
			if (forEditorParser) {
				appendWithBrackets(sb, leftStr);
				sb.append('/');
				appendWithBrackets(sb, rightStr);
				break;
			}

			// left wing
			// put parentheses around +, -, *
			if (left.isExpressionNode()
					&& ((ExpressionNode) left)
							.getOperation() == Operation.MULTIPLY
					&& !((ExpressionNode) left).hasBrackets()
					&& ExpressionNode.isConstantDouble(
							((ExpressionNode) left).getRight(), Math.PI)) {
				sb.append(leftStr);
			} else {
				append(sb, leftStr, left, Operation.DIVIDE);
			}
			appendOptionalSpace(sb);
			sb.append("/");
			appendOptionalSpace(sb);
			// right wing
			append(sb, rightStr, right, Operation.POWER); // not +, -, *, /
		}
		return sb.toString();
	}

	/**
	 * @param left
	 *            expression
	 * @param leftStr
	 *            serialized expression
	 * @return !left
	 */
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

			case GIAC:
				sb.append("!");
				appendWithBrackets(sb, leftStr);
				return sb.toString();

			default:
				sb.append(strNOT);
			}
			if (left.isLeaf()) {
				sb.append(leftStr);
			} else {
				appendWithBrackets(sb, leftStr);
			}
		}
		return sb.toString();
	}

	/**
	 * @param sb
	 *            builder
	 * @param string
	 *            binary op name
	 * @param leftStr
	 *            first argument
	 * @param rightStr
	 *            second argument
	 */
	public static void appendOp(StringBuilder sb, String string, String leftStr,
			String rightStr) {
		sb.append(string);
		sb.append('(');
		sb.append(leftStr);
		sb.append(',');
		sb.append(rightStr);
		sb.append(')');
	}

	/**
	 * @param left
	 *            left expression
	 * @param right
	 *            right expression
	 * @param leftStr
	 *            left string
	 * @param rightStr
	 *            right string
	 * @return leftStr || rightStr for this string type
	 */
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

	/**
	 * @param left
	 *            left expression
	 * @param right
	 *            right expression
	 * @param leftStr
	 *            left string
	 * @param rightStr
	 *            right string
	 * @return leftStr XOR rightStr for this string type
	 */
	public String xorString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr) {
		StringBuilder sb = new StringBuilder();

		if (stringType.equals(StringType.CONTENT_MATHML)) {
			MathmlTemplate.mathml(sb, "<xor/>", leftStr, rightStr);
		} else if (stringType.equals(StringType.GIAC)) {
			// !! to convert number -> boolean
			// !!a != !!b
			sb.append("(!!(");
			sb.append(leftStr);
			sb.append(")!=!!(");
			sb.append(rightStr);
			sb.append("))");
		} else {
			append(sb, leftStr, left, Operation.XOR);
			sb.append(' ');

			switch (stringType) {
			case LATEX:
				if (isInsertLineBreaks()) {
					sb.append("\\-");
				}
				sb.append("\\oplus");
				break;
			case LIBRE_OFFICE:
				sb.append("xor");
				break;

			default:
				sb.append(strXOR);
			}

			sb.append(' ');
			append(sb, rightStr, right, Operation.XOR);
			// sb.append(rightStr);
		}
		return sb.toString();
	}

	/**
	 * @return &gt;= sign
	 */
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

	/**
	 * @return &lt;= sign
	 */
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

	/**
	 * @return &gt; for this string type
	 */
	public String greaterSign() {
		if (hasType(StringType.LATEX) && isInsertLineBreaks()) {
			return "\\->";
		}
		return ">";
	}

	/**
	 * @return &lt; for this string type
	 */
	public String lessSign() {
		if (hasType(StringType.LATEX) && isInsertLineBreaks()) {
			return "\\-<";
		}
		return "<";
	}

	/**
	 * @return strict subset sign
	 */
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

	/**
	 * @return subset sign
	 */
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

	/**
	 * @return != sign
	 */
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

	/**
	 * @return == sign
	 */
	public String equalSign() {
		switch (getStringType()) {
		case LATEX:
			if (isInsertLineBreaks()) {
				return "\\-\\questeq ";
			}
			// #4068 changed from \stackrel{ \small ?}{=}
			return "\\questeq ";

		case LIBRE_OFFICE:
		case GIAC:
			return "=";
		default:
			return strEQUAL_BOOLEAN;
		}
	}

	/**
	 * @return sign for perpendicular lines
	 */
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

	/**
	 * @return sign for parallel lines
	 */
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

	/**
	 * Append left (op) right to the builder with brackets as needed.
	 *
	 * @param sb
	 *            builder
	 * @param left
	 *            left expression
	 * @param right
	 *            right expression
	 * @param operation
	 *            operation
	 * @param leftStr
	 *            serialized left expression
	 * @param rightStr
	 *            serialized right expression
	 * @param operationString
	 *            serialized operation
	 */
	public void infixBinary(StringBuilder sb, ExpressionValue left,
			ExpressionValue right, Operation operation, String leftStr,
			String rightStr, String operationString) {

		append(sb, leftStr, left, operation);
		appendOptionalSpace(sb);
		sb.append(operationString);
		appendOptionalSpace(sb);
		append(sb, rightStr, right, operation);
	}

	/**
	 * Serialize chained boolean operations, eg 2>x>1.
	 *
	 * @param left
	 *            left expression eg 2>x
	 * @param right
	 *            right expression eg x>1
	 *
	 * @param leftStr
	 *            serialized left expression
	 * @param rightStr
	 *            serialized right expression
	 * @param valueForm
	 *            whether to substitute variables
	 * @return 2>x>1 with appropriate brackets
	 *
	 */
	public String andIntervalString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr, boolean valueForm) {
		StringBuilder sb = new StringBuilder();
		if (stringType.equals(StringType.CONTENT_MATHML)
				|| stringType.isGiac()) {
			return andString(left, right, leftStr, rightStr);
		}
		if (right.isExpressionNode()) {
			sb.append(expressionToString(left, valueForm));
			appendOptionalSpace(sb);
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
			appendOptionalSpace(sb);
			sb.append(expressionToString(((ExpressionNode) right).getRight(), valueForm));
			return sb.toString();
		}
		return andString(left, right, leftStr, rightStr);
	}

	private String expressionToString(ExpressionValue left, boolean valueForm) {
		return valueForm ? left.toValueString(this)
				: ExpressionNode.getLabelOrDefinition(left, this);
	}

	/**
	 * @param left
	 *            left expression
	 * @param right
	 *            right expression
	 * @param leftStr
	 *            left string
	 * @param rightStr
	 *            right string
	 * @return leftStr AND rightStr for this string type
	 */
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

	/**
	 * @param left
	 *            left expression
	 * @param right
	 *            right expression
	 * @param leftStr
	 *            left string
	 * @param rightStr
	 *            right string
	 * @param valueForm
	 *            whether to substitute variables
	 * @param loc
	 *            localization
	 * @return leftStr || rightStr for this string type
	 */
	public String powerString(ExpressionValue left, ExpressionValue right,
			String leftStr, String rightStr, boolean valueForm,
			Localization loc) {
		if (stringType.equals(StringType.CONTENT_MATHML)) {
			StringBuilder sb = new StringBuilder();
			MathmlTemplate.mathml(sb, "<power/>", leftStr, rightStr);
			return sb.toString();
		} else if (stringType.equals(StringType.SCREEN_READER)) {
			return ScreenReader.power(leftStr, rightStr, loc);

		} else {
			StringBuilder sb = new StringBuilder();

			// support for sin^2(x)
			if ((stringType.equals(StringType.LATEX) || stringType.equals(StringType.GEOGEBRA))
					&& left.isExpressionNode() && isTrigFunction((ExpressionNode) left)
					&& right.isConstant()) {
				boolean latex = stringType.equals(StringType.LATEX);

				double indexD = right.evaluateDouble();

				// only positive integers
				// sin^-1(x) is arcsin
				// sin^-2(x) not standard notation
				if (indexD > 0 && DoubleUtil.isInteger(indexD)) {
					int index = (int) Math.round(indexD);
					String leftStrTrimmed = leftStr.trim();

					int spaceIndex = leftStrTrimmed.indexOf(latex ? ' ' : '(');
					sb.append(leftStrTrimmed, 0, spaceIndex);

					if (latex) {
						sb.append(" ^{");
						sb.append(rightStr);
						sb.append("}");
					} else {
						// alternative using Unicode
						sb.append(StringUtil.numberToIndex(index));
					}
					// everything except the "\\sin " or "sin"
					sb.append(leftStrTrimmed.substring(spaceIndex + (latex ? 1 : 0)));

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

				if ("e".equals(leftStr)
						|| Unicode.EULER_STRING.equals(leftStr)) {
					sb.append("exp(");
					sb.append(rightStr);
					sb.append(")");
					break;
				}

				if (right.isExpressionNode() && ((ExpressionNode) right)
						.getOperation() == Operation.DIVIDE
						&& right.isConstant()) {
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
					if (left.evaluatesToList()
							&& left.getListDepth() != 2) {
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
				if ((leftStr.charAt(0) != '-') && // no unary
						isSinglePowerArg(left) || left.isOperation(Operation.NROOT)
						|| left.isOperation(Operation.CBRT)) { // not +, -, *, /, ^,
					// e^x

					// we might need more brackets here #4764
					sb.append(leftStr);
				} else {
					appendWithBrackets(sb, leftStr);
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
				boolean addParentheses = (right.isExpressionNode()
						&& ((ExpressionNode) right).getOperation()
								.equals(Operation.POWER));

				sb.append('{');
				if (addParentheses) {
					appendWithBrackets(sb, rightStr);
				} else {
					sb.append(rightStr);
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
				appendWithBrackets(sb, rightStr);
				break;

			default:
				if ((isSinglePowerArg(right) && !isFraction(right))
						|| ((ExpressionNode
						.opID(right) > Operation.POWER.ordinal())
						&& (ExpressionNode.opID(right) != Operation.EXP
								.ordinal()))) {
					// not +, -, *, /, ^, e^x
					try {
						// display integer powers as unicode superscript
						int i = Integer.parseInt(rightStr);
						StringUtil.numberToIndex(i, sb);
					} catch (RuntimeException e) {
						sb.append('^');
						sb.append(rightStr);
					}

				} else {
					sb.append('^');
					appendWithBrackets(sb, rightStr);
				}
			}
			return sb.toString();
		}
	}

	/**
	 * Checks for composite expressions and numbers like -5, 2*10^5
	 * @param val expression value
	 * @return whether val can be used as argument for power/factorial without brackets
	 */
	public boolean isSinglePowerArg(ExpressionValue val) {
		return val instanceof MySpecialDouble
				? !((MySpecialDouble) val).isScientificNotation() : val.isLeaf();
	}

	private boolean isTrigFunction(ExpressionNode expr) {
		switch (expr.getOperation()) {
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
			return true;
		default:
			return false;
		}
	}

	/**
	 * Append expression to builder, add brackets.
	 *
	 * @param sb
	 *            builder
	 * @param leftStr
	 *            serialized expression
	 */
	public void appendWithBrackets(StringBuilder sb, String leftStr) {
		sb.append(leftBracket());
		sb.append(leftStr);
		sb.append(rightBracket());
	}

	/**
	 * Converts 5.1E-20 to 5.1*10^(-20) or 5.1 \cdot 10^{-20} depending on
	 * current print form
	 *
	 * @param scientificStr
	 *            string in scientific notation
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
		return sb.toString();
	}

	/**
	 * Convert 3E3 to 3000 convert 3.33 to 333/100 convert 3E-3 to 3/1000.
	 *
	 * @param originalString
	 *            raw number string
	 * @return decimal fraction
	 */
	public String convertScientificNotationGiac(String originalString) {

		if (isNumeric()) {
			return originalString.replace('E', 'e');
		}

		if (originalString.contains("E-")) {

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

		} else if (originalString.contains("E")) {
			String[] s = originalString.split("E");

			int i = Integer.parseInt(s[1]);

			int dotIndex = s[0].indexOf('.');

			if (dotIndex > -1) {
				// eg 2.22E100 need i=98
				i -= s[0].length() - dotIndex - 1;
				s[0] = s[0].replace(".", "");
			}
			// c: -5116.91572736879x^2 - 15556.1551078899x y -
			// 11496.6010564053y^2
			// - 2234610.47543873x - 3369532.76964123y = 243297252.338397
			// d: -19182.5685338018x^2 - 7781.50649444574x y -
			// 639.272043575625y^2
			// - 5784784.13901330x - 1154843.72376044y = 435372862.870553
			// Intersect[c, d]
			// eg 4.35372862870553E8
			// need to add decimal point back in
			if (i < 0) {
				return "(" + s[0] + "/1" + StringUtil.repeat('0', -i) + ")";
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
						+ originalString
								.substring(0, originalString.length() - 1)
								.replace(".", "")
						+ "/1" + StringUtil.repeat('0',
								originalString.length() - dotIndex)
						+ ")";
			}

			// eg 2.22 -> (222/100) or 02.22 -> (222/100)
			return "("
					+ (originalString.replace(".", "")).replaceFirst("^0+(?!$)",
							"")
					+ "/1" + StringUtil.repeat('0',
							originalString.length() - dotIndex - 1)
					+ ")";
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

	/**
	 * @return whether to print decimals equal to 3.14... as pi
	 */
	public boolean allowPiHack() {
		return this.allowPiHack;
	}

	/**
	 * Split eg 3.1E10 to 3.1 and 10; keep small numbers.
	 *
	 * @param decimal
	 *            number
	 * @param kernel
	 *            kernel
	 * @param parts
	 *            splits of the number
	 * @return (coefficient, exponent) or (number, null)
	 */
	public static String[] printLimitedWidth(double decimal, Kernel kernel,
			String[] parts) {
		if (Math.abs(decimal) < 1E4
				&& (Math.abs(decimal) > 1E-4 || DoubleUtil.isZero(decimal))) {
			parts[0] = kernel.format(decimal, defaultTemplate);
			parts[1] = null;
			return parts;
		}
		StringTemplate stl = StringTemplate.printScientific(StringType.GEOGEBRA,
				2, false);

		// returns string like 3456E-7
		String str = kernel.format(decimal, stl);

		return str.split("E");
	}

	/**
	 * Overridden in subtypes; by default does nothing.
	 *
	 * @param string
	 *            string
	 * @return escaped string
	 */
	public String escapeString(String string) {
		return string;
	}

	/**
	 * @return whether to print NaN as ?
	 */
	public boolean hasQuestionMarkForNaN() {
		return this.questionMarkForNaN;
	}

	/**
	 * Append left curly bracket to the builder
	 *
	 * @param sb
	 *            builder
	 */
	public void leftCurlyBracket(StringBuilder sb) {
		if (hasType(StringType.LATEX)) {
			sb.append("\\left\\{");
		} else if (hasType(StringType.SCREEN_READER)) {
			sb.append(ScreenReader.getOpenBrace());
		} else {
			sb.append("{");
		}
	}

	/**
	 * Append right curly bracket to the builder
	 *
	 * @param sb
	 *            builder
	 */
	public void rightCurlyBracket(StringBuilder sb) {
		if (hasType(StringType.LATEX)) {
			sb.append("\\right\\}");
		} else if (hasType(StringType.SCREEN_READER)) {
			sb.append(ScreenReader.getCloseBrace());
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

	/**
	 * @return whether symbolic fractions should be printed as fractions
	 */
	public boolean supportsFractions() {
		return supportsFractions;
	}

	/**
	 * @param fractions
	 *            whether to support fractions
	 * @return copy of this template with given fraction support
	 */
	public StringTemplate deriveWithFractions(boolean fractions) {
		if (supportsFractions == fractions) {
			return this;
		}

		StringTemplate ret = this.copy();
		ret.supportsFractions = fractions;
		return ret;
	}

	/**
	 * @param questionMark whether to use "?" for Double.NaN
	 * @return copy of this template with adjusted question mark flag
	 */
	public StringTemplate deriveWithQuestionmark(boolean questionMark) {
		if (questionMarkForNaN == questionMark) {
			return this;
		}

		StringTemplate ret = this.copy();
		ret.questionMarkForNaN = questionMark;
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
				return wrapInTexttt(s + wrapInPhantom(
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
			return wrapInTexttt(s + wrapInPhantom(
					StringUtil.string("0", zerosToAdd), suffix));
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

	/**
	 * @return whether to use English quotes instead of "
	 */
	public boolean niceQuotes() {
		return niceQuotes;
	}

	/**
	 * @return " or English opening quote
	 */
	public char getOpenQuote() {
		if (niceQuotes || stringType.equals(StringType.LATEX)) {
			return Unicode.OPEN_DOUBLE_QUOTE;
		}
		return '\"';
	}

	/**
	 * @return " or English closing quote
	 */
	public char getCloseQuote() {
		if (niceQuotes || stringType.equals(StringType.LATEX)) {
			return Unicode.CLOSE_DOUBLE_QUOTE;
		}
		return '\"';
	}

	/**
	 *
	 * @return ")" or, for XML, "]"
	 */
	public String rightCommandBracket() {
		return isPrintLocalizedCommandNames() || shouldPrintMethodsWithParenthesis
				? rightBracket()
				: rightSquareBracket();
	}

	/**
	 *
	 * @return "(" or, for XML, "["
	 */
	public String leftCommandBracket() {
		return isPrintLocalizedCommandNames() || shouldPrintMethodsWithParenthesis
				? leftBracket()
				: leftSquareBracket();
	}

	/**
	 * @return ^2 for this string type
	 */
	public String squared() {
		switch (getStringType()) {
		case LATEX:
			return "^{2}";

		case GIAC:
			return "^2";

		default:
			return "\u00b2";
		}
	}

	/**
	 * @return true TODO remove this?
	 */
	public boolean degreeMode() {
		return true;
	}

	/**
	 *
	 * @param kernel
	 *            kernel
	 * @return "asin" or "asind" as appropriate
	 */
	public String asind(Kernel kernel) {
		if (changeArcTrig && kernel.degreesMode()) {
			return "asin";
		}
		return "asind";
	}

	/**
	 *
	 * @param kernel
	 *            kernel
	 * @return "acos" or "acosd" as appropriate
	 */
	public String acosd(Kernel kernel) {
		if (changeArcTrig && kernel.degreesMode()) {
			return "acos";
		}
		return "acosd";
	}

	/**
	 *
	 * @param kernel
	 *            kernel
	 * @return "atan" or "atand" as appropriate
	 */
	public String atand(Kernel kernel) {
		if (changeArcTrig && kernel.degreesMode()) {
			return "atan";
		}
		return "atand";
	}

	/**
	 * Turns on or off command name localization
	 * @param localizeCmds If false, the command names won't be localized,
	 *                        otherwise they will be localized
	 */
	public void setLocalizeCmds(boolean localizeCmds) {
		this.localizeCmds = localizeCmds;
	}

	/**
	 * @return degree symbol
	 */
	public String getDegree() {
		switch (stringType) {
		case GIAC:
			return "pi/180";
		case LATEX:
			return "^{\\circ}";
		case SCREEN_READER:
			return "degree";
		}
		return Unicode.DEGREE_STRING;
	}

	/**
	 * @return name for euler-mascheroni constant
	 */
	public String getEulerGamma() {
		switch (stringType) {
		case GIAC:
			return "euler\\_gamma";
		case LATEX:
			return "\\mathit{e_{\\gamma}}";
		case SCREEN_READER:
			return "euler gamme";
		}
		return Unicode.EULER_GAMMA_STRING;
	}

	/**
	 * @return name for euler number
	 */
	public String getEulerNumber() {
		switch (stringType) {
		case GIAC:
			return "e";
		case LATEX:
			return "\\textit{e}";
		case SCREEN_READER:
			return "euler number";
		}
		return Unicode.EULER_STRING;
	}

	/**
	 * Sets whether the parameters of the methods should be printed in parenthesis
	 * (rather than square brackets).
	 *
	 * @param shouldPrintMethodsWithParenthesis If true, the parameters of the methods
	 *                                          will be printed inside parenthesis,
	 *                                          otherwise these might be printed
	 *                                          inside square brackets
	 *                                          (if the localization of the commands is turned off).
	 */
	public void setPrintMethodsWithParenthesis(boolean shouldPrintMethodsWithParenthesis) {
		this.shouldPrintMethodsWithParenthesis = shouldPrintMethodsWithParenthesis;
	}

	/**
	 * @return equal sign with appropriate whitespace symbols
	 */
	public String getEqualsWithSpace() {
		if (forEditorParser) {
			return "=";
		}
		switch (stringType) {
		case LATEX:
			return "\\, = \\,";
		case SCREEN_READER:
			return " equals ";
		default:
			return " = ";
		}
	}

	public boolean isLatex() {
		return stringType.equals(StringType.LATEX);
	}

	public boolean isRad(ExpressionValue value) {
		return value.toString(this).equals(RAD);
	}

	/**
	 * Appends brackets to log argument if necessary
	 *
	 * @param sb
	 *            builder
	 * @param str
	 *            serialized expression
	 * @param left
	 *            left subtree
	 */
	public void addLogBracketsIfNecessary(StringBuilder sb, String str, ExpressionValue left) {
		if ((forEditorParser || stringType == StringType.LATEX)
				&& left.isOperation(Operation.ABS)) {
			sb.append(str);
		} else {
			appendWithBrackets(sb, str);
		}
	}

	public boolean allowShortLhs() {
		return allowShortLhs;
	}

	/**
	 * Appends localized comma to a StringBuilder
	 * @param sb builder
	 * @param localization localization
	 */
	public void getComma(StringBuilder sb, Localization localization) {
		if (hasType(StringType.SCREEN_READER)) {
			sb.append(ScreenReader.getComma());
		} else {
			sb.append(localization.getComma());
		}
	}
}
