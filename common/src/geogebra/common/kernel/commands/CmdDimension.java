package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

public class CmdDimension extends CommandProcessor {

	public CmdDimension(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		switch (n) {
		
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						kernelA.Dimension(c.getLabel(),
								(GeoList) arg[0]) };
				return ret;
			}
			if (arg[0].isGeoPoint()) {
				GeoElement[] ret = { 
						kernelA.Dimension(c.getLabel(),
								(GeoPointND) arg[0]) };
				return ret;
			}
			throw argErr(app,c.getName(),arg[0]);
		default:
			throw argNumErr(app, c.getName(), n);	
		}
	}

}
