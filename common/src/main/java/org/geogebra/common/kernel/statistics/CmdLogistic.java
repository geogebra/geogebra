package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Logistic Distribution
 */
public class CmdLogistic extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLogistic(Kernel kernel) {
		super(kernel);
	}

	@Override
	@SuppressFBWarnings({ "SF_SWITCH_FALLTHROUGH",
			"missing break is deliberate" })
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		BooleanValue cumulative = null; // default for n=3
		arg = resArgs(c);

		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction()
					|| !((GeoFunction) arg[2]).toString(
							StringTemplate.defaultTemplate).equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}

			if (arg[3].isGeoBoolean()) {
				cumulative = (BooleanValue) arg[3];
			} else
				throw argErr(app, c.getName(), arg[3]);

			// fall through
		case 3:
			if ((ok = arg[0] instanceof GeoNumberValue)
					&& (arg[1] instanceof GeoNumberValue)) {
				if (arg[2].isGeoFunction()
						&& ((GeoFunction) arg[2]).toString(
								StringTemplate.defaultTemplate).equals("x")) {

					AlgoLogisticDF algo = new AlgoLogisticDF(cons,
							c.getLabel(), (GeoNumberValue) arg[0],
							(GeoNumberValue) arg[1], cumulative);
					return algo.getResult().asArray();

				} else if (arg[2] instanceof GeoNumberValue) {
					AlgoLogistic algo = new AlgoLogistic(cons, c.getLabel(),
							(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
							(GeoNumberValue) arg[2]);
					return algo.getResult().asArray();

				} else
					throw argErr(app, c.getName(), arg[2]);
			}
			throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
