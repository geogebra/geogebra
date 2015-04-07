package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

public class CmdPolyhedronConvex extends CommandProcessor {

	public CmdPolyhedronConvex(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();

		if (n < 4) {
			throw argNumErr(app, c.getName(), n);
		}

		GeoElement[] arg;

		arg = resArgs(c);

		for (int i = 0; i < n; i++) {
			if (!arg[i].isGeoPoint()) {
				throw argErr(app, c.getName(), arg[i]);
			}
		}

		return kernelA.getManager3D().PolyhedronConvex(c.getLabels(), arg);

	}

}
