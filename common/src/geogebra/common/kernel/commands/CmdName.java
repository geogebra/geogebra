package geogebra.common.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/*
 * Name[ <GeoElement> ]
 */
public class CmdName extends CommandProcessor {

	public CmdName(AbstractKernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			// Name[ <GeoElement> ]
			arg = resArgs(c);			
			GeoElement[] ret = { kernelA.Name(c.getLabel(),
								arg[0]) };
			return ret;


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
