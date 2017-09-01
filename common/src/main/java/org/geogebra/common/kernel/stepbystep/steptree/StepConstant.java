package org.geogebra.common.kernel.stepbystep.steptree;

import java.text.DecimalFormat;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class StepConstant extends StepNode {
	private double value;

	public StepConstant(double value) {
		this.value = value;
	}

	@Override
	public boolean equals(StepNode sn) {
		return sn instanceof StepConstant && isEqual(sn.getValue(), value);
	}

	@Override
	public StepNode deepCopy() {
		StepConstant sc = new StepConstant(value);
		sc.setColor(color);
		return sc;
	}

	@Override
	public boolean isOperation() {
		return false;
	}

	@Override
	public boolean isOperation(Operation op) {
		return false;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public boolean canBeEvaluated() {
		return true;
	}

	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public double getValueAt(StepNode variable, double replaceWith) {
		return value;
	}

	@Override
	public StepNode regroup(SolutionBuilder sb) {
		return this;
	}

	@Override
	public StepNode regroup() {
		return this;
	}

	@Override
	public StepNode expand(SolutionBuilder sb) {
		return this;
	}

	@Override
	public StepNode getCoefficient() {
		return this;
	}

	@Override
	public StepNode getVariable() {
		return null;
	}

	@Override
	public StepConstant getIntegerCoefficient() {
		if (nonSpecialConstant()) {
			return this;
		}
		return null;
	}

	@Override
	public StepNode getNonInteger() {
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
	public String toLaTeXString(Localization loc) {
		return toLaTeXString(loc, false);
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		if (colored && color != 0) {
			return "\\fgcolor{" + getColorHex() + "}{" + toLaTeXString(loc, false) + "}";
		}
		if (isEqual(value, Math.PI)) {
			return "\\pi";
		} else if (isEqual(value, Math.E)) {
			return "\\e";
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
