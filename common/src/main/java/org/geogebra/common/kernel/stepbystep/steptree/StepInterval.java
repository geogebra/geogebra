package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.Localization;

public class StepInterval extends StepNode {

	private StepExpression leftBound;
	private StepExpression rightBound;
	private boolean leftClosed;
	private boolean rightClosed;

	public final static StepInterval R = new StepInterval(StepConstant.NEG_INF, StepConstant.POS_INF,
			false, false);

	public StepInterval(StepExpression leftBound, StepExpression rightBound, boolean leftClosed, boolean rightClosed) {
		this.leftBound = leftBound;
		this.rightBound = rightBound;
		this.leftClosed = leftClosed;
		this.rightClosed = rightClosed;
	}

	public StepExpression getLeftBound() {
		return leftBound;
	}

	public StepExpression getRightBound() {
		return rightBound;
	}

	public boolean isClosedLeft() {
		return leftClosed;
	}

	public boolean isClosedRight() {
		return rightClosed;
	}

	public boolean contains(StepExpression sn) {
		if (sn.canBeEvaluated()) {
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

		return equals(R);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((leftBound == null) ? 0 : leftBound.hashCode());
		result = prime * result + (leftClosed ? 1231 : 1237);
		result = prime * result + ((rightBound == null) ? 0 : rightBound.hashCode());
		result = prime * result + (rightClosed ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepInterval) {
			StepInterval si = (StepInterval) obj;
			return si.leftClosed == leftClosed && si.rightClosed == rightClosed && si.leftBound.equals(leftBound)
					&& si.rightBound.equals(rightBound);
		}
		return false;
	}

	@Override
	public StepInterval deepCopy() {
		StepInterval si = new StepInterval(leftBound, rightBound, leftClosed, rightClosed);
		si.setColor(color);
		return si;
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
		sb.append(", ");
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
		sb.append(loc.intervalStartBracket(leftClosed, StringTemplate.latexTemplate));
		sb.append(leftBound.toLaTeXString(loc, colored));
		sb.append(",");
		sb.append(rightBound.toLaTeXString(loc, colored));
		sb.append(loc.intervalEndBracket(rightClosed, StringTemplate.latexTemplate));
		return sb.toString();
	}

	@Override
	public String toLaTeXString(Localization loc) {
		return toLaTeXString(loc, false);
	}
}
