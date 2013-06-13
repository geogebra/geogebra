package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoSumLeft;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.main.MyError;

/**
 * LeftSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
public class CmdLeftSum extends CommandProcessor {

	/**
	* Create new command processor
	* @param kernel kernel
	*/
	public CmdLeftSum (Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4 :
			arg = resArgs(c);
			if ((ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1]  instanceof GeoNumberValue))
					&& (ok[2] = (arg[2]  instanceof GeoNumberValue))
					&& (ok[3] = (arg[3]  instanceof GeoNumberValue))) {
				AlgoSumLeft algo = new AlgoSumLeft(cons, c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(GeoNumberValue) arg[1],
						(GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3]);

				GeoElement[] ret = { algo.getSum() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok,arg));

		default :
			throw argNumErr(app, c.getName(), n);
		}
	}
}//CmdLeftSum
