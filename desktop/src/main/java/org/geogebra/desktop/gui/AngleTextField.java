package org.geogebra.desktop.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.main.AppD;

import com.himamis.retex.editor.share.util.Unicode;

/*
 * Michael Borcherds
 * 
 * Extends JTextField
 * Alt-o inserts a degree sign at the end (only one allowed)
 * Alt-p inserts pi at the end (only one allowed)
 * Ctrl-o Ctrl-p on Mac OSX
 */

public class AngleTextField extends MyTextFieldD implements KeyListener {

	private static final long serialVersionUID = 1L;

	/**
	 * @param columns columns
	 * @param app app
	 */
	public AngleTextField(int columns, AppD app) {
		super(app, columns);
		this.addKeyListener(this);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// do nothing
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// do nothing
	}

	@Override
	public void keyReleased(KeyEvent e) {

		boolean modifierKeyPressed = AppD.MAC_OS ? e.isControlDown()
				: e.isAltDown();

		// we don't want to act when AltGr is down
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown()) {
			modifierKeyPressed = false;
		}

		String insertString = "";

		switch (StringUtil.toLowerCaseUS(KeyEvent.getKeyText(e.getKeyCode()))
				.charAt(0)) {
		default:
			// do nothing
			break;
		case 'o':
			insertString = Unicode.DEGREE_STRING;
			break;
		case 'p':
			insertString = Unicode.PI_STRING;
			break;
		}

		if (modifierKeyPressed && !"".equals(insertString)) {
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
				if (pos < sb.length()) {
					setCaretPosition(pos);
				}
			}

			String oldText = getText();

			// don't insert more than one degree sign or pi *in total*
			if (oldText.indexOf(Unicode.DEGREE_CHAR) == -1
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
