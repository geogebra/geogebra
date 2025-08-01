package org.geogebra.common.cas.view;

import java.util.Collections;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.Editing;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

/**
 * Platform independent abstract CAS view
 */
public abstract class CASView implements Editing, SetLabels {
	/** Default CAS toolbar */
	public static final String TOOLBAR_DEFINITION_D = "1001 | 1002 | 1003 "
			+ " || 1005 | 1004 || 1006 | 1007 | 1010 || 1008 1009 || 66 68 || 6";
	/**
	 * Default CAS toolbar for Web (before the prob. calc and function inspector
	 * are implemented)
	 */
	public static final String TOOLBAR_DEFINITION = "1001 | 1002 | 1003 "
			+ " || 1005 | 1004 || 1006 | 1007 | 1010 || 1008 | 1009 || 6";
	private GeoGebraCAS cas;
	/** kernel */
	final protected Kernel kernel;
	/** input handler */
	private CASInputHandler casInputHandler;

	/**
	 * @param kernel2
	 *            kernel
	 */
	public CASView(Kernel kernel2) {
		kernel = kernel2;
		getCAS();
	}

	/**
	 * @return CAS table
	 */
	public abstract CASTable getConsoleTable();

	/**
	 * @return application
	 */
	public abstract App getApp();

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
	@Override
	public void setLabels() {
		getConsoleTable().setLabels();
	}

	@Override
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
				.getValue();

		// if we don't have an outputVE, we let GeoCasCell deal with it :)
		if (outVE == null) {
			return getConsoleTable().getGeoCasCell(n)
					.getOutput(StringTemplate.numericDefault);
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
		return getConsoleTable().getGeoCasCell(n)
				.getLocalizedInput();
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
			cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
		}

		return cas;
	}

	/**
	 * Handles toolbar mode changes
	 */
	@Override
	public void setMode(int mode, ModeSetter m) {
		if (m != ModeSetter.TOOLBAR && m != ModeSetter.CAS_BLUR) {
			return;
		}
		boolean focus = m == ModeSetter.TOOLBAR;
		String command = EuclidianConstants.getModeTextSimple(mode); // e.g.
		// "Derivative"
		boolean backToEvaluate = true;
		switch (mode) {
		case EuclidianConstants.MODE_CAS_EVALUATE:
		case EuclidianConstants.MODE_CAS_NUMERIC:
		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			// no parameters, keep mode
			backToEvaluate = false;
			processInput(command, focus);
			break;
		case EuclidianConstants.MODE_CAS_EXPAND:
		case EuclidianConstants.MODE_CAS_FACTOR:
		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
		case EuclidianConstants.MODE_CAS_NUMERICAL_SOLVE:
		case EuclidianConstants.MODE_CAS_SOLVE:

			// no parameters
			processInput(command, focus);
			break;
		case EuclidianConstants.MODE_DELETE:
			// make sure we don't switch to evaluate if delete tool is used in
			// EV
			if (getApp().getGuiManager() != null && getApp().getGuiManager()
					.getActiveToolbarId() != this.getViewID()) {
				backToEvaluate = false;
			}
			boolean undo = deleteCasCells(getConsoleTable().getSelectedRows());
			if (undo) {
				getConsoleTable().getApplication().storeUndoInfo();
			}
			break;
		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			// make sure we don't switch to evaluate if delete tool is used in
			// EV
			if (getApp().getGuiManager() != null && getApp().getGuiManager()
					.getActiveToolbarId() != this.getViewID()) {
				backToEvaluate = false;
			}
			if (getConsoleTable().getSelectedRows().length > 0) {
				GeoCasCell cell = getConsoleTable()
						.getGeoCasCell(getConsoleTable().getSelectedRows()[0]);
				if (cell != null && cell.getTwinGeo() instanceof GeoFunction) {
					this.getApp().getDialogManager().showFunctionInspector(
							(GeoFunction) cell.getTwinGeo());
				}
			}
			break;

		case EuclidianConstants.MODE_CAS_DERIVATIVE:
		case EuclidianConstants.MODE_CAS_INTEGRAL:
			processInput(command, focus);
			break;
		default:
			backToEvaluate = false;
			// ignore other modes
		}
		if (backToEvaluate) {
			getApp().setMode(EuclidianConstants.MODE_CAS_EVALUATE,
					ModeSetter.CAS_VIEW);
			getApp().closePopups();
		}
	}

	/**
	 * Renames function definitions in the CAS
	 */
	@Override
	public void rename(GeoElement geo) {
		update(geo);
	}

	@Override
	public void clearView() {
		// delete all rows
		getConsoleTable().deleteAllRows();
		ensureOneEmptyRow();
		if (getConsoleTable().hasEditor()) {
			getConsoleTable().getEditor().clearInputText();
		}
	}

	/**
	 * Makes sure we have an empty row at the end.
	 */
	public void ensureOneEmptyRow() {
		int rows = getRowCount();
		// add an empty one when we have no rows or last one is not empty or the
		// last is in construction list
		if (rows == 0 || !isRowOutputEmpty(rows - 1) || getConsoleTable()
				.getGeoCasCell(rows - 1).isInConstructionList()) {
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

	@Override
	public void reset() {
		repaintView();
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// do nothing
	}

	/**
	 * Defines new functions in the CAS
	 */
	@Override
	public void add(GeoElement geo) {
		update(geo);
		ensureOneEmptyRow();
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// TODO
	}

	/**
	 * Removes function definitions from the CAS
	 */
	@Override
	public void remove(GeoElement geo) {
		if (geo instanceof GeoCasCell) {
			GeoCasCell casCell = (GeoCasCell) geo;
			int row = casCell.getRowNumber();
			if (row < 0) {
				return;
			}
			casCell.resetRowNumber();
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
	@Override
	public void update(GeoElement geo) {

		if (geo instanceof GeoCasCell) {
			GeoCasCell casCell = (GeoCasCell) geo;
			if (casCell.getRowNumber() < 0) {
				casCell.reloadRowNumber();
			}
			getConsoleTable().setRow(casCell.getRowNumber(), casCell);
		}

	}

	@Override
	final public void updateVisualStyle(GeoElement geo, GProperty prop) {
		update(geo);
	}

	/**
	 * Process currently selected cell using the given command and parameters,
	 * e.g. "Integral", [ "x" ]
	 * 
	 * @param ggbcmd
	 *            command name
	 * @param focus
	 *            whether the view should keep focus afterwards
	 */
	public void processInput(String ggbcmd, boolean focus) {
		getApp().getCommandDictionaryCAS(); // #5456 make sure we have the right
											// dict
		// before evaluating
		StringBuilder oldXML = getApp().getKernel().getConstruction().getCurrentUndoXML(false);
		getInputHandler().processCurrentRow(ggbcmd, focus, oldXML.toString());
		getApp().storeUndoInfo();
	}

	/**
	 * Processes given row.
	 * 
	 * @see CASInputHandler#processRowThenEdit(int, boolean, String)
	 * @param row
	 *            row index
	 */
	public void processRowThenEdit(int row, String oldXML) {
		getInputHandler().processRowThenEdit(row, true, oldXML);
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
		String result = getInputHandler().resolveCASrowReferences(inputExp, row,
				GeoCasCell.ROW_REFERENCE_STATIC, false);
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
				ret.append(casCell.getLaTeXOutput(false));

				if (i < selRows.length - 1) {
					// LaTeX linebreak
					ret.append(" \\\\ ");
				}

			}
		}

		return ret.toString();
	}

	/**
	 * @param selRows
	 *            list of rows
	 * @return \n separated cell outputs
	 */
	public final String getTextFromCells(int[] selRows) {

		StringBuilder ret = new StringBuilder();

		for (int i = 0; i < selRows.length; i++) {
			GeoCasCell casCell = getConsoleTable().getGeoCasCell(selRows[i]);
			if (casCell != null) {
				ret.append(casCell.getOutput(StringTemplate.casCopyTemplate));

				// LaTeX linebreak
				if (i != selRows.length - 1) {
					ret.append("\n ");
				}

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
		if (row < 0) {
			return false;
		}

		GeoCasCell value = getConsoleTable().getGeoCasCell(row);
		return value.isEmpty();
	}

	/**
	 * @param row
	 *            row index (starting from 0)
	 * @return true if given cell's input is empty
	 */
	public boolean isRowInputEmpty(int row) {
		if (row < 0) {
			return false;
		}

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
			if (startEditing) {
				getConsoleTable().startEditingRow(lastRow);
			}
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
		if (row < 0) {
			return false;
		}

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
	 *            cell index
	 * @return input
	 */
	public String getCellInput(int i) {
		GeoCasCell casCell = getConsoleTable().getGeoCasCell(i);
		if (casCell != null) {
			return casCell.getInternalInput();
		}
		return null;
	}

	@Override
	public void cancelEditItem() {
		CASTable table = getConsoleTable();
		table.stopEditing();

	}

	/**
	 * @param casInputHandler
	 *            input handler
	 */
	protected void setCasInputHandler(CASInputHandler casInputHandler) {
		this.casInputHandler = casInputHandler;
	}

	/**
	 * Updates arbitraryConstantTable in construction.
	 *
	 * @param row
	 *            row index (starting from 0) where cell insertion is done
	 */
	public void updateAfterInsertArbConstTable(int row) {
		if (kernel.getConstruction().getArbitraryConsTable()
				.size() > 0) {
			// find last row number
			Integer max = Collections.max(kernel.getConstruction()
					.getArbitraryConsTable().keySet());
			for (int key = max; key >= row; key--) {
				ArbitraryConstantRegistry myArbConst = kernel
						.getConstruction()
						.getArbitraryConsTable().get(key);
				if (myArbConst != null
						&& !kernel.getConstruction().isCasCellUpdate()
						&& !kernel.getConstruction().isFileLoading()
						&& kernel.getConstruction().isNotXmlLoading()) {
					kernel.getConstruction().getArbitraryConsTable()
							.remove(key);
					kernel.getConstruction().getArbitraryConsTable()
							.put(key + 1, myArbConst);
				}
			}
		}
	}
}
