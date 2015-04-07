package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Ellipse[ <GeoPoint>, <GeoPoint>, <NumberValue> ]
 */
public class CmdEllipseHyperbola extends CommandProcessor {

	final protected int type; // ellipse or hyperbola

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 * @param type
	 *            ellipse/hyperbola
	 */
	public CmdEllipseHyperbola(Kernel kernel, final int type) {
		super(kernel);
		this.type = type;
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
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
				return new GeoElement[] { ellipseHyperbola(c.getLabel(),
						(GeoPointND) arg[0], (GeoPointND) arg[1],
						(GeoNumberValue) arg[2]) };
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { ellipse(c.getLabel(), (GeoPointND) arg[0],
						(GeoPointND) arg[1], (GeoPointND) arg[2]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), getBadArg(ok, arg));
			}

		case 4:
			arg = resArgs(c);

			GeoElement[] ret = process4(c, arg, ok);

			if (ret != null) {
				return ret;
			}

			// syntax error
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * @param label
	 *            label
	 * @param a
	 *            first focus
	 * @param b
	 *            second focus
	 * @param c
	 *            point on ellipse
	 * @return ellipse
	 */
	protected GeoElement ellipse(String label, GeoPointND a, GeoPointND b,
			GeoPointND c) {
		return getAlgoDispatcher().EllipseHyperbola(label, a, b, c, type);
	}

	/**
	 * process when 4 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 * @throws MyError
	 *             in 2D, not possible with 4 args
	 */
	protected GeoElement[] process4(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {
		throw argNumErr(app, c.getName(), 4);
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param a
	 *            first focus
	 * @param b
	 *            second focus
	 * @param v
	 *            value
	 * @return ellipse/hypebola
	 */
	protected GeoElement ellipseHyperbola(String label, GeoPointND a,
			GeoPointND b, GeoNumberValue v) {
		if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			return getAlgoDispatcher().Hyperbola(label, a, b, v);
		}
		return getAlgoDispatcher().Ellipse(label, a, b, v);
	}
}
