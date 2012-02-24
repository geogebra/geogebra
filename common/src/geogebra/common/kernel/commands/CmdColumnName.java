package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

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

			if (arg[0].getLabelSimple() != null
					&& GeoElementSpreadsheet.isSpreadsheetLabel(arg[0].getLabelSimple()))
{
				GeoElement[] ret = { kernelA.ColumnName(c.getLabel(), arg[0]) };

				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
