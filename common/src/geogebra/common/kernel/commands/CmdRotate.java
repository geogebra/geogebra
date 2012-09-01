package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Transform;
import geogebra.common.kernel.TransformRotate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;

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

				ret = Rotate(label, arg[0], phi);
				return ret;
			}

			throw argErr(app, c.getName(), getBadArg(ok,arg));
			

		case 3:
			// ROTATION AROUND POINT
			arg = resArgs(c);

			// rotate point, line or conic
			if ((ok[0] = true) && (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {

				NumberValue phi = (NumberValue) arg[1];
				GeoPoint Q = (GeoPoint) arg[2];

				ret = kernelA.Rotate(label, arg[0], phi, Q);
				return ret;
			}

			// rotate polygon

			throw argErr(app, c.getName(), getBadArg(ok,arg));
			

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * rotate geoRot by angle phi around (0,0)
	 */
	final private GeoElement[] Rotate(String label, GeoElement geoRot,
			NumberValue phi) {
		Transform t = new TransformRotate(cons, phi);
		return t.transform(geoRot, label);
	}


}
