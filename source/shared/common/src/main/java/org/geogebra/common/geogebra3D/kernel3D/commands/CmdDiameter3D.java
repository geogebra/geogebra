package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdDiameter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Diameter line
 *
 */
public class CmdDiameter3D extends CmdDiameter {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdDiameter3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement diameter(String label, GeoLineND l, GeoConicND c) {
		return kernel.getManager3D().diameterLine3D(label, l, c);
	}

	@Override
	protected GeoElement diameter(String label, GeoVectorND v, GeoConicND c) {
		return kernel.getManager3D().diameterLine3D(label, v, c);
	}
}
