package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdParabola;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class CmdParabola3D extends CmdParabola {

	public CmdParabola3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement parabola(String label, GeoPointND a, GeoLineND d) {
		if (a.isGeoElement3D() || d.isGeoElement3D()) {
			return kernelA.getManager3D().Parabola3D(label, a, d);
		}

		return super.parabola(label, a, d);
	}

}
