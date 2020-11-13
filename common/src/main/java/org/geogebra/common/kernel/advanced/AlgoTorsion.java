package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author Victor Franco Espino, Markus Hohenwarter
 * @version 11-02-2007
 *
 *          Calculate Curvature for function:
 */

public class AlgoTorsion extends AlgoElement {

	private GeoPointND A; // input
	private GeoCurveCartesianND f;
	private GeoNumeric K; // output

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

	@Override
	public Commands getClassName() {
		return Commands.Torsion;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = A.toGeoElement();
		input[1] = f;

		super.setOutputLength(1);
		super.setOutput(0, K);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return curvature
	 */
	public GeoNumeric getResult() {
		return K;
	}

	@Override
	public final void compute() {
		if (f.isDefined()) {
			try {
				double t = f.getClosestParameterForCurvature(A,
						f.getMinParameter());
				K.setValue(f.evaluateTorsion(t));
			} catch (Exception ex) {
				ex.printStackTrace();
				K.setUndefined();
			}
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