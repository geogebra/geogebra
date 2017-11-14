package org.geogebra.common.kernel.stepbystep.steptree;

import java.text.DecimalFormat;

import org.geogebra.common.main.Localization;

public class StepConstant extends StepExpression {
	private double value;

	public static final StepConstant PI = new StepConstant(Math.PI);
	public static final StepConstant E = new StepConstant(Math.E);

	public StepConstant(double value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepExpression) {
			StepExpression se = (StepExpression) obj;
			if (se.nonSpecialConstant()) {
				return isEqual(se.getValue(), value);
			} else if (se instanceof StepConstant) {
				return isEqual(((StepConstant) obj).value, value);
			}
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
		if (isEqual(value, Math.PI)) {
			return "pi";
		} else if (isEqual(value, Math.E)) {
			return "e";
		} else if (Double.isNaN(value)) {
			return "NaN";
		} else if (Double.isInfinite(value)) {
			if (value < 0) {
				return "-inf";
			}
			return "inf";
		}
		return new DecimalFormat("#0.##").format(value);
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		if (colored && color != 0) {
			return "\\fgcolor{" + getColorHex() + "}{" + toLaTeXString(loc, false) + "}";
		}
		if (isEqual(value, Math.PI)) {
			return "\\pi";
		} else if (isEqual(value, Math.E)) {
			return "e";
		} else if (Double.isNaN(value)) {
			return "NaN";
		} else if (Double.isInfinite(value)) {
			if (value < 0) {
				return "-\\infty";
			}
			return "\\infty";
		}
		return new DecimalFormat("#0.##").format(value);
	}
}
