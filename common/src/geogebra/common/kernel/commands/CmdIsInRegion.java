package geogebra.common.kernel.commands;

import geogebra.common.kernel.Region;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;

/**
 * IsInRegion[<Point>,<Region>]
 */
public class CmdIsInRegion extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIsInRegion(AbstractKernel kernel) {
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

		GeoBoolean slider = kernelA.isInRegion(c.getLabel(),
				(GeoPointND) arg[0], (Region) arg[1]);
		return new GeoElement[] { slider };
	}
}
