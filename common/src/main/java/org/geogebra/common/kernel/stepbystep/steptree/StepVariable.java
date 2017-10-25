package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.main.Localization;

public class StepVariable extends StepExpression {
	private String label;

	public StepVariable(String label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		return label.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepVariable) {
			return ((StepVariable) obj).label.equals(label);
		}

		return false;
	}

	@Override
	public StepVariable deepCopy() {
		StepVariable sv = new StepVariable(label);
		sv.setColor(color);
		return sv;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean canBeEvaluated() {
		return false;
	}

	@Override
	public double getValue() {
		return Double.NaN;
	}

	@Override
	public double getValueAt(StepVariable variable, double replaceWith) {
		if (equals(variable)) {
			return replaceWith;
		}
		return Double.NaN;
	}

	@Override
	public StepExpression getCoefficient() {
		return null;
	}

	@Override
	public StepExpression getVariable() {
		return this;
	}

	@Override
	public StepConstant getIntegerCoefficient() {
		return null;
	}

	@Override
	public StepExpression getNonInteger() {
		return this;
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		if (colored && color != 0) {
			return "\\fgcolor{" + getColorHex() + "}{" + label + "}";
		}
		return label;
	}
}
