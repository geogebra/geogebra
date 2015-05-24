package org.geogebra.desktop.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;

import org.geogebra.common.util.Unicode;
import org.geogebra.desktop.gui.inputfield.MyTextField;
import org.geogebra.desktop.main.AppD;

/*
 * Michael Borcherds
 * 
 * Extends JTextField
 * Alt-o inserts a degree sign at the end (only one allowed)
 * Alt-p inserts pi at the end (only one allowed)
 * Ctrl-o Ctrl-p on Mac OSX
 */

public class AngleTextField extends MyTextField implements KeyListener {

	private static final long serialVersionUID = 1L;

	public AngleTextField(int columns, AppD app) {
		super(app, columns);
		this.addKeyListener(this);
	}

	public void keyPressed(KeyEvent e) {
		// do nothing
	}

	public void keyTyped(KeyEvent e) {
		// do nothing
	}

	public void keyReleased(KeyEvent e) {

		boolean modifierKeyPressed = AppD.MAC_OS ? e.isControlDown() : e
				.isAltDown();

		// we don't want to act when AltGr is down
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown()) {
			modifierKeyPressed = false;
		}

		// Application.debug(e+"");

		String insertString = "";

		switch (KeyEvent.getKeyText(e.getKeyCode()).toLowerCase(Locale.US)
				.charAt(0)) {
		case 'o':
			insertString = Unicode.degree;
			break;
		case 'p':
			insertString = Unicode.PI_STRING;
			break;
		}

		if (modifierKeyPressed && !insertString.equals("")) {
			int start = getSelectionStart();
			int end = getSelectionEnd();
			// clear selection if there is one
			if (start != end) {
				int pos = getCaretPosition();
				String oldText = getText();
				StringBuilder sb = new StringBuilder();
				sb.append(oldText.substring(0, start));
				sb.append(oldText.substring(end));
				setText(sb.toString());
				if (pos < sb.length())
					setCaretPosition(pos);
			}

			String oldText = getText();

			// don't insert more than one degree sign or pi *in total*
			if (oldText.indexOf(Unicode.degreeChar) == -1
					&& oldText.indexOf(Unicode.pi) == -1) {
				int pos = oldText.length(); // getCaretPosition();
				StringBuilder sb = new StringBuilder();
				sb.append(oldText.substring(0, pos));
				sb.append(insertString);
				sb.append(oldText.substring(pos));
				setText(sb.toString());
				setCaretPosition(pos + insertString.length());
			}

			e.consume();
		}
	}

}
