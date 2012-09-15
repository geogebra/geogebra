package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

public class CmdContingencyTable extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdContingencyTable(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 2:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())) {
				AlgoContingencyTable algo = new AlgoContingencyTable(cons,
						c.getLabel(), (GeoList) arg[0], (GeoList) arg[1], null);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 3:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoText())) {
				AlgoContingencyTable algo = new AlgoContingencyTable(cons,
						c.getLabel(), (GeoList) arg[0], (GeoList) arg[1],
						(GeoText) arg[2]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
				
			} else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())) {
				AlgoContingencyTable algo = new AlgoContingencyTable(cons,
						c.getLabel(), (GeoList) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], null);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 4:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())
					&& (ok[3] = arg[3].isGeoText())) {

				AlgoContingencyTable algo = new AlgoContingencyTable(cons,
						c.getLabel(), (GeoList) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], (GeoText) arg[3]);
				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
