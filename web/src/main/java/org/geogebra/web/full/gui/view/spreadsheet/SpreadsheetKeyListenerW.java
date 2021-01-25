package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GlobalKeyDispatcher;
import org.geogebra.common.main.SpreadsheetTableModelSimple;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * Key event handler for spreadsheet
 */
public class SpreadsheetKeyListenerW
		implements KeyDownHandler, KeyPressHandler, KeyUpHandler {

	private AppW app;
	private SpreadsheetViewW view;
	private Kernel kernel;
	private MyTableW table;
	private SpreadsheetTableModelSimple model;
	private MyCellEditorW editor;
	boolean keyDownSomething = false;

	/**
	 * @param app
	 *            application
	 * @param table
	 *            table
	 */
	public SpreadsheetKeyListenerW(AppW app, MyTableW table) {
		this.app = app;
		this.kernel = app.getKernel();
		this.table = table;
		this.view = (SpreadsheetViewW) table.getView();
		this.model = (SpreadsheetTableModelSimple) table.getModel();
		this.editor = table.getEditor();
	}

	@Override
	public void onKeyDown(KeyDownEvent e) {
		e.stopPropagation();
		GlobalKeyDispatcherW.setDownKeys(e);
		// cancel as this may prevent the keyPress in some browsers
		// hopefully it is enough to preventDefault in onKeyPress
		// e.preventDefault();

		// pass the event on to the cell editor if editing
		if (table.editing) {
			table.sendEditorKeyDownEvent(e);
			return;
		}
		table.setAllowAutoEdit();
		int keyCode = e.getNativeKeyCode();
		boolean shiftDown = e.isShiftKeyDown();
		boolean ctrlDown = e.isControlKeyDown() || e.isMetaKeyDown();

		GPoint pos = new GPoint(table.getSelectedColumn(),
				table.getSelectedRow());

		switch (keyCode) {
		case KeyCodes.KEY_UP:
			e.preventDefault();
			handleKeyUp(ctrlDown, shiftDown, pos);
			break;

		case KeyCodes.KEY_LEFT:
			e.preventDefault();
			handleKeyLeft(ctrlDown, shiftDown, pos);
			break;

		case KeyCodes.KEY_DOWN:
			e.preventDefault();
			handleKeyDown(ctrlDown, shiftDown, pos);
			break;

		case KeyCodes.KEY_HOME:
			e.preventDefault();
			handleHomeKey(ctrlDown, shiftDown, pos);
			break;

		case KeyCodes.KEY_END:
			e.preventDefault();
			handleEndKey(shiftDown, pos);
			break;

		case KeyCodes.KEY_RIGHT:
			e.preventDefault();
			// auto increase spreadsheet size when you go off the right
			handleKeyRight(ctrlDown, shiftDown, pos);
			break;

		case KeyCodes.KEY_SHIFT:
		case KeyCodes.KEY_CTRL:
		case KeyCodes.KEY_ALT:
			// do nothing
			break;

		case GWTKeycodes.KEY_F9:
			kernel.updateConstruction(true);
			break;

		case GWTKeycodes.KEY_R:
			if (ctrlDown) {
				kernel.updateConstruction(true);
			} else {
				letterOrDigitTyped();
			}
			break;

		// needs to be here to stop keypress starting a cell edit after the undo
		case GWTKeycodes.KEY_Z:
			if (ctrlDown) {
				app.getGuiManager().undo();
			} else {
				letterOrDigitTyped();
			}
			break;

		// needs to be here to stop keypress starting a cell edit after the redo
		case GWTKeycodes.KEY_Y:
			if (ctrlDown) {
				app.getGuiManager().redo();
			} else {
				letterOrDigitTyped();
			}
			break;

		case GWTKeycodes.KEY_D:
		case GWTKeycodes.KEY_BACK_QUOTE:
			if (ctrlDown) {
				GlobalKeyDispatcher.toggleAlgebraStyle(app);
				e.preventDefault();
			} else {
				letterOrDigitTyped();
			}
			break;

		case GWTKeycodes.KEY_DELETE:
		case GWTKeycodes.KEY_BACKSPACE:
			if (!editor.isEditing()) {
				e.preventDefault();
				boolean storeUndo = table.delete();
				if (storeUndo) {
					app.storeUndoInfo();
				}
				return;
			}
			break;

		case GWTKeycodes.KEY_F2:
			if (!editor.isEditing()) {
				table.setAllowEditing(true);
				table.editCellAt(table.getSelectedRow(),
						table.getSelectedColumn());
				table.setAllowEditing(false);
			}
			break;

		case KeyCodes.KEY_ENTER:
			if (!editor.isEditing()) {
				editOnEnter();
				break;
			}
			editor.onEnter();

			//$FALL-THROUGH$
		case GWTKeycodes.KEY_PAGEDOWN:
			e.preventDefault();

			int pixelx = table.getPixel(pos.x, pos.y, true).getX();
			int pixely = view.getFocusPanel().getAbsoluteTop()
			        + view.getFocusPanel().getOffsetHeight();
			GPoint gip = table.getIndexFromPixel(pixelx, pixely);
			if (gip != null) {
				table.changeSelection(gip.getY(), pos.x, false);
			} else {
				table.changeSelection(model.getRowCount() - 1, pos.x, false);
			}
			break;

		case GWTKeycodes.KEY_PAGEUP:
			e.preventDefault();

			int pixx = table.getPixel(pos.x, pos.y, true).getX();
			int pixy = view.getFocusPanel().getAbsoluteTop();
			GPoint gi = table.getIndexFromPixel(pixx, pixy);
			if (gi != null) {
				table.changeSelection(gi.getY(), pos.x, false);
				// stop cell being erased before moving
			} else {
				table.changeSelection(0, pos.x, false);
			}
			break;

		// stop TAB erasing cell before moving
		case KeyCodes.KEY_TAB:
			e.preventDefault();
			handleTabKey(shiftDown, pos);
			break;

		case GWTKeycodes.KEY_A:
			if (ctrlDown) {
				selectAll(pos);
			}
			//$FALL-THROUGH$
		default:
			if (!editor.isEditing() && !(ctrlDown || e.isAltKeyDown())) {
				letterOrDigitTyped();
			}
		}
	}

	private void handleHomeKey(boolean ctrl, boolean shift, GPoint pos) {
		// if shift pressed, select cells too
		if (ctrl) {
			// move to top left of spreadsheet
			table.changeSelection(0, 0, shift);
		} else {
			// move to left of current row
			table.changeSelection(pos.y, 0, shift);
		}
	}

	private void handleTabKey(boolean shift, GPoint pos) {
		// disable shift-tab in column A
		if (table.getSelectedColumn() == 0 && shift) {
			// e.consume();
		} else {
			if (shift) {
				// if (table.getSelectedColumn() == 0)
				// this cannot happen

				table.changeSelection(pos.y, pos.x - 1, false);

			} else {
				if (table.getSelectedColumn() + 1 >= table.getColumnCount()
						- 1) {
					if (table.getSelectedRow() + 1 < table.getRowCount() - 1) {
						table.changeSelection(pos.y + 1, 0, false);
					}
				} else {
					table.changeSelection(pos.y, pos.x + 1, false);
				}
			}
		}

	}

	private void selectAll(GPoint pos) {
		pos.y = 0;
		pos.x = 0;
		// find rectangle that will contain all defined cells
		for (int c = 0; c < table.getColumnCount() - 1; c++) {
			for (int r = 0; r < table.getRowCount() - 1; r++) {
				if ((r > pos.y || c > pos.x)
						&& model.getValueAt(r, c) != null) {
					if (r > pos.y) {
						pos.y = r;
					}
					if (c > pos.x) {
						pos.x = c;
					}
				}
			}
		}
		table.changeSelection(0, 0, false);
		table.changeSelection(pos.y, pos.x, true);

	}

	private void handleEndKey(boolean shift, GPoint pos) {

		// move to bottom right of spreadsheet
		// if shift pressed, select cells too

		// find rectangle that will contain all cells
		for (int c = 0; c < table.getColumnCount() - 1; c++) {
			for (int r = 0; r < table.getRowCount() - 1; r++) {
				if ((r > pos.y || c > pos.x)
						&& model.getValueAt(r, c) != null) {
					if (r > pos.y) {
						pos.y = r;
					}
					if (c > pos.x) {
						pos.x = c;
					}
				}
			}
		}
		table.changeSelection(pos.y, pos.x, shift);

	}

	private void handleKeyLeft(boolean ctrl, boolean shift,
			GPoint pos) {
		if (ctrl) {
			// AppD.isControlDown(e)) {

			if (model.getValueAt(pos.y, pos.x) != null) {
				// move to left of current "block"
				// if shift pressed, select cells too
				while (pos.x > 0
						&& model.getValueAt(pos.y, pos.x - 1) != null) {
					pos.x--;
				}
				table.changeSelection(pos.y, pos.x, shift);
			} else {
				// move left to next defined cell
				while (pos.x > 0
						&& model.getValueAt(pos.y, pos.x - 1) == null) {
					pos.x--;
				}
				table.changeSelection(pos.y, Math.max(0, pos.x - 1), false);
			}

			// e.consume();
		} else {
			// default action
			pos.y = table.getLeadSelectionRow();
			pos.x = table.getLeadSelectionColumn();
			if (pos.x > 0) {
				table.changeSelection(pos.y, pos.x - 1, shift);
			}
		}
	}

	private void handleKeyUp(boolean ctrl, boolean shift,
			GPoint pos) {
		if (ctrl) {
			// AppW.isControlDown(e)) {
			if (model.getValueAt(pos.y, pos.x) != null) {
				// move to top of current "block"
				// if shift pressed, select cells too
				while (pos.y > 0
						&& model.getValueAt(pos.y - 1, pos.x) != null) {
					pos.y--;
				}
				table.changeSelection(pos.y, pos.x, shift);
			} else {
				// move up to next defined cell
				while (pos.y > 0
						&& model.getValueAt(pos.y - 1, pos.x) == null) {
					pos.y--;
				}
				table.changeSelection(Math.max(0, pos.y - 1), pos.x, false);

			}
			// e.consume();
		} else {
			// default action
			pos.y = table.getLeadSelectionRow();
			pos.x = table.getLeadSelectionColumn();
			if (pos.y > 0) {
				table.changeSelection(pos.y - 1, pos.x, shift);
			}
		}

	}

	private void handleKeyDown(boolean ctrl, boolean shift, GPoint pos) {
		// auto increase spreadsheet size when you go off the bottom
		if (table.getSelectedRow() + 1 >= table.getRowCount()
				&& table.getSelectedRow() + 1 < app
						.getMaxSpreadsheetRowsVisible()) {
			model.setRowCount(table.getRowCount() + 1);

			// getView().getRowHeader().revalidate(); //G.STURR 2010-1-9

			table.changeSelection(pos.y + 1, pos.x, shift);

		} else if (ctrl) {
			// AppD.isControlDown(e)) {

			if (model.getValueAt(pos.y, pos.x) != null) {

				// move to bottom of current "block"
				// if shift pressed, select cells too
				while (pos.y < table.getRowCount() - 1
						&& model.getValueAt(pos.y + 1, pos.x) != null) {
					pos.y++;
				}
				table.changeSelection(pos.y, pos.x, shift);
			} else {
				// move down to next selected cell
				while (pos.y < table.getRowCount() - 1
						&& model.getValueAt(pos.y + 1, pos.x) == null) {
					pos.y++;
				}
				table.changeSelection(
						Math.min(table.getRowCount() - 1, pos.y + 1), pos.x,
						shift);

			}

			// e.consume();
		} else {
			// default action
			pos.y = table.getLeadSelectionRow();
			pos.x = table.getLeadSelectionColumn();
			if (pos.y < table.getRowCount() - 1) {

				table.changeSelection(pos.y + 1, pos.x, shift);
			}
		}

	}

	private void handleKeyRight(boolean ctrl, boolean shift, GPoint pos) {
		if (table.getSelectedColumn() + 1 >= table.getColumnCount()
				&& table.getSelectedColumn() + 1 < app
						.getMaxSpreadsheetColumnsVisible()) {

			// table.setRepaintAll();
			model.setColumnCount(table.getColumnCount() + 1);
			// view.pos.xHeaderRevalidate();

			// view.repaint();//FIXME: setRepaintAll is not compatible with
			// TimerSystemW!
			// table.repaint();

			// view.getFocusPanel().setWidth(table.getGrid().getOffsetWidth()+"px");

			// these two lines are a workaround for Java 6
			// (Java bug?)
			table.changeSelection(pos.y, pos.x + 1, false);
		} else if (ctrl) {
			// AppD.isControlDown(e)) {

			if (model.getValueAt(pos.y, pos.x) != null) {
				// move to bottom of current "block"
				// if shift pressed, select cells too
				while (pos.x < table.getColumnCount() - 1
						&& model.getValueAt(pos.y, pos.x + 1) != null) {
					pos.x++;
				}
				table.changeSelection(pos.y, pos.x, shift);
			} else {
				// move right to next defined cell
				while (pos.x < table.getColumnCount() - 1
						&& model.getValueAt(pos.y, pos.x + 1) == null) {
					pos.x++;
				}
				table.changeSelection(pos.y,
						Math.min(table.getColumnCount() - 1, pos.x + 1),
						false);
			}
			// e.consume();
		} else {

			// default action
			pos.y = table.getLeadSelectionRow();
			pos.x = table.getLeadSelectionColumn();
			if (pos.x < table.getColumnCount() - 1) {
				table.changeSelection(pos.y, pos.x + 1, shift);
			}
		}

	}

	/**
	 * Activate editor after letter or digit was typed
	 */
	public void letterOrDigitTyped() {

		// memorize that this is OK according to keyCode
		keyDownSomething = true;

		table.setAllowEditing(true);
		table.repaint(); // G.Sturr 2009-10-10: cleanup when keypress edit
		                 // begins

		// check if cell fixed
		if (table.getSelectedRow() < 0 || table.getSelectedColumn() < 0) {
			table.setInitialCellSelection(0, 0);
		}
		Object o = model.getValueAt(table.getSelectedRow(),
		        table.getSelectedColumn());
		if (o instanceof GeoElement) {
			GeoElement geo = (GeoElement) o;
			if (geo.isProtected(EventType.UPDATE)) {
				return;
			}
		}

		model.setValueAt(null, table.getSelectedRow(),
		        table.getSelectedColumn());

		table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		table.setAllowEditing(false);
	}

	private void editOnEnter() {
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
		// memorize that this is OK according to keyCode
		keyDownSomething = true;

		table.setAllowEditing(true);
		table.repaint(); // G.Sturr 2009-10-10: cleanup when keypress edit
		// begins

		// check if cell fixed
		Object o = model.getValueAt(table.getSelectedRow(),
				table.getSelectedColumn());
		if (o instanceof GeoElement) {
			GeoElement geo = (GeoElement) o;
			if (geo.isProtected(EventType.UPDATE)) {
				return;
			}
		}

		model.setValueAt(model.getValueAt(row, col), row, col);

		table.editCellAt(row, col);

		editor.selectAll();
		table.setAllowEditing(false);
	}

	@Override
	public void onKeyPress(KeyPressEvent e) {

		// make sure e.g. SHIFT+ doesn't trigger default browser action
		e.stopPropagation();

		// prevent default action in all cases here except CTRL+V
		// but how to detect CTRL+V? Just detect "V" and "v", and
		// check e.ctrlKeyDown! This is only needed in Firefox, to
		// properly trigger the "paste" event... in other browsers
		// we could call preventDefault unconditionally (paste OK)
		if (!e.isControlKeyDown()) {
			e.preventDefault();
		} else if (e.getCharCode() != 86 && e.getCharCode() != 118 && // "V"
				e.getCharCode() != 67 && e.getCharCode() != 99 && // "C"
				e.getCharCode() != 88 && e.getCharCode() != 120) { // "X"
			e.preventDefault();
		}

		// pass the event on to the cell editor if editing
		if (table.editing) {
			table.sendEditorKeyPressEvent(e);
			return;
		}

		// check if this is OK according to keyCode too (right key)
		if (keyDownSomething) {
			keyDownSomething = false;
		} else {
			return;
		}

		// as the following doesn't work for KeyDownEvent:
		// e.getNativeEvent().getCharCode();
		final int charcode = e.getUnicodeCharCode();

		// make sure that this code runs after the editor has actually been
		// created
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				// if the user enters something meaningful, spare an additional
				// entering
				String str = "";
				if (charcode > 0) {
					str = new String(Character.toChars(charcode));
				}

				Object ce = table.getCellEditor();
				GeoClass ceType = table.getCellEditorType(
				        table.getSelectedRow(), table.getSelectedColumn());
				if (ce instanceof MyCellEditorW && ceType == GeoClass.DEFAULT
						&& !"".equals(str)) {
					((MyCellEditorW) ce).setText(str);
					((MyCellEditorW) ce).getTextfield()
					        .setCaretPosition(((MyCellEditorW) ce)
					                .getEditingValue().length());
				}
			}
		});
	}

	/**
	 * @param elem
	 *            element for catching copy/paste events
	 */
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
				self.@org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetKeyListenerW::onPaste(Ljava/lang/String;)(text);
			}
		}
		elem.oncopy = function(even2) {
			self.@org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetKeyListenerW::onCopy(Z)(even2.altKey);
			// do not prevent default!!!
			// it will take care of the copy...
		}
		elem.oncut = function(even3) {
			self.@org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetKeyListenerW::onCut()();
			// do not prevent default!!!
			// it will take care of the cut...
		}
	}-*/;

	/**
	 * @param text
	 *            pasted text
	 */
	public void onPaste(String text) {
		boolean storeUndo = table.paste(text);
		view.rowHeaderRevalidate();
		if (storeUndo) {
			app.storeUndoInfo();
		}
	}

	/**
	 * Handle copy
	 * 
	 * @param altDown
	 *            whether alt is pressed
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
				table.copy(altDown, true);
			}
		});
	}

	/** Handle Cut */
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
				boolean storeUndo = table.cut(true);
				if (storeUndo) {
					app.storeUndoInfo();
				}
			}
		});
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		GlobalKeyDispatcherW.setDownKeys(event);
	}
}
