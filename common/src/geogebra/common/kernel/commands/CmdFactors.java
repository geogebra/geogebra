package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoPrimeFactorization;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.cas.AlgoFactors;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 *Factors[ &lt;Function> ]
 *Factors[ &lt;Number> ]
 */
public class CmdFactors extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFactors(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (arg[0].isGeoFunction()) {

				AlgoFactors algo = new AlgoFactors(cons, c.getLabel(),
						(GeoFunction) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else	if (arg[0].isGeoNumeric()) {

				AlgoPrimeFactorization algo = new AlgoPrimeFactorization(cons, c.getLabel(),
						(GeoNumeric) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
