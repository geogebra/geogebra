package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoBoolean;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.Region;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.MyError;

/**
 * IsInRegion[<Point>,<Region>]
 */
class CmdIsInRegion extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIsInRegion(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n != 2)
			throw argNumErr(app, c.getName(), n);
		if (!arg[0].isGeoPoint())
			throw argErr(app, c.getName(), arg[0]);
		if (!arg[1].isRegion())
			throw argErr(app, c.getName(), arg[1]);

		GeoBoolean slider = kernel.isInRegion(c.getLabel(),
				(GeoPointND) arg[0], (Region) arg[1]);
		return new GeoElement[] { slider };
	}
}
