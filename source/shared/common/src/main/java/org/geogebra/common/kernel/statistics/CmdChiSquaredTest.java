package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * ChiSquaredTest of independence and goodness of fit
 */
public class CmdChiSquaredTest extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdChiSquaredTest(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			if (arg[0].isGeoList() && ((GeoList) arg[0]).isMatrix()) {
				AlgoChiSquaredTest algo = new AlgoChiSquaredTest(cons,
						(GeoList) arg[0], null, null);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[]{ algo.getResult() };
			}
			throw argErr(c, arg[0]);
		case 2:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())) {

				AlgoChiSquaredTest algo = new AlgoChiSquaredTest(cons,
						(GeoList) arg[0], (GeoList) arg[1], null);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[]{ algo.getResult() };

			} else {
				throw argErr(c, getBadArg(ok, arg));
			}
		case 3:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isNumberValue())) {

				AlgoChiSquaredTest algo = new AlgoChiSquaredTest(cons,
						(GeoList) arg[0], (GeoList) arg[1], (GeoNumberValue) arg[2]);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[]{ algo.getResult() };

			} else {
				throw argErr(c, getBadArg(ok, arg));
			}
		default:
			throw argNumErr(c);
		}
	}
}
