package org.geogebra.web.web.gui.view.spreadsheet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.EventUtil;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

public class SpreadsheetColumnHeaderW implements MouseDownHandler,
        MouseUpHandler, MouseMoveHandler, ClickHandler, DoubleClickHandler,
        KeyDownHandler, LongTouchHandler, TouchStartHandler, TouchMoveHandler, TouchEndHandler

{
	private static final long serialVersionUID = 1L;
	private AppW app;
	private SpreadsheetViewW view;
	private Kernel kernel;
	private MyTableW table;
	private Grid grid;

	private FlowPanel container;
	private FocusPanel focusPanel;

	private int mouseXOffset, resizingColumn = -1;

	private boolean isMouseDown = false;

	protected int column0 = -1;
	private boolean doColumnResize = false;

	private int overTraceButtonColumn = -1;
	
	private LongTouchManager longTouchManager;

	/***************************************************
	 * Constructor
	 */
	public SpreadsheetColumnHeaderW(AppW app, MyTableW table) {

		this.app = app;
		this.table = table;
		this.view = (SpreadsheetViewW) table.getView();
		this.kernel = app.getKernel();

		prepareGUI();
		registerListeners();

		longTouchManager = LongTouchManager.getInstance();
	}

	private void registerListeners() {

		grid.addDomHandler(this, MouseDownEvent.getType());
		grid.addDomHandler(this, MouseUpEvent.getType());
		grid.addDomHandler(this, MouseMoveEvent.getType());
		grid.addDomHandler(this, ClickEvent.getType());
		grid.addDomHandler(this, DoubleClickEvent.getType());
		grid.addDomHandler(this, TouchStartEvent.getType());
		grid.addDomHandler(this, TouchEndEvent.getType());
		grid.addDomHandler(this, TouchMoveEvent.getType());

	}

	// ============================================
	// GUI handlers
	// ============================================

	private void initializeCell(int colIndex) {

		String name = GeoElementSpreadsheet.getSpreadsheetColumnName(colIndex);
		grid.setText(0, colIndex, name);

		int columnWidth = table.preferredColumnWidth;
		grid.getColumnFormatter().getElement(colIndex).getStyle()
		        .setWidth(columnWidth, Style.Unit.PX);

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
		        .setHeight(rowHeight, Style.Unit.PX);

		for (int col = 0; col < grid.getColumnCount(); col++) {
			initializeCell(col);
		}

		focusPanel = new FocusPanel();
		focusPanel.addKeyDownHandler(this);
		Style s = focusPanel.getElement().getStyle();
		// s.setDisplay(Style.Display.NONE);
		s.setPosition(Style.Position.ABSOLUTE);
		s.setTop(0, Unit.PX);
		s.setLeft(0, Unit.PX);

		container = new FlowPanel();
		container.add(grid);
		container.add(focusPanel);

	}

	public void updateColumnCount() {

		if (grid.getColumnCount() >= table.getColumnCount())
			return;

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
		grid.getElement().getStyle().setCursor(Style.Cursor.COL_RESIZE);
	}

	private void setDefaultCursor() {
		grid.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
	}

	public void renderSelection() {

		/*String defaultBackground = MyTableW.BACKGROUND_COLOR_HEADER.toString();
		String selectedBackground = MyTableW.SELECTED_BACKGROUND_COLOR_HEADER
		        .toString();*/

		for (int colIndex = 0; colIndex < grid.getColumnCount(); colIndex++) {
			/*Style s = grid.getCellFormatter().getElement(0, colIndex)
			        .getStyle();*/

			if (table.getSelectionType() == MyTable.ROW_SELECT) {
				//setBgColorIfNeeded(s, defaultBackground);
				updateCellSelection(false, colIndex);
			} else {
				if (table.selectedColumnSet.contains(colIndex)
				        || (colIndex >= table.minSelectionColumn && colIndex <= table.maxSelectionColumn)) {
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
	 * Added "selected" class to the table headers of the selected cell
	 * needed for css styling
	 * @param selected
	 * @param index
	 */
	private void updateCellSelection(boolean selected, int index) {
		if(selected) {
			grid.getCellFormatter().addStyleName(0, index, "selected");
		} else {
			grid.getCellFormatter().removeStyleName(0, index, "selected");
		}
	}

	/**
	 * @param rowIndex
	 *            index of row to set height
	 * @param rowHeight
	 *            new row height
	 */
	public void setColumnWidth(int columnIndex, int width) {

		if (columnIndex >= grid.getColumnCount()) {
			return;
		}

		grid.getColumnFormatter().getElement(columnIndex).getStyle()
		        .setWidth(width, Style.Unit.PX);
	}

	/* Steffi: not needed anymore -> NOW: updateCellSelection
	 * private static void setBgColorIfNeeded(Style s, String bgColor) {
		if (!s.getBackgroundColor().equals(bgColor))
			s.setBackgroundColor(bgColor);
	}*/

	/**
	 * @param p
	 *            location of mouse (in client area pixels)
	 * @param boundary
	 *  		  the boundary
	 * @return index of the column to be resized if mouse point p is near a
	 *         column boundary
	 */
	private int getResizingColumn(GPoint p, int boundary) {
		int resizeColumn = -1;
		GPoint point = table.getIndexFromPixel(p.x, 0);
		if (point != null) {
			// test if mouse is 3 pixels from column boundary
			int cellColumn = point.getX();
			if (cellColumn >= 0) {
				GRectangle r = table.getCellRect(0, cellColumn, true);
				// near column left ?
				if (p.x < r.getX() + boundary) {
					resizeColumn = cellColumn - 1;
				}
				// near column right ?
				if (p.x > r.getX() + r.getWidth() - boundary) {
					resizeColumn = cellColumn;
				}
			}
		}

		return resizeColumn;
	}
	
	private int getBoundary(PointerEventType eventType) {
		return eventType == PointerEventType.MOUSE ? 3 : 6;
	}

	// ===============================================
	// Renderer
	// ===============================================

	/**
	 * Update the rowHeader list when row selection changes in the table
	 */
	/*
	 * public void valueChanged(ListSelectionEvent e) { ListSelectionModel
	 * selectionModel = (ListSelectionModel) e.getSource(); minSelectionRow =
	 * selectionModel.getMinSelectionIndex(); maxSelectionRow =
	 * selectionModel.getMaxSelectionIndex(); repaint(); }
	 */

	// Returns index of row to be resized if mouse point P is
	// near a row boundary (within 3 pixels)
	/*
	 * private int getResizingRow(java.awt.Point p) { int resizeRow = -1; GPoint
	 * point = table.getIndexFromPixel(p.x, p.y); if (point != null) { // test
	 * if mouse is 3 pixels from row boundary int cellRow = point.getY(); if
	 * (cellRow >= 0) { Rectangle r = table.getCellRect(cellRow, 0, true); //
	 * near row bottom if (p.y < r.y + 3) { resizeRow = cellRow - 1; } // near
	 * row top if (p.y > r.y + r.height - 3) { resizeRow = cellRow; } } } return
	 * resizeRow; }
	 */

	// Cursor change for when mouse is over a row boundary
	/*
	 * private void swapCursor() { Cursor tmp = getCursor();
	 * setCursor(otherCursor); otherCursor = tmp; }
	 */

	// ===============================================
	// Mouse Listener Methods
	// ===============================================

	/*
	 * public void mouseClicked(MouseEvent e) {
	 * 
	 * // Double clicking on a row boundary auto-adjusts the // height of the
	 * row above the boundary (the resizingRow)
	 * 
	 * if (resizingRow >= 0 && !AppD.isRightClick(e) && e.getClickCount() == 2)
	 * { table.fitRow(resizingRow); e.consume(); } }
	 * 
	 * public void mouseEntered(MouseEvent e) { }
	 * 
	 * public void mouseExited(MouseEvent e) { }
	 */

	public void onMouseDown(MouseDownEvent e) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		e.preventDefault();
		PointerEvent event = PointerEvent.wrapEvent(e, ZeroOffset.instance);
		onPointerDown(event);
	}

	public void onMouseUp(MouseUpEvent e) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		e.preventDefault();
		PointerEvent event = PointerEvent.wrapEvent(e, ZeroOffset.instance);
		onPointerUp(event);
	}

	// ===============================================
	// MouseMotion Listener Methods
	// ===============================================

	public void onMouseMove(MouseMoveEvent e) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		e.preventDefault();
		PointerEvent event = PointerEvent.wrapEvent(e, ZeroOffset.instance);
		onPointerMove(event);
	}
	
	private void onPointerDown(PointerEvent e) {
		Event.setCapture(grid.getElement());
		isMouseDown = true;

		if (table.getEditor().isEditing()) {
			table.getEditor().setAllowProcessGeo(true);
			table.getEditor().stopCellEditing();
			table.getEditor().setAllowProcessGeo(false);
			table.finishEditing();
		}

		int x = SpreadsheetMouseListenerW.getAbsoluteX(e.getWrappedEvent(), app);
		int y = SpreadsheetMouseListenerW.getAbsoluteY(e.getWrappedEvent(), app);

		boolean metaDown = e.isControlDown();
		boolean shiftDown = e.isShiftDown();
		boolean rightClick = e.isRightClick();

		if (!rightClick) {
			GPoint point = table.getIndexFromPixel(x, y);

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
				if (point.x == this.overTraceButtonColumn) {
					int column = point.getX();
					table.setColumnSelectionInterval(column, column);
					// ?//view.showTraceDialog(null,
					// ?// table.selectedCellRanges.get(0));
					// ?//e.consume();
					return;
				}

				// otherwise handle column selection
				if (table.getSelectionType() != MyTable.COLUMN_SELECT) {
					table.setSelectionType(MyTable.COLUMN_SELECT);
					// ?//if (table.getTableHeader() != null) {
					// ?// table.getTableHeader().requestFocusInWindow();
					// ?//}
				}

				if (shiftDown) {
					if (column0 != -1) {
						int column = point.getX();
						table.setColumnSelectionInterval(column0, column);
					}
				} else if (metaDown) {
					column0 = point.getX();
					// Note: ctrl-select now handled in
					// table.changeSelection
					table.setColumnSelectionInterval(column0, column0);
				} else {
					column0 = point.getX();
					table.setColumnSelectionInterval(column0, column0);
				}
				renderSelection();
			}

		}
	}
	
	private void onPointerUp(PointerEvent e) {
		Event.releaseCapture(grid.getElement());
		isMouseDown = false;

		boolean rightClick = (e.isRightClick());

		if (rightClick) {

			if (!app.letShowPopupMenu()) {
				return;
			}

			GPoint p = table.getIndexFromPixel(
					SpreadsheetMouseListenerW.getAbsoluteX(e.getWrappedEvent(), app),
					SpreadsheetMouseListenerW.getAbsoluteY(e.getWrappedEvent(), app));
					
			if (p == null) {
				return;
			}

			// if click is outside current selection then change selection
			if (p.getY() < table.minSelectionRow
			        || p.getY() > table.maxSelectionRow
			        || p.getX() < table.minSelectionColumn
			        || p.getX() > table.maxSelectionColumn) {
				// switch to column selection mode and select column
				if (table.getSelectionType() != MyTable.COLUMN_SELECT) {
					table.setSelectionType(MyTable.COLUMN_SELECT);
				}

				// selectNone();
				table.setColumnSelectionInterval(p.getX(), p.getX());
				renderSelection();
			}

			showContextMenu(e.getX(), e.getY(), true);
		}

		// left click
		
		if (doColumnResize) {
			// If column resize has happened, resize all other selected columns
			App.debug("doing column resize");
			int columnWidth = table.getColumnWidth(resizingColumn);
			// App.debug("doRowResiz for selection: " + rowHeight);
			// App.debug("min/max " + table.minSelectionRow + " , " +
			// table.maxSelectionRow);
			if (table.minSelectionColumn != -1
			        && table.maxSelectionColumn != -1
			        && (table.maxSelectionColumn - table.minSelectionColumn > 0)) {
				if (table.isSelectAll())
					table.setColumnWidth(columnWidth);
				else
					for (int col = table.minSelectionColumn; col <= table.maxSelectionColumn; col++) {
						App.debug("setting column, width: " + col + " , "
						        + columnWidth);
						table.setColumnWidth(col, columnWidth);
					}
			}
			table.repaint();
			table.renderSelectionDeferred();
			doColumnResize = false;
		}

	}
	
	private void onPointerMove(PointerEvent e) {
		// Show resize cursor when mouse is over a row boundary
		HumanInputEvent<?> event = e.getWrappedEvent();
		GPoint p = new GPoint(
				EventUtil.getTouchOrClickClientX(event),
				EventUtil.getTouchOrClickClientY(event));
		int r = this.getResizingColumn(p, getBoundary(e.getType()));
		if (r >= 0 && !getCursor().equals(Style.Cursor.COL_RESIZE)) {
			setColumnResizeCursor();
		} else if (!getCursor().equals(Style.Cursor.DEFAULT)) {
			setDefaultCursor();
		}
		// DRAG

		if (isMouseDown) {

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
				GPoint point = table.getIndexFromPixel(x, y);
				if (point != null) {
					int column = point.getX();
					if (column0 == -1) {
						column0 = column;
					}
					table.setColumnSelectionInterval(column0, column);
					renderSelection();
				}
			}
		}
	}

	public void onDoubleClick(DoubleClickEvent event) {
		// TODO Auto-generated method stub

	}

	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	public void onKeyDown(KeyDownEvent event) {
		// TODO Auto-generated method stub

	}

	public void onTouchEnd(TouchEndEvent event) {
		longTouchManager.cancelTimer();
		event.preventDefault();
		PointerEvent e = PointerEvent.wrapEvent(event, ZeroOffset.instance);
		onPointerUp(e);
	    CancelEventTimer.touchEventOccured();
    }

	public void onTouchMove(TouchMoveEvent event) {
		event.preventDefault();
		PointerEvent e = PointerEvent.wrapEvent(event, ZeroOffset.instance);
		if (doColumnResize) {
			// resizing a column cancel long touch
			longTouchManager.cancelTimer();
		} else {
			longTouchManager.rescheduleTimerIfRunning(this, e.getX(), e.getY(), false);
		}
		onPointerMove(e);
		CancelEventTimer.touchEventOccured();
    }

	public void onTouchStart(TouchStartEvent event) {
		event.preventDefault();
		PointerEvent e = PointerEvent.wrapEvent(event, ZeroOffset.instance);
		longTouchManager.scheduleTimer(this, e.getX(), e.getY());
		onPointerDown(e);
		CancelEventTimer.touchEventOccured();
    }

	public void handleLongTouch(int x, int y) {
	    showContextMenu(x, y, false);
    }
	
	private void showContextMenu(int x, int y, boolean relative) {
		if (!app.letShowPopupMenu()) {
			return;
		}
		SpreadsheetContextMenuW contextMenu = ((GuiManagerW) app
		        .getGuiManager()).getSpreadsheetContextMenu(table);
		GPopupMenuW popup = (GPopupMenuW) contextMenu.getMenuContainer();
		if (relative) {
			popup.show(grid, x, y);
		} else {
			popup.show(new GPoint(x, y));
		}
	}
}
