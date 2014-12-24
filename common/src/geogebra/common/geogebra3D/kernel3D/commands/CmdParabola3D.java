package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdParabola;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;

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
