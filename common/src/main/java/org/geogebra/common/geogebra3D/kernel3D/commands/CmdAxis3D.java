package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAxis3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.AlgoAxis;
import org.geogebra.common.kernel.advanced.CmdAxis;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * MajorAxis command processor
 *
 */
public class CmdAxis3D extends CmdAxis {

	/**
	 * @param kernel
	 *            kernel
	 * @param axisId
	 *            0 for major, 1 for minor
	 */
	public CmdAxis3D(Kernel kernel, int axisId) {
		super(kernel, axisId);
	}

	@Override
	protected AlgoAxis getAlgoAxisFirst(Construction cons1, String label,
			GeoConicND geoConicND) {
		if (geoConicND instanceof GeoConic3D) {
			return new AlgoAxis3D(cons1, label, geoConicND, axisId);
		}
		return new AlgoAxis(cons1, label, geoConicND, axisId);

	}

}
