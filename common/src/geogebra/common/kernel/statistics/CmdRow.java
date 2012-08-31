package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoRow;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.MyError;

/**
 * Row[ <GeoElement> ]
 */
public class CmdRow extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdRow(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			// Name[ <GeoElement> ]
			arg = resArgs(c);			
			if (GeoElementSpreadsheet.hasSpreadsheetLabel(arg[0])) {
				AlgoRow algo = new AlgoRow(cons, c.getLabel(),
						arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
