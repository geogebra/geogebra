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
