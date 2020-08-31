package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoText;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Text[ &lt;text> ]
 */
public class CmdText extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdText(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, true, info);
			AlgoText algo = new AlgoText(cons, c.getLabel(), arg[0]);

			GeoElement[] ret = { algo.getGeoText() };
			return ret;

		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoBoolean()) {
				algo = new AlgoText(cons, c.getLabel(), arg[0],
						(GeoBoolean) arg[1]);

				GeoElement[] ret2 = { algo.getGeoText() };
				return ret2;
			} else if (arg[1].isGeoPoint()) {
				algo = new AlgoText(cons, c.getLabel(), arg[0],
						(GeoPointND) arg[1]);

				GeoElement[] ret2 = { algo.getGeoText() };
				return ret2;
			} else {
				throw argErr(c, arg[1]);
			}

		case 3:
			arg = resArgs(c);
			if ((ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				algo = new AlgoText(cons, c.getLabel(), arg[0],
						(GeoPointND) arg[1], (GeoBoolean) arg[2]);

				GeoElement[] ret2 = { algo.getGeoText() };
				return ret2;
			}
			throw argErr(c, getBadArg(ok, arg));

		case 4:
			arg = resArgs(c);
			if ((ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoBoolean())
					&& (ok[3] = arg[3].isGeoBoolean())) {

				algo = new AlgoText(cons, c.getLabel(), arg[0],
						(GeoPointND) arg[1], (GeoBoolean) arg[2],
						(GeoBoolean) arg[3]);

				GeoElement[] ret2 = { algo.getGeoText() };
				return ret2;
			}
			throw argErr(c, getBadArg(ok, arg));

		case 5:
			arg = resArgs(c);
			if ((ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoBoolean())
					&& (ok[3] = arg[3].isGeoBoolean())
					&& (ok[4] = arg[4].isGeoNumeric())) {

				algo = new AlgoText(cons, c.getLabel(), arg[0],
						(GeoPointND) arg[1], (GeoBoolean) arg[2],
						(GeoBoolean) arg[3], (GeoNumeric) arg[4], null);

				GeoElement[] ret2 = { algo.getGeoText() };
				return ret2;
			}
			throw argErr(c, getBadArg(ok, arg));

		case 6:
			arg = resArgs(c);
			if ((ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoBoolean())
					&& (ok[3] = arg[3].isGeoBoolean())
					&& (ok[4] = arg[4].isGeoNumeric())
					&& (ok[5] = arg[5].isGeoNumeric())) {

				algo = new AlgoText(cons, c.getLabel(), arg[0],
						(GeoPointND) arg[1], (GeoBoolean) arg[2],
						(GeoBoolean) arg[3], (GeoNumeric) arg[4], (GeoNumeric) arg[5]);

				GeoElement[] ret2 = { algo.getGeoText() };
				return ret2;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

}
