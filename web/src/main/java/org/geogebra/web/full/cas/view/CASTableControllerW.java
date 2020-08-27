package org.geogebra.web.full.cas.view;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.cas.view.CASTableCellController;
import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.EventUtil;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.HumanInputEvent;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Widget;

/**
 * HTML5 version of CAS controller
 *
 */
public class CASTableControllerW extends CASTableCellController implements
        MouseDownHandler, MouseUpHandler, MouseMoveHandler, KeyHandler,
        BlurHandler, TouchStartHandler, TouchEndHandler, TouchMoveHandler,
        LongTouchHandler {

	private CASViewW view;
	private AppW app;
	private int startSelectRow;

	private LongTouchManager longTouchManager;
	private boolean mouseDown;
	private boolean touchDown;

	private boolean contextOpened;

	/**
	 * @param casViewW
	 *            cas view
	 * @param app
	 *            application
	 */
	public CASTableControllerW(CASViewW casViewW, AppW app) {
		view = casViewW;
		this.app = app;
		longTouchManager = LongTouchManager.getInstance();
	}

	@Override
	public void handleLongTouch(int x, int y) {
		CASTableW table = view.getConsoleTable();
		if (!table.isSelectedIndex(startSelectRow)) {
			table.setSelectedRows(startSelectRow, startSelectRow);
		}
		if (table.getSelectedRows().length > 0) {
			// TODO select cells for copy
			RowHeaderPopupMenuW popupMenu = ((GuiManagerW) app.getGuiManager())
					.getCASContextMenu(table);
			popupMenu.show(x, y);
			contextOpened = true;
		}
	}

	/**
	 * Sets the toolbar to CAS
	 */
	private void setActiveToolbar() {
		if (app.getToolbar() != null) {
			GuiManagerInterfaceW gm = app.getGuiManager();
			gm.setActivePanelAndToolbar(App.VIEW_CAS);
		}
	}

	/**
	 * Copies the output of a cell into the cell being edited if there is an
	 * editing cell and a cell output was clicked.
	 * 
	 * @param event
	 *            event
	 * @return true if copying happened
	 */
	private boolean copyOutputIfSource(HumanInputEvent<?> event) {
		if (event.getSource() != view.getComponent()) {
			CASTableW table = view.getConsoleTable();
			CASTableCellW clickedCell = table.getCasCellForEvent(event);
			return copyOutputToEditingCell(clickedCell);
		}
		return false;
	}

	private boolean copyOutputToEditingCell(CASTableCellW clickedCell) {
		CASTableW table = view.getConsoleTable();
		CASTableCellW editingCell = table.getEditingCell();
		if (editingCell != null && clickedCell != null
				&& clickedCell.getCASCell() != null
				&& !clickedCell.getCASCell().isError()) {
			editingCell.insertInput(clickedCell.getOutputString());
			return true;
		}
		return false;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		handleMouseMoveSelection(event);
		event.stopPropagation();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		mouseDown = false;
		CASTableW table = view.getConsoleTable();
		GPoint point = table.getPointForEvent(event);
		if (checkHeaderClick(event)) {
			// do this even if left/right click, even if clipboard is not
			// supported!
			Widget wid = table.getWidget(point.y, point.x);
			if ((wid != null) && (wid instanceof RowHeaderWidget)) {
				// quick implementation would call the handler
				if (((RowHeaderWidget) wid).getHandler() != null) {
					((RowHeaderWidget) wid).getHandler().onMouseUp(event);
				}
			}
		} else if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
			// in theory, checkHeaderClick(event) is already false here...
			// only do this action when supported
			// only makes sense for mouse events yet
			// TODO: add this functionality to touch events,
			// maybe override onPointerUp??

			if (!table.isSelectedIndex(point.y)) {
				table.setSelectedRows(point.y, point.y);
			}

			// CASTableCellEditor tableCellEditor = table.getEditor();
			RowHeaderPopupMenuW popupMenu = ((GuiManagerW) app.getGuiManager())
					.getCASContextMenu(table);
			popupMenu.show(event.getClientX() + Window.getScrollLeft(),
							event.getClientY() + Window.getScrollTop());
		} else {
			onPointerUp(event);
		}
		event.stopPropagation();
	}

	/**
	 * @param event
	 *            mouse event
	 * @return whether column is 0
	 */
	public boolean checkHeaderClick(HumanInputEvent<?> event) {
		CASTableW cw = view.getConsoleTable();
		GPoint gp = cw.getPointForEvent(event);
		if (gp != null && gp.x == 0) {
			return true;
		}
		return false;
	}

	/**
	 * @param event
	 *            mouse event
	 * @return whether input of a cell was clicked
	 */
	public boolean checkQuestionClick(HumanInputEvent<?> event) {
		CASTableW cw = view.getConsoleTable();
		GPoint gp = cw.getPointForEvent(event);
		if (gp == null || gp.x == 0) {
			return false;
		}
		CASTableCellW ctw = cw.getCasCellForEvent(event);
		Widget output = ctw.getOutputWidget();
		int x = event.getNativeEvent().getClientX();
		int y = event.getNativeEvent().getClientY();
		if ((output.getAbsoluteLeft() <= x)
				&& (output.getAbsoluteLeft() + output.getOffsetWidth() >= x)
				&& (output.getAbsoluteTop() <= y)
				&& (output.getAbsoluteTop() + output.getOffsetHeight() >= y)) {
			// this is an "answer" click!
			return false;
		}
		// supposing there is nothing else just the
		// "header" click, "answer" click or "question" click
		return true;
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		mouseDown = true;
		handleMouseDownSelection(event);
		onPointerDown();

	}

	private void onPointerDown() {
		setActiveToolbar();
		((GuiManagerW) app.getGuiManager()).removePopup();
		boolean oldFocus = ((CASTableCellEditor) view.getEditor()).hasFocus();
		app.closePopups();
		if (oldFocus) {
			view.getEditor().setFocus(true);
		}
	}

	private void onPointerUp(HumanInputEvent<?> event) {
		if (copyOutputIfSource(event)) {
			event.stopPropagation();
			return;
		}
		CASTableW table = view.getConsoleTable();
		Cell cell = table.getCellForEvent(event);
		table.setFirstRowFront(false);
		if (cell == null) {
			return;
		}
		if (cell.getCellIndex() == CASTableW.COL_CAS_CELLS_WEB) {
			int rowIndex = cell.getRowIndex();
			table.startEditingRow(rowIndex);
			table.adjustCaret(event);
		}
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint point = table.getPointForEvent(event);
		if (point == null || startSelectRow < 0) {
			longTouchManager.cancelTimer();
			return;
		}
		longTouchManager.rescheduleTimerIfRunning(this,
		        EventUtil.getTouchOrClickClientX(event),
		        EventUtil.getTouchOrClickClientY(event));
		handleTouchMoveSelection(event);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		longTouchManager.cancelTimer();
		touchDown = false;
		if (!contextOpened) {
			onPointerUp(event);
		} else {
			contextOpened = false;
		}
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		handleTouchStartSelection(event);
		touchDown = true;
		longTouchManager.scheduleTimer(this,
		        EventUtil.getTouchOrClickClientX(event),
		        EventUtil.getTouchOrClickClientY(event));
		onPointerDown();
		CancelEventTimer.touchEventOccured();
	}

	private void handleMouseDownSelection(MouseDownEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint point = table.getPointForEvent(event);
		if (point == null || point.getX() != CASTableW.COL_CAS_HEADER) {
			startSelectRow = -1;
			return;
		}
		int currentRow = point.getY();
		if (!(event.getNativeButton() == NativeEvent.BUTTON_RIGHT
				&& selectionContainsRow(currentRow))) {
			if (event.isShiftKeyDown()) {
				table.setSelectedRows(startSelectRow, currentRow);
			} else if (event.isControlKeyDown()) {
				table.addSelectedRows(currentRow, currentRow);
			} else {
				startSelectRow = currentRow;
				table.setSelectedRows(currentRow, currentRow);
			}
		}
	}

	private boolean selectionContainsRow(int row) {
		CASTableW table = view.getConsoleTable();
		for (Integer item : table.getSelectedRows()) {
			if (item.equals(row)) {
				return true;
			}
		}
		return false;
	}

	private void handleMouseMoveSelection(MouseMoveEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint point = table.getPointForEvent(event);
		if (point == null || point.getX() != CASTableW.COL_CAS_HEADER
		        || startSelectRow < 0 || !mouseDown) {
			return;
		}
		int currentRow = point.getY();
		if (event.isControlKeyDown()) {
			table.setSelectedRows(startSelectRow, currentRow);
		} else {
			table.addSelectedRows(currentRow, currentRow);
		}
	}

	private void handleTouchStartSelection(TouchStartEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint point = table.getPointForEvent(event);
		if (point == null) {
			this.startSelectRow = -1;
			return;
		}
		int currentRow = point.getY();
		startSelectRow = currentRow;
		table.setSelectedRows(currentRow, currentRow);
	}

	private void handleTouchMoveSelection(TouchMoveEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint point = table.getPointForEvent(event);
		if (point == null || startSelectRow < 0 || !touchDown) {
			return;
		}
		int currentRow = point.getY();
		table.setSelectedRows(startSelectRow, currentRow);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		char ch = e.getCharCode();
		CASTableW table = view.getConsoleTable();
		int editingRow = table.getEditingRow();
		if (editingRow < 0) {
			Log.debug("Key " + ch + " pressed, no row is being edited.");
			return;
		}
		CASTableCellEditor editor = table.getEditor();
		String text = editor.getInput();
		// if closing paranthesis is typed and there is no opening parenthesis
		// for it
		// add one in the beginning
		if (editingRow > 0 && text.length() == 0) {
			if (handleFirstLetter(ch, editingRow, editor)) {
				e.preventDefault();
			}
		}
		if (e.isEnterKey()) {
			this.handleEnterKey(e.isCtrlDown(), e.isAltDown(), app, true);
			e.preventDefault();
		}
	}

	/**
	 * @param ch
	 *            first character
	 * @param editingRow
	 *            row index
	 * @param editor
	 *            editor
	 * @return true if special handling was necessary
	 */
	public boolean handleFirstLetter(char ch, int editingRow,
			CASTableCellEditor editor) {
		switch (ch) {
		case ' ':
		case '|':
			// insert output of previous row (not in parentheses)

				GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(
				        editingRow - 1);
				editor.setInput(selCellValue
				        .getOutputRHS(StringTemplate.defaultTemplate) + " ");
			return true;

		case ')':
			// insert output of previous row in parentheses

			selCellValue = view.getConsoleTable().getGeoCasCell(
				        editingRow - 1);
				String prevOutput = selCellValue
				        .getOutputRHS(StringTemplate.defaultTemplate);
				editor.setInput("(" + prevOutput + ")");
			return true;

		case '=':
			// insert input of previous row

			selCellValue = view.getConsoleTable().getGeoCasCell(
				        editingRow - 1);
				editor.setInput(selCellValue
				        .getInput(StringTemplate.defaultTemplate));
			return true;
		}
		return false;

	}

	@Override
	public void onBlur(BlurEvent event) {
		CASTableCellEditor editor = view.getConsoleTable().getEditor();
		if (!((CASEditorW) editor).isSuggesting()) {
			view.getConsoleTable().stopEditing();
			view.getConsoleTable().setFirstRowFront(false);
		}
	}

}
