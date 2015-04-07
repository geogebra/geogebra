package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/*
 * Line[ <GeoPoint3D>, <GeoPoint3D> ] or CmdLine
 */
public class CmdSurfaceCartesian3D extends CmdCurveCartesian {

	public CmdSurfaceCartesian3D(Kernel kernel) {
		super(kernel);

	}

	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];

		switch (n) {
		case 9:
			GeoElement[] arg;
			// create local variables and resolve arguments
			arg = resArgsLocalNumVar(c, new int[] { 3, 6 }, new int[] { 4, 7 });
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (ok[3] = arg[3] instanceof GeoNumeric)
					&& (ok[4] = arg[4] instanceof GeoNumberValue)
					&& (ok[5] = arg[5] instanceof GeoNumberValue)
					&& (ok[6] = arg[6] instanceof GeoNumeric)
					&& (ok[7] = arg[7] instanceof GeoNumberValue)
					&& (ok[8] = arg[8] instanceof GeoNumberValue)) {
				GeoElement[] ret = new GeoElement[1];
				ret[0] = kernelA.getManager3D().SurfaceCartesian3D(
						c.getLabel(), (GeoNumberValue) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
						(GeoNumeric) arg[3], (GeoNumberValue) arg[4],
						(GeoNumberValue) arg[5], (GeoNumeric) arg[6],
						(GeoNumberValue) arg[7], (GeoNumberValue) arg[8]);
				return ret;
			}

			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
