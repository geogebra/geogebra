package geogebra.web.gui.view.spreadsheet;

import geogebra.common.awt.GPoint;
import geogebra.common.awt.GRectangle;
import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

public class SpreadsheetColumnHeaderW implements MouseDownHandler,
        MouseUpHandler, MouseMoveHandler, ClickHandler, DoubleClickHandler,
        KeyDownHandler

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

		/*
		 * setFocusable(true); setAutoscrolls(false); addMouseListener(this);
		 * addMouseMotionListener(this); addKeyListener(this);
		 * setFixedCellWidth(SpreadsheetView.ROW_HEADER_WIDTH);
		 * 
		 * setCellRenderer(new RowHeaderRenderer(table, this));
		 * 
		 * table.getSelectionModel().addListSelectionListener(this);
		 */

	}

	private void registerListeners() {

		grid.addDomHandler(this, MouseDownEvent.getType());
		grid.addDomHandler(this, MouseUpEvent.getType());
		grid.addDomHandler(this, MouseMoveEvent.getType());
		grid.addDomHandler(this, ClickEvent.getType());
		grid.addDomHandler(this, DoubleClickEvent.getType());

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
		elm.getStyle().setBackgroundColor(
		        MyTableW.BACKGROUND_COLOR_HEADER.toString());
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

		String defaultBackground = MyTableW.BACKGROUND_COLOR_HEADER.toString();
		String selectedBackground = MyTableW.SELECTED_BACKGROUND_COLOR_HEADER
		        .toString();

		for (int colIndex = 0; colIndex < grid.getColumnCount(); colIndex++) {
			Style s = grid.getCellFormatter().getElement(0, colIndex)
			        .getStyle();

			if (table.getSelectionType() == MyTable.ROW_SELECT) {
				setBgColorIfNeeded(s, defaultBackground);
			} else {
				if (table.selectedColumnSet.contains(colIndex)
				        || (colIndex >= table.minSelectionColumn && colIndex <= table.maxSelectionColumn)) {
					setBgColorIfNeeded(s, selectedBackground);
				} else {
					setBgColorIfNeeded(s, defaultBackground);
				}
			}
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

	private static void setBgColorIfNeeded(Style s, String bgColor) {
		if (!s.getBackgroundColor().equals(bgColor))
			s.setBackgroundColor(bgColor);
	}

	/**
	 * @param p
	 *            location of mouse (in client area pixels)
	 * @return index of the column to be resized if mouse point p is near a
	 *         column boundary (within 3 pixels)
	 */
	private int getResizingColumn(GPoint p) {
		int resizeColumn = -1;
		GPoint point = table.getIndexFromPixel(p.x, 0);
		if (point != null) {
			// test if mouse is 3 pixels from column boundary
			int cellColumn = point.getX();
			if (cellColumn >= 0) {
				GRectangle r = table.getCellRect(0, cellColumn, true);
				// near column left ?
				if (p.x < r.getX() + 3) {
					resizeColumn = cellColumn - 1;
				}
				// near column right ?
				if (p.x > r.getX() + r.getWidth() - 3) {
					resizeColumn = cellColumn;
				}
			}
		}

		return resizeColumn;
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

		isMouseDown = true;
		e.preventDefault();

		int x = SpreadsheetMouseListenerW.getAbsoluteX(e, app);
		int y = SpreadsheetMouseListenerW.getAbsoluteY(e, app);
		boolean metaDown = e.isControlKeyDown();// ||
		                                        // e.isMetaKeyDown();//AppW.isControlDown(e);
		boolean shiftDown = e.isShiftKeyDown();
		boolean rightClick = (e.getNativeButton() == NativeEvent.BUTTON_RIGHT);// AppW.isRightClick(e);

		// ?//if (!view.hasViewFocus())
		// ?// ((LayoutW) app.getGuiManager().getLayout()).getDockManager()
		// ?// .setFocusedPanel(App.VIEW_SPREADSHEET);

		if (!rightClick) {
			GPoint point = table.getIndexFromPixel(x, y);

			if (point == null) {
				return;
			}

			// mouse down in resizing region
			GPoint p = new GPoint(x, y);
			resizingColumn = getResizingColumn(p);
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

	public void onMouseUp(MouseUpEvent e) {

		isMouseDown = false;
		e.preventDefault();

		boolean rightClick = (e.getNativeButton() == NativeEvent.BUTTON_RIGHT);

		if (rightClick) {

			if (!app.letShowPopupMenu()) {
				return;
			}

			GPoint p = table.getIndexFromPixel(
			        SpreadsheetMouseListenerW.getAbsoluteX(e, app),
			        SpreadsheetMouseListenerW.getAbsoluteY(e, app));
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

			// show contextMenu
			SpreadsheetContextMenuW popupMenu = ((GuiManagerW) app
			        .getGuiManager()).getSpreadsheetContextMenu(table,
			        e.isShiftKeyDown());
			popupMenu.show(view.getFocusPanel(), e.getX(), e.getY());
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

	// ===============================================
	// MouseMotion Listener Methods
	// ===============================================

	public void onMouseMove(MouseMoveEvent e) {

		e.preventDefault();

		// Show resize cursor when mouse is over a row boundary
		GPoint p = new GPoint(e.getClientX(), e.getClientY());
		int r = this.getResizingColumn(p);
		if (r >= 0 && !getCursor().equals(Style.Cursor.ROW_RESIZE)) {
			setColumnResizeCursor();
		} else if (!getCursor().equals(Style.Cursor.DEFAULT)) {
			setDefaultCursor();
		}

		// handles mouse over a trace button

		/*
		 * TODO int column = -1; boolean isOver = false; java.awt.Point mouseLoc
		 * = e.getPoint(); GPoint cellLoc = table.getIndexFromPixel(mouseLoc.x,
		 * mouseLoc.y); if (cellLoc != null) { column = cellLoc.x; if
		 * (app.getTraceManager().isTraceColumn(column)) { // adjust mouseLoc to
		 * the coordinate space of this column header mouseLoc.x = mouseLoc.x -
		 * table.getCellRect(0, column, true).x;
		 * 
		 * // int lowBound = table.getCellRect(0, column, true).x + 3; // isOver
		 * = mouseLoc.x > lowBound && mouseLoc.x < lowBound + 24;
		 * 
		 * // Point sceeenMouseLoc = //
		 * MouseInfo.getPointerInfo().getLocation(); isOver =
		 * ((ColumnHeaderRenderer) table.getColumnModel()
		 * .getColumn(column).getHeaderRenderer()) .isOverTraceButton(column,
		 * mouseLoc, table .getColumnModel().getColumn(column)
		 * .getHeaderValue()); } }
		 * 
		 * // "isOver = " + isOver ); if (isOver && overTraceButtonColumn !=
		 * column) { overTraceButtonColumn = column; if (table.getTableHeader()
		 * != null) { table.getTableHeader().resizeAndRepaint(); } } if (!isOver
		 * && overTraceButtonColumn > 0) { overTraceButtonColumn = -1; if
		 * (table.getTableHeader() != null) {
		 * table.getTableHeader().resizeAndRepaint(); } }
		 */

		// DRAG

		if (isMouseDown) {

			if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
				return;
			}

			int x = SpreadsheetMouseListenerW.getAbsoluteX(e, app);
			int y = SpreadsheetMouseListenerW.getAbsoluteY(e, app);

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

	// ===============================================
	// Key Listener Methods
	// ===============================================

	/*
	 * public void keyTyped(KeyEvent e) { }
	 * 
	 * public void keyPressed(KeyEvent e) {
	 * 
	 * int keyCode = e.getKeyCode();
	 * 
	 * boolean metaDown = AppD.isControlDown(e); boolean altDown =
	 * e.isAltDown(); boolean shiftDown = e.isShiftDown();
	 * 
	 * // Application.debug(keyCode); switch (keyCode) {
	 * 
	 * case KeyEvent.VK_UP:
	 * 
	 * if (shiftDown) { // extend the column selection int row =
	 * table.getSelectionModel().getLeadSelectionIndex();
	 * table.changeSelection(row - 1, -1, false, true); } else { // select
	 * topmost cell in first column to the left of the // selection if
	 * (table.minSelectionRow > 0) table.setSelection(0, table.minSelectionRow -
	 * 1); else table.setSelection(0, table.minSelectionRow);
	 * table.requestFocus(); } break;
	 * 
	 * case KeyEvent.VK_DOWN: if (shiftDown) { // extend the column selection
	 * int row = table.getSelectionModel().getLeadSelectionIndex();
	 * table.changeSelection(row + 1, -1, false, true); } else { // select
	 * topmost cell in first column to the left of the // selection if
	 * (table.minSelectionRow > 0) table.setSelection(0, table.minSelectionRow +
	 * 1); else table.setSelection(0, table.minSelectionRow);
	 * table.requestFocus(); } break;
	 * 
	 * case KeyEvent.VK_C: // control + c if (metaDown && minSelectionRow != -1
	 * && maxSelectionRow != -1) { table.copyPasteCut.copy(0, minSelectionRow,
	 * table.getModel() .getColumnCount() - 1, maxSelectionRow, altDown);
	 * e.consume(); } break; case KeyEvent.VK_V: // control + v if (metaDown &&
	 * minSelectionRow != -1 && maxSelectionRow != -1) { boolean storeUndo =
	 * table.copyPasteCut.paste(0, minSelectionRow,
	 * table.getModel().getColumnCount() - 1, maxSelectionRow); if (storeUndo)
	 * app.storeUndoInfo(); e.consume(); } break; case KeyEvent.VK_X: // control
	 * + x if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
	 * table.copyPasteCut.copy(0, minSelectionRow, table.getModel()
	 * .getColumnCount() - 1, maxSelectionRow, altDown); e.consume(); } boolean
	 * storeUndo = table.copyPasteCut.delete(0, minSelectionRow,
	 * table.getModel().getColumnCount() - 1, maxSelectionRow); if (storeUndo)
	 * app.storeUndoInfo(); break;
	 * 
	 * case KeyEvent.VK_DELETE: // delete case KeyEvent.VK_BACK_SPACE: // delete
	 * on MAC storeUndo = table.copyPasteCut.delete(0, minSelectionRow, table
	 * .getModel().getColumnCount() - 1, maxSelectionRow); if (storeUndo)
	 * app.storeUndoInfo(); break; } }
	 * 
	 * public void keyReleased(KeyEvent e) { }
	 */

}
