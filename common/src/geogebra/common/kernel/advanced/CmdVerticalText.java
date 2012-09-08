package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

/**
 *VerticalText
 */
public class CmdVerticalText extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdVerticalText(Kernel kernel) {
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
			if (arg[0].isGeoText()) {
				
				AlgoVerticalText algo = new AlgoVerticalText(cons, c.getLabel(),
						(GeoText) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
			
		case 2:
			
			if ((ok[0] = (arg[0].isGeoText()))
					&& (ok[1] = arg[1].isGeoPoint())) {
				AlgoVerticalText algo = new AlgoVerticalText(cons, c.getLabel(),
						(GeoText) arg[0],  (GeoPoint) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));     

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
