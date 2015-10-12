package org.geogebra.common.cas.view;

import java.util.ArrayList;
import java.util.Vector;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

/**
 * Platform independent abstract CAS view
 */
public abstract class CASView implements View, SetLabels {
	/** Default CAS toolbar */
	public static final String TOOLBAR_DEFINITION_D = "1001 | 1002 | 1003  || 1005 | 1004 || 1006 | 1007 | 1010 || 1008 1009 || 66 68 || 6";
	/**
	 * Default CAS toolbar for Web (before the prob. calc and function inspector
	 * are implemented)
	 */
	public static final String TOOLBAR_DEFINITION = "1001 | 1002 | 1003  || 1005 | 1004 || 1006 | 1007 | 1010 || 1008 | 1009 || 6";
	private GeoGebraCAS cas;
	/** kernel */
	protected Kernel kernel;
	/** input handler */
	protected CASInputHandler casInputHandler;

	/**
	 * @return CAS table
	 */
	public abstract CASTable getConsoleTable();

	/**
	 * @return application
	 */
	public abstract App getApp();

	// list of last substitutions
	public abstract ArrayList<Vector<Vector<String>>> getSubstData();

	/**
	 * Shows dialog for substitution tool
	 * 
	 * @param prefix
	 *            prefix (keep as is)
	 * @param evalText
	 *            evaluable text (do substitution here)
	 * @param postfix
	 *            postfix (keep as is again)
	 * @param selRow
	 *            row index (starting from 0)
	 */
	public abstract void showSubstituteDialog(String prefix, String evalText,
			String postfix, int selRow);

	/**
	 * Updates labels to match current locale
	 */
	public void setLabels() {
		getConsoleTable().setLabels();
	}

	public int getViewID() {
		return App.VIEW_CAS;
	}

	/**
	 * Returns the output string in the n-th row of this CAS view.
	 * 
	 * @param n
	 *            row index (starting from 0)
	 * @return output value
	 */
	public String getRowOutputValue(int n) {
		ValidExpression outVE = getConsoleTable().getGeoCasCell(n)
				.getOutputValidExpression();

		// if we don't have an outputVE, we let GeoCasCell deal with it :)
		if (outVE == null) {
			return getConsoleTable().getGeoCasCell(n).getOutput(
					StringTemplate.numericDefault);
		}
		if (outVE.unwrap() instanceof GeoElement) {
			return ((GeoElement) outVE.unwrap())
					.toOutputValueString(StringTemplate.numericDefault);
		}
		return outVE.toString(StringTemplate.numericDefault);
	}

	/**
	 * Returns the input string in the n-th row of this CAS view. If the n-th
	 * cell has no output string, the input string of this cell is returned.
	 * 
	 * @param n
	 *            row index (starting from 0)
	 * @return input string in the n-th row of this CAS view
	 */
	public String getRowInputValue(int n) {
		return getConsoleTable().getGeoCasCell(n).getInput(
				StringTemplate.defaultTemplate);
	}

	/**
	 * Returns the number of rows of this CAS view.
	 * 
	 * @return the number of rows of this CAS view.
	 */
	public int getRowCount() {
		return getConsoleTable().getRowCount();
	}

	/**
	 * @return the GoGebraCAS used by this view
	 */
	final public synchronized GeoGebraCAS getCAS() {
		if (cas == null) {
			cas = (org.geogebra.common.cas.GeoGebraCAS) kernel.getGeoGebraCAS();
		}

		return cas;
	}

	/**
	 * Handles toolbar mode changes
	 */
	public void setMode(int mode, ModeSetter m) {
		if (m != ModeSetter.TOOLBAR)
			return;

		String command = EuclidianConstants.getModeText(mode); // e.g.
																// "Derivative"
		boolean backToEvaluate = true;
		switch (mode) {
		case EuclidianConstants.MODE_CAS_EVALUATE:
		case EuclidianConstants.MODE_CAS_NUMERIC:
		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			// no parameters, keep mode
			backToEvaluate = false;
			processInput(command);
			break;
		case EuclidianConstants.MODE_CAS_EXPAND:
		case EuclidianConstants.MODE_CAS_FACTOR:
		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
		case EuclidianConstants.MODE_CAS_NUMERICAL_SOLVE:
		case EuclidianConstants.MODE_CAS_SOLVE:

			// no parameters
			processInput(command);
			break;
		case EuclidianConstants.MODE_DELETE:
			// make sure we don't switch to evaluate if delete tool is used in
			// EV
			if (getApp().getGuiManager() != null
					&& getApp().getGuiManager().getActiveToolbarId() != this
							.getViewID())
				backToEvaluate = false;
			boolean undo = deleteCasCells(getConsoleTable().getSelectedRows());
			if (undo)
				getConsoleTable().getApplication().storeUndoInfo();
			break;
		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			// make sure we don't switch to evaluate if delete tool is used in
			// EV
			if (getApp().getGuiManager() != null
					&& getApp().getGuiManager().getActiveToolbarId() != this
							.getViewID())
				backToEvaluate = false;
			if (getConsoleTable().getSelectedRows().length > 0) {
				GeoCasCell cell = getConsoleTable().getGeoCasCell(
						getConsoleTable().getSelectedRows()[0]);
				if (cell != null && cell.getTwinGeo() instanceof GeoFunction) {
					this.getApp()
							.getDialogManager()
							.showFunctionInspector(
									(GeoFunction) cell.getTwinGeo());
				}
			}
			break;

		case EuclidianConstants.MODE_CAS_DERIVATIVE:
		case EuclidianConstants.MODE_CAS_INTEGRAL:
			processInput(command);
			break;
		default:
			backToEvaluate = false;
			// ignore other modes
		}
		if (backToEvaluate){
			showTooltip(mode);
			getApp().setMode(EuclidianConstants.MODE_CAS_EVALUATE,
					ModeSetter.CAS_VIEW);
		}
	}

	protected void showTooltip(int mode) {
		// only in web

	}

	/**
	 * Renames function definitions in the CAS
	 */
	public void rename(GeoElement geo) {
		update(geo);
	}

	public void clearView() {
		// delete all rows
		getConsoleTable().deleteAllRows();
		ensureOneEmptyRow();
		getConsoleTable().getEditor().clearInputText();
	}

	/**
	 * Makes sure we have an empty row at the end.
	 */
	public void ensureOneEmptyRow() {
		int rows = getRowCount();
		// add an empty one when we have no rows or last one is not empty or the
		// last is in construction list
		if (rows == 0
				|| !isRowOutputEmpty(rows - 1)
				|| getConsoleTable().getGeoCasCell(rows - 1)
						.isInConstructionList()) {
			GeoCasCell casCell = new GeoCasCell(kernel.getConstruction());
			// for new cell add new empty substitution data
			getSubstData().add(null);
			getConsoleTable().insertRow(rows, casCell, false);
		}
	}

	/**
	 * Attaches this view to kernel to receive messages
	 */
	public void attachView() {
		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
	}

	/**
	 * Detach this view from kernel
	 */
	public void detachView() {
		kernel.detach(this);
		clearView();
	}

	public void reset() {
		repaintView();
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// do nothing
	}

	/**
	 * Defines new functions in the CAS
	 */
	public void add(GeoElement geo) {
		update(geo);
		ensureOneEmptyRow();
	}

	/**
	 * Removes function definitions from the CAS
	 */
	public void remove(GeoElement geo) {
		if (geo instanceof GeoCasCell) {
			GeoCasCell casCell = (GeoCasCell) geo;
			int row = casCell.getRowNumber();
			// we must stop editing here, otherwise content of deleted cell is
			// copied below
			boolean wasEditing = getConsoleTable().isEditing();
			getConsoleTable().stopEditing();

			getConsoleTable().deleteRow(row);
			if (wasEditing) {
				getConsoleTable().startEditingRow(row);
			}
		}
	}

	/**
	 * Handles updates of geo in CAS view.
	 */
	public void update(GeoElement geo) {

		if (geo instanceof GeoCasCell) {
			GeoCasCell casCell = (GeoCasCell) geo;
			getConsoleTable().setRow(casCell.getRowNumber(), casCell);
		}

	}

	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	/**
	 * Process currently selected cell using the given command and parameters,
	 * e.g. "Integral", [ "x" ]
	 * 
	 * @param ggbcmd
	 *            command name
	 */
	public void processInput(String ggbcmd) {
		getApp().getCommandDictionaryCAS(); // #5456 make sure we have the right
											// dict
										// before evaluating
		getInputHandler().processCurrentRow(ggbcmd);
		getApp().storeUndoInfo();
	}

	/**
	 * Processes given row.
	 * 
	 * @see CASInputHandler#processRowThenEdit(int, boolean)
	 * @param row
	 *            row index
	 * @param flag
	 *            start editing
	 */
	public void processRowThenEdit(int row, boolean flag) {
		getInputHandler().processRowThenEdit(row, flag);
	}

	/**
	 * Resolves both static (#) and dynamic($) expressions in selected row,
	 * replacement of dynamic references is also done statically.
	 * 
	 * @param inputExp
	 *            input row
	 * @param row
	 *            row the expression is in
	 * @return string with replaced references
	 */
	public String resolveCASrowReferences(String inputExp, int row) {
		String result = getInputHandler().resolveCASrowReferences(inputExp,
				row, GeoCasCell.ROW_REFERENCE_STATIC, false);
		return getInputHandler().resolveCASrowReferences(result, row,
				GeoCasCell.ROW_REFERENCE_DYNAMIC, false);
	}

	/**
	 * Deletes given CAS cells both from view and from construction
	 * 
	 * @param selRows
	 *            selected rows
	 * @return true if undo needed
	 */
	public boolean deleteCasCells(int[] selRows) {
		boolean undoNeeded = false;
		Log.debug(selRows.length);
		// reverse order makes sure we don't move cells that are waiting for
		// deletion
		for (int i = selRows.length - 1; i >= 0; i--) {
			GeoCasCell casCell = getConsoleTable().getGeoCasCell(selRows[i]);
			if (casCell != null) {
				casCell.remove();
				// remove the last substitution if cell is deleted
				removeCellsSubstDialog(i);
				undoNeeded = true;
			}
		}
		if (selRows.length > 0) {
			getConsoleTable().resetRowNumbers(selRows[0]);
		}

		return undoNeeded;
	}

	/**
	 * returns latex from selected cells
	 * 
	 * @param selRows
	 *            selected rows
	 * @return LaTeX for cells, separated by \\
	 */
	public String getLaTeXfromCells(int[] selRows) {

		StringBuilder ret = new StringBuilder();

		for (int i = 0; i < selRows.length; i++) {
			GeoCasCell casCell = getConsoleTable().getGeoCasCell(selRows[i]);
			if (casCell != null) {
				ret.append(casCell.getLaTeXOutput());

				// LaTeX linebreak
				ret.append(" \\\\ ");

			}
		}

		return ret.toString();
	}

	/**
	 * @return input handler
	 */
	public CASInputHandler getInputHandler() {
		return casInputHandler;
	}

	/**
	 * @param row
	 *            row index (starting from 0)
	 * @return true if given cell is empty
	 */
	public boolean isRowEmpty(int row) {
		if (row < 0)
			return false;

		GeoCasCell value = getConsoleTable().getGeoCasCell(row);
		return value.isEmpty();
	}

	/**
	 * @param row
	 *            row index (starting from 0)
	 * @return true if given cell's input is empty
	 */
	public boolean isRowInputEmpty(int row) {
		if (row < 0)
			return false;

		GeoCasCell value = getConsoleTable().getGeoCasCell(row);
		return value.isInputEmpty();
	}

	/**
	 * Inserts a row at the end and starts editing the new row.
	 * 
	 * @param newValue
	 *            CAS cell to be added
	 * @param startEditing
	 *            true to start editing
	 */
	public void insertRow(GeoCasCell newValue, boolean startEditing) {
		GeoCasCell toInsert = newValue;
		int lastRow = getRowCount() - 1;
		if (isRowEmpty(lastRow)) {
			if (toInsert == null) {
				toInsert = new GeoCasCell(kernel.getConstruction());
				// kernel.getConstruction().setCasCellRow(newValue, lastRow);
			}
			getConsoleTable().setRow(lastRow, toInsert);
			if (startEditing)
				getConsoleTable().startEditingRow(lastRow);
		} else {
			getConsoleTable().insertRow(lastRow + 1, toInsert, startEditing);
		}
	}

	/**
	 * @param row
	 *            row index (starting from 0)
	 * @return true if given cell is empty and it's not a text cell
	 */
	public boolean isRowOutputEmpty(int row) {
		if (row < 0)
			return false;

		GeoCasCell value = getConsoleTable().getGeoCasCell(row);
		return value.isOutputEmpty() && !value.isUseAsText();
	}

	@Override
	public void startBatchUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endBatchUpdate() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param i
	 *            - nr of cell in table
	 */
	public abstract void removeCellsSubstDialog(int i);

}
