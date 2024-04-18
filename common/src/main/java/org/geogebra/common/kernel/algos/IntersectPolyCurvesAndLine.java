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
	private final Coords coefficients;
	private final FunctionVariable functionVariable;
	private final MyList conditions;
	private final Spline spline;
	private final Output output = new Output();

	private static class Spline {

		private final MyList xCurves;
		private final MyList yCurves;

		public Spline(ExpressionValue xCurves, ExpressionValue yCurves) {
			this.xCurves = (MyList) xCurves;
			this.yCurves = (MyList) yCurves;
		}

		public int size() {
			return xCurves.size();
		}

		public ExpressionNode getFunctionExpressionX(int idx) {
			return xCurves.getItem(idx).wrap();
		}

		public ExpressionNode getFunctionExpressionY(int idx) {
			return yCurves.getItem(idx).wrap();
		}
	}

	private class Output {
		private final List<Double> roots = new ArrayList<>();
		private final List<PolyCurveParams> params = new ArrayList<>();

		public void add(double root, PolyCurveParams polyCurveParams) {
			if (roots.contains(root)) {
				return;
			}

			roots.add(root);
			params.add(polyCurveParams);
		}

		private void updatePoint(FunctionVariable functionVariable,
				AlgoElement.OutputHandler<GeoPointND> outputPoints) {
			outputPoints.adjustOutputSize(roots.size());
			for (int index = 0; index < roots.size(); index++) {
				GeoPointND point = outputPoints.getElement(index);
				functionVariable.set(roots.get(index));
				ExpressionNode xFun1 = params.get(index).xFun;
				ExpressionNode yFun1 = params.get(index).yFun;
				point.setCoords(xFun1.evaluateDouble(), yFun1.evaluateDouble(), 1, 1.0);
			}
		}
	}

	public IntersectPolyCurvesAndLine(GeoCurveCartesianND curve, Coords coefficients) {
		this.kernel = curve.kernel;
		this.coefficients = coefficients;

		Function xFun = curve.getFun(0);
		Function yFun = curve.getFun(1);
		ExpressionNode xFunExpression = xFun.getExpression();

		functionVariable = xFun.getFunctionVariable();
		conditions = (MyList) xFunExpression.getLeft();
		spline =
				new Spline(xFunExpression.getRight(), yFun.getExpression().getRight());
	}

	public void compute(AlgoElement.OutputHandler<GeoPointND> outputPoints) {
		for (int i = 0; i < spline.size(); i++) {
			PolyCurveParams params = new PolyCurveParams(
					spline.getFunctionExpressionX(i),
					spline.getFunctionExpressionY(i),
					functionVariable, coefficients);
			params.multiplyWithLine();
			findRoots(params);
		}

		output.updatePoint(functionVariable, outputPoints);
	}

	private void findRoots(PolyCurveParams params) {
		GeoFunction function1 = params.buildFunctionX(functionVariable);
		Solution solution = new Solution();

		AlgoRootsPolynomial.calcRootsMultiple(function1.getFunction(),
				0, solution, kernel.getEquationSolver());

		solution.sortAndMakeUnique();

		if (solution.curRoots != null) {
			collectRoots(params, solution);

		}
	}

	private void collectRoots(PolyCurveParams params, Solution solution) {
		for (int i = 0; i < solution.curRealRoots; i++) {
			double root = solution.curRoots[i];
			if (isRootMatching(root, i)) {
				output.add(root, params);
				break;
			}
		}

		if (output.roots.isEmpty() && hasElseInIfList()) {
			output.add(solution.curRoots[0], params);
		}
	}

	private boolean hasElseInIfList() {
		return conditions.size() < spline.xCurves.size();
	}

	private boolean isRootMatching(double root, int i) {
		functionVariable.set(root);
		if (i == 0 && root >= 0 && isConditionalHoldsAt(i)) {
			return true;
		}

		if (i > 0 && i < conditions.size() - 1
				&& isConditionalHoldsAt(i) && !isConditionalHoldsAt(i -1)) {
				return true;
		}

		return root <= 1 && i == conditions.size() - 1
			&& isConditionalHoldsAt(i);
	}

	private boolean isConditionalHoldsAt(int idx) {
		ExpressionValue cond = conditions.getItem(idx);
		ExpressionValue val = cond.evaluate(StringTemplate.defaultTemplate);
		return ((BooleanValue) val).getBoolean();
	}
}