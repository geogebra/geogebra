package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetTableController;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.event.KeyEventsHandler;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.user.client.ui.SimplePanel;

import com.himamis.retex.editor.share.util.AltKeys;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * Default cell editor for the spreadsheet, extends
 * DefaultCellEditor(JTextField)
 * 
 */
public class MyCellEditorW implements BaseCellEditor {

	protected Kernel kernel;
	protected AppW app;

	protected GeoElementND value;
	protected MyTableW table;
	AutoCompleteTextFieldW autoCompleteTextField;

	protected int column = -1;
	protected int row = -1;
	private boolean editing = false;

	private boolean allowProcessGeo = false;

	private SpreadsheetCellEditorKeyListener keyListener;

	private boolean allowAutoEdit;

	private SpreadsheetTableController controller;
	// keep track of when <tab> was first pressed
	// so we can return to that column when <enter> pressed
	private int tabReturnCol = -1;

	public void setAllowProcessGeo(boolean allowProcessGeo) {
		this.allowProcessGeo = allowProcessGeo;
	}

	public void setEnableAutoComplete(boolean enableAutoComplete) {
		autoCompleteTextField.setAutoComplete(enableAutoComplete);
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param editorPanel
	 *            panel
	 * @param controller
	 *            controller
	 */
	public MyCellEditorW(Kernel kernel,
			SimplePanel editorPanel, SpreadsheetTableController controller) {
		this.controller = controller;
		this.kernel = kernel;
		app = (AppW) kernel.getApplication();
		keyListener = new SpreadsheetCellEditorKeyListener(false);
		autoCompleteTextField = new AutoCompleteTextFieldW(0,
		        (AppW) kernel.getApplication(), false, keyListener, false);
		autoCompleteTextField.addInsertHandler(text -> {
			if (!editing) {
				((SpreadsheetViewW) app.getGuiManager().getSpreadsheetView())
					.letterOrDigitTyped();
				autoCompleteTextField.setText(text);
			}

		});
		autoCompleteTextField.setAutoComplete(
				app.getSettings().getSpreadsheet().isEnableAutoComplete());
		autoCompleteTextField.setStyleName("SpreadsheetEditorCell");
		editorPanel.add(autoCompleteTextField);
	}

	/**
	 * Update editor content.
	 * 
	 * @param text
	 *            editor content
	 */
	public void setText(String text) {
		if (!autoCompleteTextField.hasFocus() && !table.isDragging) {
			autoCompleteTextField.setText(text);
		}
	}

	/**
	 * Start editing given cell and return textfield.
	 * 
	 * @param table0
	 *            table
	 * @param value0
	 *            cell value
	 * @param isSelected
	 *            cell selected
	 * @param row0
	 *            row
	 * @param column0
	 *            column
	 * @return editor textfield
	 */
	public AutoCompleteTextFieldW getTableCellEditorWidget(MyTableW table0,
			Object value0,
			boolean isSelected, int row0, int column0) {

		table = table0;

		if (value0 instanceof String) { // clicked to type
			value = null;
		} else {
			value = (GeoElement) value0;
		}

		column = column0;
		row = row0;
		String text = "";

		if (value != null) {
			text = controller.getEditorInitString(value);
			int index = text.indexOf("=");
			if (!value.isGeoText()) {
				if (index == -1) {
					text = "=" + text;
				}
			}
		}

		autoCompleteTextField.setText(text);
		// autoCompleteTextField.setFont(app.getFontCanDisplay(text));
		autoCompleteTextField.requestFocus();

		editing = true;

		return autoCompleteTextField;
	}

	/**
	 * set flag to require text start with "=" to activate autocomplete
	 * 
	 * @param equalsRequired
	 *            whether = is needed for autocomplete
	 */
	public void setEqualsRequired(boolean equalsRequired) {
		autoCompleteTextField.setEqualsRequired(equalsRequired);
	}

	/**
	 * Gets flag that requires text start with "=" to activate autocomplete
	 * 
	 * @return whether = is needed for autocomplete
	 */
	public boolean isEqualsRequired() {
		return autoCompleteTextField.isEqualsRequired();
	}

	public void setLabels() {
		autoCompleteTextField.setDictionary(false);
	}
	
	/**
	 * @return whether editor text starts with "="
	 */
	public boolean textStartsWithEquals() {
		String text = getEditingValue();
		return text.startsWith("=");
	}

	// =======================================================
	// In-cell Editing Methods
	// =======================================================

	public boolean isEditing() {
		return editing;
	}

	public int getCaretPosition() {
		return autoCompleteTextField.getCaretPosition();
	}

	/**
	 * Insert a geo label into current editor string.
	 * 
	 * @param label
	 *            label to be inserted
	 */
	public void addLabel(String label) {
		if (!editing) {
			return;
		}
		// String text = (String) delegate.getCellEditorValue();
		// delegate.setValue(text + label);
		autoCompleteTextField.insertString(" " + label + " ");
	}

	/**
	 * @param text
	 *            cell content
	 */
	public void setLabel(String text) {
		if (!editing) {
			return;
		}
		autoCompleteTextField.setText(text);
	}

	public String getEditingValue() {
		return autoCompleteTextField.getText();
	}

	public Object getCellEditorValue() {
		return value;
	}

	// =======================================================
	// Stop/Cancel Editing
	// =======================================================

	@Override
	public void cancelCellEditing() {
		editing = false;
		
		if (table != null) { // ?
			table.finishEditing(false);
		}
	}

	/**
	 * Stops editing and tries to process input.
	 * 
	 * @return whether processing input was successful
	 */
	public boolean stopCellEditing() {
		autoCompleteTextField.removeDummyCursor();

		// try to redefine or create the cell geo with the current editing
		// string
		if (!processGeo()) {
			return false;
		}

		editing = false;
		// TODO return super.stopCellEditing();

		return true;
	}

	void stopCellEditing(int colOff, int rowOff, boolean editNext) {
		stopCellEditingAndProcess();
		moveSelectedCell(colOff, rowOff);
		table.finishEditing(editNext); // don't finish, we
		if (editNext) {
			table.setAllowEditing(true);
			table.editCellAt(row + rowOff, column + colOff);
			table.setAllowEditing(false);
			// this should be deferred so that browser cannot steal focus from
			// SS
			autoCompleteTextField.getTextField().setFocus(true);
		}
	}

	void stopCellEditingAndProcess() {
		allowProcessGeo = true;
		stopCellEditing();
		allowProcessGeo = false;
	}

	private void moveSelectedCell(int colOff, int rowOff) {
		int nextRow = Math.min(row + rowOff, table.getRowCount());
		int nextColumn = Math.min(column + colOff, table.getColumnCount());
		table.setSelection(nextColumn, nextRow);
	}

	/**
	 * Attempts to create or redefine the cell geo using the current editing
	 * string
	 * 
	 * @return success
	 */
	private boolean processGeo() {
		try {

			if (allowProcessGeo) {
				String text = autoCompleteTextField.getText();
				// get GeoElement of current cell
				value = kernel.lookupLabel(GeoElementSpreadsheet
				        .getSpreadsheetCellName(column, row));

				if ("".equals(text)) {
					if (value != null) {
						value.removeOrSetUndefinedIfHasFixedDescendent();
						value = null;
					}

				} else {
					GeoElementND newVal = new RelativeCopy(kernel)
					        .prepareAddingValueToTableNoStoringUndoInfo(text,
									value, column, row, false);
					if (newVal == null) {
						return false;
					}
					value = newVal;
				}

				if (value != null) {
					app.storeUndoInfo();
				}
			}

		} catch (Exception ex) {
			// show GeoGebra error dialog
			// kernel.getApplication().showError(ex.getMessage());
			Log.debug(ex);
			// TODO super.stopCellEditing();
			editing = false;
			return false;
		}
		return true;
	}

	// =======================================================
	// Key and Focus Listeners
	// =======================================================

	/**
	 * Emulate key press
	 * 
	 * @param e
	 *            key press event
	 */
	public void sendKeyPressEvent(KeyPressEvent e) {
		autoCompleteTextField.getTextField().setFocus(true);
		keyListener.onKeyPress(e);
	}

	/**
	 * Emulate key down
	 * 
	 * @param e
	 *            key down event
	 */
	public void sendKeyDownEvent(KeyDownEvent e) {
		autoCompleteTextField.getTextField().setFocus(true);
		keyListener.onKeyDown(e);
	}

	public class SpreadsheetCellEditorKeyListener implements KeyEventsHandler {

		// boolean escape = false;
		boolean isFormulaBarListener;

		public SpreadsheetCellEditorKeyListener(boolean isFormulaBarListener) {
			this.isFormulaBarListener = isFormulaBarListener;
		}

		@Override
		public void onKeyDown(KeyDownEvent e) {

			// stopping propagation is needed to prevent duplicate events
			//e.stopPropagation();

			checkCursorKeys(e);
			int keyCode = e.getNativeKeyCode();

			if (GlobalKeyDispatcherW.isLeftAltDown()) {
				e.preventDefault();
			}

			if (keyCode == KeyCodes.KEY_ESCAPE) {
				e.preventDefault();
				e.stopPropagation();
				GeoElement oldGeo = kernel.getGeoAt(column, row);
				cancelCellEditing();

				// restore old text in spreadsheet
				table.getModel().setValueAt(oldGeo, row, column);

				// stopCellEditing(0,0);
				// force nice redraw
				table.setSelection(column, row);

				// update the formula bar after escape
				// ?//table.getView().updateFormulaBar();
			}
			autoCompleteTextField.onKeyDown(e);
			e.stopPropagation();
		}

		@Override
		public void onKeyPress(KeyPressEvent e) {
			// iOS: we do receive the event but nothing is actually printed
			// because focus moved from dummy textarea into editor
			if (MathFieldW.checkCode(e.getNativeEvent(), "NumpadDecimal")) {
				autoCompleteTextField.insertString(".");
				e.preventDefault();
				e.stopPropagation();
				return;
			}
			final String charcode = e.getCharCode() + "";
			if (MyCellEditorW.this.allowAutoEdit) {
				app.invokeLater(() -> {
					String text = autoCompleteTextField.getText();
					if (text == null || text.length() == 0) {
						autoCompleteTextField.setText(charcode);
					}
				});
				MyCellEditorW.this.allowAutoEdit = false;
			}
			
			// stopping propagation is needed to prevent
			// the prevention of the default action at another place
			// but call the autocomplete textfield handler explicitly
			autoCompleteTextField.onKeyPress(e);
			e.stopPropagation();
		}

		@Override
		public void onKeyUp(KeyUpEvent e) {
			// stopping propagation may be needed in strange browsers
			// this also makes sure no top-level action is done on keyUp
			// but the default action of the event should have already been
			// expired
			autoCompleteTextField.onKeyUp(e);
			GlobalKeyDispatcherW.setDownKeys(e);
			e.stopPropagation();
		}

		/**
		 * Handle arrow keys.
		 * 
		 * @param e
		 *            key down event
		 */
		public void checkCursorKeys(KeyDownEvent e) {
			String text = autoCompleteTextField.getText();

			int keyCode = e.getNativeKeyCode();
			switch (keyCode) {

			case KeyCodes.KEY_UP:
			
				if (isSuggesting()) {
					return;
				}
				if (isFormulaBarListener) {
					return;
				}
				stopCellEditing(0, -1, false);
				// ?//e.consume();
				setTabReturnCol(-1);
				break;

			case KeyCodes.KEY_TAB:
				if (isFormulaBarListener) {
					return;
				}
				Log.debug(" tab");
				// shift-tab moves left
				// tab moves right
				if (getTabReturnCol() == -1) {
					setTabReturnCol(column);
				}
				stopCellEditing(e.isShiftKeyDown() ? -1 : 1, 0, false);
				e.preventDefault();
				break;

			case KeyCodes.KEY_ENTER:
				if (isSuggesting()) {
					return;
				}
				
				// if incomplete command entered, want to move the cursor to
				// between []
				int bracketsIndex = text.indexOf("[]");
				if (bracketsIndex == -1) {

					if (getTabReturnCol() != -1) {
						int colOffset = getTabReturnCol() - column;
						stopCellEditing(colOffset, 1, true);
					} else {

						// TODO: in desktop this works with column, row + 1
						String cellBelowStr = GeoElementSpreadsheet
						        .getSpreadsheetCellName(column, row + 1);
						GeoElement cellBelow = kernel.getConstruction()
						        .lookupLabel(cellBelowStr);

						boolean moveDown = cellBelow == null
								|| !cellBelow.isProtected(EventType.UPDATE);

						// don't move down to cell below after <Enter> if it's
						// fixed
						stopCellEditing(0, moveDown ? 1 : 0, moveDown);

					}
				} else {
					autoCompleteTextField.setCaretPosition(bracketsIndex + 1);
					// ?//e.consume();
				}

				setTabReturnCol(-1);
				break;

			case KeyCodes.KEY_DOWN:
				if (isSuggesting()) {
					return;
				}

				if (isFormulaBarListener) {
					// ?//e.consume();
					return;
				}
				stopCellEditing(0, 1, false);
				setTabReturnCol(-1);
				break;

			case KeyCodes.KEY_LEFT:
				if (isFormulaBarListener) {
					return;
				}
				// Allow left/right keys to exit cell for easier data entry
				if (getCaretPosition() == 0) {
					stopCellEditing(-1, 0, false);
				}
				setTabReturnCol(-1);
				break;

			case KeyCodes.KEY_RIGHT:
				if (isFormulaBarListener) {
					return;
				}
				// Allow left/right keys to exit cell for easier data entry
				if (getCaretPosition() == text.length()) {
					stopCellEditing(1, 0, false);
				}

				setTabReturnCol(-1);
				break;

			case KeyCodes.KEY_PAGEDOWN:
			case KeyCodes.KEY_PAGEUP:
				e.preventDefault();
				// ?//e.consume();
				setTabReturnCol(-1);
				break;

			default:
				if (GlobalKeyDispatcherW.isLeftAltDown()) {
					if (AltKeys.isGeoGebraShortcut(
							e.getNativeKeyCode(), e.isShiftKeyDown(), true)) {
						e.preventDefault();
					}
				}
				break;

			// An F1 keypress causes the focus to be lost, so we
			// need to set 'editing' to false to prevent the focusLost()
			// method from calling stopCellEditing()
			// ?//case KeyEvent.VK_F1:
			// ?// editing = false;
			// ?// break;

			}

		}

	}

	public AutoCompleteTextFieldW getTextfield() {
		return autoCompleteTextField;
	}
	
	boolean isSuggesting() {
		return autoCompleteTextField.isSuggesting();
	}

	public void setAllowAutoEdit() {
		this.allowAutoEdit = true;
	}

	protected int getTabReturnCol() {
		return tabReturnCol;
	}

	protected void setTabReturnCol(int tabReturnCol) {
		this.tabReturnCol = tabReturnCol;
	}

	/**
	 * Hanldle Enter key.
	 */
	public void onEnter() {
		if (tabReturnCol > -1) {
			table.changeSelection(row, tabReturnCol, false);
			setTabReturnCol(-1);
		}
	}

	/**
	 * Selects all the text in editor.
	 */
	public void selectAll() {
		autoCompleteTextField.getTextField().setFocus(true);
		autoCompleteTextField.selectAll();
	}
}
