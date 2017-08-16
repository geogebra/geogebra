package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.plugin.Operation;

public class StepVariable extends StepNode {
	private String label;

	public StepVariable(String label) {
		this.label = label;
	}

	@Override
	public boolean equals(StepNode sn) {
		return sn.toString().equals(label);
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
		return false;
	}

	@Override
	public int getPriority() {
		return 5;
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
	public StepConstant getConstantCoefficient() {
		return new StepConstant(1);
	}

	@Override
	public StepNode constantRegroup() {
		return this;
	}

	@Override
	public StepNode getCoefficient() {
		return null;
	}

	@Override
	public StepNode getVariable() {
		return this;
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public String toLaTeXString() {
		return label;
	}

	@Override
	public StepNode deepCopy() {
		return new StepVariable(label);
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
	public StepNode divideAndSimplify(double x) {
		return null;
	}

	@Override
	public int compareTo(StepNode sn) {
		if (sn instanceof StepVariable) {
			return label.compareTo(sn.toString());
		} else if (sn instanceof StepConstant) {
			return -1;
		} else {
			return 1;
		}
	}

}
