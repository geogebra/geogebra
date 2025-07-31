package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.MyError;

/**
 * PlaneBisector[ &lt;GeoPoint3D&gt;, &lt;GeoCoordSys&gt; ]
 */
public class CmdPlaneBisector extends CommandProcessor {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdPlaneBisector(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);
			if (arg[0] instanceof GeoSegmentND) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D()
						.planeBisector(c.getLabel(), (GeoSegmentND) arg[0]) };
				return ret;
			}
			throw argErr(c, arg[0]);

		case 2:
			arg = resArgs(c, info);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D()
						.planeBisector(c.getLabel(), (GeoPointND) arg[0],
								(GeoPointND) arg[1]) };
				return ret;
			}
			if (!ok[0]) {
				throw argErr(c, arg[0]);
			}

			throw argErr(c, arg[1]);

		default:
			throw argNumErr(c);
		}

	}

}
