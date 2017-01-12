package org.geogebra.desktop.gui.inputfield;

import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.desktop.gui.util.SelectionTableD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * Prepares and shows a JPopupMenu containing a symbol table for MyTextField.
 * 
 * @author G. Sturr
 *
 */
public class SymbolTablePopupD {

	private MyTextFieldD textField;
	private JPopupMenu popup;

	private KeyListener keyListener;
	private KeyListener[] textFieldKeyListeners;

	private SelectionTableD symbolTable;
	private AppD app;
	private Locale locale;
	private boolean openUpwards = true;

	/******************************************************
	 * Constructs a symbol table popup.
	 * 
	 * @param app
	 * @param textField
	 */
	public SymbolTablePopupD(AppD app, MyTextFieldD textField) {

		this.app = app;
		this.textField = textField;

		popup = new JPopupMenu();
		popup.setFocusable(false);
		popup.setBorder(
				BorderFactory.createLineBorder(SystemColor.controlShadow));

		// created in setLabels(), not needed here
		// createSymbolTable();
		registerListeners();
		locale = app.getLocale();

		app.setComponentOrientation(popup);
	}

	private void createSymbolTable() {

		LocalizationD loc = app.getLocalization();

		String[][] map = TableSymbols.basicSymbolsMap(loc);

		symbolTable = new SelectionTableD(app,
				TableSymbols.basicSymbols(loc, map), -1, 10,
				new Dimension(24, 24), SelectionTable.MODE_TEXT);

		symbolTable.setShowGrid(true);
		symbolTable.setHorizontalAlignment(SwingConstants.CENTER);
		symbolTable.setSelectedIndex(1);
		symbolTable.setFocusable(false);
		symbolTable
				.setToolTipArray(TableSymbols.basicSymbolsToolTips(loc, map));

		symbolTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleMouseClick(e);
			}
		});

		popup.removeAll();
		popup.add(symbolTable);
	}

	public void setLabels() {
		createSymbolTable();
	}

	private class PopupListener implements PopupMenuListener {

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

			// remove text field key listeners and replace with our own;
			textFieldKeyListeners = textField.getKeyListeners();
			for (KeyListener listener : textFieldKeyListeners) {
				textField.removeKeyListener(listener);
			}
			textField.addKeyListener(keyListener);
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			// return old key listeners to the text field
			textField.removeKeyListener(keyListener);
			for (KeyListener listener : textFieldKeyListeners) {
				textField.addKeyListener(listener);
			}
		}

	}

	private void registerListeners() {

		// create key listener (will be added by PopupListener)
		keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				handleSpecialKeys(e);
			}
		};

		popup.addPopupMenuListener(new PopupListener());
	}

	// =======================================================
	// Handle popup visiblilty
	// =======================================================

	/**
	 * Gets the pixel location of the caret. Used to locate the popup.
	 */
	private Point getCaretPixelPosition() {
		int position = textField.getCaretPosition();
		Rectangle r;
		try {
			r = textField.modelToView(position);
		} catch (BadLocationException e) {
			return null;
		}
		return new Point(r.x, r.y - popup.getPreferredSize().height - 10);
	}

	public void showPopup(boolean locateAtFieldEnd) {

		getSymbolTable().updateFonts();
		if (locale != app.getLocale()) {
			locale = app.getLocale();
			setLabels();
		}

		if (locateAtFieldEnd) {
			Dimension d = popup.getPreferredSize();
			if (openUpwards) {
				popup.show(textField,
						textField.getX() + textField.getWidth() - d.width,
						-d.height);
			} else {
				popup.show(textField,
						textField.getX() + textField.getWidth() - d.width,
						textField.getY() + textField.getHeight());
			}
		} else {
			if (openUpwards) {
				popup.show(textField, getCaretPixelPosition().x,
						getCaretPixelPosition().y);
			} else {
				popup.show(textField, getCaretPixelPosition().x,
						textField.getY() + textField.getHeight());
			}
		}
	}

	private SelectionTableD getSymbolTable() {
		if (symbolTable == null) {
			createSymbolTable();
		}
		return symbolTable;
	}

	private boolean isPopupVisible() {
		return popup.isVisible();
	}

	private void hidePopup() {
		if (!isPopupVisible()) {
			return;
		}
		popup.setVisible(false);
	}

	/**
	 * @param openUpwards
	 *            true => popup opens sitting above the textfield, false =>
	 *            popup opens below
	 */
	public void setOpenUpwards(boolean openUpwards) {
		this.openUpwards = openUpwards;
	}

	// =======================================================
	// Handle key and mouse events
	// =======================================================

	public void handleMouseClick(MouseEvent e) {
		handlePopupSelection();
		hidePopup();
	}

	public void handlePopupSelection() {
		if (symbolTable.getSelectedValue() != null) {
			textField.insertString((String) symbolTable.getSelectedValue());
		}
	}

	public void handleSpecialKeys(KeyEvent keyEvent) {
		if (!isPopupVisible()) {
			return;
		}

		int keyCode = keyEvent.getKeyCode();

		switch (keyCode) {
		case VK_ESCAPE: // [ESC] cancel the popup and undo any changes
			hidePopup();
			keyEvent.consume();
			break;

		case VK_ENTER:
			handlePopupSelection();
			hidePopup();
			keyEvent.consume();
			break;

		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:

			int row = symbolTable.getSelectedRow();
			int column = symbolTable.getSelectedColumn();
			if (keyCode == KeyEvent.VK_RIGHT
					&& column != symbolTable.getColumnCount() - 1) {
				++column;
			}
			if (keyCode == KeyEvent.VK_LEFT && column >= 0) {
				--column;
			}
			if (keyCode == KeyEvent.VK_DOWN
					&& row != symbolTable.getRowCount() - 1) {
				++row;
			}
			if (keyCode == KeyEvent.VK_UP && row >= 0) {
				--row;
			}

			symbolTable.changeSelection(row, column, false, false);
			keyEvent.consume();
			break;

		default:
			hidePopup();
		}
	}

}
