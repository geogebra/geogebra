package org.geogebra.common.euclidian.draw;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.PathPlotter;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.shape.Point;

class DrawConditionalFunction {
	private final EuclidianView view;
	private final PathPlotter gp;

	public DrawConditionalFunction(EuclidianView view, PathPlotter gp) {
		this.view = view;
		this.gp = gp;
	}

	boolean draw(GeoFunction f, double min, double max,
			boolean labelVisible, boolean fillCurve) {
		if (!f.isGeoFunctionConditional()) {
			return false;
		}

		ExpressionNode expression = f.getFunctionExpression();
		Operation operation = expression.getOperation();
		if (operation == Operation.IF_ELSE) {
			plotIfElse(f, min, max, labelVisible, fillCurve, expression);
			return true;
		}

		if (operation == Operation.IF_LIST) {
			plotIfList(f, min, max, labelVisible, fillCurve, expression);
			return true;
		}
		return false;
	}

	private void plotIfList(GeoFunction f, double min, double max, boolean labelVisible,
			boolean fillCurve, ExpressionNode expression) {
		double min1=0;
		double max1=0;
		MyList conditions = (MyList) expression.getLeft();
		for (int i = 0; i < conditions.size(); i++) {
			Point limits = evalConditional(conditions.getItem(i).wrap());
			min1 = Double.isNaN(limits.getX()) ? min : limits.getX();
			max1 = Double.isNaN(limits.getY()) ? max : limits.getY();

			CurvePlotter.plotCurve(f, min1, max1, view, gp,
					labelVisible, fillCurve ? Gap.CORNER
							: Gap.MOVE_TO);

		}
	}

	private void plotIfElse(GeoFunction f, double min, double max, boolean labelVisible,
			boolean fillCurve, ExpressionNode expression) {
		MyNumberPair pair = (MyNumberPair) expression.getLeft();
		ExpressionNode conditional = pair.getX().wrap();
		double tMax = conditional.evaluateDouble();
		CurvePlotter.plotCurve(f, min, tMax, view, gp,
				labelVisible, fillCurve ? Gap.CORNER
						: Gap.MOVE_TO);
		CurvePlotter.plotCurve(f, tMax, max, view, gp,
				labelVisible, fillCurve ? Gap.CORNER
						: Gap.MOVE_TO);
	}

	private Point evalConditional(ExpressionNode conditon) {
		Operation operation = conditon.getOperation();
		Log.debug(operation.toString());
		if (Operation.GREATER.equals(operation) || Operation.GREATER_EQUAL.equals(operation)) {
			return new Point(Double.NaN, conditon.evaluateDouble());
		}

		if (Operation.LESS.equals(operation) || Operation.LESS_EQUAL.equals(operation)) {
			return new Point(conditon.evaluateDouble(), Double.NaN);
		}

		if (Operation.AND_INTERVAL.equals(operation)) {
			return new Point(conditon.getLeftTree().getLeft().evaluateDouble(),
					conditon.getRightTree().getRight().evaluateDouble());
		}

		// EQUAL
		return new Point(conditon.getRight().evaluateDouble(), conditon.getRight().evaluateDouble());
	}

}
