package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdPolyLine;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Polyline[ &lt;GeoPoint3D&gt;, &lt;GeoPoint3D&gt;, ... ] or CmdPolyline
 */
public class CmdPolyLine3D extends CmdPolyLine {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdPolyLine3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] polyLine(String label, GeoList pointList) {
		for (int i = 0; i < pointList.size(); i++) {
			if (pointList.get(i).isGeoElement3D()) {
				return kernel.getManager3D().polyLine3D(label, pointList);
			}
		}

		return super.polyLine(label, pointList);
	}

	@Override
	protected boolean checkIs3D(boolean is3D, GeoElement geo) {
		if (is3D) {
			return true;
		}

		return geo.isGeoElement3D();
	}

	@Override
	protected GeoElement[] polyLine(String label, GeoPointND[] points,
			boolean is3D) {
		if (is3D) {
			return kernel.getManager3D().polyLine3D(label, points);
		}

		return kernel.polyLine(label, points);
	}

}
