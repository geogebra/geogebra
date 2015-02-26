package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

/**
 * 
 */
public class CmdClosestPointRegion extends CommandProcessor {

	public CmdClosestPointRegion(Kernel kernel) {
		super(kernel);

	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			if ((ok[0] = arg[0].isRegion()) && (ok[1] = arg[1].isGeoPoint())) {
				return new GeoElement[] { (GeoElement) kernelA.getManager3D()
						.ClosestPoint(c.getLabel(), (Region) arg[0],
								(GeoPointND) arg[1]) };
			}

			if ((ok[1] = arg[1].isRegion()) && (ok[0] = arg[0].isGeoPoint())) {
				return new GeoElement[] { (GeoElement) kernelA.getManager3D()
						.ClosestPoint(c.getLabel(), (Region) arg[1],
								(GeoPointND) arg[0]) };
			}

			if (ok[0] && !ok[1])
				throw argErr(app, c.getName(), arg[1]);
			throw argErr(app, c.getName(), arg[0]);

		default:
			// return super.process(c);
			throw argNumErr(app, c.getName(), n);
		}
	}
}