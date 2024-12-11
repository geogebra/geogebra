package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;

/**
 * @author Victor Franco Espino
 * @version 11-02-2007
 * 
 *          Calculate Curvature Vector for function: c(x) =
 *          (1/T^4)*(-f'*f'',f''), T = sqrt(1+(f')^2)
 */

public class AlgoCurvatureVector extends AlgoElement {

	private GeoPoint A; // input
	private GeoFunction f; // input
	private GeoFunction f1; // f1 = f'
	private GeoFunction f2; // f2 = f''
	private GeoVector v; // output

	AlgoDerivative algoCAS;
	AlgoDerivative algoCAS2;

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point
	 * @param f
	 *            function
	 */
	public AlgoCurvatureVector(Construction cons, GeoPoint A, GeoFunction f) {
		super(cons);
		this.A = A;
		this.f = f;

		// create new vector
		v = new GeoVector(cons);
		try {
			v.setStartPoint(A);
		} catch (CircularDefinitionException e) {
			// can't happen with new vectors
		}
		EvalInfo info = new EvalInfo(false);
		// First derivative of function f
		algoCAS = new AlgoDerivative(cons, f, info);
		cons.removeFromConstructionList(algoCAS);
		this.f1 = (GeoFunction) algoCAS.getResult();

		// Second derivative of function f
		algoCAS2 = new AlgoDerivative(cons, f1, info);
		cons.removeFromConstructionList(algoCAS2);
		this.f2 = (GeoFunction) algoCAS2.getResult();

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.CurvatureVector;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = A;
		input[1] = f;

		setOnlyOutput(v);
		setDependencies(); // done by AlgoElement
	}

	/** @return the resultant vector */
	public GeoVector getVector() {
		return v;
	}

	@Override
	public final void compute() {
		try {
			double f1eval = f1.value(A.inhomX);
			double f2eval = f2.value(A.inhomX);
			double t = Math.sqrt(1 + f1eval * f1eval);
			double t4 = t * t * t * t;

			v.x = - (f1eval * f2eval) / t4;
			v.y = f2eval / t4;
			v.z = 0.0;
		} catch (Exception e) {
			// in case something went wrong, e.g. derivatives not defined
			v.setUndefined();
		}
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();
		A.removeAlgorithm(algoCAS);
		f.removeAlgorithm(algoCAS);
		A.removeAlgorithm(algoCAS2);
		f.removeAlgorithm(algoCAS2);
	}

}