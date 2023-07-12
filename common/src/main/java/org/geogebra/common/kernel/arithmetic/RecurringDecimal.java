package org.geogebra.common.kernel.arithmetic;

import java.util.Objects;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;

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
		super(kernel, properties.toDouble());
		this.properties = properties;
	}

	public RecurringDecimal(Kernel kernel, int integerPart, Integer nonRecurringPart,
			int recurringPart) {
		this(kernel, new RecurringDecimalProperties(integerPart, nonRecurringPart, recurringPart));
	}

	public double toDouble() {
		return properties.toDouble();
	}

	/**
	 * Copy constructor
	 * @param rd RecurringDecimal
	 */
	public RecurringDecimal(RecurringDecimal rd) {
		super(rd);
		this.properties = rd.properties;
	}

	/**
	 * @param tpl {@link StringTemplate}
	 * @return the latex string of fraction.
	 */
	public String toFraction(StringTemplate tpl) {
		return toFraction(wrap(), tpl);
	}

	/**
	 * @param expression of the recurring decimal.
	 * @param tpl {@link StringTemplate}
	 * @return the latex string of fraction.
	 */
	public static String toFraction(ExpressionNode expression, StringTemplate tpl) {
		return Fractions.getResolution(expression, expression.getKernel(),
				false).toValueString(tpl);
	}

	/**
	 *
	 * @param parts for the result
	 * @param expr to get as a fractiom.
	 */
	public static void asFraction(ExpressionValue[] parts, ExpressionNode expr) {
		Kernel kernel = expr.getKernel();
		RecurringDecimal rd = (RecurringDecimal) expr.unwrap();
		parts[0] = new MyDouble(kernel, rd.properties.numerator());
		parts[1] = new MyDouble(kernel, rd.properties.denominator());
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
		return Objects.hash(super.hashCode(), properties);
	}

	/**
	 * Parses RecuringDecimal from string.
	 *
	 * @param kernel {@link Kernel}
	 * @param loc {@link Localization}
	 * @param str the string to parse.
	 * @param percent if it is meant to be in percent.
	 * @return the new, RecurringDecimal instance.
	 */
	public static RecurringDecimal parse(Kernel kernel, Localization loc, String str,
			boolean percent) {
		return new RecurringDecimal(kernel, parseProperties(loc, str, percent));
	}

	private static RecurringDecimalProperties parseProperties(Localization loc, String str,
			boolean percent) {
		StringBuilder sb = serializeDigits(str, true);
		try {
			return RecurringDecimalProperties.parse(sb.toString(), percent);
		} catch (NumberFormatException e) {
			throw new MyError(loc, MyError.Errors.InvalidInput, str);
		}
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (tpl.hasType(StringType.GIAC)) {
			return toFraction(tpl);
		}
		return properties.toString(tpl);
	}

	@Override
	public boolean isRecurringDecimal() {
		return true;
	}

}