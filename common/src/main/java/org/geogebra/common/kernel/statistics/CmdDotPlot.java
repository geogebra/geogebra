package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * DotPlot[ <List of Numeric> ] G.Sturr 2010-8-10
 */
public class CmdDotPlot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDotPlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { doCommand(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		case 2:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoNumeric())) {
				AlgoDotPlot algo = new AlgoDotPlot(cons, c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]);
				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoBoolean())) {
				AlgoDotPlot algo = new AlgoDotPlot(cons, c.getLabel(),
						(GeoList) arg[0], (GeoBoolean) arg[1], null);
				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		case 3:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoBoolean())
					&& (ok[2] = arg[2].isGeoNumeric())) {
				AlgoDotPlot algo = new AlgoDotPlot(cons, c.getLabel(),
						(GeoList) arg[0], (GeoBoolean) arg[1],
						(GeoNumeric) arg[2]);
				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	final private GeoElement doCommand(String a, GeoList b) {
		AlgoDotPlot algo = new AlgoDotPlot(cons, a, b);
		return algo.getResult();
	}

}
