package org.geogebra.web.web.gui.view.spreadsheet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.SpreadsheetTableModelW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

public class SpreadsheetKeyListenerW implements KeyDownHandler, KeyPressHandler {

	private AppW app;
	private SpreadsheetViewW view;
	private Kernel kernel;
	private MyTableW table;
	private SpreadsheetTableModelW model;
	private MyCellEditorW editor;
	boolean keyDownSomething = false;

	public SpreadsheetKeyListenerW(AppW app, MyTableW table) {

		this.app = app;
		this.kernel = app.getKernel();
		this.table = table;
		this.view = (SpreadsheetViewW) table.getView();
		this.model = (SpreadsheetTableModelW) table.getModel();
		this.editor = table.getEditor();

	}

	public void onKeyDown(KeyDownEvent e) {

		e.stopPropagation();

		// cancel as this may prevent the keyPress in some browsers
		// hopefully it is enough to preventDefault in onKeyPress
		// e.preventDefault();

		// pass the event on to the cell editor if editing
		if (table.isEditing) {
			table.sendEditorKeyDownEvent(e);
			return;
		}

		int keyCode = e.getNativeKeyCode();// .getKeyCode();
		// Application.debug(keyCode+"");
		// boolean shiftDown = e.isShiftDown();
		boolean altDown = e.isAltKeyDown();
		boolean ctrlDown = e.isControlKeyDown() || e.isMetaKeyDown();

		// AppW.isControlDown(e) // Windows ctrl/Mac Meta
		// || e.isControlDown(); // Fudge (Mac ctrl key)

		int row = table.getSelectedRow();
		int column = table.getSelectedColumn();

		switch (keyCode) {

		case KeyCodes.KEY_UP:// KeyEvent.VK_UP:
			e.preventDefault();
			if (e.isControlKeyDown()) {
				// AppW.isControlDown(e)) {
				if (model.getValueAt(row, column) != null) {
					// move to top of current "block"
					// if shift pressed, select cells too
					while (row > 0 && model.getValueAt(row - 1, column) != null)
						row--;
					table.changeSelection(row, column, 
					        e.isShiftKeyDown());
				} else {
					// move up to next defined cell
					while (row > 0 && model.getValueAt(row - 1, column) == null)
						row--;
					table.changeSelection(Math.max(0, row - 1), column, false);

				}
				// e.consume();
			} else {
				// default action
				row = table.getLeadSelectionRow();
				column = table.getLeadSelectionColumn();
				if (row > 0) {
					table.changeSelection(row - 1, column, e.isShiftKeyDown());
				}
			}
			// copy description into input bar when a cell is entered
			// GeoElement geo = (GeoElement)
			// getModel().getValueAt(table.getSelectedRow() - 1,
			// table.getSelectedColumn());
			// if (geo != null) {
			// AlgebraInput ai =
			// (AlgebraInput)(app.getGuiManager().getAlgebraInput());
			// ai.setString(geo);
			// }

			break;

		case KeyCodes.KEY_LEFT:// VK_LEFT:
			e.preventDefault();
			if (e.isControlKeyDown()) {
				// AppD.isControlDown(e)) {

				if (model.getValueAt(row, column) != null) {
					// move to left of current "block"
					// if shift pressed, select cells too
					while (column > 0
					        && model.getValueAt(row, column - 1) != null)
						column--;
					table.changeSelection(row, column, e.isShiftKeyDown());
				} else {
					// move left to next defined cell
					while (column > 0
					        && model.getValueAt(row, column - 1) == null)
						column--;
					table.changeSelection(row, Math.max(0, column - 1), false);
				}

				// e.consume();
			} else {
				// default action
				row = table.getLeadSelectionRow();
				column = table.getLeadSelectionColumn();
				if (column > 0) {
					table.changeSelection(row, column - 1, e.isShiftKeyDown());
				}
			}
			// // copy description into input bar when a cell is entered
			// geo = (GeoElement) getModel().getValueAt(table.getSelectedRow(),
			// table.getSelectedColumn() - 1);
			// if (geo != null) {
			// AlgebraInput ai =
			// (AlgebraInput)(app.getGuiManager().getAlgebraInput());
			// ai.setString(geo);
			// }
			break;

		case KeyCodes.KEY_DOWN:// VK_DOWN:
			e.preventDefault();
			// auto increase spreadsheet size when you go off the bottom
			if (table.getSelectedRow() + 1 >= table.getRowCount()
			        && table.getSelectedRow() + 1 < Kernel.MAX_SPREADSHEET_ROWS_VISIBLE) {
				model.setRowCount(table.getRowCount() + 1);

				// getView().getRowHeader().revalidate(); //G.STURR 2010-1-9

				table.changeSelection(row + 1, column, e.isShiftKeyDown());

			} else if (e.isControlKeyDown()) {
				// AppD.isControlDown(e)) {

				if (model.getValueAt(row, column) != null) {

					// move to bottom of current "block"
					// if shift pressed, select cells too
					while (row < table.getRowCount() - 1
					        && model.getValueAt(row + 1, column) != null)
						row++;
					table.changeSelection(row, column, e.isShiftKeyDown());
				} else {
					// move down to next selected cell
					while (row < table.getRowCount() - 1
					        && model.getValueAt(row + 1, column) == null)
						row++;
					table.changeSelection(
					        Math.min(table.getRowCount() - 1, row + 1), column,
					        e.isShiftKeyDown());

				}

				// e.consume();
			} else {
				// default action
				row = table.getLeadSelectionRow();
				column = table.getLeadSelectionColumn();
				if (row < table.getRowCount() - 1) {

					table.changeSelection(row + 1, column, e.isShiftKeyDown());
				}
			}

			// // copy description into input bar when a cell is entered
			// geo = (GeoElement)
			// getModel().getValueAt(table.getSelectedRow()+1,
			// table.getSelectedColumn());
			// if (geo != null) {
			// AlgebraInput ai =
			// (AlgebraInput)(app.getGuiManager().getAlgebraInput());
			// ai.setString(geo);
			// }

			break;

		case KeyCodes.KEY_HOME:// .VK_HOME:
			e.preventDefault();

			// if shift pressed, select cells too
			if (e.isControlKeyDown()) {
				// AppD.isControlDown(e)) {

				// move to top left of spreadsheet
				table.changeSelection(0, 0, e.isShiftKeyDown());
			} else {
				// move to left of current row
				table.changeSelection(row, 0, e.isShiftKeyDown());
			}

			// e.consume();
			break;

		case KeyCodes.KEY_END:// .VK_END:
			e.preventDefault();

			// move to bottom right of spreadsheet
			// if shift pressed, select cells too

			// find rectangle that will contain all cells
			for (int c = 0; c < table.getColumnCount() - 1; c++)
				for (int r = 0; r < table.getRowCount() - 1; r++)
					if ((r > row || c > column)
					        && model.getValueAt(r, c) != null) {
						if (r > row)
							row = r;
						if (c > column)
							column = c;
					}
			table.changeSelection(row, column, e.isShiftKeyDown());

			// e.consume();
			break;

		case KeyCodes.KEY_RIGHT: // Event.VK_RIGHT:
			e.preventDefault();
			// auto increase spreadsheet size when you go off the right

			if (table.getSelectedColumn() + 1 >= table.getColumnCount()
			        && table.getSelectedColumn() + 1 < Kernel.MAX_SPREADSHEET_COLUMNS_VISIBLE) {

				// table.setRepaintAll();
				model.setColumnCount(table.getColumnCount() + 1);
				// view.columnHeaderRevalidate();

				// view.repaint();//FIXME: setRepaintAll is not compatible with
				// TimerSystemW!
				// table.repaint();

				// view.getFocusPanel().setWidth(table.getGrid().getOffsetWidth()+"px");

				// these two lines are a workaround for Java 6
				// (Java bug?)
				table.changeSelection(row, column + 1, false);
			} else if (e.isControlKeyDown()) {
				// AppD.isControlDown(e)) {

				if (model.getValueAt(row, column) != null) {
					// move to bottom of current "block"
					// if shift pressed, select cells too
					while (column < table.getColumnCount() - 1
					        && model.getValueAt(row, column + 1) != null)
						column++;
					table.changeSelection(row, column, e.isShiftKeyDown());
				} else {
					// move right to next defined cell
					while (column < table.getColumnCount() - 1
					        && model.getValueAt(row, column + 1) == null)
						column++;
					table.changeSelection(row,
					        Math.min(table.getColumnCount() - 1, column + 1),
					        false);
				}
				// e.consume();
			} else {

				// default action
				row = table.getLeadSelectionRow();
				column = table.getLeadSelectionColumn();
				if (column < table.getColumnCount() - 1) {
					table.changeSelection(row, column + 1, e.isShiftKeyDown());
				}
			}

			// // copy description into input bar when a cell is entered
			// geo = (GeoElement) getModel().getValueAt(table.getSelectedRow(),
			// table.getSelectedColumn() + 1);
			// if (geo != null) {
			// AlgebraInput ai =
			// (AlgebraInput)(app.getGuiManager().getAlgebraInput());
			// ai.setString(geo);
			// }
			break;

		case KeyCodes.KEY_SHIFT:// .VK_SHIFT:
		case KeyCodes.KEY_CTRL:// Event.VK_CONTROL:
		case KeyCodes.KEY_ALT:// Event.VK_ALT:
			// case KeyEvent.VK_META: //MAC_OS Meta
			// e.consume(); // stops editing start
			break;

		case GWTKeycodes.KEY_F9:// Event.VK_F9:
			kernel.updateConstruction();
			// e.consume(); // stops editing start
			break;

		case GWTKeycodes.KEY_R:// KeyEvent.VK_R:
			if (e.isControlKeyDown()) {
				// AppD.isControlDown(e)) {
				kernel.updateConstruction();
				// e.consume();
			} else
				letterOrDigitTyped();
			break;

		// needs to be here to stop keypress starting a cell edit after the undo
		case GWTKeycodes.KEY_Z:// KeyEvent.VK_Z: //undo
			if (ctrlDown) {
				// Application.debug("undo");
				app.getGuiManager().undo();
				// e.consume();
			} else
				letterOrDigitTyped();
			break;

		// needs to be here to stop keypress starting a cell edit after the redo
		case GWTKeycodes.KEY_Y:// KeyEvent.VK_Y: //redo
			if (ctrlDown) {
				// Application.debug("redo");
				app.getGuiManager().redo();
				// e.consume();
			} else
				letterOrDigitTyped();
			break;

		case GWTKeycodes.KEY_C:// KeyEvent.VK_C:
		case GWTKeycodes.KEY_V:// KeyEvent.VK_V:
		case GWTKeycodes.KEY_X:// KeyEvent.VK_X:
			if (!editor.isEditing()) {
				if (!(ctrlDown || e.isAltKeyDown())) {
					letterOrDigitTyped();
				} else if (ctrlDown) {
					// e.consume();
					if (keyCode == GWTKeycodes.KEY_C) {
						// KeyEvent.VK_C) {
						table.copy(altDown);
					} else if (keyCode == GWTKeycodes.KEY_V) {
						// KeyEvent.VK_V) {

						// nooo! this cannot get what should be
						// pasted well! so using the "paste"
						// event instead, addPasteHandlerTo!

						// sadly, this is needed in Internet Explorer!
						// workaround comes later... Browser.isIE is wrong!
						boolean storeUndo = table.paste();
						view.rowHeaderRevalidate();
						if (storeUndo)
							app.storeUndoInfo();

						// but still, CTRL+V should survive the
						// keypress event in order to properly
						// send a paste event in Firefox!
						// workaround could come here, but we
						// can also detect CTRL+v at keypress
					} else if (keyCode == GWTKeycodes.KEY_X) {
						// KeyEvent.VK_X) {
						boolean storeUndo = table.cut();
						if (storeUndo)
							app.storeUndoInfo();
					}
				}
			}
			break;

		case GWTKeycodes.KEY_DELETE:// KeyEvent.VK_DELETE:
		case GWTKeycodes.KEY_BACKSPACE:// KeyEvent.VK_BACK_SPACE:
			if (!editor.isEditing()) {
				e.preventDefault();
				// e.consume();
				// Application.debug("deleting...");
				boolean storeUndo = table.delete();
				if (storeUndo)
					app.storeUndoInfo();
				return;
			}
			break;

		// case KeyEvent.VK_ENTER:
		case GWTKeycodes.KEY_F2:// KeyEvent.VK_F2: //FIXME
			if (!editor.isEditing()) {
				table.setAllowEditing(true);
				table.editCellAt(table.getSelectedRow(),
				        table.getSelectedColumn());
				// ?//final JTextComponent f =
				// (JTextComponent)table.getEditorComponent();
				// ?// f.requestFocus();
				// ?// f.getCaret().setVisible(true);
				table.setAllowEditing(false);
			}
			// e.consume();
			break;

		case KeyCodes.KEY_ENTER:// KeyEvent.VK_ENTER:
			if (MyCellEditorW.tabReturnCol > -1) {
				table.changeSelection(row, MyCellEditorW.tabReturnCol, false);
				MyCellEditorW.tabReturnCol = -1;
			}

			// fall through
		case GWTKeycodes.KEY_PAGEDOWN:// KeyEvent.VK_PAGE_DOWN:
			e.preventDefault();

			int pixelx = table.getPixel(column, row, true).getX();
			int pixely = view.getFocusPanel().getAbsoluteTop()
			        + view.getFocusPanel().getOffsetHeight();
			GPoint gip = table.getIndexFromPixel(pixelx, pixely);
			if (gip != null) {
				table.changeSelection(gip.getY(), column, false);
			} else {
				table.changeSelection(model.getRowCount() - 1, column, false);
			}
			break;

		case GWTKeycodes.KEY_PAGEUP:// KeyEvent.VK_PAGE_UP:
			e.preventDefault();

			int pixx = table.getPixel(column, row, true).getX();
			int pixy = view.getFocusPanel().getAbsoluteTop();
			GPoint gi = table.getIndexFromPixel(pixx, pixy);
			if (gi != null) {
				table.changeSelection(gi.getY(), column, false);
				// stop cell being erased before moving
			} else {
				table.changeSelection(0, column, false);
			}
			break;

		// stop TAB erasing cell before moving
		case KeyCodes.KEY_TAB:// KeyEvent.VK_TAB:
			e.preventDefault();

			// disable shift-tab in column A
			if (table.getSelectedColumn() == 0 && e.isShiftKeyDown()) {
				// e.consume();
			} else {
				if (e.isShiftKeyDown()) {
					// if (table.getSelectedColumn() == 0)
					// this cannot happen

					table.changeSelection(row, column - 1, false);

				} else {
					if (table.getSelectedColumn() + 1 >= table.getColumnCount() - 1) {
						if (table.getSelectedRow() + 1 < table.getRowCount() - 1) {
							table.changeSelection(row + 1, 0, false);
						}
					} else {
						table.changeSelection(row, column + 1, false);
					}
				}
			}
			break;

		case GWTKeycodes.KEY_A:// KeyEvent.VK_A:
			if (e.isControlKeyDown()) {
				// AppD.isControlDown(e)) {

				// select all cells

				row = 0;
				column = 0;
				// find rectangle that will contain all defined cells
				for (int c = 0; c < table.getColumnCount() - 1; c++)
					for (int r = 0; r < table.getRowCount() - 1; r++)
						if ((r > row || c > column)
						        && model.getValueAt(r, c) != null) {
							if (r > row)
								row = r;
							if (c > column)
								column = c;
						}
				table.changeSelection(0, 0, false);
				table.changeSelection(row, column, true);

				// e.consume();

			}
			// no break, fall through
		default:
			if (/*
				 * ? !Character.isIdentifierIgnorable(
				 * Character.toChars(e.getNativeEvent().getCharCode())[0]
				 * //e.getKeyChar() ) &&
				 */
			!editor.isEditing() && !(ctrlDown || e.isAltKeyDown())) {
				letterOrDigitTyped();
			} else
				// e.consume();
				break;

		}

		/*
		 * if (keyCode >= 37 && keyCode <= 40) { if (editor.isEditing()) return;
		 * }
		 * 
		 * for (int i = 0; i < defaultKeyListeners.length; ++ i) { if
		 * (e.isConsumed()) break; defaultKeyListeners[i].keyPressed(e); }
		 */

	}

	public void letterOrDigitTyped() {

		// memorize that this is OK according to keyCode
		keyDownSomething = true;

		table.setAllowEditing(true);
		table.repaint(); // G.Sturr 2009-10-10: cleanup when keypress edit
		                 // begins

		// check if cell fixed
		Object o = model.getValueAt(table.getSelectedRow(),
		        table.getSelectedColumn());
		if (o != null && o instanceof GeoElement) {
			GeoElement geo = (GeoElement) o;
			if (geo.isFixed())
				return;
		}

		model.setValueAt(null, table.getSelectedRow(),
		        table.getSelectedColumn());

		table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
		table.setAllowEditing(false);
	}

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
		} else if (e.getCharCode() != 86 && e.getCharCode() != 118) {
			e.preventDefault();
		}

		// pass the event on to the cell editor if editing
		if (table.isEditing) {
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
			public void execute() {
				// if the user enters something meaningful, spare an additional
				// entering
				String ch = "";
				if (charcode > 0)
					ch = new String(Character.toChars(charcode));

				Object ce = table.getCellEditor(table.getSelectedRow(),
				        table.getSelectedColumn());
				GeoClass ceType = table.getCellEditorType(
				        table.getSelectedRow(), table.getSelectedColumn());
				if (ce instanceof MyCellEditorW && ceType == GeoClass.DEFAULT
				        && ch != "") {
					((MyCellEditorW) ce).setText(ch);
					((AutoCompleteTextFieldW) ((MyCellEditorW) ce)
					        .getTextfield())
					        .setCaretPosition(((MyCellEditorW) ce)
					                .getEditingValue().length());
				}
			}
		});
	}

	public native void addPasteHandlerTo(Element elem) /*-{
		var self = this;
		elem.onpaste = function(event) {
			if (event.clipboardData) {
				var cbd = event.clipboardData;
				var text = cbd.getData('text/plain');
				self.@org.geogebra.web.web.gui.view.spreadsheet.SpreadsheetKeyListenerW::onPaste(Ljava/lang/String;)(text);
			}
		}
	}-*/;

	public void onPaste(String text) {
		boolean storeUndo = table.paste(text);
		view.rowHeaderRevalidate();
		if (storeUndo)
			app.storeUndoInfo();
	}
}
