package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;

/**
 * CircumcircleArc[ <GeoPoint center>, <GeoPoint>, <GeoPoint> ]
 */
public class CmdCircumcircleArc extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCircumcircleArc(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { getArc(c.getLabel(), arg[0], arg[1],
						arg[2]) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * @param label
	 *            label
	 * @param A
	 *            center
	 * @param B
	 *            start point
	 * @param C
	 *            end point
	 * @return arc
	 */
	protected GeoElement getArc(String label, GeoElement A, GeoElement B,
			GeoElement C) {
		return getAlgoDispatcher().CircumcircleArc(label, (GeoPoint) A,
				(GeoPoint) B, (GeoPoint) C);
	}
}
