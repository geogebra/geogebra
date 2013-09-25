package geogebra.common.cas.view;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * Common class for substitution dialogs
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
		 * @param var old expression
		 * @param val new expression
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
		 * @return new expression
		 */
		public String getValue() {
			return value;
		}
	}

	protected int editRow;
	protected String prefix, evalText, postfix;
	
	protected static final int DEFAULT_TABLE_WIDTH = 200;
	protected static final int DEFAULT_TABLE_HEIGHT = 150;
	
	protected Vector<Vector<String>> data;


	/**
	 * @param prefix before selection, not effected by the substitution
	 * @param evalText the String which will be substituted
	 * @param postfix after selection, not effected by the substitution
	 * @param editRow row to edit
	 */
	public CASSubDialog(String prefix, String evalText, String postfix, int editRow) {
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
	 * @param cell initialize table with cell information
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
			if (i == data.size()
					|| !data.get(i).firstElement().equals(nextVar)) {
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
		if(var instanceof GeoCasCell){
			ValidExpression ve = ((GeoCasCell)var).getOutputValidExpression();
			if(ve!=null)
				vars.addAll(ve.getVariables());
		}
		else vars.add(var);
	}
	
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
				fromExpr = getCASView().resolveCASrowReferences(fromExpr, editRow);
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
