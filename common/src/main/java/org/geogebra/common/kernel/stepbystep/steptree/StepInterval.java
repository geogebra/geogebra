package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class StepInterval extends StepNode {

	private StepNode leftBound;
	private StepNode rightBound;
	private boolean leftClosed;
	private boolean rightClosed;

	public StepInterval(StepNode leftBound, StepNode rightBound, boolean leftClosed, boolean rightClosed) {
		this.leftBound = leftBound;
		this.rightBound = rightBound;
		this.leftClosed = leftClosed;
		this.rightClosed = rightClosed;
	}

	public StepNode getLeftBound() {
		return leftBound;
	}

	public StepNode getRightBound() {
		return rightBound;
	}

	public boolean isClosedLeft() {
		return leftClosed;
	}

	public boolean isClosedRight() {
		return rightClosed;
	}

	public boolean contains(StepNode sn) {
		if (sn.isConstant()) {
			double value = sn.getValue();
			double leftBoundValue = leftBound.getValue();
			double rightBoundValue = rightBound.getValue();

			if (leftClosed && isEqual(leftBoundValue, value)) {
				return true;
			}

			if (rightClosed && isEqual(rightBoundValue, value)) {
				return true;
			}

			return leftBoundValue < value && value < rightBoundValue;
		}

		return false;
	}

	@Override
	public boolean equals(StepNode sn) {
		if (sn instanceof StepInterval) {
			StepInterval si = (StepInterval) sn;
			return si.leftClosed == leftClosed && si.rightClosed == rightClosed && si.leftBound.equals(leftBound)
					&& si.rightBound.equals(rightBound);
		}
		return false;
	}

	@Override
	public StepNode deepCopy() {
		StepInterval si = new StepInterval(leftBound, rightBound, leftClosed, rightClosed);
		si.setColor(color);
		return si;
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
	public double getValueAt(StepNode variable, double value) {
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
	public StepNode getIntegerCoefficient() {
		return null;
	}

	@Override
	public StepNode getNonInteger() {
		return this;
	}

	@Override
	public String toString() {
		if (Double.isInfinite(leftBound.getValue()) && Double.isInfinite(rightBound.getValue())) {
			return "R";
		}
		StringBuilder sb = new StringBuilder();
		if (leftClosed) {
			sb.append("[");
		} else {
			sb.append("(");
		}
		sb.append(leftBound.toString());
		sb.append(",");
		sb.append(rightBound.toString());
		if (rightClosed) {
			sb.append("]");
		} else {
			sb.append(")");
		}
		return sb.toString();
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		if (colored && color != 0) {
			return "\\fgcolor{" + getColorHex() + "}{" + toLaTeXString(loc, false) + "}";
		}

		if (Double.isInfinite(leftBound.getValue()) && Double.isInfinite(rightBound.getValue())) {
			return "\\mathbb{R}";
		}
		StringBuilder sb = new StringBuilder();
		if (leftClosed) {
			sb.append("\\left[");
		} else {
			sb.append("\\left(");
		}
		sb.append(leftBound.toLaTeXString(loc, colored));
		sb.append(",");
		sb.append(rightBound.toLaTeXString(loc, colored));
		if (rightClosed) {
			sb.append("\\right]");
		} else {
			sb.append("\\right)");
		}
		return sb.toString();
	}

	@Override
	public String toLaTeXString(Localization loc) {
		return toLaTeXString(loc, false);
	}
}
