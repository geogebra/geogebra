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

	// substitutions + evaltext with vars
	private Vector<Vector<String>> substData;

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

		boolean firstSubst = true;
		// get last substitution data
		Vector<Vector<String>> lastData = getCASView().getSubstData().get(
				editRow);
		// substituted vars form last substitution
		HashSet<String> lastVars = new HashSet<String>();
		// substitutions
		HashSet<Vector<String>> lastSubsts = new HashSet<Vector<String>>();
		// case input was already substituted
		if (lastData != null && !lastData.isEmpty()) {
			// collect substitutes
			for (int i = 0; i < lastData.size() - 1; i++) {
				lastSubsts.add(lastData.get(i));
			}
			// input eq with substituted vars
			Vector<String> lastEqVars = lastData.get(lastData.size() - 1);
			// collect substituted vars
			for (int k = 1; k < lastEqVars.size(); k++) {
				lastVars.add(lastEqVars.get(k));
			}
			// case input of cell was changed
			// e.g. f(x) -> g(x)
			if (lastData.get(lastData.size() - 1).get(0)
					.equals(String.valueOf(evalText))) {
				firstSubst = false;
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
				// case was already made a substitution and var has subst value
				if (!firstSubst && lastVars.contains(nextVar)) {
					// iterate through last substitutions
					Iterator<Vector<String>> it = lastSubsts.iterator();
					while (it.hasNext()) {
						Vector<String> vector = it.next();
						if (vector.get(0).equals(nextVar)) {
							row.add(vector.get(1));
						}
					}
				} else {
					row.add("");
				}
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
		// current substitution values
		substData = new Vector<Vector<String>>(
				data.size());
		// input equation with vars
		// e.g. f(x),a,b,c,x for input f(x):=a*x^2+b*x+c
		Vector<String> inputEq = new Vector<String>();
		inputEq.add(evalText);
		for (int i = 0; i < data.size(); i++) {
			String fromExpr = data.get(i).get(0).trim();
			String toExpr = data.get(i).get(1).trim();
			if (!fromExpr.equals("") && !toExpr.equals("")) {
				if (substList.length() > 1) {
					substList.append(',');
					substComment.append(',');
				}
				// add vars of expression
				inputEq.add(fromExpr);
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
			// collect substitutions
			substData.add(data.get(i));
		}
		substList.append('}');

		// add input equation with vars
		substData.set(substData.size() - 1, inputEq);

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
			getCASView().getSubstData().set(editRow, substData);
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
