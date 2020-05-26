package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.plugin.Operation;

/**
 * Created by kh on 18.01.2018.
 */
public class AlgoHolesPolynomial extends AlgoElement implements UsesCAS {

	private GeoFunction f; // input
	private GeoPoint[] output;
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	private boolean indcludesInfinite;

	/**
	 * @param cons construction
	 * @param f function
	 */
	public AlgoHolesPolynomial(Construction cons, GeoFunction f) {
		this(cons, f, true);
	}

	/**
	 * @param cons construction
	 * @param f function
	 * @param indcludesInfinite include infinite values
	 */
	public AlgoHolesPolynomial(Construction cons, GeoFunction f, boolean indcludesInfinite) {
		super(cons);

		this.f = f;
		this.indcludesInfinite = indcludesInfinite;
		output = new GeoPoint[0];

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Holes;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f.toGeoElement();
		setOutput(output);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public void compute() {
		Function fun = f.getFunction();
		List<GeoPoint> result = new ArrayList<>();
		solveExpr(fun.getExpression(), result);
		output = result.toArray(output);
		setOutput(output);
	}

	private void solveExpr(ExpressionValue expr, List<GeoPoint> result) {
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

	private void solveDivision(ExpressionValue exp, List<GeoPoint> result) {
		StringBuilder sb = new StringBuilder("solve(");
		sb.append(exp.toString(StringTemplate.prefixedDefault));
		sb.append(" = 0)");

		arbconst.startBlocking();
		String solns = kernel.evaluateCachedGeoGebraCAS(sb.toString(),
				arbconst);
		GeoList raw = kernel.getAlgebraProcessor().evaluateToList(solns);

		for (int i = 0; i < raw.size(); i++) {
			GeoElement element = raw.get(i);
			if (element instanceof GeoLine) {
				GeoLine line = (GeoLine) element;

				double x = -line.getZ() / line.getX();

				double above = limit(x, 1);
				double below = limit(x, -1);

				if (above == below) {
					add(x, above, result);
				} else {
					add(x, below, result);
					add(x, above, result);
				}
			}
		}
	}

	private void add(double x, double y, List<GeoPoint> result) {
		if (indcludesInfinite || !Double.isInfinite(y)) {
			GeoPoint point = new GeoPoint(cons, x, y, 1.0);
			point.setParentAlgorithm(this);
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

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("HolesOfA", "Holes of %0",
				f.getLabel(tpl));
	}
}
