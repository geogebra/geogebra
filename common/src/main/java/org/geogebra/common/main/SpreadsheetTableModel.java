package org.geogebra.common.main;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.UpdateLocationView;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Abstract class for managing spreadsheet GeoElement cells in a table model
 * that supports the spreadsheet.
 * 
 * The View interface is implemented so that the model can adapt when
 * GeoElements with spreadsheet labels (e.g. A1) are changed.
 * 
 * @author G. Sturr
 * 
 */
public abstract class SpreadsheetTableModel implements UpdateLocationView {

	protected App app;
	private int highestUsedColumn = -1;
	private int highestUsedRow = -1;

	/** tells that it's initing */
	protected boolean isIniting = true;

	/**
	 * maintains a list of all AlgoCellRanges in the construction and handles
	 * updates to the cell range lists as cells are added or removed
	 */
	private AlgoCellRangeManager cellRangeManager;

	/***************************************************
	 * Constructor
	 * 
	 * @param app
	 *            ggb Application
	 * @param rows
	 *            initial number of rows
	 * @param columns
	 *            initial number of columns
	 */
	public SpreadsheetTableModel(App app, int rows, int columns) {
		isIniting = true;
		this.app = app;

		cellRangeManager = new AlgoCellRangeManager();
	}

	/**************************************************
	 * Abstract Methods
	 */

	public abstract int getRowCount();

	public abstract int getColumnCount();

	public abstract void setRowCount(int rowCount);

	public abstract void setColumnCount(int columnCount);

	public abstract Object getValueAt(int row, int column);

	public abstract void setValueAt(Object value, int row, int column);

	/*
	 * ************************************************
	 */

	/**
	 * Attach to the kernel
	 */
	public void attachView() {
		app.getKernel().notifyAddAll(this);
		app.getKernel().attach(this);
	}

	/**
	 * Detach from the kernel
	 */
	public void detachView() {
		app.getKernel().detach(this);
	}

	/**
	 * Returns index of highest used column. Returns -1 if empty spreadsheet.
	 */
	public int getHighestUsedColumn() {
		return highestUsedColumn;
	}

	/**
	 * Returns index of highest used row. Returns -1 if empty spreadsheet.
	 */
	public int getHighestUsedRow() {
		return highestUsedRow;
	}

	/**
	 * Updates highestUsedColumn and highestUsedRow
	 */
	private void updateHighestUsedColAndRow(int col, int row) {

		if (col == highestUsedColumn) {
			boolean updatedHighestUsedColumn = false;
			for (int c = highestUsedColumn; c >= 0; c--) {
				boolean columnEmpty = true;
				for (int r = 0; r <= highestUsedRow; r++) {
					if (getValueAt(r, c) != null) {
						// column not empty
						columnEmpty = false;
						break;
					}
				}
				if (!columnEmpty) {
					highestUsedColumn = c;
					updatedHighestUsedColumn = true;
					break;
				}
			}
			if (!updatedHighestUsedColumn) {
				highestUsedColumn = -1;
			}
		}
		if (row == highestUsedRow) {
			boolean updatedHighestUsedRow = false;
			for (int r = highestUsedRow; r >= 0; r--) {
				boolean rowEmpty = true;
				for (int c = 0; c <= highestUsedColumn; c++) {
					if (getValueAt(r, c) != null) {
						// row not empty
						rowEmpty = false;
						break;
					}
				}
				if (!rowEmpty) {
					highestUsedRow = r;
					updatedHighestUsedRow = true;
					break;
				}
			}
			if (!updatedHighestUsedRow) {
				highestUsedRow = -1;
			}
		}
	}

	/**
	 * @return cellRangeManager for handling AlgoCellRange updates
	 */
	public AlgoCellRangeManager getCellRangeManager() {
		return cellRangeManager;
	}

	// ====================================
	// VIEW implementation
	// ====================================

	public void add(GeoElement geo) {
		update(geo);
		addToCellRangeAlgos(geo);
	}

	private void addWithoutTrace(GeoElement geo) {
		updateWithoutTrace(geo);
		addToCellRangeAlgos(geo);
	}

	public void remove(GeoElement geo) {
		GPoint location = geo.getSpreadsheetCoords();
		if (location != null) {
			doRemove(geo, location.y, location.x);
			cellRangeManager.updateCellRangeAlgos(geo, location, true);
		}
	}

	public void rename(GeoElement geo) {
		GPoint location = geo.getOldSpreadsheetCoords();
		if (location != null) {
			doRemove(geo, location.y, location.x);
			cellRangeManager.updateCellRangeAlgos(geo, location, true);
		}
		addWithoutTrace(geo);
	}

	private void doRemove(GeoElement geo, int row, int col) {
		setValueAt(null, row, col);
		updateHighestUsedColAndRow(col, row);
	}

	public void update(GeoElement geo) {
		if (geo.isEmptySpreadsheetCell() && geo.isDefined()) {
			geo.setEmptySpreadsheetCell(false);
		}
		updateWithoutTrace(geo);

		// trace value
		if (!isIniting && geo.getSpreadsheetTrace()) {
			app.getTraceManager().traceToSpreadsheet(geo);
		}



	}

	private void addToCellRangeAlgos(GeoElement geo) {
		GPoint location = geo.getSpreadsheetCoords();
		if (location != null) {
			cellRangeManager.addToCellRangeAlgos(geo, location);
		}

	}

	private void updateWithoutTrace(GeoElement geo) {
		GPoint location = geo.getSpreadsheetCoords();

		if (location != null
				&& location.x < app.getMaxSpreadsheetColumnsVisible()
				&& location.y < app.getMaxSpreadsheetRowsVisible()) {

			highestUsedColumn = Math.max(highestUsedColumn, location.x);
			highestUsedRow = Math.max(highestUsedRow, location.y);

			if (location.y >= getRowCount()) {
				setRowCount(location.y + 1);
			}

			if (location.x >= getColumnCount()) {
				// table.setMyColumnCount(location.x + 1);
				// JViewport cH = spreadsheet.getColumnHeader();

				// bugfix: double-click to load ggb file gives cH = null
				// if (cH != null) cH.revalidate();
			}
			setValueAt(geo, location.y, location.x);

			/*
			 * DONE ELSEWHERE // add tracing geos to the trace collection if
			 * (!isIniting && geo.getSpreadsheetTrace()) {
			 * app.getTraceManager().addSpreadsheetTraceGeo(geo); }
			 */
		}

	}

	public void updateLocation(GeoElement geo) {
		updateWithoutTrace(geo);
	}

	public void clearView() {

		for (int c = 0; c < getColumnCount(); ++c) {
			for (int r = 0; r < getRowCount(); ++r) {
				setValueAt(null, r, c);
			}
		}
		highestUsedColumn = -1;
		highestUsedRow = -1;
		cellRangeManager.removeAll();
	}

	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		updateWithoutTrace(geo);
	}

	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// not used
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// ignore
	}

	public void repaintView() {
		// ignore
	}

	public void reset() {
		// ignore
	}

	public void setMode(int mode, ModeSetter m) {
		// ignore
	}

	public int getViewID() {
		return App.VIEW_TABLE_MODEL;
	}

	@Override
	public void startBatchUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endBatchUpdate() {
		// TODO Auto-generated method stub

	}
}
