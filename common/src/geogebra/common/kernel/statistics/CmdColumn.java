package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.MyError;

/**
 * Column[ <GeoElement> ]
 */
public class CmdColumn extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdColumn(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);			
			if (GeoElementSpreadsheet.hasSpreadsheetLabel(arg[0])) {
				
				AlgoColumn algo = new AlgoColumn(cons, c.getLabel(),
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
