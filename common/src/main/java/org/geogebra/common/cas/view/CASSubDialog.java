package org.geogebra.common.cas.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Common class for substitution dialogs
 * 
 * @author balazs.bencze
 *
 */
public abstract class CASSubDialog {

	/**
	 * Class containing row information
	 */
	protected static class SubstituteValue {
		private String variable;
		private String value;

		/**
		 * @param var
		 *            old expression
		 * @param val
		 *            new expression
		 */
		public SubstituteValue(String var, String val) {
			variable = var;
			value = val;
		}

		/**
		 * @return old expression
		 */
		public String getVariable() {
			return variable;
		}

		/**
		 * @param var
		 *            variable
		 */
		public void setVariable(String var) {
			variable = var;
		}

		/**
		 * @return new expression
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param val
		 *            value
		 */
		public void setValue(String val) {
			value = val;
		}
	}

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
		HashSet<GeoElement> vars = new HashSet<GeoElement>();
		if (cell.getInputVE().getVariables() != null) {
			for (GeoElement var : cell.getInputVE().getVariables()) {
				addVariables(var, vars);
			}
		}
		Vector<String> row;
		data = new Vector<Vector<String>>(vars.size() + 1);
		Iterator<GeoElement> iter = vars.iterator();
		while (iter.hasNext()) {
			row = new Vector<String>(2);
			GeoElement var = iter.next();
			String nextVar = var.getLabel(StringTemplate.defaultTemplate);
			int i = 0;
			for (i = 0; i < data.size(); i++) {
				if (data.get(i).firstElement().compareTo(nextVar) >= 0) {
					break;
				}
			}
			if (i == data.size() || !data.get(i).firstElement().equals(nextVar)) {
				row.add(nextVar);
				row.add("");
				data.insertElementAt(row, i);
			}
		}
		row = new Vector<String>(2);
		row.add("");
		row.add("");
		data.add(row);
	}

	private static void addVariables(GeoElement var, HashSet<GeoElement> vars) {
		if (var instanceof GeoCasCell) {
			ValidExpression ve = ((GeoCasCell) var).getOutputValidExpression();
			if (ve != null)
				vars.addAll(ve.getVariables());
		} else
			vars.add(var);
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
			if (!fromExpr.equals("") && !toExpr.equals("")) {
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

		if ("{}".equals(substList.toString()))
			return false;

		// make sure pure substitute is not evaluated
		boolean keepInput = false;

		// substitute command
		String subCmd = "Substitute[" + evalText + "," + substList + "]";
		if (actionCommand.equals("Substitute")) {
			subCmd = "Substitute[" + evalText + "," + substList + "]";
			keepInput = true;
		} else if (actionCommand.equals("Numeric")) {
			subCmd = "Numeric[" + subCmd + "]";
			keepInput = false;
		}

		try {
			GeoCasCell currCell = table.getGeoCasCell(editRow);
			currCell.setProcessingInformation(prefix, subCmd, postfix);
			currCell.setEvalCommand("Substitute");
			currCell.setEvalComment(substComment.toString());

			// make sure pure substitute is not evaluated
			currCell.setKeepInputUsed(keepInput);

			getCASView().processRowThenEdit(editRow, true);
			// table.startEditingRow(editRow + 1);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

}
