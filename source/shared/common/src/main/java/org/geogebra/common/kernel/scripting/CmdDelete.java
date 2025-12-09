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

package org.geogebra.common.kernel.scripting;

import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDependentGeoCopy;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.statistics.AlgoCellRange;
import org.geogebra.common.main.MyError;
import org.geogebra.common.spreadsheet.core.SelectionType;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;

/**
 * Delete[ &lt;GeoElement&gt; ]
 */
public class CmdDelete extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDelete(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		switch (n) {
		case 1:
			GeoElement[] arg;
			try {
				arg = resArgs(c);
			} catch (Error e) {
				return new GeoElement[0];
			}

			GeoElement geo = arg[0];

			AlgoElement algoParent = geo.getParentAlgorithm();
			if (algoParent instanceof AlgoDependentGeoCopy) {
				algoParent.getInput(0)
						.removeOrSetUndefinedIfHasFixedDescendent();
			} else if (algoParent instanceof AlgoCellRange) {
				// delete cells
				AlgoCellRange algo = (AlgoCellRange) algoParent;
				SpreadsheetCoords startCoords = GeoElementSpreadsheet
						.getSpreadsheetCoordsForLabel(algo.getStart());
				SpreadsheetCoords endCoords = GeoElementSpreadsheet
						.getSpreadsheetCoordsForLabel(algo.getEnd());
				CopyPasteCut.delete(app, startCoords.column, startCoords.row,
						endCoords.column, endCoords.row, SelectionType.CELLS);

			} else if (geo.isLabelSet()) {
				// delete object
				geo.removeOrSetUndefinedIfHasFixedDescendent();
			}

			return arg;

		default:
			throw argNumErr(c);
		}
	}
}
