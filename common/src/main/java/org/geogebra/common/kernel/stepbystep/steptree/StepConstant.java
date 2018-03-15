package org.geogebra.common.kernel.stepbystep.steptree;

import java.text.DecimalFormat;

import org.geogebra.common.main.Localization;

public final class StepConstant extends StepExpression {

	private double value;

	public static final StepConstant PI = new StepConstant(Math.PI);
	public static final StepConstant E = new StepConstant(Math.E);

	public static final StepConstant UNDEFINED = new StepConstant(Double.NaN);

	public static final StepExpression NEG_INF = StepConstant.create(Double.NEGATIVE_INFINITY);
	public static final StepExpression POS_INF = StepConstant.create(Double.POSITIVE_INFINITY);

	private StepConstant(double value) {
		this.value = value;
	}

	public static StepExpression create(double value) {
		if (value < 0) {
			return minus(new StepConstant(-value));
		}

		return new StepConstant(value);
	}

	@Override
	public int hashCode() {
		return Double.hashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepConstant) {
			StepConstant se = (StepConstant) obj;

			return Double.isInfinite(se.value) && Double.isInfinite(value) ||
					isEqual(se.value, value);
		}

		return false;
	}

	@Override
	public StepConstant deepCopy() {
		StepConstant sc = new StepConstant(value);
		sc.setColor(color);
		return sc;
	}

	@Override
	public boolean isConstantIn(StepVariable sv) {
		return true;
	}

	@Override
	public int degree(StepVariable var) {
		return 0;
	}

	@Override
	public boolean canBeEvaluated() {
		return true;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public double getValueAt(StepVariable variable, double replaceWith) {
		return value;
	}

	@Override
	public StepExpression getCoefficientIn(StepVariable sv) {
		return this;
	}

	@Override
	public StepExpression getVariableIn(StepVariable sv) {
		return null;
	}

	@Override
	public StepExpression getIntegerCoefficient() {
		if (nonSpecialConstant()) {
			return this;
		}
		return null;
	}

	@Override
	public StepExpression getNonInteger() {
		if (nonSpecialConstant()) {
			return null;
		}
		return this;
	}

	@Override
	public String toString() {
		if (Double.isNaN(value)) {
			return "undefined";
		} else if (isEqual(value, Math.PI)) {
			return "pi";
		} else if (isEqual(value, Math.E)) {
			return "e";
		} else if (Double.isNaN(value)) {
			return "NaN";
		} else if (Double.isInfinite(value)) {
			return "inf";
		}
		return new DecimalFormat("#0.#########").format(value);
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		if (colored && color != 0) {
			return "\\fgcolor{" + getColorHex() + "}{" + toLaTeXString(loc, false) + "}";
		}
		if (Double.isNaN(value)) {
			return "\\text{Undefined}";
		} else if (isEqual(value, Math.PI)) {
			return "\\pi";
		} else if (isEqual(value, Math.E)) {
			return "e";
		} else if (Double.isNaN(value)) {
			return "NaN";
		} else if (Double.isInfinite(value)) {
			return "\\infty";
		}
		return new DecimalFormat("#0.#########").format(value);
	}
}
