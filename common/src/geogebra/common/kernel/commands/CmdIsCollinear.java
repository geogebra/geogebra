package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;

public class CmdIsCollinear extends CommandProcessor {

	public CmdIsCollinear(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int n=c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n==3) {
			if (arg[0] instanceof GeoPoint2 && arg[1] instanceof GeoPoint2 && arg[2] instanceof GeoPoint2){
			GeoElement[] ret = {kernelA.IsCollinear(c.getLabel(), (GeoPoint2) arg[0],(GeoPoint2) arg[1],(GeoPoint2) arg[2])};
			return ret;
			}
		}
		throw argNumErr(app, c.getName(), n);
		
	}

}
