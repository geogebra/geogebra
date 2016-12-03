package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Algorithm for random polynomials with given degree and coefficient range
 * 
 * @author Zbynek Konecny
 *
 */
public class AlgoRandomPolynomial extends AlgoElement {

	private GeoNumberValue degree, min, max;
	private GeoFunction polynomial;
	private Function f;
	private FunctionVariable fv;

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param degree
	 *            maximal degree
	 * @param min
	 *            minimal coefficient vlaue
	 * @param max
	 *            maximal coefficient value
	 */
	public AlgoRandomPolynomial(Construction cons, String label,
			GeoNumberValue degree, GeoNumberValue min, GeoNumberValue max) {
		super(cons);
		this.degree = degree;
		this.min = min;
		this.max = max;
		fv = new FunctionVariable(kernel);
		f = new Function(new ExpressionNode(kernel, fv), fv);
		polynomial = new GeoFunction(cons);
		setInputOutput();
		compute();
		polynomial.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(polynomial);
		input = new GeoElement[] { degree.toGeoElement(), min.toGeoElement(),
				max.toGeoElement() };
		setDependencies();
	}

	@Override
	public void compute() {
		// cover undefined cases
		if (!degree.isDefined() || !min.isDefined() || !max.isDefined()
				|| degree.getDouble() < 0) {
			polynomial.setUndefined();
			return;
		}
		int lower = (int) Math.ceil(min.getDouble());
		int upper = (int) Math.floor(max.getDouble());
		if (lower > upper || (lower == 0 && upper == 0)) {
			polynomial.setUndefined();
			return;
		}
		// input is sane, we can do the computation
		int deg = (int) Math.floor(degree.getDouble());
		ExpressionNode varExpr = new ExpressionNode(kernel, fv);
		ExpressionNode newExpr = randomCoef(deg != 0);
		for (int i = 1; i <= deg; i++) {
			newExpr = varExpr.power(new MyDouble(kernel, i))
					.multiply(randomCoef(i != deg)).plus(newExpr);
		}
		f.setExpression(newExpr, fv);
		polynomial.setFunction(f);

	}

	private ExpressionNode randomCoef(boolean acceptZero) {

		double minD = min.getDouble();
		double maxD = max.getDouble();

		if (acceptZero
		// either both positive, both negative
		// or both zero (which shouldn't happen)
		// eg RandomPolynomial[3,0,0] returns undefined
				|| Math.signum(maxD) == Math.signum(minD)) {
			return new ExpressionNode(kernel, kernel.getApplication()
					.getRandomIntegerBetween(minD, maxD));
		}

		// logic doen't work unless minD < 0 < maxD
		int rnd = kernel.getApplication().getRandomIntegerBetween(minD,
				maxD - 1);
		return new ExpressionNode(kernel, rnd >= 0 ? rnd + 1 : rnd);
	}

	@Override
	public Commands getClassName() {
		return Commands.RandomPolynomial;
	}

	/**
	 * @return resulting polynomial
	 */
	public GeoFunction getResult() {
		return polynomial;
	}

	

}
