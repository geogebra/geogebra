package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.main.MyError;
/**
 * IntersectRegion[<Polygon>, <Polygon> ]
 *
 */
public class CmdIntersectRegion extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIntersectRegion(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[1];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
			case 2:		
				if ((ok[0]=arg[0].isGeoPolygon()) && arg[1].isGeoPolygon() ) {
					GeoElement[] ret =  getAlgoDispatcher().IntersectPolygons(c.getLabels(),
					(GeoPolygon) arg[0], (GeoPolygon)arg[1] ) ;
					return ret;
				}
			throw argErr(app, c.getName(), ok[0]?arg[1]:arg[0]);
			default: 
				throw argNumErr(app, c.getName(), n);
		}
	}
}
