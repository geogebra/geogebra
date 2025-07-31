package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * Convex polyhedron through points
 *
 */
public class CmdPolyhedronConvex extends CommandProcessor {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdPolyhedronConvex(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {

		int n = c.getArgumentNumber();

		if (n < 4) {
			throw argNumErr(c);
		}

		GeoElement[] arg;

		arg = resArgs(c, info);

		for (int i = 0; i < n; i++) {
			if (!arg[i].isGeoPoint()) {
				throw argErr(c, arg[i]);
			}
		}

		return kernel.getManager3D().polyhedronConvex(c.getLabels(), arg);

	}

}
