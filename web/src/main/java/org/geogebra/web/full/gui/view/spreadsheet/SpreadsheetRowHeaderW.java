package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.view.spreadsheet.MyTableInterface;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.util.AdvancedFocusPanel;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO: Consider creating SpreadsheetHeaderW class, with all the common method
 * from the row and column header
 * 
 *
 */
public class SpreadsheetRowHeaderW implements SpreadsheetHeader {
	private static final long serialVersionUID = 1L;
	private AppW app;
	private MyTableW table;
	private Grid grid;
	private FlowPanel container;

	AdvancedFocusPanel focusPanel;

	private int mouseYOffset;
	private int resizingRow = -1;
	private boolean doRowResize = false;

	protected int row0 = -1;

	private boolean isMouseDown = false;

	private LongTouchManager longTouchManager;
	private SpreadsheetHeaderController headerController;

	/**
	 * @param app
	 *            application
	 * @param table
	 *            spreadsheet table
	 */
	public SpreadsheetRowHeaderW(AppW app, MyTableW table) {

		this.app = app;
		this.table = table;

		prepareGUI();
		headerController = new SpreadsheetHeaderController(this);
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
		longTouchManager = LongTouchManager.getInstance();
	}

	// ============================================
	// GUI handlers
	// ============================================

	private void registerListeners() {
		grid.addDomHandler(headerController, MouseDownEvent.getType());
		grid.addDomHandler(headerController, MouseUpEvent.getType());
		grid.addDomHandler(headerController, MouseMoveEvent.getType());
		grid.addDomHandler(this, TouchStartEvent.getType());
		grid.addDomHandler(this, TouchMoveEvent.getType());
		grid.addDomHandler(this, TouchEndEvent.getType());
	}

	private void prepareGUI() {

		grid = new Grid(table.getModel().getRowCount(), 1);

		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		grid.setHeight("0px");

		grid.getElement().addClassName("geogebraweb-table-spreadsheet");

		grid.getColumnFormatter().getElement(0).getStyle()
				.setWidth(SpreadsheetViewW.ROW_HEADER_WIDTH, Style.Unit.PX);

		for (int row = 0; row < grid.getRowCount(); row++) {
			initializeCell(row);
		}

		focusPanel = new AdvancedFocusPanel();
		// focusPanel.addKeyDownHandler(this);
		focusPanel.addDomHandler(this, KeyDownEvent.getType());
		addPasteHandlerTo(focusPanel.getTextarea());

		Style s = focusPanel.getElement().getStyle();
		// s.setDisplay(Style.Display.NONE);
		s.setPosition(Style.Position.ABSOLUTE);
		s.setTop(0, Unit.PX);
		s.setLeft(0, Unit.PX);

		container = new FlowPanel();
		container.add(grid);
		container.add(focusPanel);
	}

	private void initializeCell(int rowIndex) {

		grid.setText(rowIndex, 0, (rowIndex + 1) + "");

		int rowHeight = app.getSettings().getSpreadsheet().preferredRowHeight();
		setRowHeight(rowIndex, rowHeight);

		Element elm = grid.getCellFormatter().getElement(rowIndex, 0);

		elm.addClassName("SVheader");
		/*
		 * elm.getStyle().setBackgroundColor(
		 * MyTableW.BACKGROUND_COLOR_HEADER.toString());
		 */
	}

	/**
	 * updates header row count to match table row count
	 */
	public void updateRowCount() {
		if (grid.getRowCount() >= table.getRowCount()) {
			return;
		}

		int oldRowCount = grid.getRowCount();
		grid.resizeRows(table.getRowCount());

		for (int i = oldRowCount; i < table.getRowCount(); ++i) {
			initializeCell(i);
		}
	}

	// ============================================
	// Getters/Setters
	// ============================================

	public Widget getContainer() {
		return container;
	}

	private String getCursor() {
		return grid.getElement().getStyle().getCursor();
	}

	private void setRowResizeCursor() {
		grid.getElement().getStyle().setCursor(Style.Cursor.ROW_RESIZE);
	}

	private void setDefaultCursor() {
		grid.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
	}

	/**
	 * @param rowIndex
	 *            index of row to set height
	 * @param rowHeight
	 *            new row height
	 */
	public void setRowHeight(int rowIndex, int rowHeight) {
		if (rowIndex >= grid.getRowCount()) {
			return;
		}

		grid.getRowFormatter().getElement(rowIndex).getStyle()
				.setHeight(rowHeight, Style.Unit.PX);
	}

	/**
	 * Renders selected and unselected rows
	 */
	public void renderSelection() {

		/*
		 * String defaultBackground =
		 * MyTableW.BACKGROUND_COLOR_HEADER.toString(); String
		 * selectedBackground = MyTableW.SELECTED_BACKGROUND_COLOR_HEADER
		 * .toString();
		 */

		for (int rowIndex = 0; rowIndex < grid.getRowCount(); rowIndex++) {
			/*
			 * Style s = grid.getCellFormatter().getElement(rowIndex, 0)
			 * .getStyle();
			 */

			if (table.getSelectionType() == MyTableInterface.COLUMN_SELECT) {
				// setBgColorIfNeeded(s, defaultBackground);
				updateCellSelection(false, rowIndex);
			} else {
				if (table.selectedRowSet.contains(rowIndex)
						|| (rowIndex >= table.minSelectionRow
								&& rowIndex <= table.maxSelectionRow)) {
					// setBgColorIfNeeded(s, selectedBackground);
					updateCellSelection(true, rowIndex);
				} else {
					// setBgColorIfNeeded(s, defaultBackground);
					updateCellSelection(false, rowIndex);
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
	 *            row index
	 */
	private void updateCellSelection(boolean selected, int index) {
		if (selected) {
			grid.getCellFormatter().addStyleName(index, 0, "selected");
		} else {
			grid.getCellFormatter().removeStyleName(index, 0, "selected");
		}
	}

	/*
	 * Steffi: not needed anymore -> NOW: updateCellSelection private static
	 * void setBgColorIfNeeded(Style s, String bgColor) { if
	 * (!s.getBackgroundColor().equals(bgColor)) s.setBackgroundColor(bgColor);
	 * }
	 */

	/**
	 * Update the rowHeader list when row selection changes in the table
	 */
	/*
	 * public void valueChanged(ListSelectionEvent e) { ListSelectionModel
	 * selectionModel = (ListSelectionModel) e.getSource(); minSelectionRow =
	 * selectionModel.getMinSelectionIndex(); maxSelectionRow =
	 * selectionModel.getMaxSelectionIndex(); repaint(); }
	 */

	/**
	 * @param p
	 *            location of mouse (in client area pixels)
	 * @return index of the row to be resized if mouse point p is near a row
	 *         boundary
	 */
	private int getResizingRow(GPoint p, int boundary) {
		int resizeRow = -1;
		GPoint point = table.getIndexFromPixel(0, p.y);
		if (point != null) {
			// test if mouse is 3 pixels from row boundary
			int cellRow = point.getY();

			if (cellRow >= 0) {
				GRectangle r = table.getCellRect(cellRow, 0, true, false);
				// near row bottom ?
				if (p.y < r.getY() + boundary) {
					resizeRow = cellRow - 1;
				}
				// near row top ?
				if (p.y > r.getY() + r.getHeight() - boundary) {
					resizeRow = cellRow;
				}
			}
		}
		return resizeRow;
	}

	private static int getBoundary(PointerEventType eventType) {
		return eventType == PointerEventType.MOUSE ? 3 : 6;
	}

	// ===============================================
	// Mouse Listeners
	// ===============================================

	// transfer focus to the table
	// @Override
	public void requestFocus() {
		Scheduler.get().scheduleDeferred(requestFocusCommand);
	}

	Scheduler.ScheduledCommand requestFocusCommand = new Scheduler.ScheduledCommand() {
		@Override
		public void execute() {
			focusPanel.setFocus(true);
			table.updateCopiableSelection();
		}
	};

	// ===============================================
	// Key Listeners
	// ===============================================

	@Override
	public void onKeyDown(KeyDownEvent e) {
		// App.debug("row header key down");
		e.stopPropagation();
		int keyCode = e.getNativeKeyCode();

		boolean shiftDown = e.isShiftKeyDown();

		switch (keyCode) {

		default:
			// do nothing
			break;
		case KeyCodes.KEY_UP:
			if (shiftDown) {
				// extend the column selection
				int row = table.getLeadSelectionRow();
				table.setSelectionType(MyTableInterface.ROW_SELECT);
				table.changeSelection(row - 1, -1, true);
			} else {
				// select topmost cell in first column left of the selection
				if (table.minSelectionRow > 0) {
					table.setSelection(0, table.minSelectionRow - 1);
				} else {
					table.setSelection(0, table.minSelectionRow);
					// table.requestFocus();
				}
			}
			break;

		case KeyCodes.KEY_DOWN:
			if (shiftDown) {
				// extend the row selection
				int row = table.getLeadSelectionRow();
				table.setSelectionType(MyTableInterface.ROW_SELECT);
				table.changeSelection(row + 1, -1, true);
			} else {
				// select topmost cell in first column left of the selection
				if (table.minSelectionRow >= 0) {
					table.setSelection(0, table.minSelectionRow + 1);
				} else {
					table.setSelection(0, table.minSelectionRow);
					// table.requestFocus();
				}
			}
			break;

		case KeyCodes.KEY_C:
			// control + c
			// handled in browser copy event! ctrlDown implied
			// if (ctrlDown && table.minSelectionRow != -1
			// && table.maxSelectionRow != -1) {
			// table.copyPasteCut.copy(0, table.minSelectionRow, table
			// .getModel().getColumnCount() - 1,
			// table.maxSelectionRow, altDown);
			// }
			break;

		case KeyCodes.KEY_V: // control + v
			// handled in browser paste event! ctrlDown implied
			// if (ctrlDown && table.minSelectionRow != -1
			// && table.maxSelectionRow != -1) {
			// boolean storeUndo = table.copyPasteCut.paste(0,
			// table.minSelectionRow, table.getModel()
			// .getColumnCount() - 1, table.maxSelectionRow);
			// if (storeUndo)
			// app.storeUndoInfo();
			// }
			break;

		case KeyCodes.KEY_X: // control + x
			// handled in browser cut event! ctrlDown implied
			// if (ctrlDown && table.minSelectionRow != -1
			// && table.maxSelectionRow != -1) {
			// table.copyPasteCut.copy(0, table.minSelectionRow, table
			// .getModel().getColumnCount() - 1,
			// table.maxSelectionRow, altDown);
			// }
			// boolean storeUndo = table.copyPasteCut.delete(0,
			// table.minSelectionRow,
			// table.getModel().getColumnCount() - 1,
			// table.maxSelectionRow);
			// if (storeUndo)
			// app.storeUndoInfo();
			break;

		case KeyCodes.KEY_DELETE: // delete
		case KeyCodes.KEY_BACKSPACE: // delete on MAC
			boolean storeUndo = table.getCopyPasteCut().delete(0,
					table.minSelectionRow,
					table.getModel().getColumnCount() - 1,
					table.maxSelectionRow);
			if (storeUndo) {
				app.storeUndoInfo();
			}
			break;
		}
	}

	public int getOffsetWidth() {
		return getContainer().getOffsetWidth();
	}

	public void setTop(int top) {
		container.getElement().getStyle().setTop(top, Unit.PX);
	}

	/**
	 * @return relative top of row header within parent
	 */
	public int getTop() {
		return container.getParent().getAbsoluteTop()
				- container.getAbsoluteTop();
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		longTouchManager.cancelTimer();
		event.preventDefault();
		PointerEvent e = PointerEvent.wrapEvent(event, ZeroOffset.INSTANCE);
		onPointerUp(e);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		event.preventDefault();
		PointerEvent e = PointerEvent.wrapEvent(event, ZeroOffset.INSTANCE);
		if (doRowResize) {
			// resizing a column cancel long touch
			longTouchManager.cancelTimer();
		} else {
			longTouchManager.rescheduleTimerIfRunning(this, e.getX(), e.getY(),
					false);
		}
		onPointerMove(e);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.preventDefault();
		PointerEvent e = PointerEvent.wrapEvent(event, ZeroOffset.INSTANCE);
		longTouchManager.scheduleTimer(this, e.getX(), e.getY());
		onPointerDown(e);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void handleLongTouch(int x, int y) {
		showContextMenu(x, y, false);
	}

	@Override
	public void onPointerDown(PointerEvent e) {
		Event.setCapture(grid.getElement());
		isMouseDown = true;

		if (table.getEditor().isEditing()) {
			table.getEditor().setAllowProcessGeo(true);
			table.getEditor().stopCellEditing();
			table.getEditor().setAllowProcessGeo(false);
			table.finishEditing(false);
		}

		requestFocus();

		boolean shiftPressed = e.isShiftDown();
		boolean rightClick = (e.isRightClick());

		int x = SpreadsheetMouseListenerW.getAbsoluteX(e.getWrappedEvent(),
				app);
		int y = SpreadsheetMouseListenerW.getAbsoluteY(e.getWrappedEvent(),
				app);

		GPoint p = new GPoint(x, y);
		resizingRow = getResizingRow(p, getBoundary(e.getType()));
		if (resizingRow >= 0) {
			mouseYOffset = p.y - table.getRowHeight(resizingRow);
		}

		// left click
		if (!rightClick) {

			if (resizingRow >= 0) {
				return;
			}

			GPoint point = table.getIndexFromPixel(x, y);
			if (point != null) {

				if (table.getSelectionType() != MyTableInterface.ROW_SELECT) {
					table.setSelectionType(MyTableInterface.ROW_SELECT);
					// ?//requestFocusInWindow();
				}

				if (shiftPressed) {
					if (row0 != -1) {
						int row = point.getY();
						table.setRowSelectionInterval(row0, row);
					}
				}

				// ctrl-select is handled in table

				else {
					row0 = point.getY();
					table.setRowSelectionInterval(row0, row0);
				}
				table.repaint();
				renderSelection();
			}
		}
	}

	@Override
	public void onPointerMove(PointerEvent e) {
		// Show resize cursor when mouse is over a row boundary
		HumanInputEvent<?> event = e.getWrappedEvent();
		GPoint p = new GPoint(
				SpreadsheetMouseListenerW.getAbsoluteX(event, app),
				SpreadsheetMouseListenerW.getAbsoluteY(event, app));
		int r = this.getResizingRow(p, getBoundary(e.getType()));
		if (r >= 0
				&& !getCursor().equals(Style.Cursor.ROW_RESIZE.getCssName())) {
			setRowResizeCursor();
		} else if (r < 0
				&& !getCursor().equals(Style.Cursor.DEFAULT.getCssName())) {
			setDefaultCursor();
		}

		if (isMouseDown) {

			if (e.isRightClick()) {
				return;
			}

			// On mouse drag either resize or select a row
			int x = SpreadsheetMouseListenerW.getAbsoluteX(e.getWrappedEvent(),
					app);
			int y = SpreadsheetMouseListenerW.getAbsoluteY(e.getWrappedEvent(),
					app);

			if (resizingRow >= 0) {
				// resize row
				int newHeight = y - mouseYOffset;
				if (newHeight > 0) {
					table.setRowHeight(resizingRow, newHeight);
					// flag to resize all selected rows on mouse release
					doRowResize = true;
					table.repaint();
					renderSelection();
				}
			} else { // select row
				GPoint point = table.getIndexFromPixel(x, y);
				if (point != null) {
					int row = point.getY();
					table.setRowSelectionInterval(row0, row);

					// G.Sturr 2010-4-4
					// keep the row header updated when drag selecting multiple
					// rows
					// ?//view.updateRowHeader();
					// ?//table.scrollRectToVisible(table.getCellRect(point.y,
					// point.x,
					// ?// true));
					table.repaint();
					renderSelection();
				}
			}
		}
	}

	@Override
	public void onPointerUp(PointerEvent e) {
		Event.releaseCapture(grid.getElement());
		isMouseDown = false;

		boolean rightClick = e.isRightClick();

		if (rightClick) {
			if (!app.letShowPopupMenu()) {
				return;
			}

			GPoint p = table.getIndexFromPixel(
					SpreadsheetMouseListenerW.getAbsoluteX(e.getWrappedEvent(),
							app),
					SpreadsheetMouseListenerW.getAbsoluteY(e.getWrappedEvent(),
							app));
			if (p == null) {
				return;
			}

			// if click is outside current selection then change selection
			if (p.getY() < table.minSelectionRow
					|| p.getY() > table.maxSelectionRow
					|| p.getX() < table.minSelectionColumn
					|| p.getX() > table.maxSelectionColumn) {

				// switch to row selection mode and select row
				if (table.getSelectionType() != MyTableInterface.ROW_SELECT) {
					table.setSelectionType(MyTableInterface.ROW_SELECT);
				}

				table.setRowSelectionInterval(p.getY(), p.getY());
				renderSelection();
			}

			// show contextMenu
			showContextMenu(e.getX(), e.getY(), true);
		}

		// If row resize has happened, resize all other selected rows
		if (doRowResize) {

			int rowHeight = table.getRowHeight(resizingRow);
			// App.debug("doRowResiz for selection: " + rowHeight);
			// App.debug("min/max " + table.minSelectionRow + " , " +
			// table.maxSelectionRow);
			if (table.minSelectionRow != -1 && table.maxSelectionRow != -1
					&& (table.maxSelectionRow - table.minSelectionRow > 0)) {
				if (table.isSelectAll()) {
					table.setRowHeight(rowHeight, true);
				} else {
					for (int row = table.minSelectionRow; row <= table.maxSelectionRow; row++) {
						// App.debug("set row height row/height: " + row + " / "
						// + rowHeight);
						table.setRowHeight(row, rowHeight);
					}
				}
			}
			table.repaint();
			table.renderSelectionDeferred();
			doRowResize = false;
		}

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

	public native void addPasteHandlerTo(Element elem) /*-{
		var self = this;
		elem.onpaste = function(event) {
			var text, cbd;
			if ($wnd.clipboardData) {
				// Windows Internet Explorer
				cbd = $wnd.clipboardData;
				if (cbd.getData) {
					text = cbd.getData('Text');
				}
			}
			if (text === undefined) {
				// all the other browsers
				if (event.clipboardData) {
					cbd = event.clipboardData;
					if (cbd.getData) {
						text = cbd.getData('text/plain');
					}
				}
			}
			if (text !== undefined) {
				self.@org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetRowHeaderW::onPaste(Ljava/lang/String;)(text);
			}
		}
		elem.oncopy = function(even2) {
			self.@org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetRowHeaderW::onCopy(Z)(even2.altKey);
			// do not prevent default!!!
			// it will take care of the copy...
		}
		elem.oncut = function(even3) {
			self.@org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetRowHeaderW::onCut()();
			// do not prevent default!!!
			// it will take care of the cut...
		}
	}-*/;

	/**
	 * Paste text.
	 * 
	 * @param text
	 *            clipboard content
	 */
	public void onPaste(String text) {
		if (table.minSelectionRow != -1 && table.maxSelectionRow != -1) {
			boolean storeUndo = ((CopyPasteCutW) table.getCopyPasteCut()).paste(
					0, table.minSelectionRow,
					table.getModel().getColumnCount() - 1,
					table.maxSelectionRow, text);
			if (storeUndo) {
				app.storeUndoInfo();
			}
		}
	}

	/**
	 * Handle copy
	 * 
	 * @param altDown
	 *            is alt down?
	 */
	public void onCopy(final boolean altDown) {
		// the default action of the browser just modifies
		// the textarea of the AdvancedFocusPanel, does
		// no harm to the other parts of the code, and
		// consequently, it should ideally be done before this!
		// so let's run the original code afterwards...

		// not sure one ScheduleDeferred is enough...
		// but in theory, it should be as code continues from
		// here towards the default action, as we are in the event
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				if (table.minSelectionRow != -1
						&& table.maxSelectionRow != -1) {
					((CopyPasteCutW) table.getCopyPasteCut()).copy(0,
							table.minSelectionRow,
							table.getModel().getColumnCount() - 1,
							table.maxSelectionRow, altDown, true);
				}
			}
		});
	}

	/**
	 * Handle cut
	 */
	public void onCut() {
		// the default action of the browser just modifies
		// the textarea of the AdvancedFocusPanel, does
		// no harm to the other parts of the code, and
		// consequently, it should ideally be done before this!

		// not sure one ScheduleDeferred is enough...
		// but in theory, it should be as code continues from
		// here towards the default action, as we are in the event
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				if (table.minSelectionRow != -1
						&& table.maxSelectionRow != -1) {
					((CopyPasteCutW) table.getCopyPasteCut()).copy(0,
							table.minSelectionRow,
							table.getModel().getColumnCount() - 1,
							table.maxSelectionRow, false, true);
				}
				boolean storeUndo = table.getCopyPasteCut().delete(0,
						table.minSelectionRow,
						table.getModel().getColumnCount() - 1,
						table.maxSelectionRow);
				if (storeUndo) {
					app.storeUndoInfo();
				}
			}
		});
	}
}
