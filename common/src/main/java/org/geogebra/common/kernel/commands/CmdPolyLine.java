package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Polyline[ <GeoPoint>, ..., <GeoPoint> ]
 */
public class CmdPolyLine extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolyLine(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		arg = resArgs(c);
		switch (n) {
		case 0:
			throw argNumErr(app, c.getName(), n);
		case 1:
			if (arg[0].isGeoList())
				return polyLine(c.getLabel(), (GeoList) arg[0]);
			throw argErr(app, c, arg[0]);

		case 2:
			if (arg[0].isGeoList()) {

				if (!arg[1].isGeoBoolean()) {
					throw argErr(app, c, arg[1]);
				}

				return polyLine(c.getLabel(), (GeoList) arg[0]);
			}
			if (arg[0].isGeoPoint()) {

				if (!arg[1].isGeoPoint()) {
					throw argErr(app, c, arg[1]);
				}

				return genericPolyline(arg, c);
			}
			throw argErr(app, c, arg[0]);
		default:

			return genericPolyline(arg, c);

		}
	}

	private GeoElement[] genericPolyline(GeoElement[] arg, Command c) {
		int size = arg.length;
		boolean penStroke = false;

		if (arg[arg.length - 1].isGeoBoolean()) {
			// pen stroke
			// last argument is boolean (normally true)
			size = size - 1;
			penStroke = ((GeoBoolean) arg[arg.length - 1]).getBoolean();
		}

		// polygon for given points
		GeoPointND[] points = new GeoPointND[size];
		// check arguments
		boolean is3D = false;
		for (int i = 0; i < size; i++) {
			if (!(arg[i].isGeoPoint()))
				throw argErr(app, c, arg[i]);
			points[i] = (GeoPointND) arg[i];
			is3D = checkIs3D(is3D, arg[i]);
		}
		// everything ok
		return polyLine(c.getLabel(), points, penStroke, is3D);
	}

	/**
	 * @param label
	 *            label
	 * @param pointList
	 *            input points
	 * @return polyline
	 */
	protected GeoElement[] polyLine(String label, GeoList pointList) {
		AlgoPolyLine algo = new AlgoPolyLine(cons, label, pointList);
		return algo.getOutput();
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
	 * @param label
	 *            output label
	 * @param points
	 *            polyline
	 * @param penStroke
	 *            whether it's a pen stroke
	 * @param is3D
	 *            whether it's a 3D object
	 * @return polyline
	 */
	protected GeoElement[] polyLine(String label, GeoPointND[] points,
			boolean penStroke, boolean is3D) {
		return kernelA.polyLine(label, points, penStroke);
	}

}
