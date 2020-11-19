package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author James471, SayarGitHub, AbbyDabby246, ThatFarziGamer
 * @version 20-11-2020
 *
 *          Calculate Torsion for function:
 */

public class AlgoTorsion extends AlgoElement {

	private GeoPointND A; // input
	private GeoCurveCartesianND f;
	private GeoNumeric K; // output
	private GeoConicND gc;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            point on function
	 * @param f
	 *            function
	 */
	public AlgoTorsion(Construction cons, String label, GeoPointND A,
			GeoCurveCartesianND f) {
		this(cons, A, f);

		if (label != null) {
			K.setLabel(label);
		} else {
			// if we don't have a label we could try k
			K.setLabel("k");
		}
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            point on curve
	 * @param f
	 *            conic
	 */
	public AlgoTorsion(Construction cons, String label, GeoPointND A,
			GeoConicND f) {
		this(cons, A, f);

		if (label != null) {
			K.setLabel(label);
		} else {
			// if we don't have a label we could try k
			K.setLabel("k");
		}
	}

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point on function
	 * @param f
	 *            function
	 */
	public AlgoTorsion(Construction cons, GeoPointND A, GeoCurveCartesianND f) {
		super(cons);
		this.f = f;
		this.A = A;
		K = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point on curve
	 * @param gc
	 *            conic
	 */
	public AlgoTorsion(Construction cons, GeoPointND A, GeoConicND gc) {
		super(cons);
		this.gc = gc;
		this.A = A;
		K = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Torsion;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		input = new GeoElement[2];
		input[0] = A.toGeoElement();
		if (gc != null){
			f = kernel.getGeoFactory().newCurve(gc instanceof GeoConic ? 2 : 3,
					cons);

			gc.toGeoCurveCartesian(f);
			input[1] = gc;
		} else {
			input[1] = f;
		}


		super.setOutputLength(1);
		super.setOutput(0, K);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return torsion
	 */
	public GeoNumeric getResult() {
		return K;
	}

	@Override
	public final void compute() {
		if (gc == null && f.isDefined()) {
			try {
				double t = f.getClosestParameterForCurvature(A,
						f.getMinParameter());
				K.setValue(f.evaluateTorsion(t));
			} catch (Exception ex) {
				ex.printStackTrace();
				K.setUndefined();
			}
		} else if(gc != null) {
			K.setValue(0);
		} else {
			K.setUndefined();
		}
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();
	}

}