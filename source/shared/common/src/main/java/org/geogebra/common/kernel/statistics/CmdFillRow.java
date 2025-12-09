/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
 * FillRow
 */
public class CmdFillRow extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFillRow(Kernel kernel) {
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
			if ((ok[0] = arg[0].isGeoNumeric())
					&& (ok[1] = arg[1].isGeoList())) {

				int row = -1 + (int) ((GeoNumeric) arg[0]).getDouble();

				if (row < 0 || row > Spreadsheet.MAX_ROWS) {
					throw argErr(c, arg[0]);
				}

				GeoList list = (GeoList) arg[1];

				GeoElement[] ret = { list };

				if (list.size() == 0) {
					return ret;
				}

				for (int col = 0; col < list.size(); col++) {

					GeoElement cellGeo = list.get(col).copy();

					try {
						kernel.getGeoElementSpreadsheet()
								.setSpreadsheetCell(app, row, col, cellGeo);
					} catch (Exception e) {
						Log.debug(e);
						throw argErr(c.getName(), arg[1], e);
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
