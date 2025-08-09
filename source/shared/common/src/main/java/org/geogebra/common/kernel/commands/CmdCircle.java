package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Circle[ &lt;GeoPoint&gt;, &lt;GeoNumeric&gt; ]
 * 
 * Circle[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 * 
 * Circle[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 */
public class CmdCircle extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCircle(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c, info);
			return process2(c, arg, ok);

		case 3:
			// make sure "x=0" in eg Circle((0,0,0), 1, x=0) is always
			// interpreted as a plane not a line (otherwise it depends on which
			// view is active)
			if (c.getArgument(2).unwrap() instanceof Equation) {
				((Equation) c.getArgument(2).unwrap()).setForcePlane();
			}
			arg = resArgs(c, info);
			return process3(c, arg, ok);

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * process when 2 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 * @throws MyError
	 *             arg error
	 */
	protected GeoElement[] process2(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = arg[0].isGeoPoint())
				&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
			GeoElement[] ret = { circle(c.getLabel(), (GeoPointND) arg[0],
					(GeoNumberValue) arg[1]) };
			return ret;
		} else if ((ok[0] = arg[0].isGeoPoint())
				&& (ok[1] = arg[1].isGeoPoint())) {
			GeoElement[] ret = { circle(c.getLabel(), (GeoPointND) arg[0],
					(GeoPointND) arg[1]) };
			return ret;
		} else {
			if (!ok[0]) {
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[1]);
		}

	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param a
	 *            center point
	 * @param v
	 *            radius
	 * @return circle (center, radius)
	 */
	protected GeoElement circle(String label, GeoPointND a, GeoNumberValue v) {
		return getAlgoDispatcher().circle(label, a, v);
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param a
	 *            center point
	 * @param b
	 *            point on circle
	 * @return circle (center, point)
	 */
	protected GeoElement circle(String label, GeoPointND a, GeoPointND b) {
		return getAlgoDispatcher().circle(label, (GeoPoint) a, (GeoPoint) b);
	}

	/**
	 * process when 3 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 * @throws MyError
	 *             arg error
	 */
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = arg[0].isGeoPoint()) && (ok[1] = arg[1].isGeoPoint())
				&& (ok[2] = arg[2].isGeoPoint())) {
			GeoElement[] ret = { circle(c.getLabel(), (GeoPointND) arg[0],
					(GeoPointND) arg[1], (GeoPointND) arg[2]) };
			return ret;
		}
		throw argErr(c, getBadArg(ok, arg));

	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param a
	 *            point on circle
	 * @param b
	 *            point on circle
	 * @param c
	 *            point on circle
	 * @return circle three points
	 */
	protected GeoElement circle(String label, GeoPointND a, GeoPointND b,
			GeoPointND c) {
		return getAlgoDispatcher().circle(label, (GeoPoint) a, (GeoPoint) b,
				(GeoPoint) c);
	}

}