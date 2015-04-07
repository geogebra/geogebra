package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAxes3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.CmdAxes;
import org.geogebra.common.kernel.algos.AlgoAxesQuadricND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 * Axes[ <GeoConic> ]
 */
public class CmdAxes3D extends CmdAxes {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxes3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected AlgoAxesQuadricND axesConic(Construction cons1, String[] labels,
			GeoQuadricND c) {

		if (c.isGeoElement3D()) {
			return new AlgoAxes3D(cons1, labels, c);
		}

		return super.axesConic(cons1, labels, c);

	}
}
