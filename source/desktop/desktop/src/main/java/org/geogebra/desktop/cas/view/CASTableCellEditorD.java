/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.cas.view;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.desktop.main.AppD;

/**
 * Cell editor; handles keystrokes
 */
public class CASTableCellEditorD extends CASTableCell
		implements TableCellEditor, KeyListener, CASTableCellEditor {

	private static final long serialVersionUID = 1L;

	private JTable table;
	private GeoCasCell cellValue;

	private boolean editing = false;
	private int editingRow;

	private String inputOnEditingStart;
	private boolean isUseAsTextOnEditingStart;

	private ArrayList<CellEditorListener> listeners = new ArrayList<>();

	/**
	 * @param view
	 *            CAS view
	 */
	public CASTableCellEditorD(CASViewD view) {
		super(view);

		getInputArea().addKeyListener(this);
	}

	@Override
	public Component getTableCellEditorComponent(JTable casTable, Object value,
			boolean isSelected, int row, int column) {
		if (value instanceof GeoCasCell) {
			editing = true;
			editingRow = row;

			// make sure we keep the substitution list
			// needed for TRAC-5522
			if (cellValue != null) {
				ArrayList<Vector<String>> substList = cellValue.getSubstList();
				if (!substList.isEmpty()) {
					((GeoCasCell) value).setSubstList(cellValue.getSubstList());
				}
			}

			// set CASTableCell value
			this.cellValue = (GeoCasCell) value;
			this.table = casTable;
			inputOnEditingStart = cellValue
					.getLocalizedInput();
			isUseAsTextOnEditingStart = cellValue.isUseAsText();
			setValue(cellValue);

			boolean isUseAsText = cellValue.isUseAsText();
			getInputArea().enableColoring(!isUseAsText);
			getInputArea().setAutoComplete(!isUseAsText);

			// update font and row height
			setFont(view.getCASViewComponent().getFont());
			updateTableRowHeight(casTable, row);

			// Set width of editor to the width of the table column.
			// This will allow scrolling of strings that are wider than the
			// cell.
			setInputPanelWidth(casTable.getParent().getWidth());
		}
		return this;
	}

	/**
	 * @return input text
	 */
	public String getInputText() {
		return getInputArea().getText();
	}

	@Override
	public String getInputSelectedText() {
		return getInputArea().getSelectedText();
	}

	@Override
	public int getInputSelectionStart() {
		return getInputArea().getSelectionStart();
	}

	@Override
	public int getInputSelectionEnd() {
		return getInputArea().getSelectionEnd();
	}

	@Override
	public void setInputSelectionStart(int pos) {
		getInputArea().setSelectionStart(pos);
	}

	@Override
	public void setInputSelectionEnd(int pos) {
		getInputArea().setSelectionEnd(pos);
	}

	/**
	 * @return caret position
	 */
	public int getCaretPosition() {
		return getInputArea().getCaretPosition();
	}

	/**
	 * @param i
	 *            caret position
	 */
	public void setCaretPosition(int i) {
		getInputArea().setCaretPosition(i);
	}

	/**
	 * Replaces selection with given text
	 * 
	 * @param text
	 *            text
	 */
	public void insertText(String text) {
		getInputArea().replaceSelection(text);
		// getInputArea().requestFocusInWindow();
	}

	/**
	 * Clears input area
	 */
	public void clearInputSelectionText() {
		getInputArea().setText(null);
	}

	@Override
	public boolean stopCellEditing() {
		// update cellValue's input using editor content
		if (editing && cellValue != null) {
			String newInput = getInput();
			if (!newInput.equals(inputOnEditingStart)
					|| cellValue.isUseAsText() != isUseAsTextOnEditingStart) {
				cellValue.setInput(getInput());
			}
			fireEditingStopped();
		}

		return true;
	}

	@Override
	public void cancelCellEditing() {
		// update cellValue's input using editor content
		if (editing && cellValue != null) {
			String newInput = getInput();
			if (!newInput.equals(inputOnEditingStart)
					|| cellValue.isUseAsText() != isUseAsTextOnEditingStart) {
				cellValue.setInput(getInput());
			}
			fireEditingCanceled();
		}
	}

	/**
	 * @return whether this cell is being edited
	 */
	public boolean isEditing() {
		return editing; // && hasFocus();
	}

	@Override
	public Object getCellEditorValue() {
		return cellValue;
	}

	/**
	 * Editing canceled
	 */
	protected void fireEditingCanceled() {
		if (editing && editingRow < table.getRowCount()) {
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = 0; i < listeners.size(); i++) {
				CellEditorListener l = listeners.get(i);
				l.editingCanceled(ce);
			}
		}

		editing = false;
	}

	/**
	 * Editing stopped
	 */
	protected void fireEditingStopped() {
		if (editing && editingRow < table.getRowCount()) {
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = 0; i < listeners.size(); i++) {
				CellEditorListener l = listeners.get(i);
				l.editingStopped(ce);
			}
		}

		editing = false;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		listeners.remove(l);
	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	/**
	 * @return index of editing row
	 */
	public final int getEditingRow() {
		return editingRow;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
		default:
			// do nothing
			break;
		case KeyEvent.VK_ESCAPE:
			e.consume();
			getInputArea().setText("");
			break;

		case KeyEvent.VK_V:
			if (AppD.isControlDown(e)) {
				// make sure Ctrl-V isn't passed on to Euclidian View
				getInputArea().paste();
				e.consume();
			}
			break;

		// case KeyEvent.VK_ENTER:
		// e.consume();
		// stopCellEditing();
		// break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// do nothing
	}

	@Override
	public void keyTyped(KeyEvent e) {
		char ch = e.getKeyChar();
		JTextComponent inputArea = getInputArea();
		String text = inputArea.getText();

		// check for special characters, which insert some text
		// from the previous cell
		if (editingRow == 0 || text.length() != 0) {
			return;
		}

		GeoCasCell selCellValue = view.getConsoleTable()
				.getGeoCasCell(editingRow - 1);

		if (!selCellValue.isError()) {
			switch (ch) {
			default:
				// do nothing
				break;
			case ' ':
			case '|':
				// insert output of previous row (not in parentheses)
				inputArea.setText(selCellValue
						.getOutputRHS(StringTemplate.defaultTemplate) + " ");
				e.consume();
				break;
			case ')':
				// insert output of previous row in parentheses
				String prevOutput = selCellValue
						.getOutputRHS(StringTemplate.defaultTemplate);
				inputArea.setText("(" + prevOutput + ")");
				e.consume();
				break;
			}
		}
		// insert input of previous row
		// should work on errors also
		if ('=' == ch) {
			inputArea.setText(
					selCellValue.getLocalizedInput());
			e.consume();
		}
	}

	@Override
	public void clearInputText() {
		clearInputSelectionText();
	}

	@Override
	public void ensureEditing() {
		// only in web
	}

	@Override
	public void onEnter(boolean b) {
		// only used in web
	}

	@Override
	public void setPixelRatio(double ratio) {
		// web only
	}
}
