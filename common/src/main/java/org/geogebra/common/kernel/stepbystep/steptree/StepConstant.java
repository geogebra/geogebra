package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.plugin.Operation;

public class StepConstant extends StepNode {
	double value;

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
		return 4;
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
	public StepNode getCoefficient() {
		return this;
	}

	@Override
	public StepNode getVariable() {
		return new StepConstant(1);
	}

	@Override
	public String toString() {
		return Double.toString(value);
	}

	@Override
	public String toLaTeXString() {
		return Double.toString(value);
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
		return this;
	}

	@Override
	public StepNode constantRegroup() {
		return this;
	}

	@Override
	public StepNode divideAndSimplify(double x) {
		return new StepConstant(value / x);
	}

	@Override
	public int compareTo(StepNode sn) {
		if (sn instanceof StepVariable) {
			return Double.compare(value, sn.getValue());
		}
		return 1;
	}
}
