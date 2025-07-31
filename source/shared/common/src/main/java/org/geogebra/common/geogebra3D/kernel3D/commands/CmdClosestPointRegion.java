package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * ClosestPointRegion command
 */
public class CmdClosestPointRegion extends CommandProcessor {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdClosestPointRegion(Kernel kernel) {
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

			if ((ok[0] = arg[0].isRegion()) && (ok[1] = arg[1].isGeoPoint())) {
				return new GeoElement[] { (GeoElement) kernel.getManager3D()
						.closestPoint(c.getLabel(), (Region) arg[0],
								(GeoPointND) arg[1]) };
			}

			if ((ok[1] = arg[1].isRegion()) && (ok[0] = arg[0].isGeoPoint())) {
				return new GeoElement[] { (GeoElement) kernel.getManager3D()
						.closestPoint(c.getLabel(), (Region) arg[1],
								(GeoPointND) arg[0]) };
			}

			if (ok[0] && !ok[1]) {
				throw argErr(c, arg[1]);
			}
			throw argErr(c, arg[0]);

		default:
			// return super.process(c);
			throw argNumErr(c);
		}
	}
}