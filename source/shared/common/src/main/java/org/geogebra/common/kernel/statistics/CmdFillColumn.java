package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.util.debug.Log;

/**
 * FillColumn
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
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		if (!info.isScripting()) {
			return new GeoElement[0];
		}
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c, info);
			if ((ok[0] = (arg[0].isGeoNumeric()))
					&& (ok[1] = (arg[1].isGeoList()))) {

				int col = -1 + (int) ((GeoNumeric) arg[0]).getDouble();

				if (col < 0 || col > Spreadsheet.MAX_COLUMNS) {
					throw argErr(c, arg[0]);
				}

				GeoList list = (GeoList) arg[1];
				GeoElement[] ret = { list };

				if (list.size() == 0) {
					return ret;
				}

				for (int row = 0; row < list.size(); row++) {
					GeoElement cellGeo = list.get(row).copy();

					try {
						kernel.getGeoElementSpreadsheet()
								.setSpreadsheetCell(app, row, col, cellGeo);
					} catch (Exception e) {
						Log.debug(e);
						throw argErr(c, arg[1]);
					}
				}

				app.storeUndoInfo();
				return ret;

			} else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
