package org.geogebra.desktop.gui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;

import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.desktop.gui.dialog.TextInputDialog;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.gui.view.spreadsheet.MyTableD;
import org.geogebra.desktop.main.AppD;

public class LatexTable extends SelectionTableD implements MenuElement {

	private static final long serialVersionUID = 1L;

	private TextInputDialog inputDialog;
	private String[] latexArray;
	private PopupMenuButton popupButton;
	private int caretPosition = 0;
	private org.geogebra.common.gui.util.SelectionTable mode;

	public LatexTable(AppD app, TextInputDialog textInputDialog,
			PopupMenuButton popupButton, String[] latexArray, int rows,
			int columns, org.geogebra.common.gui.util.SelectionTable mode) {

		super(app, latexArray, rows, columns, new Dimension(24, 24), mode);
		this.inputDialog = textInputDialog;
		this.latexArray = latexArray;
		this.popupButton = popupButton;
		this.mode = mode;
		// setShowGrid(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setSelectedIndex(0);
		// this.setUseColorSwatchBorder(true);
		this.setShowGrid(true);
		this.setGridColor(org.geogebra.desktop.awt.GColorD
				.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR));
		this.setBorder(BorderFactory
				.createLineBorder(MyTableD.TABLE_GRID_COLOR));
		// this.setBorder(BorderFactory.createEmptyBorder());
		this.setShowSelection(false);
	}

	public void setCaretPosition(int caretPosition) {
		this.caretPosition = caretPosition;
	}

	// support for MenuElement interface

	public Component getComponent() {
		return this;
	}

	public MenuElement[] getSubElements() {
		return new MenuElement[0];
	}

	public void menuSelectionChanged(boolean arg0) {
	}

	public void processKeyEvent(KeyEvent arg0, MenuElement[] arg1,
			MenuSelectionManager arg2) {
	}

	public void processMouseEvent(MouseEvent arg0, MenuElement[] arg1,
			MenuSelectionManager arg2) {

		if (this.getSelectedIndex() >= latexArray.length)
			return;

		if (arg0.getID() == MouseEvent.MOUSE_RELEASED) {

			// get the selected string
			StringBuffer sb = new StringBuffer(
					latexArray[this.getSelectedIndex()]);
			// if LaTeX string, adjust the string to include selected text
			// within braces
			if (mode == org.geogebra.common.gui.util.SelectionTable.MODE_LATEX) {

				String selText = ((InputPanelD) inputDialog.getInputPanel())
						.getSelectedText();
				if (selText != null) {
					sb.deleteCharAt(sb.indexOf("{") + 1);
					sb.insert(sb.indexOf("{") + 1, selText);
				}
			}

			// now insert the string
			inputDialog.insertString(sb.toString(), inputDialog.isLaTeX());

			// if unicode string, add the string to the recent symbol list
			if (mode == org.geogebra.common.gui.util.SelectionTable.MODE_TEXT) {
				inputDialog.addRecentSymbol(sb.toString());
			}

			// notify the popup button
			popupButton.handlePopupActionEvent();
		}
	}

}
