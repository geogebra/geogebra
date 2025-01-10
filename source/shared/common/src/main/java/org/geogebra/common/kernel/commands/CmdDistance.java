package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Distance[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 * 
 * Distance[ &lt;GeoPoint&gt;, &lt;GeoLine&gt; ]
 * 
 * Distance[ &lt;GeoLine&gt;, &lt;GeoPoint&gt; ]
 * 
 * Distance[ &lt;GeoLine&gt;, &lt;GeoLine&gt; ]
 */
public class CmdDistance extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDistance(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// distance between two points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { getAlgoDispatcher().distance(c.getLabel(),
						(GeoPointND) arg[0], (GeoPointND) arg[1]) };
				return ret;
			}

			// distance between point and line
			else if (arg[0].isGeoPoint()) {
				GeoElement[] ret = { getAlgoDispatcher().distance(c.getLabel(),
						(GeoPointND) arg[0], arg[1]) };
				return ret;
			}

			// distance between line and point
			else if (arg[1].isGeoPoint()) {
				GeoElement[] ret = { getAlgoDispatcher().distance(c.getLabel(),
						(GeoPointND) arg[1], arg[0]) };
				return ret;
			}

			// distance between line and line
			else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				GeoElement[] ret = { getAlgoDispatcher().distance(c.getLabel(),
						(GeoLine) arg[0], (GeoLine) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				if (ok[0] && !ok[1]) {
					throw argErr(c, arg[1]);
				}
				throw argErr(c, arg[0]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
