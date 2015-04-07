package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdPolyLine;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/*
 * Polygon[ <GeoPoint3D>, <GeoPoint3D>, ... ] or CmdPolygon
 */
public class CmdPolyLine3D extends CmdPolyLine {

	public CmdPolyLine3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	protected GeoElement[] PolyLine(String[] labels, GeoList pointList) {
		for (int i = 0; i < pointList.size(); i++) {
			if (pointList.get(i).isGeoElement3D()) {
				return kernelA.getManager3D().PolyLine3D(labels, pointList);
			}
		}

		return super.PolyLine(labels, pointList);
	}

	@Override
	protected boolean checkIs3D(boolean is3D, GeoElement geo) {
		if (is3D) {
			return true;
		}

		return geo.isGeoElement3D();
	}

	@Override
	protected GeoElement[] PolyLine(String[] labels, GeoPointND[] points,
			boolean penStroke, boolean is3D) {

		if (is3D) {
			return kernelA.getManager3D().PolyLine3D(labels, points);
		}

		return kernelA.PolyLine(labels, points, penStroke);
	}

}
