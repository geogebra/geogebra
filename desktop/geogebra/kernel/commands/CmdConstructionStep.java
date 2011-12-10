package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/*
 * Defined[ Object ]
 * Michael Borcherds
 * 2008-03-06
 */
public class CmdConstructionStep extends CommandProcessorDesktop {

	public CmdConstructionStep(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 0:

			GeoElement[] ret = { 
					kernel.ConstructionStep(c.getLabel() ) };
			return ret;
	
		case 1:
			GeoElement[] ret3 = { 
					kernel.ConstructionStep(c.getLabel(), arg[0] ) };
			//kernel.Step(c.getLabel() ) };
			return ret3;
	
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
