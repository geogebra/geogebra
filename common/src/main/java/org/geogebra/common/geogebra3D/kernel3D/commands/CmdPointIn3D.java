package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.commands.CmdPointIn;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * PointIn[ &lt;Region&gt; ]
 */
public class CmdPointIn3D extends CmdPointIn {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdPointIn3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	protected GeoElement[] pointIn(String label, Region region) {
		if (region.isRegion3D()) {
			GeoElement[] ret = { (GeoElement) kernel.getManager3D()
					.point3DIn(label, region, false) };
			return ret;
		}

		return super.pointIn(label, region);
	}
}