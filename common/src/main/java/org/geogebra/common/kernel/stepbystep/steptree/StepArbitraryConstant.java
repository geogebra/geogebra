package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.plugin.Operation;

public class StepArbitraryConstant extends StepNode {

	public enum ConstantType {
		INTEGER, REAL
	}

	private String label;
	private int index;
	private ConstantType type;

	public StepArbitraryConstant(String label, int index, ConstantType type) {
		this.label = label;
		this.index = index;
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public int getIndex() {
		return index;
	}

	public ConstantType getType() {
		return type;
	}

	@Override
	public boolean equals(StepNode sn) {
		if (sn instanceof StepArbitraryConstant) {
			return ((StepArbitraryConstant) sn).label.equals(label) && ((StepArbitraryConstant) sn).index == index;
		}
		return false;
	}

	@Override
	public StepNode deepCopy() {
		return new StepArbitraryConstant(label, index, type);
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
		return Double.NaN;
	}

	@Override
	public double getValueAt(StepNode variable, double value) {
		return Double.NaN;
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
	public StepNode getConstantCoefficient() {
		return new StepConstant(1);
	}

	@Override
	public String toString() {
		return "[" + label + index + "]";
	}

	@Override
	public String toLaTeXString() {
		return label + (index != 0 ? "_{" + index + "}" : "");
	}

	@Override
	public StepNode regroup() {
		return this;
	}

	@Override
	public StepNode expand(Boolean[] changed) {
		return this;
	}

	@Override
	public StepNode simplify() {
		return this;
	}

	@Override
	public StepNode divideAndSimplify(double x) {
		return this;
	}

	@Override
	public boolean canBeEvaluated() {
		return false;
	}
}
