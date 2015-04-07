package org.geogebra.common.kernel.advanced;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSurfaceCartesian3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * @author michael Brioschi formula
 *         http://en.wikipedia.org/wiki/Gaussian_curvature
 * 
 *         test-cases E := 1+v^2; F := 2*u*v; G := 1+u^2; K =
 *         (u^2+v^2)/(1+u^2+v^2-3u^2v^2)^2
 * 
 * 
 */

public class AlgoCurvatureSurfaceParametric extends AlgoElement {

	private GeoNumberValue param1, param2; // input
	private GeoSurfaceCartesian3D surface;

	private GeoFunctionNVar e, f, g;
	private GeoFunctionNVar eu, ev, fu, fv, gu, gv, evv, fuv, guu; // partial
																	// derivatives
	private GeoNumeric n; // output

	private Array2DRowRealMatrix matrix1 = new Array2DRowRealMatrix(3, 3);
	private Array2DRowRealMatrix matrix2 = new Array2DRowRealMatrix(3, 3);

	private AlgoDerivative algoCASeu, algoCASev, algoCASevv, algoCASfu,
			algoCASfv, algoCASfuv, algoCASgu, algoCASgv, algoCASguu;

	@SuppressWarnings("javadoc")
	public AlgoCurvatureSurfaceParametric(Construction cons, String label,
			GeoNumberValue param1, GeoNumberValue param2,
			GeoSurfaceCartesian3D f) {
		this(cons, param1, param2, f);

		if (label != null) {
			n.setLabel(label);
		}
	}

	@SuppressWarnings("javadoc")
	AlgoCurvatureSurfaceParametric(Construction cons, GeoNumberValue param1,
			GeoNumberValue param2, GeoSurfaceCartesian3D surface) {
		super(cons);
		this.param1 = param1;
		this.param2 = param2;
		this.surface = surface;

		n = new GeoNumeric(cons);

		FunctionVariable[] vars = f.getFunctionVariables();

		if (vars.length != 2) {
			return;
		}

		FunctionNVar[] functions = surface.getFunctions();
		e = new GeoFunctionNVar(cons, functions[0]);
		f = new GeoFunctionNVar(cons, functions[1]);
		g = new GeoFunctionNVar(cons, functions[2]);

		GeoNumeric u = new GeoNumeric(cons);
		u.setLocalVariableLabel(vars[0].getSetVarString());

		GeoNumeric v = new GeoNumeric(cons);
		v.setLocalVariableLabel(vars[1].getSetVarString());

		MyDouble one = new MyDouble(kernel, 1);

		algoCASeu = new AlgoDerivative(cons, e, u, one, false);
		cons.removeFromConstructionList(algoCASeu);
		this.eu = (GeoFunctionNVar) algoCASeu.getResult();

		algoCASfu = new AlgoDerivative(cons, f, u, one, false);
		cons.removeFromConstructionList(algoCASfu);
		this.fu = (GeoFunctionNVar) algoCASfu.getResult();

		algoCASgu = new AlgoDerivative(cons, g, u, one, false);
		cons.removeFromConstructionList(algoCASgu);
		this.gu = (GeoFunctionNVar) algoCASgu.getResult();

		algoCASev = new AlgoDerivative(cons, e, v, one, false);
		cons.removeFromConstructionList(algoCASev);
		this.ev = (GeoFunctionNVar) algoCASev.getResult();

		algoCASfv = new AlgoDerivative(cons, f, v, one, false);
		cons.removeFromConstructionList(algoCASfv);
		this.fv = (GeoFunctionNVar) algoCASfv.getResult();

		algoCASgv = new AlgoDerivative(cons, g, v, one, false);
		cons.removeFromConstructionList(algoCASgv);
		this.gv = (GeoFunctionNVar) algoCASgv.getResult();

		algoCASevv = new AlgoDerivative(cons, ev, v, one, false);
		cons.removeFromConstructionList(algoCASevv);
		this.evv = (GeoFunctionNVar) algoCASevv.getResult();

		algoCASfuv = new AlgoDerivative(cons, fu, v, one, false);
		cons.removeFromConstructionList(algoCASfuv);
		this.fuv = (GeoFunctionNVar) algoCASfuv.getResult();

		algoCASguu = new AlgoDerivative(cons, gu, u, one, false);
		cons.removeFromConstructionList(algoCASguu);
		this.guu = (GeoFunctionNVar) algoCASguu.getResult();

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
		input = new GeoElement[3];
		input[0] = (GeoElement) param1;
		input[1] = (GeoElement) param2;
		input[2] = surface;

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

		if (!param1.isDefined() || !param2.isDefined() || eu == null
				|| ev == null || fu == null || fv == null || gu == null
				|| gv == null || evv == null || fuv == null || guu == null) {
			n.setUndefined();
			return;
		}

		double x = param1.getDouble();
		double y = param2.getDouble();

		double[] xy = { x, y };

		double eEval = e.evaluate(xy);
		double fEval = f.evaluate(xy);
		double gEval = g.evaluate(xy);
		double euEval = eu.evaluate(xy);
		double evEval = ev.evaluate(xy);
		double fuEval = fu.evaluate(xy);
		double fvEval = fv.evaluate(xy);
		double guEval = gu.evaluate(xy);
		double gvEval = gv.evaluate(xy);
		double evvEval = evv.evaluate(xy);
		double fuvEval = fuv.evaluate(xy);
		double guuEval = guu.evaluate(xy);

		// Brioschi formula
		// http://math.stackexchange.com/questions/270231/calculation-of-gaussian-curvature-from-first-fundamental-form
		// A := Matrix([[-diff(E,v,v)/2+diff(F,u,v)-diff(G,u,u)/2, diff(E,u)/2,
		// diff(F,u)-diff(E,v)/2], [diff(F,v)-diff(G,u)/2, E, F], [diff(G,v)/2,
		// F, G]]);
		// B := Matrix([[0, diff(E,v)/2, diff(G,u)/2], [diff(E,v)/2, E, F],
		// [diff(G,u)/2, F, G]]);

		double[][] m1 = {
				{ -evvEval / 2 + fuvEval - guuEval / 2, euEval / 2,
						fuEval - evEval / 2 },
				{ fvEval - guEval / 2, eEval, fEval },
				{ gvEval / 2, fEval, gEval } };
		double[][] m2 = { { 0, evEval / 2, guEval / 2 },
				{ evEval / 2, eEval, fEval }, { guEval / 2, fEval, gEval } };

		matrix1.setSubMatrix(m1, 0, 0);
		@SuppressWarnings("deprecation")
		double det1 = matrix1.getDeterminant();
		matrix2.setSubMatrix(m2, 0, 0);
		@SuppressWarnings("deprecation")
		double det2 = matrix2.getDeterminant();

		double denomSqrt = eEval * gEval - fEval * fEval;

		double k = (det1 - det2) / (denomSqrt * denomSqrt);

		n.setValue(k);

	}

	@Override
	public void remove() {
		if (removed)
			return;
		super.remove();
		((GeoElement) param1).removeAlgorithm(algoCASeu);
		((GeoElement) param2).removeAlgorithm(algoCASeu);
		surface.removeAlgorithm(algoCASeu);
		((GeoElement) param1).removeAlgorithm(algoCASev);
		((GeoElement) param2).removeAlgorithm(algoCASev);
		surface.removeAlgorithm(algoCASev);
		((GeoElement) param1).removeAlgorithm(algoCASfu);
		((GeoElement) param2).removeAlgorithm(algoCASfu);
		surface.removeAlgorithm(algoCASfu);
		((GeoElement) param1).removeAlgorithm(algoCASfv);
		((GeoElement) param2).removeAlgorithm(algoCASfv);
		surface.removeAlgorithm(algoCASfv);
		((GeoElement) param1).removeAlgorithm(algoCASgu);
		((GeoElement) param2).removeAlgorithm(algoCASgu);
		surface.removeAlgorithm(algoCASgu);
		((GeoElement) param1).removeAlgorithm(algoCASgv);
		((GeoElement) param2).removeAlgorithm(algoCASgv);
		surface.removeAlgorithm(algoCASgv);

		((GeoElement) param1).removeAlgorithm(algoCASevv);
		((GeoElement) param2).removeAlgorithm(algoCASevv);
		surface.removeAlgorithm(algoCASevv);
		((GeoElement) param1).removeAlgorithm(algoCASfuv);
		((GeoElement) param2).removeAlgorithm(algoCASfuv);
		surface.removeAlgorithm(algoCASfuv);
		((GeoElement) param1).removeAlgorithm(algoCASguu);
		((GeoElement) param2).removeAlgorithm(algoCASguu);
		surface.removeAlgorithm(algoCASguu);
	}

}