package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 *FillColumn
 */
public class CmdFillColumn extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFillColumn(Kernel kernel) {
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
			if ((ok[0] = (arg[0].isGeoNumeric()))
					&& (ok[1] = (arg[1].isGeoList()))) {

				int col = -1 + (int) ((GeoNumeric) arg[0]).getDouble();

				if (col < 0 || col > Kernel.MAX_SPREADSHEET_COLUMNS_DESKTOP)
					throw argErr(app, c.getName(), arg[0]);

				GeoList list = (GeoList) arg[1];
				GeoElement[] ret = { list };

				if (list.size() == 0)
					return ret;

				for (int row = 0; row < list.size(); row++) {
					GeoElement cellGeo = list.get(row).copy();

					try {
						kernelA.getGeoElementSpreadsheet().setSpreadsheetCell(app, row, col, cellGeo);
					} catch (Exception e) {
						e.printStackTrace();
						throw argErr(app, c.getName(), arg[1]);
					}
				}

				app.storeUndoInfo();
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
