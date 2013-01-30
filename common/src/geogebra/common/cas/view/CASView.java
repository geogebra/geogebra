package geogebra.common.cas.view;

import geogebra.common.cas.GeoGebraCAS;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.View;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
/**
 * Platform independent abstract CAS view
 */
public abstract class CASView implements View{
	public static final String TOOLBAR_DEFINITION = "1001 | 1002 | 1003  || 1005 | 1004 || 1006 | 1007 | 1010 | 1011 || 1008 1009 || 6";
	private GeoGebraCAS cas;
	/** kernel */
	protected Kernel kernel;
	/** input handler */
	protected CASInputHandler casInputHandler;
	private boolean toolbarIsUpdatedByDockPanel;
	/**
	 * @return CAS table
	 */
	public abstract CASTable getConsoleTable();
	/**
	 * @return application
	 */
	public abstract App getApp();
	/**
	 * @return row headers
	 */
	public abstract RowHeader getRowHeader();
	/**
	 * Shows dialog for substitution tool
	 * @param prefix prefix (keep as is)
	 * @param evalText evaluable text (do substitution here)
	 * @param postfix postfix (keep as is again)
	 * @param selRow row index (starting from 0)
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
	 * @param n row index (starting from 0)
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
		return outVE.toString(StringTemplate.numericDefault);
	}

	/**
	 * Returns the input string in the n-th row of this CAS view. If the n-th
	 * cell has no output string, the input string of this cell is returned.
	 * @param n row index (starting from 0)
	 * @return input string in the n-th row of this CAS view
	 */
	public String getRowInputValue(int n) {
		return getConsoleTable().getGeoCasCell(n).getInput(
				StringTemplate.defaultTemplate);
	}

	/**
	 * Returns the number of rows of this CAS view.
	 * @return the number of rows of this CAS view.
	 */
	public int getRowCount() {
		return getConsoleTable().getRowCount();
	}
	
	/**
	 * @return the GoGebraCAS used  by this view
	 */
	final public synchronized GeoGebraCAS getCAS() {
		if (cas == null) {
			cas = (geogebra.common.cas.GeoGebraCAS) kernel.getGeoGebraCAS();
		}

		return cas;
	}
	
	/**
	 * Handles toolbar mode changes
	 */
	public void setMode(int mode,ModeSetter m) {
		if (m != ModeSetter.TOOLBAR)
			return;

		String command = kernel.getModeText(mode); // e.g. "Derivative"
		boolean backToEvaluate = true;
		switch (mode) {
		case EuclidianConstants.MODE_CAS_EVALUATE:
		case EuclidianConstants.MODE_CAS_NUMERIC:
		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			// no parameters, keep mode
			backToEvaluate = false;
			processInput(command, null);
			break;
		case EuclidianConstants.MODE_CAS_EXPAND:
		case EuclidianConstants.MODE_CAS_FACTOR:
		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
		case EuclidianConstants.MODE_CAS_NUMERICAL_SOLVE:
		case EuclidianConstants.MODE_CAS_SOLVE:
			
			// no parameters
			processInput(command, null);
			break;
		case EuclidianConstants.MODE_DELETE:
			// make sure we don't switch to evaluate if delete tool is used in EV
			if(getApp().getGuiManager().getActiveToolbarId()!=this.getViewID())
				backToEvaluate = false;
			boolean undo = deleteCasCells(getConsoleTable().getSelectedRows());
			if(undo)
				getConsoleTable().getApplication().storeUndoInfo();
			break;

		case EuclidianConstants.MODE_CAS_DERIVATIVE:
		case EuclidianConstants.MODE_CAS_INTEGRAL:
			// use first variable in expression as parameter
			processInput(command, new String[] { "%0" });
			break;
		default:
			backToEvaluate = false;
			// ignore other modes
		}
		if(backToEvaluate)
			getApp().setMode(EuclidianConstants.MODE_CAS_EVALUATE,ModeSetter.CAS_VIEW);
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
	}
	
	/**
	 * Makes sure we have an empty row at the end.
	 */
	private void ensureOneEmptyRow() {
		int rows = getRowCount();
		// add an empty one when we have no rows or last one is not empty
		 if (rows == 0 || !isRowEmpty(rows-1)) {
			GeoCasCell casCell = new GeoCasCell(kernel.getConstruction());
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
		//do nothing
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
	 * @param ggbcmd command name
	 * @param params parameters
	 */
	public void processInput(String ggbcmd, String[] params) {
		getInputHandler().processCurrentRow(ggbcmd, params);
		getApp().storeUndoInfo();
	}
	
	/**
	 * Processes given row.
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
	 * @param inputExp input row
	 * @param row row the expression is in
	 * @return string with replaced references
	 */
	public String resolveCASrowReferences(String inputExp, int row) {
		String result = getInputHandler().resolveCASrowReferences(inputExp, row,
				GeoCasCell.ROW_REFERENCE_STATIC, false);
		return getInputHandler().resolveCASrowReferences(result, row,
				GeoCasCell.ROW_REFERENCE_DYNAMIC, false);
	}
	
	/**
	 * Deletes given CAS cells both from view and from construction
	 * @param selRows selected rows
	 * @return true if undo needed
	 */
	public boolean deleteCasCells(int[] selRows) {
		boolean undoNeeded = false;
		App.debug(selRows.length);
		//reverse order makes sure we don't move cells that are waiting for deletion
		for (int i=selRows.length-1; i >= 0; i--) {
			GeoCasCell casCell = getConsoleTable().getGeoCasCell(selRows[i]);
			if (casCell != null) {
				casCell.remove();
				undoNeeded = true;
			}
		}
		return undoNeeded;
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
	
	
}
