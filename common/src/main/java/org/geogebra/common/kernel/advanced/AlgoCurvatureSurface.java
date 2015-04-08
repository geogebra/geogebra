package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.cas.AlgoDerivative;
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
 *         test-cases 
 *         z=x^2+y^2, k=4/(1+4x^2+4y^2)^2 
 *         z=x^2-y^2, k=-4/(1+4x^2+4y^2)^2 
 *         z=x^3+y^3, k=-36x y / (1 + 9x^4 + 9y^4)^2
 *         z=sqrt(1-x^2-y^2), k=1 (sphere) 
 *         z=a x + b y, k=0 (plane!)
 */

public class AlgoCurvatureSurface extends AlgoElement {

	private GeoPointND A; // input
	private GeoFunctionNVar f;
	private GeoFunctionNVar fx, fxx, fy, fyy, fxy; // partial derivatives
	private GeoNumeric n; // output

	private AlgoDerivative algoCASfx, algoCASfxx, algoCASfy, algoCASfyy,
			algoCASfxy;

	@SuppressWarnings("javadoc")
	public AlgoCurvatureSurface(Construction cons, String label, GeoPointND A,
			GeoFunctionNVar f) {
		this(cons, A, f);

		if (label != null) {
			n.setLabel(label);
		}
	}

	@SuppressWarnings("javadoc")
	AlgoCurvatureSurface(Construction cons, GeoPointND A, GeoFunctionNVar f) {
		super(cons);
		this.A = A;
		this.f = f;

		n = new GeoNumeric(cons);

		FunctionVariable[] vars = f.getFunctionVariables();

		if (vars.length != 2) {
			return;
		}

		GeoNumeric x = new GeoNumeric(cons);
		// x.setLocalVariableLabel("x");
		x.setLocalVariableLabel(vars[0].getSetVarString());

		GeoNumeric y = new GeoNumeric(cons);
		// y.setLocalVariableLabel("y");
		y.setLocalVariableLabel(vars[1].getSetVarString());

		MyDouble one = new MyDouble(kernel, 1);

		// First derivative of function f
		algoCASfx = new AlgoDerivative(cons, f, x, one, false);
		cons.removeFromConstructionList(algoCASfx);
		this.fx = (GeoFunctionNVar) algoCASfx.getResult();

		// Second derivative of function f
		algoCASfxx = new AlgoDerivative(cons, fx, x, one, false);
		cons.removeFromConstructionList(algoCASfxx);
		this.fxx = (GeoFunctionNVar) algoCASfxx.getResult();

		// First derivative of function f
		algoCASfy = new AlgoDerivative(cons, f, y, one, false);
		cons.removeFromConstructionList(algoCASfy);
		this.fy = (GeoFunctionNVar) algoCASfy.getResult();

		// Second derivative of function f
		algoCASfyy = new AlgoDerivative(cons, fy, y, one, false);
		cons.removeFromConstructionList(algoCASfyy);
		this.fyy = (GeoFunctionNVar) algoCASfyy.getResult();

		// Second derivative of function f
		algoCASfxy = new AlgoDerivative(cons, fx, y, one, false);
		cons.removeFromConstructionList(algoCASfxy);
		this.fxy = (GeoFunctionNVar) algoCASfxy.getResult();

		// Log.debug("x' = "+ fx.toString(StringTemplate.defaultTemplate));
		// Log.debug("x'' = "+ fxx.toString(StringTemplate.defaultTemplate));
		// Log.debug("y' = "+ fy.toString(StringTemplate.defaultTemplate));
		// Log.debug("y'' = "+ fyy.toString(StringTemplate.defaultTemplate));
		// Log.debug("fxy = "+ fxy.toString(StringTemplate.defaultTemplate));

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Curvature;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) A;
		input[1] = f;

		super.setOutputLength(1);
		super.setOutput(0, n);
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

		if (!A.isFinite() || fx == null || fxx == null || fy == null
				|| fyy == null || fxy == null) {
			n.setUndefined();
			return;
		}

		double x = A.getInhomX();
		double y = A.getInhomY();
		// don't need z, take point vertically above if not on surface

		double[] xy = { x, y };

		double fxEval = fx.evaluate(xy);
		double fyEval = fy.evaluate(xy);
		double fxxEval = fxx.evaluate(xy);
		double fyyEval = fyy.evaluate(xy);
		double fxyEval = fxy.evaluate(xy);

		double top = (fxxEval * fyyEval - fxyEval * fxyEval);
		double bottomSqrt = 1 + fxEval * fxEval + fyEval * fyEval;

		double k = top / (bottomSqrt * bottomSqrt);

		n.setValue(k);

	}

	@Override
	public void remove() {
		if (removed)
			return;
		super.remove();
		((GeoElement) A).removeAlgorithm(algoCASfx);
		f.removeAlgorithm(algoCASfx);
		((GeoElement) A).removeAlgorithm(algoCASfxx);
		f.removeAlgorithm(algoCASfxx);
		((GeoElement) A).removeAlgorithm(algoCASfy);
		f.removeAlgorithm(algoCASfy);
		((GeoElement) A).removeAlgorithm(algoCASfyy);
		f.removeAlgorithm(algoCASfyy);
		((GeoElement) A).removeAlgorithm(algoCASfxy);
		f.removeAlgorithm(algoCASfxy);
	}

	// TODO Consider locusequability

}