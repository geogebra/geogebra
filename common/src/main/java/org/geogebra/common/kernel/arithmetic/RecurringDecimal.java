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

	private final String representation;

	/**
	 * @param kernel Kernel
	 * @param val Value
	 * @param representation Representation of the recurring decimal
	 */
	public RecurringDecimal(Kernel kernel, double val, String representation) {
		super(kernel, val);
		this.representation = representation;
	}

	/**
	 * Copy constructor
	 * @param rd RecurringDecimal
	 */
	public RecurringDecimal(RecurringDecimal rd) {
		super(rd);
		this.representation = rd.representation;
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (tpl.isLatex()) {
			return convertToLaTeX(this.representation);
		}
		return this.representation;
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
					&& ((RecurringDecimal) o).representation.equals(this.representation);
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
}
