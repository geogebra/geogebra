package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdPolygon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Polygon[ <GeoPoint3D>, <GeoPoint3D>, ... ] or CmdPolygon
 */
public class CmdPolygon3D extends CmdPolygon {

	/**
	 * constructor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolygon3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	protected GeoElement[] process(Command c, int n, GeoElement[] arg)
			throws MyError {

		if (n == 4) {
			// regular polygon with direction
			if (arg[0].isGeoPoint() && arg[1].isGeoPoint()
					&& arg[2] instanceof GeoNumberValue
					&& arg[3] instanceof GeoDirectionND)
				return regularPolygon(c.getLabels(), (GeoPointND) arg[0],
						(GeoPointND) arg[1], (GeoNumberValue) arg[2],
						(GeoDirectionND) arg[3]);
		}

		// use super method
		return super.process(c, n, arg);

	}

	@Override
	protected boolean checkIs3D(boolean is3D, GeoElement geo) {
		if (is3D) {
			return true;
		}

		return geo.isGeoElement3D();
	}

	@Override
	protected GeoElement[] polygon(String[] labels, GeoPointND[] points,
			boolean is3D) {

		// if one point is 3D, use 3D algo
		if (is3D) {
			return kernelA.getManager3D().Polygon3D(labels, points);
		}

		// else use 2D algo
		return super.polygon(labels, points, is3D);
	}

	@Override
	protected GeoElement[] regularPolygon(String[] labels, GeoPointND A,
			GeoPointND B, GeoNumberValue n) {

		if (A.isGeoElement3D() || B.isGeoElement3D()) {
			return regularPolygon(labels, A, B, n, kernelA.getXOYPlane());
		}

		return super.regularPolygon(labels, A, B, n);
	}

	private GeoElement[] regularPolygon(String[] labels, GeoPointND A,
			GeoPointND B, GeoNumberValue n, GeoDirectionND direction) {

		return kernelA.getManager3D()
				.RegularPolygon(labels, A, B, n, direction);

	}
}
