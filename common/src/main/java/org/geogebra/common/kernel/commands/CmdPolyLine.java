package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Polyline[ &lt;GeoPoint>, ..., &lt;GeoPoint> ]
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
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		switch (n) {
		case 0:
			throw argNumErr(c);
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				return polyLine(c.getLabel(), (GeoList) arg[0]);
			}
			throw argErr(c, arg[0]);
		case 2:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {

				if (!arg[1].isGeoBoolean()) {
					throw argErr(c, arg[1]);
				}

				return polyLine(c.getLabel(), (GeoList) arg[0]);
			}
			throw argErr(c, arg[0]);
		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param label
	 *            label
	 * @param pointList
	 *            input points
	 * @return polyline
	 */
	protected GeoElement[] polyLine(String label, GeoList pointList) {
		AlgoPolyLine algo = new AlgoPolyLine(cons, pointList);
		algo.getOutput(0).setLabel(label);
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
	 * @param is3D
	 *            whether it's a 3D object
	 * @return polyline
	 */
	protected GeoElement[] polyLine(String label, GeoPointND[] points,
			boolean is3D) {
		return kernel.polyLine(label, points);
	}

}
