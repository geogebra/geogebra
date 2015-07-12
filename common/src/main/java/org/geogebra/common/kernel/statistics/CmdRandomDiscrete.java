package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

public class CmdRandomDiscrete extends CommandProcessor {

	public CmdRandomDiscrete(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);

		if (n != 2)
			throw argNumErr(app, c.getName(), n);

		if (arg[0].isGeoList() && arg[1].isGeoList()) {
			AlgoRandomDiscrete algo = new AlgoRandomDiscrete(cons,
					c.getLabel(), (GeoList) arg[0], (GeoList) arg[1]);

			GeoElement[] ret = { algo.getResult() };
			return ret;
		} else
			return null;
	}

}
