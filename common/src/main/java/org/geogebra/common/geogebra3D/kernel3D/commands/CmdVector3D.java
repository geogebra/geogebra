package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdVector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/*
 * Vector[ <GeoPoint3D>, <GeoPoint3D> ] or CmdVector
 */
public class CmdVector3D extends CmdVector {

	public CmdVector3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	protected GeoElement vector(String label, GeoPointND p0, GeoPointND p1) {
		if (p0.isGeoElement3D() || p1.isGeoElement3D()) {
			return kernelA.getManager3D().Vector3D(label, p0, p1);
		}

		return super.vector(label, p0, p1);
	}

}
