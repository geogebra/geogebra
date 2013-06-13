package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.cas.AlgoPolynomialDivision;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.main.MyError;

/**
 * Division[number, number]
 * Division[number, polynomial]
 * @author zbynek
 *
 */
public class CmdDivision extends CommandProcessor {

	/**
	 * @param kernel kernel
	 */
	public CmdDivision(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0] instanceof GeoNumberValue))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {
				
				AlgoDivision algo = new AlgoDivision(cons,c.getLabel(),
						(GeoNumberValue) arg[0], (GeoNumberValue) arg[1]);

				GeoElement[] ret = {  algo.getResult() };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoFunction()))
					&& (ok[1] = (arg[1].isGeoFunction()))) {
				
				AlgoPolynomialDivision algo = new AlgoPolynomialDivision(cons,c.getLabel(),
						(GeoFunction) arg[0], (GeoFunction) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
