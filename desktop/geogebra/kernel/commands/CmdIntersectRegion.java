package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPolygon;
import geogebra.main.MyError;

class CmdIntersectRegion extends CommandProcessor {

	/**
	 * @param kernel
	 */
	public CmdIntersectRegion(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[1];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
			case 2:		
				if ((ok[0]=arg[0].isGeoPolygon()) && arg[1].isGeoPolygon() ) {
					GeoElement[] ret =  kernel.IntersectPolygons(c.getLabels(),
					(GeoPolygon) arg[0], (GeoPolygon)arg[1] ) ;
					return ret;
				} 
				else 
					throw argErr(app, c.getName(), ok[0]?arg[1]:arg[0]);
			default: 
				throw argNumErr(app, c.getName(), n);
		}
	}
}
