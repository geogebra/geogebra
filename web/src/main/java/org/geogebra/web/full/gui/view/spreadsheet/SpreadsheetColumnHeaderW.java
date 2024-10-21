package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.spreadsheet.core.SelectionType;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Cursor;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.HumanInputEvent;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.FocusPanel;
import org.gwtproject.user.client.ui.Grid;
import org.gwtproject.user.client.ui.Widget;

public class SpreadsheetColumnHeaderW implements SpreadsheetHeader {
	private AppW app;
	private MyTableW table;
	private Grid grid;

	private FlowPanel container;
	private FocusPanel focusPanel;

	private int mouseXOffset;
	private int resizingColumn = -1;

	protected int column0 = -1;
	private boolean doColumnResize = false;

	private int overTraceButtonColumn = -1;

	private SpreadsheetHeaderController headerController;

	/***************************************************
	 * Constructor
	 */
	public SpreadsheetColumnHeaderW(AppW app, MyTableW table) {
		this.app = app;
		this.table = table;

		prepareGUI();
		headerController = new SpreadsheetHeaderController(this, grid, table);
		registerListeners();
	}

	private void registerListeners() {
		grid.addBitlessDomHandler(headerController, MouseDownEvent.getType());
		grid.addBitlessDomHandler(headerController, MouseUpEvent.getType());
		grid.addBitlessDomHandler(headerController, MouseMoveEvent.getType());
		grid.addBitlessDomHandler(headerController, TouchStartEvent.getType());
		grid.addBitlessDomHandler(headerController, TouchEndEvent.getType());
		grid.addBitlessDomHandler(headerController, TouchMoveEvent.getType());
	}

	// ============================================
	// GUI handlers
	// ============================================

	private void initializeCell(int colIndex) {

		String name = GeoElementSpreadsheet.getSpreadsheetColumnName(colIndex);
		grid.setText(0, colIndex, name);

		int columnWidth = table.preferredColumnWidth();
		grid.getColumnFormatter().getElement(colIndex).getStyle()
		        .setWidth(columnWidth, Unit.PX);

		Element elm = grid.getCellFormatter().getElement(0, colIndex);
		elm.addClassName("SVheader");
		/*elm.getStyle().setBackgroundColor(
		        MyTableW.BACKGROUND_COLOR_HEADER.toString());*/
	}

	private void prepareGUI() {

		grid = new Grid(1, table.getModel().getColumnCount());

		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		grid.setHeight("0px");

		grid.getElement().addClassName("geogebraweb-table-spreadsheet");

		int rowHeight = app.getSettings().getSpreadsheet().preferredRowHeight();
		grid.getRowFormatter().getElement(0).getStyle()
		        .setHeight(rowHeight, Unit.PX);

		for (int col = 0; col < grid.getColumnCount(); col++) {
			initializeCell(col);
		}

		focusPanel = new FocusPanel();
		focusPanel.addKeyDownHandler(this);
		Style s = focusPanel.getElement().getStyle();
		// s.setDisplay(Style.Display.NONE);
		s.setPosition(Position.ABSOLUTE);
		s.setTop(0, Unit.PX);
		s.setLeft(0, Unit.PX);

		container = new FlowPanel();
		container.add(grid);
		container.add(focusPanel);
	}

	/**
	 * Update the column count and width in header to match the table.
	 */
	public void updateColumnCount() {
		if (grid.getColumnCount() >= table.getColumnCount()) {
			return;
		}

		int oldColumnCount = grid.getColumnCount();
		grid.resizeColumns(table.getColumnCount());

		for (int i = oldColumnCount; i < table.getColumnCount(); ++i) {
			initializeCell(i);
		}
	}

	// ============================================
	// Getters/Setters
	// ============================================

	public Widget getContainer() {
		return container;
	}

	public void setLeft(int left) {
		container.getElement().getStyle().setLeft(left, Unit.PX);
	}

	public int getOffsetHeight() {
		return getContainer().getOffsetHeight();
	}

	private String getCursor() {
		return grid.getElement().getStyle().getCursor();
	}

	private void setColumnResizeCursor() {
		grid.getElement().getStyle().setCursor(Cursor.COL_RESIZE);
	}

	private void setDefaultCursor() {
		grid.getElement().getStyle().setCursor(Cursor.DEFAULT);
	}

	/**
	 * Update selection rectangle on screen
	 */
	public void renderSelection() {

		/*String defaultBackground = MyTableW.BACKGROUND_COLOR_HEADER.toString();
		String selectedBackground = MyTableW.SELECTED_BACKGROUND_COLOR_HEADER
		        .toString();*/

		for (int colIndex = 0; colIndex < grid.getColumnCount(); colIndex++) {
			/*Style s = grid.getCellFormatter().getElement(0, colIndex)
			        .getStyle();*/

			if (table.getSelectionType() == SelectionType.ROWS) {
				//setBgColorIfNeeded(s, defaultBackground);
				updateCellSelection(false, colIndex);
			} else {
				if (table.selectedColumnSet.contains(colIndex)
						|| (colIndex >= table.minSelectionColumn
								&& colIndex <= table.maxSelectionColumn)) {
					//setBgColorIfNeeded(s, selectedBackground);
					updateCellSelection(true, colIndex);
				} else {
					//setBgColorIfNeeded(s, defaultBackground);
					updateCellSelection(false, colIndex);
				}
			}
		}
	}

	/**
	 * Added "selected" class to the table headers of the selected cell needed
	 * for css styling
	 *
	 * @param selected
	 *            whether to select
	 * @param index
	 *            column index
	 */
	private void updateCellSelection(boolean selected, int index) {
		if (selected) {
			grid.getCellFormatter().addStyleName(0, index, "selected");
		} else {
			grid.getCellFormatter().removeStyleName(0, index, "selected");
		}
	}

	/**
	 * @param columnIndex
	 *            index of row to set height
	 * @param width
	 *            new row height
	 */
	public void setColumnWidth(int columnIndex, int width) {

		if (columnIndex >= grid.getColumnCount()) {
			return;
		}

		grid.getColumnFormatter().getElement(columnIndex).getStyle()
		        .setWidth(width, Unit.PX);
	}

	/**
	 * @param p location of mouse (in client area pixels)
	 * @param boundary the boundary
	 * @return index of the column to be resized if mouse point p is near a
	 *         column boundary
	 */
	private int getResizingColumn(GPoint p, int boundary) {
		int resizeColumn = -1;
		SpreadsheetCoords point = table.getIndexFromPixel(p.x, 0);
		if (point != null) {
			// test if mouse is 3 pixels from column boundary
			int cellColumn = point.column;
			if (cellColumn >= 0) {
				GRectangle r = table.getCellRect(0, cellColumn, false);
				// near column left ?
				if (r != null && p.x < r.getX() + boundary) {
					resizeColumn = cellColumn - 1;
				}
				// near column right ?
				if (r != null && p.x > r.getX() + r.getWidth() - boundary) {
					resizeColumn = cellColumn;
				}
			}
		}

		return resizeColumn;
	}

	private static int getBoundary(PointerEventType eventType) {
		return eventType == PointerEventType.MOUSE ? 3 : 6;
	}

	// ===============================================
	// MouseMotion Listener Methods
	// ===============================================
	@Override
	public void onPointerDown(PointerEvent e) {
		int x = SpreadsheetMouseListenerW.getAbsoluteX(e.getWrappedEvent(), app);
		int y = SpreadsheetMouseListenerW.getAbsoluteY(e.getWrappedEvent(), app);

		boolean shiftDown = e.isShiftDown();
		boolean rightClick = e.isRightClick();
		if (!rightClick) {
			SpreadsheetCoords point = table.getIndexFromPixel(x, y);

			if (point == null) {
				return;
			}
			// mouse down in resizing region
			GPoint p = new GPoint(x, y);
			resizingColumn = getResizingColumn(p, getBoundary(e.getType()));
			if (resizingColumn >= 0) {
				mouseXOffset = p.x - table.getColumnWidth(resizingColumn);
			}

			// standard mouse down
			else {

				// launch trace dialog if over a trace button
				if (point.column == this.overTraceButtonColumn) {
					int column = point.column;
					table.setColumnSelectionInterval(column, column);
					return;
				}

				// otherwise handle column selection
				if (table
						.getSelectionType() != SelectionType.COLUMNS) {
					table.setSelectionType(SelectionType.COLUMNS);
				}

				if (shiftDown) {
					if (column0 != -1) {
						int column = point.column;
						table.setColumnSelectionInterval(column0, column);
					}
				} else {

					column0 = point.column;
					table.setColumnSelectionInterval(column0, column0);
				}
				renderSelection();
			}
		}
	}

	@Override
	public void onPointerUp(PointerEvent e) {

		// left click

		if (doColumnResize) {
			// If column resize has happened, resize all other selected columns
			Log.debug("doing column resize");
			int columnWidth = table.getColumnWidth(resizingColumn);
			// Log.debug("doRowResiz for selection: " + rowHeight);
			// Log.debug("min/max " + table.minSelectionRow + " , " +
			// table.maxSelectionRow);
			if (table.minSelectionColumn != -1
			        && table.maxSelectionColumn != -1
			        && (table.maxSelectionColumn - table.minSelectionColumn > 0)) {
				if (table.isSelectAll()) {
					table.setColumnWidth(columnWidth);
				} else {
					int maxColumn = table.maxSelectionColumn;
					for (int col = table.minSelectionColumn; col <= maxColumn; col++) {
						Log.debug("setting column, width: " + col + " , "
						        + columnWidth);
						table.setColumnWidth(col, columnWidth);
					}
				}
			}
			table.repaint();
			table.renderSelectionDeferred();
			doColumnResize = false;
		}

	}

	@Override
	public void onPointerMove(PointerEvent e) {
		// Show resize cursor when mouse is over a row boundary
		HumanInputEvent<?> event = e.getWrappedEvent();
		GPoint p = new GPoint(
				SpreadsheetMouseListenerW.getAbsoluteX(event, app),
				SpreadsheetMouseListenerW.getAbsoluteY(event, app));
		int r = this.getResizingColumn(p, getBoundary(e.getType()));

		if (r >= 0 && !getCursor().equals(Cursor.COL_RESIZE.getCssName())) {
			setColumnResizeCursor();
		} else if (r < 0
				&& !getCursor().equals(Cursor.DEFAULT.getCssName())) {
			setDefaultCursor();
		}
		// DRAG

		if (headerController.isMouseDown) {

			if (e.isRightClick()) {
				return;
			}

			int x = SpreadsheetMouseListenerW.getAbsoluteX(e.getWrappedEvent(), app);
			int y = SpreadsheetMouseListenerW.getAbsoluteY(e.getWrappedEvent(), app);

			// Handle mouse drag

			if (resizingColumn >= 0) {
				// Resize a column
				int newWidth = x - mouseXOffset;
				if (newWidth > 0) {
					table.setColumnWidth(resizingColumn, newWidth);
					// flag to resize all selected rows on mouse release
					doColumnResize = true;
					table.repaint();
					renderSelection();
				}
			}

			else {
				// Select a column
				SpreadsheetCoords point = table.getIndexFromPixel(x, y);
				if (point != null) {
					int column = point.column;
					if (column0 == -1) {
						column0 = column;
					}
					table.setColumnSelectionInterval(column0, column);
					renderSelection();
				}
			}
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showContextMenu(int x, int y, boolean relative) {
		if (!app.letShowPopupMenu()) {
			return;
		}
		SpreadsheetContextMenuW contextMenu = ((GuiManagerW) app
		        .getGuiManager()).getSpreadsheetContextMenu(table);
		GPopupMenuW popup = contextMenu.getMenuContainer();
		if (relative) {
			popup.show(grid, x, y);
		} else {
			popup.show(x, y);
		}
	}

	@Override
	public boolean isResizing() {
		return doColumnResize;
	}

	@Override
	public void updateSelection(SpreadsheetCoords p) {
		// switch to column selection mode and select column
		if (table
				.getSelectionType() != SelectionType.COLUMNS) {
			table.setSelectionType(SelectionType.COLUMNS);
		}

		// selectNone();
		table.setColumnSelectionInterval(p.column, p.column);
		renderSelection();
	}

	public int getContentWidth() {
		return grid.getOffsetWidth();
	}
}
