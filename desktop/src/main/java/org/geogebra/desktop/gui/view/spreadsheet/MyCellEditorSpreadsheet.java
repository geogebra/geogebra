package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetController;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.inputfield.KeyNavigation;
import org.geogebra.desktop.gui.virtualkeyboard.VirtualKeyboardD;
import org.geogebra.desktop.main.AppD;

/**
 * Default cell editor for the spreadsheet, extends
 * DefaultCellEditor(JTextField)
 * 
 */
public class MyCellEditorSpreadsheet extends DefaultCellEditor implements FocusListener {

	private static final long serialVersionUID = 1L;

	protected Kernel kernel;
	protected AppD app;
	protected GeoElementND value;
	protected MyTableD table;
	AutoCompleteTextFieldD textField;

	protected int column;
	protected int row;
	boolean editing = false;
	private boolean errorOnStopEditing = false;

	private boolean allowProcessGeo = false;

	public boolean allowProcessGeo() {
		return allowProcessGeo;
	}

	public void setAllowProcessGeo(boolean allowProcessGeo) {
		this.allowProcessGeo = allowProcessGeo;
	}

	private boolean enableAutoComplete = false;

	private SpreadsheetController controller;

	public boolean isEnableAutoComplete() {
		return enableAutoComplete;
	}

	public void setEnableAutoComplete(boolean enableAutoComplete) {
		this.enableAutoComplete = enableAutoComplete;
		textField.setAutoComplete(enableAutoComplete);
	}

	public MyCellEditorSpreadsheet(Kernel kernel, SpreadsheetController controller) {

		super(new AutoCompleteTextFieldD(0, (AppD) kernel.getApplication(),
				KeyNavigation.IGNORE));
		this.kernel = kernel;
		this.controller = controller;
		app = (AppD) kernel.getApplication();
		textField = (AutoCompleteTextFieldD) editorComponent;
		textField.setAutoComplete(enableAutoComplete);

		editorComponent.addKeyListener(new SpreadsheetCellEditorKeyListener(
				false));
		editorComponent.addFocusListener(this);

		DocumentListener documentListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent documentEvent) {
				// do nothing
			}

			public void insertUpdate(DocumentEvent documentEvent) {
				updateFormulaBar(documentEvent);
			}

			public void removeUpdate(DocumentEvent documentEvent) {
				updateFormulaBar(documentEvent);
			}

			private void updateFormulaBar(DocumentEvent documentEvent) {
				if (table.view.getShowFormulaBar()
						&& (textField.hasFocus() || table.isDragging2))
					table.view.getFormulaBar().setEditorText(
							textField.getText());
			}
		};
		textField.getDocument().addDocumentListener(documentListener);

	}

	public void setText(String text) {
		if (!textField.hasFocus() && !table.isDragging2)
			textField.setText(text);

	}

	@Override
	public Component getTableCellEditorComponent(JTable table0, Object value0,
			boolean isSelected, int row0, int column0) {

		if (table0 instanceof MyTableD) {
			table = (MyTableD) table0;
		} else {
			return null;
		}

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
			if ((!value.isGeoText())) {
				if (index == -1) {
					text = "=" + text;
				}
			}
		}
		delegate.setValue(text);

		Component component = getComponent();
		component.setFont(app.getFontCanDisplayAwt(text));

		editing = true;

		return component;
	}

	/**
	 * set flag to require text start with "=" to activate autocomplete
	 */
	public void setEqualsRequired(boolean equalsRequired) {
		textField.setEqualsRequired(equalsRequired);
	}

	/**
	 * returns flag that requires text start with "=" to activate autocomplete
	 */
	public boolean isEqualsRequired() {
		return textField.isEqualsRequired();
	}

	public void setLabels() {
		textField.setDictionary(true);
	}

	/**
	 * 
	 * @return true if the completion popup is open
	 */
	public boolean completionsPopupOpen() {
		return textField.getCompletions() != null;
	}

	// =======================================================
	// In-cell Editing Methods
	// =======================================================



	public boolean isEditing() {
		return editing;
	}

	public int getCaretPosition() {
		return textField.getCaretPosition();
	}

	/** Insert a geo label into current editor string. */
	public void addLabel(String label) {
		if (!editing)
			return;
		// String text = (String) delegate.getCellEditorValue();
		// delegate.setValue(text + label);
		textField.replaceSelection(" " + label + " ");
	}

	public void setLabel(String text) {
		if (!editing)
			return;
		delegate.setValue(text);
	}

	public String getEditingValue() {
		return (String) delegate.getCellEditorValue();
	}

	@Override
	public Object getCellEditorValue() {
		return value;
	}

	// =======================================================
	// Stop/Cancel Editing
	// =======================================================

	@Override
	public void cancelCellEditing() {
		editing = false;
		errorOnStopEditing = false;

		super.cancelCellEditing();

		// give the table the focus in case the formula bar is the editor
		if (table.getView().getFormulaBar().editorHasFocus()) {
			// Application.debug("give focus to table");
			table.requestFocus();
		}
	}

	@Override
	public boolean stopCellEditing() {

		errorOnStopEditing = true; // flag to handle column resizing during
									// editing (see focusLost method)

		// try to redefine or create the cell geo with the current editing
		// string
		if (!processGeo())
			return false;

		errorOnStopEditing = false;
		editing = false;
		boolean success = super.stopCellEditing();

		// give the table the focus in case the formula bar is the editor
		if (table.getView().getFormulaBar().editorHasFocus()) {
			// Application.debug("give focus to table");
			table.requestFocus();
		}
		return success;
	}

	boolean stopCellEditing(int colOff, int rowOff) {
		allowProcessGeo = true;
		boolean success = stopCellEditing();
		moveSelectedCell(colOff, rowOff);
		allowProcessGeo = false;
		return success;
	}

	private void moveSelectedCell(int colOff, int rowOff) {
		int nextRow = Math.min(row + rowOff, table.getRowCount() - 1);
		int nextColumn = Math.min(column + colOff, table.getColumnCount() - 1);
		table.setSelection(nextColumn, nextRow);
	}

	/**
	 * Attempts to create or redefine the cell geo using the current editing
	 * string
	 * 
	 * @return
	 */
	private boolean processGeo() {

		try {

			if (allowProcessGeo) {
				String text = (String) delegate.getCellEditorValue();
				// get GeoElement of current cell
				value = kernel.lookupLabel(GeoElementSpreadsheet
						.getSpreadsheetCellName(column, row));

				if (text.equals("")) {
					if (value != null) {
						value.removeOrSetUndefinedIfHasFixedDescendent();
						value = null;
					}

				} else {
					GeoElementND newVal = RelativeCopy
							.prepareAddingValueToTableNoStoringUndoInfo(kernel,
									app, text, value, column, row, false);
					if (newVal == null) {
						return false;
					}
					value = newVal;
				}

				if (value != null)
					app.storeUndoInfo();
			}

		} catch (Exception ex) {
			// show GeoGebra error dialog
			// kernel.getApplication().showError(ex.getMessage());
			ex.printStackTrace();
			super.stopCellEditing();
			editing = false;
			return false;
		}
		return true;
	}

	// =======================================================
	// Key and Focus Listeners
	// =======================================================

	/**
	 * keep track of when <tab> was first pressed so we can return to that
	 * column when <enter> pressed
	 */
	public int tabReturnCol = -1;

	public class SpreadsheetCellEditorKeyListener implements KeyListener {

		// boolean escape = false;
		boolean isFormulaBarListener;

		public SpreadsheetCellEditorKeyListener(boolean isFormulaBarListener) {
			this.isFormulaBarListener = isFormulaBarListener;
		}

		public void keyTyped(KeyEvent e) {
			//
		}

		public void keyPressed(KeyEvent e) {
			checkCursorKeys(e);
			int keyCode = e.getKeyCode();

			if (keyCode == KeyEvent.VK_ESCAPE) {
				GeoElement oldGeo = kernel.getGeoAt(column, row);
				cancelCellEditing();

				// restore old text in spreadsheet
				table.getModel().setValueAt(oldGeo, row, column);

				// stopCellEditing(0,0);
				// force nice redraw
				table.setSelection(column, row);

				// update the formula bar after escape
				table.getView().updateFormulaBar();



			}
		}

		public void keyReleased(KeyEvent e) {
			//
		}

		public void checkCursorKeys(KeyEvent e) {

			String text = (String) delegate.getCellEditorValue();

			int keyCode = e.getKeyCode();
			// Application.debug(e+"");
			switch (keyCode) {
			default:
				// do nothing
				break;
			case KeyEvent.VK_UP:
				if (isFormulaBarListener)
					return;

				// Application.debug("UP");
				stopCellEditing(0, -1);
				editing = false;
				e.consume();
				tabReturnCol = -1;
				break;

			case KeyEvent.VK_TAB:
				if (isFormulaBarListener)
					return;
				Log.debug(" tab");
				// Application.debug("RIGHT");
				// shift-tab moves left
				// tab moves right
				if (tabReturnCol == -1)
					tabReturnCol = column;
				stopCellEditing(e.isShiftDown() ? -1 : 1, 0);
				editing = false;

				break;

			case KeyEvent.VK_ENTER:
				// if incomplete command entered, want to move the cursor to
				// between []
				int bracketsIndex = text.indexOf("[]");
				if (bracketsIndex == -1) {

					if (tabReturnCol != -1) {
						int colOffset = tabReturnCol - column;
						stopCellEditing(colOffset, 1);
						editing = false;
					} else {

						String cellBelowStr = GeoElementSpreadsheet
								.getSpreadsheetCellName(column, row + 1);
						GeoElement cellBelow = kernel.getConstruction()
								.lookupLabel(cellBelowStr);

						boolean moveDown = cellBelow == null
								|| !cellBelow.isFixed();

						// don't move down to cell below after <Enter> if it's
						// fixed
						stopCellEditing(0, moveDown ? 1 : 0);

					}
				} else {
					textField.setCaretPosition(bracketsIndex + 1);
					e.consume();
				}

				tabReturnCol = -1;
				break;

			case KeyEvent.VK_DOWN:
				if (isFormulaBarListener) {
					e.consume();
					return;
				}
				// Application.debug("DOWN");
				stopCellEditing(0, 1);
				editing = false;
				tabReturnCol = -1;
				break;

			case KeyEvent.VK_LEFT:
				if (isFormulaBarListener)
					return;
				// Application.debug("LEFT");
				// Allow left/right keys to exit cell for easier data entry
				if (getCaretPosition() == 0) {
					stopCellEditing(-1, 0);
					editing = false;
				}
				editing = false;
				tabReturnCol = -1;
				break;

			case KeyEvent.VK_RIGHT:
				if (isFormulaBarListener)
					return;
				// Application.debug("RIGHT");
				// Allow left/right keys to exit cell for easier data entry
				if (getCaretPosition() == text.length()) {
					stopCellEditing(1, 0);
					editing = false;
				}

				editing = false;
				tabReturnCol = -1;
				break;

			case KeyEvent.VK_PAGE_DOWN:
			case KeyEvent.VK_PAGE_UP:
				e.consume();
				tabReturnCol = -1;
				break;

			// An F1 keypress causes the focus to be lost, so we
			// need to set 'editing' to false to prevent the focusLost()
			// method from calling stopCellEditing()
			case KeyEvent.VK_F1:
				editing = false;
				break;

			}

		}

	}

	public void focusGained(FocusEvent arg0) {
		editing = true;
	}

	public void focusLost(FocusEvent arg0) {

		// VirtualKeyboard gets the focus very briefly when opened
		// so ignore this!
		if (arg0.getOppositeComponent() instanceof VirtualKeyboardD)
			return;

		// only needed if eg columns resized
		if (editing == true) {
			if (!errorOnStopEditing) {

				// Process the current edit, exit the editor and update the
				// formula bar
				setAllowProcessGeo(true);
				stopCellEditing();
				setAllowProcessGeo(false);
				table.getView().getFormulaBar().update();

			} else if (!app.isErrorDialogShowing()) {
				cancelCellEditing();
			}
		}
	}

}
