package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/*
 * Name[ <GeoElement> ]
 */
public class CmdLaTeX extends CommandProcessor {

	public CmdLaTeX(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			
			arg = resArgs(c);	

				GeoElement[] ret = { kernelA.LaTeX(c.getLabel(),
									arg[0]) };
				return ret;

		case 2:
			
			arg = resArgs(c);	
			if (arg[1].isGeoBoolean()) {
				GeoElement[] ret2 = { kernelA.LaTeX(c.getLabel(),
									arg[0], (GeoBoolean)arg[1], null) };
				return ret2;
			}
			else
           	 	throw argErr(app, c.getName(), arg[1]);   
			
		case 3:
			
			arg = resArgs(c);	
			if (arg[1].isGeoBoolean() && arg[2].isGeoBoolean()) {
				GeoElement[] ret2 = { kernelA.LaTeX(c.getLabel(),
									arg[0], (GeoBoolean)arg[1], (GeoBoolean)arg[2]) };
				return ret2;
			}
			
			else if (!arg[1].isGeoBoolean())
				throw argErr(app, c.getName(), arg[1]);
			else 
				throw argErr(app, c.getName(), arg[2]);


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
