package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * DynamicCoordinates
 */
public class CmdDynamicCoordinates extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDynamicCoordinates(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 3:
			boolean[] ok = new boolean[2];
			if ((ok[0] = (arg[0] instanceof GeoPoint && arg[0].isMoveable()))
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (arg[2] instanceof GeoNumberValue)) {

				AlgoDynamicCoordinates algo = new AlgoDynamicCoordinates(cons,
						c.getLabel(), (GeoPoint) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2]);

				GeoElement[] ret = { algo.getPoint().toGeoElement() };
				return ret;
			} else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else {
				throw argErr(c, arg[2]);
			}

		case 4:
			ok = new boolean[3];
			if ((ok[0] = (arg[0] instanceof GeoPointND && arg[0].isMoveable()))
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (arg[3] instanceof GeoNumberValue)) {

				AlgoDynamicCoordinates3D algo = new AlgoDynamicCoordinates3D(
						cons, c.getLabel(), (GeoPointND) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3]);

				GeoElement[] ret = { (GeoElement) algo.getPoint() };
				return ret;
			} else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else if (!ok[2]) {
				throw argErr(c, arg[2]);
			} else {
				throw argErr(c, arg[3]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
