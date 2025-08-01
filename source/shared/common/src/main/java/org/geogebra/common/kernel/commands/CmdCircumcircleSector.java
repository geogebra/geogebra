package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;

/**
 * CircumcircleSector[ &lt;GeoPoint center&gt;, &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 */
public class CmdCircumcircleSector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCircumcircleSector(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c, info);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = {
						getSector(c.getLabel(), arg[0], arg[1], arg[2]) };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
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
	protected GeoElement getSector(String label, GeoElement A, GeoElement B,
			GeoElement C) {
		return getAlgoDispatcher().circumcircleSector(label, (GeoPoint) A,
				(GeoPoint) B, (GeoPoint) C);
	}
}
