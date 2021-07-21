package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steps.RegroupTracker;
import org.geogebra.common.kernel.stepbystep.steps.SimplificationStepGenerator;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class StepArbitraryInteger extends StepExpression {

	private final String label;
	private final int index;

	public StepArbitraryInteger(String label, int index) {
		this.label = label;
		this.index = index;
	}

	public String getLabel() {
		return label;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + label.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepArbitraryInteger) {
			return ((StepArbitraryInteger) obj).label.equals(label)
					&& ((StepArbitraryInteger) obj).index == index;
		}

		return false;
	}

	@Override
	public StepArbitraryInteger deepCopy() {
		StepArbitraryInteger sac = new StepArbitraryInteger(label, index);
		sac.setColor(color);
		return sac;
	}

	@Override
	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public boolean isOperation(Operation operation) {
		return false;
	}

	@Override
	public boolean nonSpecialConstant() {
		return false;
	}

	@Override
	public boolean specialConstant() {
		return false;
	}

	@Override
	public boolean isInteger() {
		return false;
	}

	@Override
	public boolean proveInteger() {
		return true;
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

	@Override
	public StepTransformable iterateThrough(SimplificationStepGenerator step, SolutionBuilder sb,
			RegroupTracker tracker) {
		return this;
	}
}
