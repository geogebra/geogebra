package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolarLine3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolarPoint3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.CmdPolar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class CmdPolar3D extends CmdPolar {

	public CmdPolar3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement PolarLine(String label, GeoPointND P, GeoConicND c) {

		if (P.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoPolarLine3D algo = new AlgoPolarLine3D(cons, label, c, P);
			return (GeoElement) algo.getLine();
		}

		return super.PolarLine(label, P, c);
	}

	@Override
	protected GeoElement PolarPoint(String label, GeoLineND line, GeoConicND c) {

		if (line.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoPolarPoint3D algo = new AlgoPolarPoint3D(cons, label, c, line);
			return (GeoElement) algo.getPoint();
		}

		return super.PolarPoint(label, line, c);
	}

}
