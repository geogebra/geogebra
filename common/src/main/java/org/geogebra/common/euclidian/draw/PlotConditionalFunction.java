package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.PathPlotter;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

class PlotConditionalFunction {
	private final EuclidianView view;
	private final PathPlotter gp;
	private static final List<Operation> supported = Arrays.asList(Operation.IF_ELSE,
			Operation.IF_LIST);
	private ExpressionNode node;
	private Operation operation;
	private GeoFunction geoFunction;
	private double min;
	private double max;
	private boolean labelVisible;
	private boolean fillCurve;

	public PlotConditionalFunction(EuclidianView view, PathPlotter gp) {
		this.view = view;
		this.gp = gp;
	}

	boolean update(GeoFunction f, double min, double max,
			boolean labelVisible, boolean fillCurve) {
		geoFunction = f;
		node = geoFunction.getFunctionExpression();
		operation = node.getOperation();

		if (!isValid()) {
			return false;
		}

		this.min = min;
		this.max = max;
		this.labelVisible = labelVisible;
		this.fillCurve = fillCurve;

		if (operation == Operation.IF_ELSE) {
			plotIfElse();
		} else if (operation == Operation.IF_LIST) {
			plotIfList();
		}

		return true;
	}

	private boolean isValid() {
		return supported.contains(operation);
	}

	private void plotIfElse() {
		MyNumberPair pair = (MyNumberPair) node.getLeft();
		ExpressionNode conditional = pair.getX().wrap();
		plotBetweenLimits(Collections.singletonList(conditional));
	}

	private void plotIfList() {
		MyList conditions = (MyList) node.getLeft();
		List<ExpressionValue> list = new ArrayList<>();
		for (int i = 0; i < conditions.size(); i++) {
			list.add(conditions.getItem(i));
		}

		plotBetweenLimits(list);
	}

	private void plotBetweenLimits(List<ExpressionValue> conditions) {
		List<Double> limits = getLimits(conditions);
		for (int i = 0; i < limits.size() - 1; i++) {
			CurvePlotter.plotCurve(geoFunction, limits.get(i), limits.get(i + 1), view, gp,
					labelVisible, fillCurve ? Gap.CORNER
							: Gap.MOVE_TO);
		}
	}

	private List<Double> getLimits(List<ExpressionValue> conditions) {
		List<Double> limits = new ArrayList<>();
		limits.add(min);

		for (int i = 0; i < conditions.size(); i++) {
			getConditionLimit(conditions.get(i), limits);
		}

		limits.add(max);
		Collections.sort(limits);
		return limits;
	}

	static void getConditionLimit(ExpressionValue ev, List<Double> limits) {
		ExpressionNode condition = ev.wrap();
		Operation operation = condition.getOperation();

		if (operation.isInequalityGreater() || operation.isInequalityLess()) {
			limits.add(getDouble(condition));
		} else if (Operation.AND_INTERVAL.equals(operation)) {
			limits.add(condition.getLeftTree().getLeft().evaluateDouble());
			limits.add(condition.getRightTree().getRight().evaluateDouble());
		} else { // EQUAL
			limits.add(condition.getRight().evaluateDouble());
		}
	}

	static double getDouble(ExpressionNode condition) {
		ExpressionValue leaf = condition.getLeft().isConstant()
				? condition.getLeft()
				: condition.getRight();
		return leaf.evaluateDouble();
	}
}