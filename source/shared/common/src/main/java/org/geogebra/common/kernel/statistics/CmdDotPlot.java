package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * DotPlot[ &lt;List of Numeric&gt; ]
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
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = {
						doCommand(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			}
			throw argErr(c, arg[0]);

		case 2:
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoNumeric())) {
				AlgoDotPlot algo = new AlgoDotPlot(cons, c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]);
				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoBoolean())) {
				AlgoDotPlot algo = new AlgoDotPlot(cons,
						(GeoList) arg[0], (GeoBoolean) arg[1], null);
				algo.getResult().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}

		case 3:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoBoolean())
					&& (ok[2] = arg[2].isGeoNumeric())) {
				AlgoDotPlot algo = new AlgoDotPlot(cons,
						(GeoList) arg[0], (GeoBoolean) arg[1],
						(GeoNumeric) arg[2]);
				algo.getResult().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else {
				throw argErr(c, arg[2]);
			}

		default:
			throw argNumErr(c);
		}
	}

	final private GeoElement doCommand(String a, GeoList b) {
		AlgoDotPlot algo = new AlgoDotPlot(cons, b);
		algo.getResult().setLabel(a);
		return algo.getResult();
	}

}
