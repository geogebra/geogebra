package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;

public class CmdConic3D extends CmdConic {

	public CmdConic3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement Conic(String label, GeoElement[] arg) {

		GeoPointND[] points = new GeoPointND[5];

		boolean is3D = false;
		for (int i = 0; i < 5; i++) {
			points[i] = (GeoPointND) arg[i];
			if (!is3D && points[i].isGeoElement3D()) {
				is3D = true;
			}
		}

		if (is3D) {
			return kernelA.getManager3D().Conic3D(label, points);
		}

		return super.Conic(label, arg);
	}

}
