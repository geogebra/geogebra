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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;

/**
 * CellRange[ &lt;start cell&gt;, &lt;end cell&gt; ], e.g. CellRange[A1, B2]
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
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		ExpressionNode[] args = c.getArguments();
		switch (n) {
		case 2:
			return new GeoElement[]{ getAlgoCellRange(c).getList() };
		case 3:
			AlgoCellRange algo = getAlgoCellRange(c);
			if (algo.getList().isEmptyList()) {
				algo.getList().setTypeStringForXML(args[2].unwrap()
						.toValueString(StringTemplate.defaultTemplate));
			}
			return new GeoElement[]{ getAlgoCellRange(c).getList() };

		default:
			throw argNumErr(c);
		}
	}

	private AlgoCellRange getAlgoCellRange(Command c) {
		ExpressionNode[] args = c.getArguments();

		// check if we really have two leaves

		String start = spreadsheetLabel(args[0], c);
		String end = spreadsheetLabel(args[1], c);

		// both start and end need to have valid spreadsheet coordinates

		return app.getSpreadsheetTableModel()
				.getCellRangeManager()
				.getAlgoCellRange(cons, c.getLabel(), start, end);
	}

	private String spreadsheetLabel(ExpressionNode expressionNode, Command c) {
		if (expressionNode.getOperation() != Operation.NO_OPERATION
				|| !expressionNode.getLeft().isVariable()) {
			throw argErr(c, expressionNode);
		}
		String cell = ((Variable) expressionNode.getLeft()).getName();
		if (GeoElementSpreadsheet.isSpreadsheetLabel(cell)) {
			return cell;
		}
		throw argErr(c, expressionNode);
	}

}
