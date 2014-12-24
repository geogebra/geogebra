package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Variable;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.MyError;
import geogebra.common.plugin.Operation;

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

		switch (n) {
		case 2:

			ExpressionNode[] args = c.getArguments();

			// check if we really have two leafs
			if (args[0].getOperation() == Operation.NO_OPERATION
					&& args[0].getLeft().isVariable()
					&& args[0].getOperation() == Operation.NO_OPERATION
					&& args[0].getLeft().isVariable()) {

				String start = ((Variable) args[0].getLeft()).getName();
				String end = ((Variable) args[1].getLeft()).getName();

				// both start and end need to have valid spreadsheet coordinates
				if (GeoElementSpreadsheet.isSpreadsheetLabel(start)
						&& GeoElementSpreadsheet.isSpreadsheetLabel(end)) {
					AlgoCellRange algo = app.getSpreadsheetTableModel()
							.getCellRangeManager()
							.getAlgoCellRange(cons, c.getLabel(), start, end);
					GeoElement[] ret = { algo.getList() };
					return ret;

				}

			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
