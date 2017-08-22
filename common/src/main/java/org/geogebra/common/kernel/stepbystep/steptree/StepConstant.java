package org.geogebra.common.kernel.stepbystep.steptree;

import java.text.DecimalFormat;

import org.geogebra.common.plugin.Operation;

public class StepConstant extends StepNode {
	private double value;

	public StepConstant(double value) {
		this.value = value;
	}
	@Override
	public boolean equals(StepNode sn) {
		return sn instanceof StepConstant && Math.abs(sn.getValue() - value) < 0.00000001;
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
	public StepNode getCoefficient() {
		return this;
	}

	@Override
	public StepNode getVariable() {
		return null;
	}

	@Override
	public String toString() {
		if (StepOperation.isEqual(value, Math.PI)) {
			return "pi";
		} else if (StepOperation.isEqual(value, Math.E)) {
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
	public String toLaTeXString() {
		if (StepOperation.isEqual(value, Math.PI)) {
			return "\\pi";
		} else if (StepOperation.isEqual(value, Math.E)) {
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

	@Override
	public StepNode deepCopy() {
		return new StepConstant(value);
	}

	@Override
	public StepNode regroup() {
		return this;
	}

	@Override
	public StepNode expand() {
		return this;
	}

	@Override
	public StepNode simplify() {
		return this;
	}

	@Override
	public StepConstant getConstantCoefficient() {
		if (nonSpecialConstant()) {
			return this;
		}
		return new StepConstant(1);
	}

	@Override
	public StepNode divideAndSimplify(double x) {
		return new StepConstant(value / x);
	}

	@Override
	public boolean canBeEvaluated() {
		return true;
	}
}
