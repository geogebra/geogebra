package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.main.Localization;

public class StepArbitraryConstant extends StepExpression {

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + label.hashCode();
		result = prime * result + type.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepArbitraryConstant) {
			return ((StepArbitraryConstant) obj).label.equals(label) &&
					((StepArbitraryConstant) obj).index == index;
		}

		return false;
	}

	@Override
	public StepArbitraryConstant deepCopy() {
		StepArbitraryConstant sac = new StepArbitraryConstant(label, index, type);
		sac.setColor(color);
		return sac;
	}

	@Override
	public boolean isConstantIn(StepVariable sv) {
		return true;
	}

	@Override
	public int degree(StepVariable sv) {
		return 0;
	}

	@Override
	public boolean canBeEvaluated() {
		return false;
	}

	@Override
	public double getValue() {
		return 0;
	}

	@Override
	public double getValueAt(StepVariable variable, double value) {
		return 0;
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
		return null;
	}

	@Override
	public StepExpression getNonInteger() {
		return this;
	}

	@Override
	public String toString() {
		return label + index;
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		if (colored && color != 0) {
			return "\\fgcolor{" + getColorHex() + "}{" + toLaTeXString(loc, false) + "}";
		}
		return label + (index != 0 ? "_{" + index + "}" : "");
	}

	public enum ConstantType {
		INTEGER, REAL
	}
}
