package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.cas.AlgoCoefficients;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public class AlgoCompleteSquare extends AlgoElement {

	private GeoFunction f;
	private GeoFunction square;
	private FunctionVariable fv;
	private MyDouble a;
	private MyDouble h;
	private MyDouble k; // a(x-h)^2+k
	private int lastDeg;
	private AlgoCoefficients algoCoef;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            function
	 */
	public AlgoCompleteSquare(Construction cons, String label, GeoFunction f) {
		super(cons);
		this.f = f;
		a = new MyDouble(kernel);
		h = new MyDouble(kernel);
		k = new MyDouble(kernel);
		square = new GeoFunction(cons);
		setInputOutput();
		compute();
		lastDeg = 0;
		square.setLabel(label);
	}

	@Override
	public void compute() {
		if (fv == null || !fv.getSetVarString().equals(
				f.getVarString(StringTemplate.defaultTemplate))) {
			initFunction();
		}
		int degInt;
		GeoList coefs = null;
		// px^2+qx+r; p+q+r=s;
		double r = f.value(0);
		double s = f.value(1);
		double p = 0.5 * (s + f.value(-1)) - r;
		double q = s - p - r;
		boolean isQuadratic = !f.isGeoFunctionConditional();
		double[] checkpoints = { 1000, -1000, Math.PI, Math.E };
		for (int i = 0; i < checkpoints.length; i++) {
			double x = checkpoints[i];
			if (!DoubleUtil.isZero(p * x * x + q * x + r - f.value(x))) {
				// Log.debug(p + "," + q + "," + r + ","
				// + (p * x * x + q * x + r - f.evaluate(x)));
				isQuadratic = false;
			}
		}
		if (!isQuadratic) {
			if (algoCoef == null) {
				algoCoef = new AlgoCoefficients(cons, f);
				algoCoef.setProtectedInput(true);
				algoCoef.remove();
			} else {
				algoCoef.compute();
			}
			coefs = algoCoef.getResult();

			degInt = coefs.size() - 1;
			isQuadratic = coefs.isDefined() && coefs.get(0).isDefined();
			for (int i = 1; i < degInt; i++) {
				if (2 * i != degInt && !DoubleUtil
						.isZero(((GeoNumeric) coefs.get(i)).getDouble())) {
					isQuadratic = false;
				}
				p = ((GeoNumeric) coefs.get(0)).getDouble();
				q = ((GeoNumeric) coefs.get(degInt / 2)).getDouble();
				r = ((GeoNumeric) coefs.get(degInt)).getDouble();
			}
		} else {
			degInt = 2;
		}

		if (MyDouble.isOdd(degInt) || degInt < 2 || !isQuadratic
				|| DoubleUtil.isZero(p)) {
			square.setUndefined();
			return;
		}

		if (lastDeg != degInt) {
			ExpressionNode squareE;
			ExpressionValue fvPower;
			if (degInt == 2) {
				fvPower = fv;
			} else {
				int power = degInt / 2;
				fvPower = new ExpressionNode(kernel, fv, Operation.POWER,
						new MyDouble(kernel, power));
			}

			// (x-h)^2
			ExpressionNode sqrTerm = new ExpressionNode(kernel, fvPower, Operation.MINUS, h)
					.power(new MyDouble(kernel, 2));
			// a(x-h)^2
			ExpressionNode sqrMultTerm = p == 1 ? sqrTerm
					: new ExpressionNode(kernel, a, Operation.MULTIPLY, sqrTerm);
			// a(x-h)^2+k
			squareE = new ExpressionNode(kernel, sqrMultTerm, Operation.PLUS, k);

			square.getFunction().setExpression(squareE);
		}
		lastDeg = degInt;

		// if one is undefined, others are as well
		square.setDefined(!Double.isNaN(r));
		a.set(p);
		h.set(-q / (2 * p));
		k.set(r - q * q / (p * 4));
	}

	private void initFunction() {
		fv = new FunctionVariable(kernel, f.getVarString(StringTemplate.defaultTemplate));
		ExpressionNode squareE = new ExpressionNode(kernel, a);
		Function squareF = new Function(squareE, fv);
		squareF.initFunction();
		square.setFunction(squareF);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f;
		setOutputLength(1);
		setOutput(0, square);
		setDependencies();

	}

	public GeoFunction getResult() {
		return square;
	}

	@Override
	public Commands getClassName() {
		return Commands.CompleteSquare;
	}

}
