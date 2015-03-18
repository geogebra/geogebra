package geogebra.web.gui.view.spreadsheet;

import geogebra.common.awt.GPoint;
import geogebra.common.awt.GRectangle;
import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.common.gui.view.spreadsheet.RelativeCopy;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.App;
import geogebra.common.main.SpreadsheetTableModel;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.gui.util.LongTouchManager;
import geogebra.html5.gui.util.LongTouchTimer.LongTouchHandler;
import geogebra.html5.main.AppW;
import geogebra.html5.openjdk.awt.geom.Rectangle2D;
import geogebra.html5.util.EventUtil;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.javax.swing.GPopupMenuW;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
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
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.user.client.Window;

public class SpreadsheetMouseListenerW implements MouseDownHandler,
        MouseUpHandler, MouseMoveHandler, DoubleClickHandler,
        TouchStartHandler, TouchEndHandler, TouchMoveHandler,
        LongTouchHandler {

	protected String selectedCellName;
	protected String prefix, postfix;

	private AppW app;
	private SpreadsheetViewW view;
	private Kernel kernel;
	private MyTableW table;
	private SpreadsheetTableModel model;
	private MyCellEditorW editor;

	private RelativeCopy relativeCopy;

	private boolean pointerIsDown = false;
	private boolean isRightClick = false;
	private final boolean editEnabled = true;

	private LongTouchManager longTouchManager;

	private int numberOfTouches = 0;
	
	/*************************************************
	 * Constructor
	 */
	public SpreadsheetMouseListenerW(AppW app, MyTableW table) {
		this.app = app;
		kernel = app.getKernel();
		this.table = table;
		view = (SpreadsheetViewW) table.getView();
		model = table.getModel();
		editor = table.getEditor();

		relativeCopy = new RelativeCopy(kernel);
		longTouchManager = LongTouchManager.getInstance();
	}
	
	public void handleLongTouch(int x, int y) {
	    showContextMenu(x, y);
	}
	
	public static int getAbsoluteX(DomEvent e, AppW app) {
		return (int) ((EventUtil.getTouchOrClickClientX(e) + Window
		        .getScrollLeft()) / app.getArticleElement().getScaleX());
	}

	public int getAbsoluteX(DomEvent e) {
		return getAbsoluteX(e, app);
	}

	public static int getAbsoluteY(DomEvent e, AppW app) {
		return (int) ((EventUtil.getTouchOrClickClientY(e) + Window
		        .getScrollTop()) / app.getArticleElement().getScaleY());
	}

	public int getAbsoluteY(DomEvent e) {
		return getAbsoluteY(e, app);
	}

	private GPoint getIndexFromEvent(DomEvent<?> event) {
		return table
		        .getIndexFromPixel(getAbsoluteX(event), getAbsoluteY(event));
	}

	private GPoint getPixelFromEvent(DomEvent<?> event) {
		return new GPoint(getAbsoluteX(event), getAbsoluteY(event));
	}

	public void onDoubleClick(DoubleClickEvent doubleClickEvent) {
		if (!editEnabled) {
			return;
		}
		if (table.isOverDot) { // auto-fill down if dragging dot is double-clicked
			// TODO handleAutoFillDown();
			return;
		}
		// otherwise, doubleClick edits cell
		GPoint point = getIndexFromEvent(doubleClickEvent);
		tryEditCellAt(point);
	}

	private void tryEditCellAt(GPoint point) {
		if (canEditCellAt(point) && !isEditingCellAt(point)) {
			editCellAt(point);
		}
	}

	private boolean canEditCellAt(GPoint point) {
		return !(table.getOneClickEditMap().containsKey(point) && view
		        .allowSpecialEditor());
	}

	private boolean isEditingCellAt(GPoint point) {
		int column = point.getX();
		int row = point.getY();
		return editor.isEditing() && editor.row == row
		        && editor.column == column;
	}

	private void editCellAt(GPoint point) {
		table.setAllowEditing(true);
		table.editCellAt(point);
		table.setAllowEditing(false);
	}

	public void onMouseDown(MouseDownEvent mouseDownEvent) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		handlePointerDown(mouseDownEvent);
	}

	public void onTouchStart(TouchStartEvent touchStartEvent) {
		numberOfTouches = touchStartEvent.getTouches().length();
		if (numberOfTouches == 1) {
			updateTableIsOverDot(touchStartEvent);
			handlePointerDown(touchStartEvent);
			longTouchManager.scheduleTimer(this, 
					getAbsoluteX(touchStartEvent), 
					getAbsoluteY(touchStartEvent));
		} // else there are double (or more) touches
		  // and we are scrolling
		CancelEventTimer.touchEventOccured();
	}

	private void handlePointerDown(DomEvent<?> event) {
		setActiveToolbarIfNecessary();
		//event.preventDefault();
		if (!editEnabled) {
			return;
		}
		pointerIsDown = true;
		GPoint point = getIndexFromEvent(event);
		if (point == null) {
			return;
		}

		if (editor.isEditing()) {
			if (editor.textStartsWithEquals()) {
				copyIntoEditorFromCellAt(point);
				startEditDragging(point);
			} else {
				// selecting the same cell should not finish editing
				// e.g. move cursor inside cell
				if (!isCurrentSelection(point)) {
					finishEditing();
				}
			}
		}

		// request focus only if there will be no editing
		// else the view steals the focus from the input
		if (!(EventUtil.isTouchEvent(event) && isCurrentSelection(point))) {
			view.requestFocus();
		}

		if (table.isOverDot) {
			if (table.showCanDragBlueDot()) {
				table.isDragingDot = true;
			}
		} else {
			if (!isCurrentSelection(point)) {
				if (!isInsideCurrentSelection(point) || !isRightClick(event)) {
					changeSelection(point, false);
				}
			} else if (EventUtil.isTouchEvent(event.getNativeEvent())) {
				tryEditCellAt(point);
			}
			// force column selection
			if (view.isColumnSelect()) {
				int column = point.getX();
				table.setColumnSelectionInterval(column, column);
			}
		}
	}

	private void changeSelection(GPoint point, boolean extend) {
		if (table.getSelectionType() != MyTable.CELL_SELECT) {
			table.setSelectionType(MyTable.CELL_SELECT);
		}
		table.changeSelection(point, extend);
	}

	private void setActiveToolbarIfNecessary() {
		if ((app.getGuiManager() != null)
		        && (app.showToolBar() || App.isFullAppGui())) {
			((GuiManagerW) app.getGuiManager())
			        .setActiveToolbarId(App.VIEW_SPREADSHEET);
		}
	}

	private void copyIntoEditorFromCellAt(GPoint pointOnMouseDown) {
		int column = pointOnMouseDown.getX();
		int row = pointOnMouseDown.getY();
		if (column == editor.column && row == editor.row) {
			return;
		}
		GeoElement geo = RelativeCopy.getValue(app, column, row);
		if (geo != null) {
			// get cell name
			String name = GeoElementSpreadsheet.getSpreadsheetCellName(column,
			        row);
			if (geo.isGeoFunction()) {
				name += "(x)"; // TODO this should not be here
				               // (x) should be coming from the geo itself
			}
			selectedCellName = name;

			// insert the geo label into the editor string
			editor.addLabel(name);
		}
	}

	private void startEditDragging(GPoint point) {
		startEditDragging(point.getY(), point.getX());
	}

	private void startEditDragging(int row, int column) {
		int caretPos = editor.getCaretPosition();
		String text = editor.getEditingValue();
		prefix = text.substring(0, caretPos);
		postfix = text.substring(caretPos, text.length());

		table.isDragging = true;
		table.minColumn = column;
		table.maxColumn = column;
		table.minRow = row;
		table.maxRow = row;
	}

	private void finishEditing() {
		editor.setAllowProcessGeo(true);
		editor.stopCellEditing();
		editor.setAllowProcessGeo(false);
		table.finishEditing();
	}

	private boolean isCurrentSelection(GPoint point) {
		return isInsideCurrentSelection(point) && singleCellSelected();
	}

	public void onMouseUp(MouseUpEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		handlePointerUp(event);
	}

	public void onTouchEnd(TouchEndEvent event) {
		longTouchManager.cancelTimer();
		numberOfTouches = event.getChangedTouches().length();
		if (numberOfTouches == 1) {
			handlePointerUp(event);
		}
		CancelEventTimer.touchEventOccured();
	}

	private void handlePointerUp(DomEvent<?> event) {
		if (!editEnabled) {
			return;
		}
		event.preventDefault();
		pointerIsDown = false;

		GPoint point = getIndexFromEvent(event);

		if (table.getTableMode() == MyTable.TABLE_MODE_AUTOFUNCTION) {
			table.stopAutoFunction();
			return;
		}
		if (isRightClick(event) && app.letShowPopupMenu()) {
			showContextMenu(event);
		}

		if (table.isDragingDot) {
			boolean success = doDragCopy();
			if (success) {
				app.storeUndoInfo();
			}
			resetDraggingFlags();
		}

		// Alt click: copy definition to input field
		if (!table.isEditing() && event.getNativeEvent().getAltKey()
		        && app.showAlgebraInput()) {
			GeoElement geo = RelativeCopy.getValue(app, point);
			if (geo != null) {
				// F3 key: copy definition to input bar
				app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(
				        3, geo);
			}
		}

		resetState();
		table.repaint();
	}

	private void showContextMenu(DomEvent<?> event) {
		int x = EventUtil.getTouchOrClickClientX(event);
		int y = EventUtil.getTouchOrClickClientY(event);
		showContextMenu(x, y);
	}

	private void showContextMenu(int x, int y) {
		SpreadsheetContextMenuW contextMenu = ((GuiManagerW) app
		        .getGuiManager()).getSpreadsheetContextMenu(table);
		GPopupMenuW popupMenu = (GPopupMenuW) contextMenu.getMenuContainer();
		popupMenu.show(new GPoint(x, y));
		app.registerPopup(popupMenu.getPopupPanel());
	}

	private boolean doDragCopy() {
		if (table.dragingToColumn == -1 || table.dragingToRow == -1) {
			return false;
		}
		int x1 = -1;
		int y1 = -1;
		int x2 = -1;
		int y2 = -1;
		// -|1|-
		// 2|-|3
		// -|4|-
		if (table.dragingToColumn < table.minSelectionColumn) { // 2
			x1 = table.dragingToColumn;
			y1 = table.minSelectionRow;
			x2 = table.minSelectionColumn - 1;
			y2 = table.maxSelectionRow;
		} else if (table.dragingToRow > table.maxSelectionRow) { // 4
			x1 = table.minSelectionColumn;
			y1 = table.maxSelectionRow + 1;
			x2 = table.maxSelectionColumn;
			y2 = table.dragingToRow;
		} else if (table.dragingToRow < table.minSelectionRow) { // 1
			x1 = table.minSelectionColumn;
			y1 = table.dragingToRow;
			x2 = table.maxSelectionColumn;
			y2 = table.minSelectionRow - 1;
		} else if (table.dragingToColumn > table.maxSelectionColumn) { // 3
			x1 = table.maxSelectionColumn + 1;
			y1 = table.minSelectionRow;
			x2 = table.dragingToColumn;
			y2 = table.maxSelectionRow;
		}

		// copy the cells
		boolean succ = relativeCopy.doDragCopy(table.minSelectionColumn,
		        table.minSelectionRow, table.maxSelectionColumn,
		        table.maxSelectionRow, x1, y1, x2, y2);

		// extend the selection to include the drag copy selection
		table.setSelection(Math.min(x1, table.minSelectionColumn),
		        Math.min(y1, table.minSelectionRow),
		        Math.max(x2, table.maxSelectionColumn),
		        Math.max(y2, table.maxSelectionRow));
		return succ;
	}

	private void resetDraggingFlags() {
		table.isOverDot = false;
		table.isDragingDot = false;
		table.dragingToRow = -1;
		table.dragingToColumn = -1;
	}

	private boolean isInsideCurrentSelection(GPoint point) {
		int column = point.getX();
		int row = point.getY();
		return row >= table.minSelectionRow && row <= table.maxSelectionRow
		        && column >= table.minSelectionColumn
		        && column <= table.maxSelectionColumn;
	}

	private boolean singleCellSelected() {
		return table.minSelectionRow == table.maxSelectionRow
		        && table.minSelectionColumn == table.maxSelectionColumn;
	}

	private boolean isRightClick(DomEvent<?> event) {
		if (EventUtil.isTouchEvent(event)) {
			return false; // right click is handled by rightClickTimer
		}
		return event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT;
	}

	private void resetState() {
		selectedCellName = null;
		prefix = null;
		postfix = null;
		table.isDragging = false;
	}

	public void onTouchMove(TouchMoveEvent event) {
		numberOfTouches = event.getTouches().length();
		if (numberOfTouches == 1) {
			event.stopPropagation();
			handlePointerMove(event);
			longTouchManager.rescheduleTimerIfRunning(this, 
					getAbsoluteX(event), 
					getAbsoluteY(event),
					false);
		} else {
			longTouchManager.cancelTimer();
		}
		CancelEventTimer.touchEventOccured();
	}

	public void onMouseMove(MouseMoveEvent event) {
		if(CancelEventTimer.cancelMouseEvent()){
			return;
		}
		handlePointerMove(event);
	}

	private void handlePointerMove(DomEvent<?> event) {
		if (!editEnabled) {
			return;
		}

		GPoint point = getIndexFromEvent(event);

		event.preventDefault();
		boolean eConsumed = false;
		if (pointerIsDown) {

			if (table.getTableMode() == MyTable.TABLE_MODE_AUTOFUNCTION
			        || table.getTableMode() == MyTable.TABLE_MODE_DROP) {
				// App.debug("drop is dragging ");
				return;
			}

			// handle editing mode drag
			if (editor.isEditing()) {
				// GPoint point = table.getIndexFromPixel(getAbsoluteX(event),
				// getAbsoluteY(event));
				if (point != null && selectedCellName != null) {
					int column2 = point.getX();
					int row2 = point.getY();

					MatchResult matcher = GeoElementSpreadsheet.spreadsheetPattern
					        .exec(selectedCellName);
					int column1 = GeoElementSpreadsheet
					        .getSpreadsheetColumn(matcher);
					int row1 = GeoElementSpreadsheet.getSpreadsheetRow(matcher);

					if (column1 > column2) {
						int temp = column1;
						column1 = column2;
						column2 = temp;
					}
					if (row1 > row2) {
						int temp = row1;
						row1 = row2;
						row2 = temp;
					}
					String name1 = GeoElementSpreadsheet
					        .getSpreadsheetCellName(column1, row1);
					String name2 = GeoElementSpreadsheet
					        .getSpreadsheetCellName(column2, row2);
					if (!name1.equals(name2)) {
						name1 += ":" + name2;
					}

					name1 = prefix + name1 + postfix;
					editor.setLabel(name1);
					table.minColumn = column1;
					table.maxColumn = column2;
					table.minRow = row1;
					table.maxRow = row2;
					table.repaint();
				}
				return;
			}

			// handle dot drag
			if (table.isDragingDot) {

				eConsumed = true;
				int mouseX = getAbsoluteX(event);
				int mouseY = getAbsoluteY(event);
				GPoint mouseCell = table.getIndexFromPixel(mouseX, mouseY);

				// save the selected cell position so it can be re-selected if
				// needed
				CellRange oldSelection = table.getSelectedCellRanges().get(0);

				if (mouseCell == null) { // user has dragged outside the table,
					                     // to
					                     // left or above
					table.dragingToRow = -1;
					table.dragingToColumn = -1;
				} else {
					table.dragingToRow = mouseCell.getY();
					table.dragingToColumn = mouseCell.getX();
					GRectangle selRect = table.getSelectionRect(true);

					// increase size if we're at the bottom of the spreadsheet
					if (table.dragingToRow + 1 == table.getRowCount()
					        && table.dragingToRow < Kernel.MAX_SPREADSHEET_ROWS_VISIBLE) {
						model.setRowCount(table.getRowCount() + 1);
					}

					// increase size if we go beyond the right edge
					if (table.dragingToColumn + 1 == table.getColumnCount()
					        && table.dragingToColumn < Kernel.MAX_SPREADSHEET_COLUMNS_VISIBLE) {
						model.setColumnCount(table.getColumnCount() + 1);
						// view.columnHeaderRevalidate();
						// Java's addColumn method will clear selection, so
						// re-select our cell
						// table.setSelection(oldSelection);
					}

					// scroll to show "highest" selected cell
					table.scrollRectToVisible(table.getCellRect(mouseCell.y,
					        mouseCell.x, true));

					if (!selRect.contains(getAbsoluteX(event),
					        getAbsoluteY(event))) {

						int rowOffset = 0, colOffset = 0;

						// get row distance
						if (table.minSelectionRow > 0
						        && table.dragingToRow < table.minSelectionRow) {
							rowOffset = mouseY - (int) selRect.getY();
							if (-rowOffset < 0.5 * table.getCellRect(
							        table.minSelectionRow - 1,
							        table.minSelectionColumn, true).getHeight())
								rowOffset = 0;
						} else if (table.maxSelectionRow < Kernel.MAX_SPREADSHEET_ROWS_VISIBLE
						        && table.dragingToRow > table.maxSelectionRow) {
							rowOffset = mouseY
							        - ((int) selRect.getY() + (int) selRect
							                .getHeight());
							if (rowOffset < 0.5 * table.getCellRect(
							        table.maxSelectionRow + 1,
							        table.maxSelectionColumn, true).getHeight())
								rowOffset = 0;
						}

						// get column distance
						if (table.minSelectionColumn > 0
						        && table.dragingToColumn < table.minSelectionColumn) {
							colOffset = mouseX - (int) selRect.getX();
							if (-colOffset < 0.5 * table.getCellRect(
							        table.minSelectionRow,
							        table.minSelectionColumn - 1, true)
							        .getWidth())
								colOffset = 0;
						} else if (table.maxSelectionColumn < Kernel.MAX_SPREADSHEET_COLUMNS_VISIBLE
						        && table.dragingToColumn > table.maxSelectionColumn) {
							colOffset = mouseX
							        - ((int) selRect.getX() + (int) selRect
							                .getWidth());
							if (colOffset < 0.5 * table.getCellRect(
							        table.maxSelectionRow,
							        table.maxSelectionColumn + 1, true)
							        .getWidth())
								colOffset = 0;
						}

						if (rowOffset == 0 && colOffset == 0) {
							table.dragingToColumn = -1;
							table.dragingToRow = -1;
						} else if (Math.abs(rowOffset) > Math.abs(colOffset)) {
							table.dragingToRow = mouseCell.y;
							table.dragingToColumn = (colOffset > 0) ? table.maxSelectionColumn
							        : table.minSelectionColumn;
						} else {
							table.dragingToColumn = mouseCell.x;
							table.dragingToRow = (rowOffset > 0) ? table.maxSelectionRow
							        : table.minSelectionRow;
						}
						table.repaint();
					}

					// handle ctrl-select dragging of cell blocks
					else {
						/*
						 * TODO if (e.isControlDown()) {
						 * table.handleControlDragSelect(e); }
						 */
					}
				}
			}

			if (eConsumed)
				return;

			// MyTable's default listeners follow, they should be simulated in
			// Web e.g. here

			// change selection if right click is outside current selection
			if (point.getY() != table.leadSelectionRow
			        || point.getX() != table.leadSelectionColumn) {
				// switch to cell selection mode

				if (point.getY() >= 0 && point.getX() >= 0) {
					
					changeSelection(point, true);
					table.repaint();
				}
			}
		} else {
			// MOVE, NO DRAG

			if (table.isEditing())
				return;

			// get GeoElement at mouse location
			int row = point.getY();// ?//table.rowAtPoint(e.getPoint());
			int col = point.getX();// ?//table.columnAtPoint(e.getPoint());
			GeoElement geo = (GeoElement) model.getValueAt(row, col);

			// set tooltip with geo's description
			if (geo != null & view.getAllowToolTips()) {
				app.getLocalization().setTooltipFlag();
				table.setToolTipText(geo.getLongDescriptionHTML(true, true));
				app.getLocalization().clearTooltipFlag();
			} else {
				table.setToolTipText(null);
			}

			updateTableIsOverDot(event);

			GPoint maxPoint = table.getMaxSelectionPixel();
			GPoint minPoint = table.getMinSelectionPixel();
			// check if over the DnD region and update accordingly
			GPoint testPoint = table.getMinSelectionPixel();
			if (testPoint != null) {
				int minX = minPoint.getX();
				int minY = minPoint.getY();
				int maxX = maxPoint.getX();
				int w = maxX - minX;
				Rectangle2D dndRect = new Rectangle2D.Double(minX, minY - 2, w,
				        4);
				boolean overDnD = dndRect.contains(getAbsoluteX(event),
				        getAbsoluteY(event));
				if (table.isOverDnDRegion != overDnD) {
					table.isOverDnDRegion = overDnD;
					// TODO//setTableCursor();
				}
			}
		}

	}

	private void updateTableIsOverDot(DomEvent<?> event) {
		// check if over the dragging dot and update accordingly
		GPoint point = getPixelFromEvent(event);
		GPoint maxPoint = table.getMaxSelectionPixel();

		if (maxPoint != null) {
			int dotX = maxPoint.getX();
			int dotY = maxPoint.getY();
			int s = MyTableW.DOT_SIZE + 2;
			if (EventUtil.isTouchEvent(event)) {
				s += 4;
			}
			Rectangle2D dotRect = new Rectangle2D.Double(dotX - s / 2, dotY - s
			        / 2, s, s);
			boolean overDot = dotRect.contains(point.getX(), point.getY());
			if (table.isOverDot != overDot) {
				table.isOverDot = overDot;

				if (table.showCanDragBlueDot()) {
					table.repaint();
				}
			}
		}
	}
}
