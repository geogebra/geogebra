package geogebra.web.cas.view;

import geogebra.common.awt.GPoint;
import geogebra.common.cas.view.CASTableCellController;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;
import geogebra.html5.gui.GuiManagerInterfaceW;
import geogebra.html5.gui.util.LongTouchManager;
import geogebra.html5.gui.util.LongTouchTimer.LongTouchHandler;
import geogebra.html5.main.AppW;
import geogebra.html5.util.EventUtil;
import geogebra.web.gui.GuiManagerW;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class CASTableControllerW extends CASTableCellController implements
        MouseDownHandler, MouseUpHandler, MouseMoveHandler, ClickHandler,
        KeyHandler, BlurHandler, TouchStartHandler, TouchEndHandler,
        TouchMoveHandler, LongTouchHandler {

	private CASViewW view;
	private AppW app;
	private int startSelectRow;
	
	private LongTouchManager longTouchManager;
	private boolean cancelNextTouchEnd = false;

	public CASTableControllerW(CASViewW casViewW, AppW app) {
		view = casViewW;
		this.app = app;
		longTouchManager = LongTouchManager.getInstance();
	}
	
	public void handleLongTouch(int x, int y) {
		CASTableW table = view.getConsoleTable();
		if (!table.isSelectedIndex(startSelectRow)) {
			table.setSelectedRows(startSelectRow, startSelectRow);
		}
		if (table.getSelectedRows().length > 0) {
			RowHeaderPopupMenuW popupMenu = ((GuiManagerW) app
			        .getGuiManager()).getCASContextMenu(null, table);
			popupMenu.show(new GPoint(x, y));
			cancelNextTouchEnd = true;
		}
	}

	public void onClick(ClickEvent event) {
		setActiveToolbar();
		if (event.getSource() != view.getComponent()) { // output clicked
			if (copyOutputToEditingCell(event)) {
				event.stopPropagation();
				return;
			}
		}
		CASTableW table = view.getConsoleTable();
		table.setFirstRowFront(false);
		Cell c = table.getCellForEvent(event);
		if (c == null)
			return;
		if (c.getCellIndex() == CASTableW.COL_CAS_CELLS_WEB) {
			int rowIndex = c.getRowIndex();
			table.startEditingRow(rowIndex);
		}
	}
	
	private void setActiveToolbar() {
		if (app.getToolbar() != null) {
			GuiManagerInterfaceW gm = app.getGuiManager();
			gm.setActiveToolbarId(App.VIEW_CAS);
		}
	}

	private boolean copyOutputToEditingCell(HumanInputEvent<?> event) {
		CASTableW table = view.getConsoleTable();
		CASTableCellW editingCell = table.getEditingCell();
		CASTableCellW clickedCell = table.getCasCellForEvent(event);
		if (editingCell != null && clickedCell != null) {
			editingCell.insertInput(clickedCell.getOutputString());
			return true;
		}
		return false;
	}

	public void onMouseMove(MouseMoveEvent event) {
		GPoint p = view.getConsoleTable().getPointForEvent(event);
		CASTableW table = view.getConsoleTable();
		if (p == null || p.getX() != CASTableW.COL_CAS_HEADER
		        || startSelectRow < 0) {
			return;
		}
		if (event.isShiftKeyDown()) {
			table.addSelectedRows(startSelectRow, p.getY());
		}
		event.stopPropagation();

	}

	public void onMouseUp(MouseUpEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint p = table.getPointForEvent(event);
		if (p == null || p.getX() != CASTableW.COL_CAS_HEADER
		        || startSelectRow < 0) {
			return;
		}
		table.cancelEditing();
		event.stopPropagation();
		if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
			for (Integer item : table.getSelectedRows()) {
				if (item.equals(startSelectRow))
					return;
			}
			table.setSelectedRows(startSelectRow, startSelectRow);
			return;
		}
		if (event.isControlKeyDown()) {
			table.addSelectedRows(startSelectRow, p.getY());
		} else {
			table.setSelectedRows(startSelectRow, p.getY());
		}
	}

	public void onMouseDown(MouseDownEvent event) {

		// Remove context menu (or other popups), if it's visible.
		((GuiManagerW) app.getGuiManager()).removePopup();

		CASTableW table = view.getConsoleTable();
		GPoint p = table.getPointForEvent(event);
		if (p == null || p.getX() != CASTableW.COL_CAS_HEADER) {
			this.startSelectRow = -1;
			return;
		}
		if (!event.isShiftKeyDown()) {
			this.startSelectRow = p.getY();
		} else if (event.isControlKeyDown()) {
			table.addSelectedRows(startSelectRow, p.getY());
		} else {
			table.setSelectedRows(startSelectRow, p.getY());
		}
		event.stopPropagation();
	}

	public void keyReleased(KeyEvent e) {
		char ch = e.getCharCode();
		CASTableW table = view.getConsoleTable();
		int editingRow = table.getEditingRow();
		if (editingRow < 0) {
			App.debug("No row is being edited.");
			return;
		}
		CASTableCellEditorW editor = table.getEditor();
		String text = editor.getInput();
		// if closing paranthesis is typed and there is no opening parenthesis
		// for it
		// add one in the beginning
		switch (ch) {
		case ' ':
		case '|':
			// insert output of previous row (not in parentheses)
			if (editingRow > 0 && text.length() == 0) {
				GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(
				        editingRow - 1);
				editor.setInput(selCellValue
				        .getOutputRHS(StringTemplate.defaultTemplate) + " ");
				e.preventDefault();
			}
			break;

		case ')':
			// insert output of previous row in parentheses
			if (editingRow > 0 && text.length() == 0) {
				GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(
				        editingRow - 1);
				String prevOutput = selCellValue
				        .getOutputRHS(StringTemplate.defaultTemplate);
				editor.setInput("(" + prevOutput + ")");
				e.preventDefault();
			}
			break;

		case '=':
			// insert input of previous row
			if (editingRow > 0 && text.length() == 0) {
				GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(
				        editingRow - 1);
				editor.setInput(selCellValue
				        .getInput(StringTemplate.defaultTemplate));
				e.preventDefault();
			}
			break;
		}
		if (e.isEnterKey()) {
			this.handleEnterKey(e, app);
			e.preventDefault();
		}
	}

	public void onBlur(BlurEvent event) {
		view.getConsoleTable().stopEditing();
		view.getConsoleTable().setFirstRowFront(false);
	}

	public void onTouchMove(TouchMoveEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint p = table.getPointForEvent(event);
		if (p == null || startSelectRow < 0) {
			longTouchManager.cancelTimer();
			return;
		}
		longTouchManager.rescheduleTimerIfRunning(this, EventUtil.getTouchOrClickClientX(event),
		        EventUtil.getTouchOrClickClientY(event));
		table.addSelectedRows(startSelectRow, p.getY());
	}

	public void onTouchEnd(TouchEndEvent event) {
		longTouchManager.cancelTimer();
		if (cancelNextTouchEnd) {
			cancelNextTouchEnd = false;
			return;
		}
		// copy output
		if (event.getSource() != view.getComponent()) { // output clicked
			if (copyOutputToEditingCell(event)) {
				event.stopPropagation();
				return;
			}
		}
		
		// edit cell
		CASTableW table = view.getConsoleTable();
		table.setFirstRowFront(false);
		GPoint p = table.getPointForEvent(event);
		if (p == null) {
			return;
		}
		if (p.getY() == CASTableW.COL_CAS_CELLS_WEB) {
			int rowIndex = p.getX();
			table.startEditingRow(rowIndex);
		}
	}

	public void onTouchStart(TouchStartEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint p = table.getPointForEvent(event);
		if (p == null) {
			this.startSelectRow = -1;
			return;
		}
		longTouchManager.scheduleTimer(this, EventUtil.getTouchOrClickClientX(event),
		        EventUtil.getTouchOrClickClientY(event));
		this.startSelectRow = p.getY();
	}

}
