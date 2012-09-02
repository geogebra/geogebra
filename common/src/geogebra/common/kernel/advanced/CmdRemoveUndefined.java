package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoRemoveUndefined;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * Clean[ <List> ]
 * @author Michael Borcherds
 * @version 2008-03-06
 */
public class CmdRemoveUndefined extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdRemoveUndefined(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

			if (arg[0].isGeoList()) {
				
				AlgoRemoveUndefined algo = new AlgoRemoveUndefined(cons, c.getLabel(),
						(GeoList) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
