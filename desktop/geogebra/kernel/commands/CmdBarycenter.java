package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoList;

class CmdBarycenter extends CommandProcessor 
{

	public CmdBarycenter(Kernel kernel) 
	{
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoList()) &&
					(ok[1] = arg[1].isGeoList())) {
				GeoElement[] ret = { kernel.Barycenter(c.getLabel(),
						(GeoList)arg[0], (GeoList)arg[1])} ;
				return ret;
				
			} else{
				if(!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}
		default:
			throw argNumErr(app, "Barycenter", n);
		}
	}
}
