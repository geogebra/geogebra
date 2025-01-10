package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoFocus3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFocus;
import org.geogebra.common.kernel.commands.CmdFocus;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * 3D version of Focus
 *
 */
public class CmdFocus3D extends CmdFocus {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdFocus3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected AlgoFocus newAlgoFocus(Construction cons1, String[] labels,
			GeoConicND c) {

		if (c.isGeoElement3D()) {
			return new AlgoFocus3D(cons1, labels, c);
		}

		return super.newAlgoFocus(cons1, labels, c);
	}

}
