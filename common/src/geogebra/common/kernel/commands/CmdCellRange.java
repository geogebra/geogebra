package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.MyError;

/**
 * CellRange[ <start cell>, <end cell> ], e.g. CellRange[A1, B2]
 */
public class CmdCellRange extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCellRange(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:						
			arg = resArgs(c);
			// both geos need to have valid spreadsheet coordinates
			if ((ok[0] = GeoElementSpreadsheet.hasSpreadsheetLabel(arg[0])) &&
				(ok[1] = GeoElementSpreadsheet.hasSpreadsheetLabel(arg[1]))) 
			{
				GeoElement[] ret = { 
						kernelA.CellRange(c.getLabel(), arg[0], arg[1]) };
				return ret;
				
			}  
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else 
				throw argErr(app, c.getName(), arg[1]);
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
