package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 * Rotate[ <GeoPoint>, <NumberValue> ] Rotate[ <GeoVector>, <NumberValue> ]
 * Rotate[ <GeoLine>, <NumberValue> ] Rotate[ <GeoConic>, <NumberValue> ]
 * Rotate[ <GeoPolygon>, <NumberValue> ]
 * 
 * Rotate[ <GeoPoint>, <NumberValue>, <GeoPoint> ] Rotate[ <GeoLine>,
 * <NumberValue>, <GeoPoint> ] Rotate[ <GeoConic>, <NumberValue>, <GeoPoint> ]
 * Rotate[ <GeoPolygon>, <NumberValue>, <GeoPoint> ]
 */
public class CmdRotate extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRotate(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 2:
			// ROTATE AROUND CENTER (0,0)
			arg = resArgs(c);

			// rotate point, line or conic
			if ((ok[0] = true) && (ok[1] = (arg[1].isNumberValue()))) {
				NumberValue phi = (NumberValue) arg[1];

				ret = kernelA.Rotate(label, arg[0], phi);
				return ret;
			}

			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		case 3:
			// ROTATION AROUND POINT
			arg = resArgs(c);

			// rotate point, line or conic
			if ((ok[0] = true) && (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {

				NumberValue phi = (NumberValue) arg[1];
				GeoPoint2 Q = (GeoPoint2) arg[2];

				ret = kernelA.Rotate(label, arg[0], phi, Q);
				return ret;
			}

			// rotate polygon

			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
