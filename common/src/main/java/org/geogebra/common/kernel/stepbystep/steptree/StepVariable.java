package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class StepVariable extends StepNode {
	private String label;

	public StepVariable(String label) {
		this.label = label;
	}

	@Override
	public boolean equals(StepNode sn) {
		return sn != null && sn.toString().equals(label);
	}

	@Override
	public StepNode deepCopy() {
		StepVariable sv = new StepVariable(label);
		sv.setColor(color);
		return sv;
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
	public boolean canBeEvaluated() {
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
	public double getValueAt(StepNode variable, double replaceWith) {
		if (equals(variable)) {
			return replaceWith;
		}
		return Double.NaN;
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
		return null;
	}

	@Override
	public StepNode getVariable() {
		return this;
	}

	@Override
	public StepConstant getIntegerCoefficient() {
		return null;
	}

	@Override
	public StepNode getNonInteger() {
		return this;
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public String toLaTeXString(Localization loc) {
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
