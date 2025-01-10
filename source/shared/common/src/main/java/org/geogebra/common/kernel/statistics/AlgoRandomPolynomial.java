package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPolynomialFromCoordinates;
import org.geogebra.common.kernel.arithmetic.Function;
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

	private GeoNumberValue degree;
	private GeoNumberValue min;
	private GeoNumberValue max;
	private GeoFunction polynomial;
	private Function f;

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param degree
	 *            maximal degree
	 * @param min
	 *            minimal coefficient value
	 * @param max
	 *            maximal coefficient value
	 */
	public AlgoRandomPolynomial(Construction cons, String label,
			GeoNumberValue degree, GeoNumberValue min, GeoNumberValue max) {
		super(cons);
		this.degree = degree;
		this.min = min;
		this.max = max;
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

		double[] coeffs = new double[deg + 1];

		for (int i = 0; i <= deg; i++) {
			coeffs[i] = randomCoef(i != deg);
			// Log.error("coeff " + i + " is " + coeffs[i]);
		}

		f = AlgoPolynomialFromCoordinates
				.buildPolyFunctionExpression(kernel, coeffs);

		polynomial.setFunction(f);

	}

	private double randomCoef(boolean acceptZero) {

		double minD = min.getDouble();
		double maxD = max.getDouble();

		if (acceptZero
				// either both positive, both negative
				// or both zero (which shouldn't happen)
				// eg RandomPolynomial[3,0,0] returns undefined
				|| Math.signum(maxD) == Math.signum(minD)) {
			return kernel.getApplication().getRandomIntegerBetween(minD, maxD);
		}

		// logic doesn't work unless minD < 0 < maxD
		int rnd = kernel.getApplication().getRandomIntegerBetween(minD,
				maxD - 1);
		return rnd >= 0 ? rnd + 1 : rnd;
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
