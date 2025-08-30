package org.geogebra.common.kernel.advanced;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;

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
	// input
	private GeoNumberValue param1;
	private GeoNumberValue param2;
	private GeoSurfaceCartesianND surface;
	// functions
	private GeoFunctionNVar e;
	private GeoFunctionNVar f;
	private GeoFunctionNVar g;
	// partial derivatives
	private GeoFunctionNVar eu;
	private GeoFunctionNVar ev;
	private GeoFunctionNVar fu;
	private GeoFunctionNVar fv;
	private GeoFunctionNVar gu;
	private GeoFunctionNVar gv;
	private GeoFunctionNVar evv;
	private GeoFunctionNVar fuv;
	private GeoFunctionNVar guu;
	private GeoNumeric n; // output

	private Array2DRowRealMatrix matrix1 = new Array2DRowRealMatrix(3, 3);
	private Array2DRowRealMatrix matrix2 = new Array2DRowRealMatrix(3, 3);

	private AlgoDerivative algoCASeu;
	private AlgoDerivative algoCASev;
	private AlgoDerivative algoCASevv;
	private AlgoDerivative algoCASfu;
	private AlgoDerivative algoCASfv;
	private AlgoDerivative algoCASfuv;
	private AlgoDerivative algoCASgu;
	private AlgoDerivative algoCASgv;
	private AlgoDerivative algoCASguu;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param param1
	 *            first parameter
	 * @param param2
	 *            second parameter
	 * @param f
	 *            2var function
	 */
	public AlgoCurvatureSurfaceParametric(Construction cons, String label,
			GeoNumberValue param1, GeoNumberValue param2,
			GeoSurfaceCartesianND f) {
		this(cons, param1, param2, f);
		n.setLabel(label);
	}

	AlgoCurvatureSurfaceParametric(Construction cons, GeoNumberValue param1,
			GeoNumberValue param2, GeoSurfaceCartesianND surface) {
		super(cons);
		this.param1 = param1;
		this.param2 = param2;
		this.surface = surface;

		n = new GeoNumeric(cons);

		FunctionNVar[] functions = surface.getFunctions();
		e = new GeoFunctionNVar(cons, functions[0]);
		f = new GeoFunctionNVar(cons, functions[1]);
		g = new GeoFunctionNVar(cons, functions.length > 2 ? functions[2]
				: new Function(kernel, new ExpressionNode(kernel, 0)));

		FunctionVariable[] vars = f.getFunctionVariables();

		if (vars.length != 2) {
			return;
		}

		GeoNumeric u = new GeoNumeric(cons);
		u.setLocalVariableLabel(vars[0].getSetVarString());

		GeoNumeric v = new GeoNumeric(cons);
		v.setLocalVariableLabel(vars[1].getSetVarString());

		GeoNumeric one = new GeoNumeric(cons, 1);
		EvalInfo info = new EvalInfo(false);
		algoCASeu = new AlgoDerivative(cons, e, u, one, false, info);
		cons.removeFromConstructionList(algoCASeu);
		this.eu = (GeoFunctionNVar) algoCASeu.getResult();

		algoCASfu = new AlgoDerivative(cons, f, u, one, false, info);
		cons.removeFromConstructionList(algoCASfu);
		this.fu = (GeoFunctionNVar) algoCASfu.getResult();

		algoCASgu = new AlgoDerivative(cons, g, u, one, false, info);
		cons.removeFromConstructionList(algoCASgu);
		this.gu = (GeoFunctionNVar) algoCASgu.getResult();

		algoCASev = new AlgoDerivative(cons, e, v, one, false, info);
		cons.removeFromConstructionList(algoCASev);
		this.ev = (GeoFunctionNVar) algoCASev.getResult();

		algoCASfv = new AlgoDerivative(cons, f, v, one, false, info);
		cons.removeFromConstructionList(algoCASfv);
		this.fv = (GeoFunctionNVar) algoCASfv.getResult();

		algoCASgv = new AlgoDerivative(cons, g, v, one, false, info);
		cons.removeFromConstructionList(algoCASgv);
		this.gv = (GeoFunctionNVar) algoCASgv.getResult();

		algoCASevv = new AlgoDerivative(cons, ev, v, one, false, info);
		cons.removeFromConstructionList(algoCASevv);
		this.evv = (GeoFunctionNVar) algoCASevv.getResult();

		algoCASfuv = new AlgoDerivative(cons, fu, v, one, false, info);
		cons.removeFromConstructionList(algoCASfuv);
		this.fuv = (GeoFunctionNVar) algoCASfuv.getResult();

		algoCASguu = new AlgoDerivative(cons, gu, u, one, false, info);
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
		// double det1 = matrix1.getDeterminant();
		double det1 = new LUDecomposition(matrix1, Kernel.STANDARD_PRECISION)
				.getDeterminant();
		matrix2.setSubMatrix(m2, 0, 0);
		// double det2 = matrix2.getDeterminant();
		double det2 = new LUDecomposition(matrix2, Kernel.STANDARD_PRECISION)
				.getDeterminant();

		double denomSqrt = eEval * gEval - fEval * fEval;

		double k = (det1 - det2) / (denomSqrt * denomSqrt);

		n.setValue(k);

	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();
		param1.removeAlgorithm(algoCASeu);
		param2.removeAlgorithm(algoCASeu);
		surface.removeAlgorithm(algoCASeu);
		param1.removeAlgorithm(algoCASev);
		param2.removeAlgorithm(algoCASev);
		surface.removeAlgorithm(algoCASev);
		param1.removeAlgorithm(algoCASfu);
		param2.removeAlgorithm(algoCASfu);
		surface.removeAlgorithm(algoCASfu);
		param1.removeAlgorithm(algoCASfv);
		param2.removeAlgorithm(algoCASfv);
		surface.removeAlgorithm(algoCASfv);
		param1.removeAlgorithm(algoCASgu);
		param2.removeAlgorithm(algoCASgu);
		surface.removeAlgorithm(algoCASgu);
		param1.removeAlgorithm(algoCASgv);
		param2.removeAlgorithm(algoCASgv);
		surface.removeAlgorithm(algoCASgv);

		param1.removeAlgorithm(algoCASevv);
		param2.removeAlgorithm(algoCASevv);
		surface.removeAlgorithm(algoCASevv);
		param1.removeAlgorithm(algoCASfuv);
		param2.removeAlgorithm(algoCASfuv);
		surface.removeAlgorithm(algoCASfuv);
		param1.removeAlgorithm(algoCASguu);
		param2.removeAlgorithm(algoCASguu);
		surface.removeAlgorithm(algoCASguu);
	}

}