package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/*
 * Percentile[ <List>, <Value> ]
 * G. Sturr
 */
public class CmdPercentile extends CommandProcessor {

	public CmdPercentile(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		boolean[] ok = new boolean[n];
		arg = resArgs(c);

		switch (n) {

		case 2:
			if ((ok[0] = arg[0].isGeoList()) 
					&& (ok[1] = arg[1].isGeoNumeric())) {
				GeoElement[] ret = 
				{ kernel.Percentile(c.getLabel(), (GeoList) arg[0], (GeoNumeric) arg[1] ) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
