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

package org.geogebra.common.cas.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.debug.Log;

/**
 * Common class for substitution dialogs
 * 
 * @author balazs.bencze
 *
 */
public abstract class CASSubDialog {
	/** Editing row from CAS table */
	protected int editRow;
	/** Evaluation prefix */
	protected String prefix;
	/** Evaluation text */
	protected String evalText;
	/** Evaluation postfix */
	protected String postfix;

	/** Evaluation symbol */
	protected static final String EVAL_SYM = "=";
	/** Numeric symbol */
	protected static final String NUM_SYM = "\u2248";
	/** Substitute symbol */
	protected static final String SUB_SYM = "\u2713";

	/** Evaluation action command */
	protected static final String ACTION_EVALUATE = "Evaluate";
	/** Numeric action command */
	protected static final String ACTION_NUMERIC = "Numeric";
	/** Substitute action command */
	protected static final String ACTION_SUBSTITUTE = "Substitute";

	/** Contains substitution values */
	protected Vector<Vector<String>> data;

	/**
	 * @param prefix
	 *            before selection, not effected by the substitution
	 * @param evalText
	 *            the String which will be substituted
	 * @param postfix
	 *            after selection, not effected by the substitution
	 * @param editRow
	 *            row to edit
	 */
	public CASSubDialog(String prefix, String evalText, String postfix,
			int editRow) {
		this.prefix = prefix;
		this.evalText = evalText;
		this.postfix = postfix;
		this.editRow = editRow;
	}

	/**
	 * @return casView
	 */
	protected abstract CASView getCASView();

	/**
	 * @param cell
	 *            initialize table with cell information
	 */
	protected void initData(GeoCasCell cell) {
		HashSet<GeoElement> vars = new HashSet<>();
		if (cell.getInputVE().getVariables(
				SymbolicMode.NONE) != null) {
			for (GeoElement var : cell.getInputVE().getVariables(
					SymbolicMode.NONE)) {
				addVariables(var, vars);
			}
		}

		// get the substitution list from cell
		ArrayList<Vector<String>> substList = cell.getSubstList();

		Vector<String> row;
		data = new Vector<>(vars.size() + 1);
		Iterator<GeoElement> iter = vars.iterator();
		while (iter.hasNext()) {
			row = new Vector<>(2);
			GeoElement var = iter.next();
			String nextVar = var.getLabel(StringTemplate.defaultTemplate);
			int i = 0;
			for (i = 0; i < data.size(); i++) {
				if (data.get(i).firstElement().compareTo(nextVar) >= 0) {
					break;
				}
			}
			if (i == data.size()
					|| !data.get(i).firstElement().equals(nextVar)) {
				row.add(nextVar);
				boolean added = false;
				if (substList != null && !substList.isEmpty()) {
					// search for nextVar in subst list
					for (int k = 0; k < substList.size(); k++) {
						// case we found it
						if (substList.get(k).get(0).equals(nextVar)) {
							// add to substitution data
							row.add(substList.get(k).get(1));
							added = true;
							break;
						}
					}
				}
				// case we didn't found
				if (!added) {
					row.add("");
				}
				data.insertElementAt(row, i);
			}
		}
		row = new Vector<>(2);
		row.add("");
		row.add("");
		data.add(row);
	}

	private static void addVariables(GeoElement var, HashSet<GeoElement> vars) {
		if (var instanceof GeoCasCell) {
			ValidExpression ve = ((GeoCasCell) var).getValue();
			if (ve != null) {
				vars.addAll(ve.getVariables(SymbolicMode.NONE));
			}
		} else {
			vars.add(var);
		}
	}

	/**
	 * @param actionCommand
	 *            Evaluate || Numeric || Substitute
	 * @return true iff any substitution applied
	 */
	protected boolean apply(String actionCommand) {

		CASTable table = getCASView().getConsoleTable();

		// create substitution list
		StringBuilder substList = new StringBuilder("{");
		StringBuilder substComment = new StringBuilder();

		for (int i = 0; i < data.size(); i++) {
			String fromExpr = data.get(i).get(0).trim();
			String toExpr = data.get(i).get(1).trim();
			if (!"".equals(fromExpr) && !"".equals(toExpr)) {
				if (substList.length() > 1) {
					substList.append(',');
					substComment.append(',');
				}
				fromExpr = getCASView().resolveCASrowReferences(fromExpr,
						editRow);
				toExpr = getCASView().resolveCASrowReferences(toExpr, editRow);
				substList.append(fromExpr);
				substList.append('=');
				substList.append(toExpr);
				substComment.append(fromExpr);
				substComment.append('=');
				substComment.append(toExpr);
			}
		}
		substList.append('}');

		if ("{}".equals(substList.toString())) {
			return false;
		}

		// make sure pure substitute is not evaluated
		boolean keepInput = false;

		// substitute command
		String subCmd = "Substitute[" + evalText + "," + substList + "]";
		if ("Substitute".equals(actionCommand)) {
			subCmd = "Substitute[" + evalText + "," + substList + "]";
			keepInput = true;
		} else if ("Numeric".equals(actionCommand)) {
			subCmd = "Numeric[" + subCmd + "]";
		}

		try {
			GeoCasCell currCell = table.getGeoCasCell(editRow);
			StringBuilder oldXML = currCell.getConstruction().getCurrentUndoXML(false);
			currCell.setProcessingInformation(prefix, subCmd, postfix);
			currCell.setEvalCommand("Substitute");
			currCell.setEvalComment(substComment.toString());

			// make sure pure substitute is not evaluated
			currCell.setKeepInputUsed(keepInput);

			getCASView().processRowThenEdit(editRow, oldXML.toString());
			// table.startEditingRow(editRow + 1);
			return true;
		} catch (Throwable e) {
			Log.debug(e);
			return false;
		}
	}

}
