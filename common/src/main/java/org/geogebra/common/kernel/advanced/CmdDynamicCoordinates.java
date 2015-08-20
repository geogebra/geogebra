package org.geogebra.common.kernel.advanced;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
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
	final public GeoElement[] process(Command c) throws MyError {
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

				GeoElement[] ret = { algo.getPoint() };
				return ret;
			} else if (!ok[0]) {
				throw argErr(app, c.getName(), arg[0]);
			} else if (!ok[1]) {
				throw argErr(app, c.getName(), arg[1]);
			} else {
				throw argErr(app, c.getName(), arg[2]);
			}

		case 4:
			ok = new boolean[3];
			if ((ok[0] = (arg[0] instanceof GeoPoint3D && arg[0].isMoveable()))
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (arg[3] instanceof GeoNumberValue)) {

				AlgoDynamicCoordinates3D algo = new AlgoDynamicCoordinates3D(
						cons, c.getLabel(), (GeoPoint3D) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3]);

				GeoElement[] ret = { (GeoElement) algo.getPoint() };
				return ret;
			} else if (!ok[0]) {
				throw argErr(app, c.getName(), arg[0]);
			} else if (!ok[1]) {
				throw argErr(app, c.getName(), arg[1]);
			} else if (!ok[2]) {
				throw argErr(app, c.getName(), arg[2]);
			} else {
				throw argErr(app, c.getName(), arg[3]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
