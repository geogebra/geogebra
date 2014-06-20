package geogebra.web.gui.view.spreadsheet;

import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Grid;

public class SpreadsheetColumnHeaderW extends Grid implements MouseDownHandler,
        MouseUpHandler, MouseMoveHandler, ClickHandler, DoubleClickHandler
/*
 * extends JList implements MouseListener, MouseMotionListener, KeyListener,
 * ListSelectionListener
 */

{
	private static final long serialVersionUID = 1L;
	private AppW app;
	private SpreadsheetViewW view;
	private Kernel kernel;
	private MyTableW table;

	// note: MyTable uses its own minSelectionRow and maxSelectionRow.
	// The selection listener keeps them in sync.
	private int minSelectionRow = -1;
	private int maxSelectionRow = -1;
	/*
	 * private ListSelectionModel selectionModel;
	 * 
	 * // fields for resizing rows private static Cursor resizeCursor = Cursor
	 * .getPredefinedCursor(Cursor.N_RESIZE_CURSOR); private Cursor otherCursor
	 * = resizeCursor;
	 */
	private int mouseYOffset, resizingRow = -1;

	private boolean isMouseDown = false;

	protected int column0 = -1;
	protected boolean isResizing = false;

	private int overTraceButtonColumn = -1;

	/***************************************************
	 * Constructor
	 */
	public SpreadsheetColumnHeaderW(AppW app, MyTableW table) {

		super(1, table.getModel().getColumnCount());

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

		addDomHandler(this, MouseDownEvent.getType());
		addDomHandler(this, MouseUpEvent.getType());
		addDomHandler(this, MouseMoveEvent.getType());
		addDomHandler(this, ClickEvent.getType());
		addDomHandler(this, DoubleClickEvent.getType());
	}

	private void prepareGUI() {

		setCellPadding(0);
		setCellSpacing(0);
		getElement().addClassName("geogebraweb-table-spreadsheet");

		int rowHeight = app.getSettings().getSpreadsheet().preferredRowHeight();
		getRowFormatter().getElement(0).getStyle()
		        .setHeight(rowHeight, Style.Unit.PX);

		for (int col = 0; col < this.getColumnCount(); col++) {
			initializeCell(col);
		}

	}

	private void initializeCell(int colIndex) {

		String name = GeoElementSpreadsheet.getSpreadsheetColumnName(colIndex);
		setText(0, colIndex, name);

		int columnWidth = table.preferredColumnWidth;
		getColumnFormatter().getElement(colIndex).getStyle()
		        .setWidth(columnWidth, Style.Unit.PX);

		Element elm = getCellFormatter().getElement(0, colIndex);
		elm.addClassName("SVheader");
		elm.getStyle().setBackgroundColor(
		        MyTableW.BACKGROUND_COLOR_HEADER.toString());
	}

	public void updateColumnCount() {

		if (getColumnCount() >= table.getColumnCount())
			return;

		int oldColumnCount = getColumnCount();
		resizeColumns(table.getColumnCount());

		for (int i = oldColumnCount; i < table.getColumnCount(); ++i) {
			initializeCell(i);
		}
	}

	public void renderSelection() {

		String defaultBackground = MyTableW.BACKGROUND_COLOR_HEADER.toString();
		String selectedBackground = MyTableW.SELECTED_BACKGROUND_COLOR_HEADER
		        .toString();

		for (int colIndex = 0; colIndex < this.getColumnCount(); colIndex++) {
			Style s = getCellFormatter().getElement(0, colIndex).getStyle();

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

	private static void setBgColorIfNeeded(Style s, String bgColor) {
		if (!s.getBackgroundColor().equals(bgColor))
			s.setBackgroundColor(bgColor);
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
			if (point != null) {

				// check if the cursor is within the resizing region (i.e.
				// border +- 3pixels)
				GPoint point2 = table
				        .getPixel(point.getX(), point.getY(), true);
				GPoint point3 = table.getPixel(point.getX(), point.getY(),
				        false);
				int x2 = point2.getX();
				int x3 = point3.getX();
				isResizing = !(x > x2 + 2 && x < x3 - 3);

				if (!isResizing) {

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
	}

	public void onMouseUp(MouseUpEvent e) {

		isMouseDown = false;
		e.preventDefault();

		boolean rightClick = (e.getNativeButton() == NativeEvent.BUTTON_RIGHT);

		if (!((AppW) kernel.getApplication()).letShowPopupMenu()) {
			return;
		}

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

		} else if (isResizing) {

			// ?//if (e.getClickCount() == 2) {
			// ?// return;
			// ?//}

			int x = SpreadsheetMouseListenerW.getAbsoluteX(e, app);
			int y = SpreadsheetMouseListenerW.getAbsoluteY(e, app);
			GPoint point = table.getIndexFromPixel(x, y);
			if (point == null) {
				return;
			}
			GPoint point2 = table.getPixel(point.getX(), point.getY(), false);
			int column = point.getX();
			if (x < point2.getX() - 3) {
				--column;
			}

			if (x <= 0) {
				x = 0; // G.Sturr 2010-4-10 prevent x=-1 with very small row
				       // size
			}

			int width = table.getGrid().getColumnFormatter().getElement(column)
			        .getOffsetWidth();
			int[] selected = table.getSelectedColumns();
			if (selected == null) {
				return;
			}
			boolean in = false;
			for (int i = 0; i < selected.length; ++i) {
				if (column == selected[i])
					in = true;
			}
			if (!in) {
				return;
			}
			for (int i = 0; i < selected.length; ++i) {
				table.getGrid().getColumnFormatter()
				        .setWidth(selected[i], width + "px");
				// FIXME: don't forget to write it as:
				// table.getColumnFormatter().getElement(selected[i]).getStyle().setWidth(width,
				// Style.Unit.PX);
				// the other syntax doesn't work probably
			}
		}
	}

	// ===============================================
	// MouseMotion Listener Methods
	// ===============================================

	public void onMouseMove(MouseMoveEvent e) {

		e.preventDefault();

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
				return; // G.Sturr 2009-9-30
			}

			if (isResizing) {
				return;
			}
			int x = SpreadsheetMouseListenerW.getAbsoluteX(e, app);
			int y = SpreadsheetMouseListenerW.getAbsoluteY(e, app);
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

	public void onDoubleClick(DoubleClickEvent event) {
		// TODO Auto-generated method stub

	}

	public void onClick(ClickEvent event) {
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
