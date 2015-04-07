package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdSegment;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/*
 * Segment[ <GeoPoint3D>, <GeoPoint3D> ] or CmdSegment
 */
public class CmdSegment3D extends CmdSegment {

	public CmdSegment3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	protected GeoElement segment(String label, GeoPointND a, GeoPointND b) {
		if (a.isGeoElement3D() || b.isGeoElement3D()) {
			return (GeoElement) kernelA.getManager3D().Segment3D(label, a, b);
		}

		return super.segment(label, a, b);
	}

}
