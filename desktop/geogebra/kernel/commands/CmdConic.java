package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoPoint;

/**
 * Conic[ <List> ]
 * Conic[ five GeoPoints ]
 */
class CmdConic extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdConic(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);
		switch (n) {
		case 1:
			if (arg[0].isGeoList())
				return kernel.Conic(c.getLabel(), (GeoList) arg[0]);
		case 5:
			for (int i=0;i<5;i++){
				if (!arg[i].isGeoPoint()){
					throw argErr(app,"Conic",arg[i]);
				}
			}
			GeoPoint[] points = { (GeoPoint) arg[0], (GeoPoint) arg[1],
					(GeoPoint) arg[2], (GeoPoint) arg[3], (GeoPoint) arg[4] };
			GeoElement[] ret = { kernel.Conic(c.getLabel(), points) };
			return ret;
		default:
			if (arg[0].isNumberValue()) {
				// try to create list of numbers
				GeoList list = wrapInList(kernel, arg, arg.length,
						GeoClass.NUMERIC);
				if (list != null) {
					ret = kernel.Conic(c.getLabel(), list);
					return ret;
				}
			}
			throw argNumErr(app, "Conic", n);
		}
	}
}
