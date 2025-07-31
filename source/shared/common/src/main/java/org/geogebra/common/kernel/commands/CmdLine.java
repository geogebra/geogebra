package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.Lineable2D;
import org.geogebra.common.main.MyError;

/**
 * Line[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 * 
 * Line[ &lt;GeoPoint&gt;, &lt;GeoVector&gt; ]
 * 
 * Line[ &lt;GeoPoint&gt;, &lt;GeoLine&gt; ]
 */
public class CmdLine extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLine(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		if (n == 2) {
			arg = resArgs(c, info);

			// line through two points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { getAlgoDispatcher().line(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1]) };
				return ret;
			}

			// line through point with direction vector
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoVector()))) {
				GeoElement[] ret = { getAlgoDispatcher().line(c.getLabel(),
						(GeoPoint) arg[0], (GeoVector) arg[1]) };
				return ret;
			}

			// line through point parallel to another line
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1] instanceof Lineable2D))) {
				GeoElement[] ret = { getAlgoDispatcher().line(c.getLabel(),
						(GeoPoint) arg[0], (Lineable2D) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				throw argErr(c, getBadArg(ok, arg));
			}
		}
		
		throw argNumErr(c);
	}

}
