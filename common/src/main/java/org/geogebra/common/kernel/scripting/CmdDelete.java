package org.geogebra.common.kernel.scripting;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.statistics.AlgoCellRange;
import org.geogebra.common.main.MyError;

/**
 * Delete[ <GeoElement> ]
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
	protected final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		
		switch (n) {
		case 1:
			try {
				arg = resArgs(c);
			} catch (Error e) {
				return;
			}
			ok[0] = arg[0].isGeoElement();
			if (ok[0]) {
				GeoElement geo = arg[0];
				
				AlgoElement algoParent = geo.getParentAlgorithm();
				if (algoParent != null && algoParent instanceof AlgoCellRange){
					// delete cells
					AlgoCellRange algo = (AlgoCellRange) algoParent;
					GPoint startCoords = GeoElementSpreadsheet
							.getSpreadsheetCoordsForLabel(algo.getStart());
					GPoint endCoords = GeoElementSpreadsheet.getSpreadsheetCoordsForLabel(algo.getEnd());
					CopyPasteCut.delete(app, startCoords.x, startCoords.y, endCoords.x, endCoords.y,
							MyTable.CELL_SELECT);
					
				}else if (geo.isLabelSet()){
					// delete object
					geo.removeOrSetUndefinedIfHasFixedDescendent();
				}

				return;
			}
			throw argErr(app, c.getName(), arg[0]);
			
		

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
