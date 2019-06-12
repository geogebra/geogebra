package org.geogebra.common.main;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.spreadsheet.CellFormat;
import org.geogebra.common.gui.view.spreadsheet.CellFormatInterface;
import org.geogebra.common.gui.view.spreadsheet.MyTableInterface;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.UpdateLocationView;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.kernelND.GeoElementND;

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

	/** application */
	protected final App app;
	private int highestUsedColumn = -1;
	private int highestUsedRow = -1;

	/** tells that it's initing */
	protected boolean isIniting = true;

	/**
	 * maintains a list of all AlgoCellRanges in the construction and handles
	 * updates to the cell range lists as cells are added or removed
	 */
	private AlgoCellRangeManager cellRangeManager;
	private CellFormatInterface formatHandler;

	/***************************************************
	 * Constructor
	 * 
	 * @param app
	 *            ggb Application
	 */
	public SpreadsheetTableModel(App app) {
		isIniting = true;
		this.app = app;

		cellRangeManager = new AlgoCellRangeManager();
	}

	/**
	 * @return number of rows
	 */
	public abstract int getRowCount();

	/**
	 * @return number of columns
	 */
	public abstract int getColumnCount();

	/**
	 * @param rowCount
	 *            number of rows
	 */
	public abstract void setRowCount(int rowCount);

	/**
	 * @param columnCount
	 *            number of columns
	 */
	public abstract void setColumnCount(int columnCount);

	/**
	 * @param row
	 *            row index
	 * @param column
	 *            column index
	 * @return GeoElement
	 */
	public abstract Object getValueAt(int row, int column);

	/**
	 * @param value
	 *            GeoElement
	 * @param row
	 *            row index
	 * @param column
	 *            column index
	 */
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
	 * @return index of highest used column. Returns -1 if empty spreadsheet.
	 */
	public int getHighestUsedColumn() {
		return highestUsedColumn;
	}

	/**
	 * @return index of highest used row. Returns -1 if empty spreadsheet.
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

	@Override
	public void add(GeoElement geo) {
		update(geo);
		addToCellRangeAlgos(geo);
	}

	private void addWithoutTrace(GeoElement geo) {
		updateWithoutTrace(geo);
		addToCellRangeAlgos(geo);
	}

	@Override
	public void remove(GeoElement geo) {
		GPoint location = geo.getSpreadsheetCoords();
		if (location != null) {
			doRemove(location.y, location.x);
			cellRangeManager.updateCellRangeAlgos(geo, location, true);
		}
	}

	@Override
	public void rename(GeoElement geo) {
		GPoint location = geo.getOldSpreadsheetCoords();
		if (location != null) {
			doRemove(location.y, location.x);
			cellRangeManager.updateCellRangeAlgos(geo, location, true);
		}
		addWithoutTrace(geo);
	}

	private void doRemove(int row, int col) {
		setValueAt(null, row, col);
		updateHighestUsedColAndRow(col, row);
	}

	@Override
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

	@Override
	public void updateLocation(GeoElement geo) {
		updateWithoutTrace(geo);
	}

	@Override
	public void clearView() {

		for (int c = 0; c < getColumnCount(); ++c) {
			for (int r = 0; r < getRowCount(); ++r) {
				setValueAt(null, r, c);
			}
		}
		highestUsedColumn = -1;
		highestUsedRow = -1;
		cellRangeManager.clear();
	}

	@Override
	public final void updateVisualStyle(GeoElement geo, GProperty prop) {
		if (prop == GProperty.FONT && geo instanceof TextProperties) {
			GPoint pt = geo.getSpreadsheetCoords();
			if (pt != null) {
				getCellFormat(null).setFormat(pt, CellFormat.FORMAT_FONTSTYLE,
						((TextProperties) geo).getFontStyle());
			}
		}
		updateWithoutTrace(geo);
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		// nothing to do here
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// not used
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// ignore
	}

	@Override
	public void repaintView() {
		// ignore
	}

	@Override
	public void reset() {
		// ignore
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		// ignore
	}

	@Override
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

	/**
	 * @param table
	 *            table
	 * @return cell formatter
	 */
	public CellFormatInterface getCellFormat(MyTableInterface table) {
		if (formatHandler == null) {
			formatHandler = new CellFormat(table, app);
		} else if (table != null) {
			formatHandler.setTable(table);
		}
		return formatHandler;

	}
}
