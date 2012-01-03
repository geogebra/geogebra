package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 * Minimize[ <dependent variable>, <independent variable> ]
 */
public class CmdMinimize extends CommandProcessor {

	public CmdMinimize(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoElement()))
                && (ok[1] = (arg[1] .isGeoNumeric()))) {
            	GeoElement[] ret=new GeoElement[1];
                ret[0] =
                         kernelA.Minimize(
                            c.getLabel(),
                            (GeoElement) arg[0],
                            (GeoNumeric) arg[1]);

                return ret;
            }

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}//process(command)

}//CmdMinimze(kernel)

