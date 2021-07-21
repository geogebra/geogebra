package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.advanced.AlgoAxis;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 * Major axis
 */
public class AlgoAxis3D extends AlgoAxis {
	private GeoLine3D axis; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 * @param axisId
	 *            0 for major, 1 for minor
	 */
	public AlgoAxis3D(Construction cons, String label, GeoConicND c,
			int axisId) {
		super(cons, c, axisId);
		axis = new GeoLine3D(cons);
		finishSetup(label);
	}

	@Override
	public final void compute() {
		// axes are lines with directions of eigenvectors
		// through midpoint b
		axis.setCoord(getConic().getMidpoint3D(),
				getConic().getEigenvec3D(axisId));
		P.setCoords(getConic().getMidpoint3D(), false);
	}

	@Override
	public GeoLineND getAxis() {
		return axis;
	}

}
