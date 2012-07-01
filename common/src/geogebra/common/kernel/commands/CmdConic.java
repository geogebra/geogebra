package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;

/**
 * Conic[ <List> ]
 * Conic[ five GeoPoints ]
 */
public class CmdConic extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdConic(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);
		switch (n) {
		case 1:
			if (arg[0].isGeoList())
				return kernelA.Conic(c.getLabel(), (GeoList) arg[0]);
		case 5:
			for (int i=0;i<5;i++){
				if (!arg[i].isGeoPoint()){
					throw argErr(app,"Conic",arg[i]);
				}
			}
			GeoPoint[] points = { (GeoPoint) arg[0], (GeoPoint) arg[1],
					(GeoPoint) arg[2], (GeoPoint) arg[3], (GeoPoint) arg[4] };
			GeoElement[] ret = { kernelA.Conic(c.getLabel(), points) };
			return ret;
		default:
			if (arg[0].isNumberValue()) {
				// try to create list of numbers
				GeoList list = wrapInList(kernelA, arg, arg.length,
						GeoClass.NUMERIC);
				if (list != null) {
					ret = kernelA.Conic(c.getLabel(), list);
					return ret;
				}
			}
			throw argNumErr(app, c.getName(), n);
		}
	}
}
