package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

public final class IntersectPolyCurvesAndLine {
	private final Kernel kernel;
	private final Coords coeffs;
	private final FunctionVariable fv;
	private final MyList conditions;
	private final MyList polyCurvesX;
	private final MyList polyCurvesY;
	private final List<PolyCurveParams> paramsList = new ArrayList<>();
	private final List<Double> result = new ArrayList<>();


	public IntersectPolyCurvesAndLine(GeoCurveCartesianND curve, Coords coeffs) {
		this.kernel = curve.kernel;
		this.coeffs = coeffs;

		Function xFun = curve.getFun(0);
		Function yFun = curve.getFun(1);
		ExpressionNode node = xFun.getExpression();

		fv = xFun.getFunctionVariable();
		conditions = (MyList) node.getLeft();
		polyCurvesX = (MyList) node.getRight();
		polyCurvesY = (MyList) yFun.getExpression().getRight();

	}

	public void compute(AlgoElement.OutputHandler<GeoPointND> outputPoints) {
		result.clear();
		paramsList.clear();

		for (int i = 0; i < polyCurvesX.size(); i++) {
			ExpressionValue curveX = polyCurvesX.getItem(i);
			ExpressionValue curveY = polyCurvesY.getItem(i);
			Function functionX = new Function(kernel, curveX.wrap());
			Function functionY = new Function(kernel, curveY.wrap());
			PolyCurveParams params = new PolyCurveParams(functionX.getExpression(),
					functionY.getExpression(), fv, coeffs);
			paramsList.add(params);
			params.multiplyWithLine();
			findRoots(params, i);

		}

		outputPoints.adjustOutputSize(getOutputSize());

		if (!result.isEmpty()) {
			updatePoint(outputPoints);
		}
	}

	private int getOutputSize() {
		return result.size();
	}

	private void findRoots(PolyCurveParams params, int polyCurveIdx) {
		GeoFunction function1 = params.buildFunctionX(fv);
		Solution solution = new Solution();

		AlgoRootsPolynomial.calcRootsMultiple(function1.getFunction(),
				0, solution, kernel.getEquationSolver());

		solution.sortAndMakeUnique();

		if (solution.curRoots != null) {
			sortRoots(params, polyCurveIdx, solution);
		}
	}

	private void sortRoots(PolyCurveParams params, int polyCurveIdx, Solution solution) {
		for (int j = 0; j < solution.curRealRoots; j++) {
			double root = solution.curRoots[polyCurveIdx];
			if (isRootMatching(conditions, fv, root)) {
				if (root != 0 && !result.contains(root)) {
					result.add(root);
					paramsList.add(params);
				}
			}
		}
	}

	private void updatePoint(AlgoElement.OutputHandler<GeoPointND> outputPoints) {
		for (int index = 0; index < getOutputSize(); index++) {
			double paramVal = result.get(index);
			GeoPointND point = outputPoints.getElement(index);
			ExpressionNode xFun1 = paramsList.get(index).xFun;
			ExpressionNode yFun1 = paramsList.get(index).yFun ;
			fv.set(paramVal);
			point.setCoords(xFun1.evaluateDouble(), yFun1.evaluateDouble(), 1, 1.0);
		}
	}

	private boolean isRootMatching(MyList conditions, FunctionVariable fv, double root) {
		for (int i = 0; i < conditions.size(); i++) {
			ExpressionValue cond = conditions.getItem(i);
			fv.set(root);
			ExpressionValue val = cond.evaluate(StringTemplate.defaultTemplate);
			if (((BooleanValue) val).getBoolean()) {
				return true;
			}
		}
		return false;
	}
}
