package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.kernel.statistics.AlgoColumnName;
import geogebra.common.main.MyError;

/**
 *ColumnName
 */
public class CmdColumnName extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdColumnName(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			if (GeoElementSpreadsheet.hasSpreadsheetLabel(arg[0]))
			{
				AlgoColumnName algo = new AlgoColumnName(cons, c.getLabel(), arg[0]);
				GeoElement[] ret = { algo.getGeoText() };

				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
