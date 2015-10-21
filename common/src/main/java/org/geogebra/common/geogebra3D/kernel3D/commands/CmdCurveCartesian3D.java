package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoCurveCartesian3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Curve[ x(t),y(t),z(t),t,from,to]
 */
public class CmdCurveCartesian3D extends CmdCurveCartesian {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdCurveCartesian3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];

		if (n == 6) {
			// Curve[ <x-coord expression>, <y-coord expression>, <z-coord
			// expression>, <number-var>, <from>, <to> ]
			// Note: x and y and z coords are numbers dependent on number-var

			// create local variable at position 3 and resolve arguments
			GeoElement[] arg = resArgsLocalNumVar(c, 3, 4);

			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4] instanceof GeoNumberValue)
					&& (ok[5] = arg[5] instanceof GeoNumberValue)) {
				GeoElement[] ret = new GeoElement[1];
				ret[0] = kernelA.getManager3D().CurveCartesian3D(
						(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
						(GeoNumberValue) arg[2], (GeoNumeric) arg[3],
						(GeoNumberValue) arg[4], (GeoNumberValue) arg[5]);
				ret[0].setLabel(c.getLabel());
				return ret;
			}
			for (int i = 0; i < n; i++) {
				if (!ok[i])
					throw argErr(app, c.getName(), arg[i]);
			}

		}

		return super.process(c);
	}

	protected AlgoCurveCartesian getCurveAlgo(NumberValue[] coords,
			GeoElement[] arg) {
		return new AlgoCurveCartesian3D(cons, coords, (GeoNumeric) arg[1],
				(GeoNumberValue) arg[2], (GeoNumberValue) arg[3]);
	}

}
