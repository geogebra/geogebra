package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Polygon[ <GeoPoint>, ..., <GeoPoint> ] Polygon[ <GeoPoint>, <GeoPoint>,
 * <Number>] for regular polygon
 */
public class CmdPolygon extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolygon(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		arg = resArgs(c);

		return process(c, n, arg);
	}

	/**
	 * 
	 * @param c
	 *            command to process
	 * @param n
	 *            number of args
	 * @param arg
	 *            args already resolved
	 * @return list of resulting geos
	 * @throws MyError
	 *             error if problem occurs
	 */
	protected GeoElement[] process(Command c, int n, GeoElement[] arg)
			throws MyError {

		switch (n) {
		case 0:
			throw argNumErr(app, c.getName(), n);
			// G.Sturr 2010-3-14
		case 1:
			if (arg[0].isGeoList())
				return getAlgoDispatcher().Polygon(c.getLabels(),
						(GeoList) arg[0]);
			// END G.Sturr

		case 3:
			// regular polygon
			if (arg[0].isGeoPoint() && arg[1].isGeoPoint()
					&& arg[2] instanceof GeoNumberValue)
				return regularPolygon(c.getLabels(), (GeoPointND) arg[0],
						(GeoPointND) arg[1], (GeoNumberValue) arg[2]);

		default:
			// polygon for given points
			GeoPointND[] points = new GeoPointND[n];
			// check arguments
			boolean is3D = false;
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				points[i] = (GeoPointND) arg[i];
				is3D = checkIs3D(is3D, arg[i]);
			}
			// everything ok
			return polygon(c.getLabels(), points, is3D);
		}
	}

	/**
	 * 
	 * @param is3D
	 *            true if already 3D
	 * @param geo
	 *            geo to check
	 * @return true if is already 3D or geo is 3D
	 */
	protected boolean checkIs3D(boolean is3D, GeoElement geo) {
		return false; // check only in 3D mode
	}

	/**
	 * 
	 * @param labels
	 * @param points
	 * @param is3D
	 *            if in 3D mode
	 * @return polygon for points
	 */
	protected GeoElement[] polygon(String[] labels, GeoPointND[] points,
			boolean is3D) {
		return kernelA.Polygon(labels, points);
	}

	/**
	 * 
	 * @param labels
	 * @param A
	 * @param B
	 * @param n
	 * @return regular polygon
	 */
	protected GeoElement[] regularPolygon(String[] labels, GeoPointND A,
			GeoPointND B, GeoNumberValue n) {
		return getAlgoDispatcher().RegularPolygon(labels, A, B, n);
	}
}
