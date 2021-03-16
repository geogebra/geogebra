package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author michael
 * 
 *         http://en.wikipedia.org/wiki/Gaussian_curvature
 *         http://emp.byui.edu/BrownD
 *         /Mathematics/Calculus-Rn-Rm/Gaussian-curvature.pdf
 * 
 *         test-cases z=x^2+y^2, k=4/(1+4x^2+4y^2)^2 z=x^2-y^2,
 *         k=-4/(1+4x^2+4y^2)^2 z=x^3+y^3, k=-36x y / (1 + 9x^4 + 9y^4)^2
 *         z=sqrt(1-x^2-y^2), k=1 (sphere) z=a x + b y, k=0 (plane!)
 */

public class AlgoCurvatureSurface extends AlgoElement {
	// input
	private GeoPointND A;
	private GeoFunctionNVar f;
	// partial derivatives
	private ExpressionNode fx;
	private ExpressionNode fxx;
	private ExpressionNode fy;
	private ExpressionNode fyy;
	private ExpressionNode fxy;
	private GeoNumeric n; // output
	private ExpressionNode lastExpression;

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point
	 * @param f
	 *            2var function
	 */
	public AlgoCurvatureSurface(Construction cons, GeoPointND A,
			GeoFunctionNVar f) {
		super(cons);
		this.A = A;
		this.f = f;

		n = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	private void expand() {
		FunctionVariable[] vars = f.getFunctionVariables();
		FunctionNVar function = f.getFunction();
		if (vars.length != 2 || function == null) {
			return;
		}

		FunctionVariable x = vars[0];
		FunctionVariable y = vars[1];
		// First derivative of function f

		fx = function.derivative(x, kernel).wrap();
		fy = function.derivative(y, kernel).wrap();

		// Second derivative of function f
		this.fxx = fx.derivative(x, kernel).wrap();
		this.fyy = fy.derivative(y, kernel).wrap();
		this.fxy = fx.derivative(y, kernel).wrap();
	}

	@Override
	public Commands getClassName() {
		return Commands.Curvature;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) A;
		input[1] = f;

		setOnlyOutput(n);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoNumeric getResult() {
		return n;
	}

	@Override
	public final void compute() {
		if (f.getFunctionExpression() != lastExpression) {
			expand();
			lastExpression = f.getFunctionExpression();
		}
		if (!A.isFinite() || fx == null || fxx == null || fy == null
				|| fyy == null || fxy == null) {
			n.setUndefined();
			return;
		}

		double x = A.getInhomX();
		double y = A.getInhomY();
		// don't need z, take point vertically above if not on surface

		f.getFunctionVariables()[0].set(x);
		f.getFunctionVariables()[1].set(y);

		double fxEval = fx.evaluateDouble();
		double fyEval = fy.evaluateDouble();
		double fxxEval = fxx.evaluateDouble();
		double fyyEval = fyy.evaluateDouble();
		double fxyEval = fxy.evaluateDouble();

		double top = (fxxEval * fyyEval - fxyEval * fxyEval);
		double bottomSqrt = 1 + fxEval * fxEval + fyEval * fyEval;

		double k = top / (bottomSqrt * bottomSqrt);

		n.setValue(k);
	}

}