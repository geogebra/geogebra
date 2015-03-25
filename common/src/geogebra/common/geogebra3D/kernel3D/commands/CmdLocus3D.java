package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdLocus;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoPointND;

public class CmdLocus3D extends CmdLocus {

	public CmdLocus3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement locus(String label, GeoPointND p1, GeoPointND p2) {

		if (p1.isGeoElement3D() || p2.isGeoElement3D()) {
			return kernelA.getManager3D().Locus3D(label, p1, p2);
		}

		return super.locus(label, p1, p2);
	}

	protected GeoElement locus(String label, GeoPointND p, GeoNumeric slider) {
		if (p.isGeoElement3D()) {
			return kernelA.getManager3D().Locus3D(label, p, slider);
		}
		return super.locus(label, p, slider);
	}

}
