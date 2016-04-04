package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAxisSecond3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.AlgoAxisSecond;
import org.geogebra.common.kernel.advanced.CmdSecondAxis;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * MajorAxis command processor
 *
 */
public class CmdSecondAxis3D extends CmdSecondAxis {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdSecondAxis3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected AlgoAxisSecond getAlgoAxisSecond(Construction cons1, String label,
			GeoConicND geoConicND) {
		if (geoConicND instanceof GeoConic3D) {
			return new AlgoAxisSecond3D(cons1, label, geoConicND);
		}
		return new AlgoAxisSecond(cons1, label, geoConicND);

	}

}
