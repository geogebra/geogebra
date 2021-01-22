package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Operation;

public class AlgoRemovableDiscontinuity extends AlgoGeoPointsFunction implements
		UsesCAS {

	private final GeoFunction f; // input
	private final MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	/**
	 * @param cons Construction
	 * @param f Function to evaluate for removable discontinuities
	 * @param labels labels for output
	 */
	public AlgoRemovableDiscontinuity(Construction cons, GeoFunction f, String[] labels) {
		super(cons, labels, true);

		this.f = f;

		setInputOutput();
		compute();
	}

	/**
	 * @param cons Construction
	 * @param f Function to evaluate for removable discontinuities
	 * @param labels labels for output
	 * @param setLabels whether to set labels
	 */
	public AlgoRemovableDiscontinuity(Construction cons, GeoFunction f, String[] labels,
			boolean setLabels) {
		super(cons, labels, setLabels);

		this.f = f;

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.RemovableDiscontinuity;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f.toGeoElement();
		setOutput(getPoints());
		setDependencies(); // done by AlgoElement
	}

	@Override
	public void compute() {
		Function fun = f.getFunction();
		List<MyPoint> result = new ArrayList<>();
		solveExpr(fun.getExpression(), result);

		double[] xs = new double[result.size()];
		double[] ys = new double[result.size()];
		for (int i = 0; i < result.size(); i++) {
			MyPoint point = result.get(i);
			xs[i] = point.x;
			ys[i] = point.y;
		}
		setPoints(xs, ys, xs.length);
		updatePoints();
	}

	private void solveExpr(ExpressionValue expr, List<MyPoint> result) {
		if (expr == null || expr.isConstant()) {
			return;
		}
		if (expr.isExpressionNode()) {
			ExpressionNode node = expr.wrap();
			if (node.getOperation() == Operation.DIVIDE) {
				solveDivision(node.getRight(), result);
			}
			solveExpr(node.getLeft(), result);
			solveExpr(node.getRight(), result);
		}
	}

	private void solveDivision(ExpressionValue exp, List<MyPoint> result) {
		arbconst.startBlocking();
		List<NumberValue> values = getValues(exp);
		for (NumberValue value: values) {
			double x = value.getDouble();

			double above = limit(x, 1);
			double below = limit(x, -1);

			add(x, above, result);
			if (above != below) {
				add(x, above, result);
			}
		}
	}

	private List<NumberValue> getValues(ExpressionValue exp) {
		String input = "Solve(" + exp.toString(StringTemplate.prefixedDefault) + " = 0)";
		String output = kernel.evaluateCachedGeoGebraCAS(input, arbconst);

		try {
			ExpressionNode node = kernel.getParser().parseExpression(output);
			ValueCollector collector = new ValueCollector();
			node.traverse(collector);
			return collector.values;
		} catch (ParseException ignored) {
			return Collections.emptyList();
		}
	}

	private void add(double x, double y, List<MyPoint> result) {
		if (!Double.isInfinite(y)) {
			MyPoint point = new MyPoint(x, y);
			result.add(point);
		}
	}

	private double limit(double x, int direction) { // from AlgoLimitAbove
		String limitString = f.getLimit(x, direction);

		try {
			String numStr = kernel.evaluateCachedGeoGebraCAS(limitString,
					arbconst);

			return kernel.getAlgebraProcessor()
					.evaluateToNumeric(numStr, ErrorHelper.silent())
					.getDouble();
		} catch (Throwable e) {
			e.printStackTrace();
			return 0;
		}
	}

	private void updatePoints() {
		for (GeoPoint point : points) {
			if (point != null) {
				point.setPointStyle(EuclidianStyleConstants.POINT_STYLE_CIRCLE);
			}
		}
	}

	static private class ValueCollector implements Traversing {

		private final List<NumberValue> values = new ArrayList<>();

		@Override
		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof Equation) {
				Equation equation = (Equation) ev;
				ExpressionValue rhs = equation.getRHS().unwrap();
				ExpressionNode lhs = equation.getLHS();
				if (lhs.containsFunctionVariable("x") && rhs instanceof NumberValue) {
					NumberValue numberValue = (NumberValue) rhs;
					values.add(numberValue);
				}
			}
			return ev;
		}
	}
}
