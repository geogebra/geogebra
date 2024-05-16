package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.CmdIntersectPath;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.MyError;

/**
 * Processor for IntersectConic and IntersectCircle commands
 *
 */
public class CmdIntersectConic extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdIntersectConic(Kernel kernel) {
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

			// between 2 quadrics
			if ((ok[0] = (arg[0] instanceof GeoQuadric3D
					|| arg[0] instanceof GeoQuadric3DLimited))
					&& (ok[1] = (arg[1] instanceof GeoQuadric3D
							|| arg[1] instanceof GeoQuadric3DLimited))) {
				GeoElement[] ret = kernel.getManager3D().intersectAsCircle(
						c.getLabels(), (GeoQuadricND) arg[0],
						(GeoQuadricND) arg[1]);
				return ret;
			}

			// intersection plane/quadric
			GeoElement ret = CmdIntersectPath.processQuadricPlane(kernel, c,
					arg, ok);
			if (ret != null) {
				return new GeoElement[] { ret };
			}

			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}
}