package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
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
public class AlgoHolesPolynomial extends AlgoElement {

	private GeoFunction f; // input
	private GeoList res;
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            function
	 */
	public AlgoHolesPolynomial(Construction cons, String label, GeoFunction f) {
		super(cons);

		this.f = f;
		this.res = new GeoList(cons);

		setInputOutput();
		compute();
		res.setLabel(label);
		res.setEuclidianVisible(true);
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

		setOnlyOutput(res);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getHolePoints() {
		return res;
	}

	@Override
	public void compute() {
		Function fun = f.getFunction();
		res.clear();
		solveExpr(fun.getExpression());
	}

	private void solveExpr(ExpressionValue expr) {
		if (expr == null || expr.isConstant()) {
			return;
		}
		if (expr.isExpressionNode()) {
			ExpressionNode node = expr.wrap();
			if (node.getOperation() == Operation.DIVIDE) {
				solveDivision(node.getRight());
			}
			solveExpr(node.getLeft());
			solveExpr(node.getRight());
		}
	}

	private void solveDivision(ExpressionValue exp) {

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
					res.add(new GeoPoint(cons, x, above,
							1.0));
				} else {
					res.add(new GeoPoint(cons, x, below,
							1.0));
					res.add(new GeoPoint(cons, x, above,
							1.0));
				}
			}
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
