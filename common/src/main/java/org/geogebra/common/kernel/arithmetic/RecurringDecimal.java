package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class for recurring decimals e.g. 1.23\u03054\u0305
 */
public class RecurringDecimal extends MyDouble {

	private RecurringDecimalProperties properties;


	/**
	 * @param kernel Kernel
	 * @param properties of the recurring decimal
	 */
	public RecurringDecimal(Kernel kernel, RecurringDecimalProperties properties) {
		super(kernel, toDouble(properties));
		this.properties = properties;
	}

	private static double toDouble(RecurringDecimalProperties properties) {
		double value = nominator(properties) / (denominator(properties) + 0.0);
		return properties.isPercent() ? 0.01 * value : value;
	}

	public double toDouble() {
		return toDouble(properties);
	}

	private static int denominator(RecurringDecimalProperties properties) {
		return denominator(properties.recurringLength,
				properties.nonRecurringLength);
	}

	private static int nominator(RecurringDecimalProperties properties) {
		return nominator(properties.integerPart, properties.nonRecurringPart,
				properties.recurringPart);
	}


	/**
	 * Copy constructor
	 * @param rd RecurringDecimal
	 */
	public RecurringDecimal(RecurringDecimal rd) {
		super(rd);
		this.properties = rd.properties;
	}

	public static String toFraction(ExpressionNode expression, Kernel kernel, StringTemplate tpl) {
		return Fractions.getResolution(expression, kernel, false).toValueString(tpl);
	}


	public static void asFraction(ExpressionValue[] parts, ExpressionNode expr) {
		Kernel kernel = expr.getKernel();
		RecurringDecimal rd = (RecurringDecimal) expr.unwrap();
		parts[0] = new MyDouble(kernel, nominator(rd.properties));
		parts[1] = new MyDouble(kernel, denominator(rd.properties));
	}

	@Override
	public MyDouble getNumber() {
		return new RecurringDecimal(this);
	}

	@Override
	public RecurringDecimal deepCopy(Kernel kernel) {
		RecurringDecimal ret = new RecurringDecimal(this);
		ret.kernel = kernel;
		return ret;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RecurringDecimal) {
			return super.equals(o)
					&& ((RecurringDecimal) o).properties.equals(this.properties);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Removes all unicode overlines from a given string and returns its appropriate LaTeX notation
	 * @param str String
	 * @return Converted String
	 */
	private String convertToLaTeX(String str) {
		StringBuilder sb = new StringBuilder(str);
		int indexLastOverLine = sb.lastIndexOf("\u0305");
		int indexFirstOverLine = sb.indexOf("\u0305");

		sb.replace(indexLastOverLine, indexLastOverLine + 1, "}");
		sb.insert(indexFirstOverLine - 1, "\\overline{");

		return sb.toString().replaceAll("\u0305", "");
	}

	/**
	 * extension of StringUtil.parseDouble() to cope with unicode digits e.g. Arabic <br>
	 * Enables parsing of recurring decimals
	 * @param str string to be parsed
	 * @param app application for showing errors
	 * @return value
	 */
	public static RecurringDecimal parse(Kernel kernel, Localization loc, String str, boolean percent) {
		return new RecurringDecimal(kernel, parseProperties(loc, str, percent));
	}

	private static RecurringDecimalProperties parseProperties(Localization loc, String str,
			boolean percent) {
		StringBuilder sb = serializeDigits(str, true);
		return RecurringDecimalProperties.parse(str, percent);
	}
	public static double parseDouble(Localization app, String str) {
		StringBuilder sb = serializeDigits(str, true);
		try {
			return parseRecurringDecimal(sb);
		} catch (NumberFormatException e) {
			// eg try to parse "1.2.3", "1..2"
			throw new MyError(app, MyError.Errors.InvalidInput, str);
		}
	}

	/**
	 * @param sb String to be parsed
	 * @return Value of the recurring decimal as a fraction e.g. 1.3\u0305 -> 12/9 = 4/3
	 * @throws NumberFormatException When trying to parse an invalid double e.g. 1.3.2\u0305
	 */
	private static double parseRecurringDecimal(StringBuilder sb) throws NumberFormatException {
		int repeatingDigits = (int) sb.chars().filter(ch -> ch == Unicode.OVERLINE).count();
		int nonRepeatingDigits = sb.substring(sb.indexOf("."), sb.indexOf("\u0305")).length() - 2;

		// Might throw a NumberFormatException (e.g. 1.2.3\u0305)
		double decimalValue = StringUtil.parseDouble(sb.toString().replaceAll("\u0305", ""));

		if (nonRepeatingDigits == 0) {
			return (decimalValue * Math.pow(10, repeatingDigits) - (int) decimalValue)
					/ (Math.pow(10, repeatingDigits) - 1);
		}

		int scaledNonRepeatingPart = (int) (decimalValue * Math.pow(10, nonRepeatingDigits));
		double scaledValue = decimalValue * Math.pow(10, repeatingDigits + nonRepeatingDigits);

		return (scaledValue - scaledNonRepeatingPart)
				/ (Math.pow(10, repeatingDigits + nonRepeatingDigits)
				- Math.pow(10, nonRepeatingDigits));
	}


	@Override
	public boolean isRecurringDecimal() {
		return true;
	}

	public String toFractionSting() {
		return nominator(properties)
				+ "/"
				+ denominator(properties);
	}

	static int nominator(int i, Integer a, int p) {
		double pL = (int) Math.log10(p) + 1;
		double aL = a != null && a == 0 ? 1
				: a != null ? (int) Math.log(a) - 1 : 0;
		int A = a != null ? a: 0;
		int iap = (int) (p + A * Math.pow(10, pL) + i * Math.pow(10, pL + aL));
		int ia = (int) (A + i * Math.pow(10, aL));
		return iap - ia;
	}

	static int denominator(int nines, int zeros) {
		int nins = nines == 0 ? 1 : (int) (Math.pow(10, nines) - 1);
		int tens = zeros == 0 ? 1 : (int) (Math.pow(10, zeros));
		return nins * tens;
	}
}
