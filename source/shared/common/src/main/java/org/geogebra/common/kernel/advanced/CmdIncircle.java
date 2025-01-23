package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Incircle[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 * 
 * @author dsun
 * @version 2011-6-26
 */
public class CmdIncircle extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIncircle(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {

				AlgoIncircle algo = new AlgoIncircle(cons, (GeoPointND) arg[0],
						(GeoPointND) arg[1], (GeoPointND) arg[2]);
				GeoConicND circle = algo.getCircle();
				circle.setToSpecificForm();
				circle.setLabel(c.getLabel());

				GeoElement[] ret = { circle };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}
} // CmdIncircle
