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

	private RecurringDecimalModel model;

	/**
	 * @param kernel Kernel
	 * @param model of the recurring decimal
	 */
	public RecurringDecimal(Kernel kernel, RecurringDecimalModel model) {
		super(kernel, model.toDouble());
		this.model = model;
		setImprecise(true);
	}

	public double toDouble() {
		return model.toDouble();
	}

	/**
	 * Copy constructor
	 * @param rd RecurringDecimal
	 */
	public RecurringDecimal(RecurringDecimal rd) {
		super(rd);
		this.model = rd.model;
		this.setImprecise(true);
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
		parts[0] = new MyDouble(kernel, rd.model.numerator());
		parts[1] = new MyDouble(kernel, rd.model.denominator());
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
					&& ((RecurringDecimal) o).model.equals(this.model);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), model);
	}

	/**
	 * Parses RecurringDecimal from string.
	 *
	 * @param kernel {@link Kernel}
	 * @param preperiod the preperiod part of the number (integer.nonrecurring)
	 * @param recurring the recurring digits
	 * @return the new, RecurringDecimal instance.
	 */
	public static RecurringDecimal parse(Kernel kernel, String preperiod,
			String recurring) {
		return new RecurringDecimal(kernel, parseProperties(kernel.getLocalization(),
				preperiod, recurring));
	}

	private static RecurringDecimalModel parseProperties(Localization loc, String preperiodUtf,
			String recurringUtf) {
		String preperiod = convertToLatinCharacters(preperiodUtf);
		String recurring = convertToLatinCharacters(recurringUtf);
		try {
			return RecurringDecimalModel.parse(preperiod, recurring);
		} catch (NumberFormatException e) {
			throw new MyError(loc, MyError.Errors.InvalidInput, preperiodUtf + recurringUtf);
		}
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (tpl.hasType(StringType.GIAC)) {
			return toFraction(tpl);
		}
		return model.toString(tpl);
	}

	@Override
	public boolean isRecurringDecimal() {
		return true;
	}

}