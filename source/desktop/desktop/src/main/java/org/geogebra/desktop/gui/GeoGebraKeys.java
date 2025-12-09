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

package org.geogebra.desktop.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.text.JTextComponent;

import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;
import org.geogebra.editor.share.util.AltKeys;
import org.geogebra.editor.share.util.Unicode;

/*
 * Michael Borcherds
 * 
 * Implements KeyListener
 * adds support for alt-codes (and alt-shift-) for special characters
 * (ctrl on MacOS)
 */

public class GeoGebraKeys implements KeyListener {

	private static StringBuilder altCodes = new StringBuilder();

	private boolean altPressed;

	@Override
	public void keyPressed(KeyEvent e) {
		// swallow eg ctrl-a ctrl-b ctrl-p on Mac
		if (AppD.MAC_OS && e.isControlDown()) {
			e.consume();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (AppD.isAltDown(e)) {
			if (!altPressed) {
				altCodes.setLength(0);
			}
			altPressed = true;
		} else {
			if (altCodes.length() > 0) {
				// intercept wrong character and replace with correct Alt-code
				char insertStr = (char) Integer.parseInt(altCodes.toString());
				JTextComponent comp = (JTextComponent) e.getComponent();
				int pos = comp.getCaretPosition();
				String oldText = comp.getText();
				String sb = oldText.substring(0, pos)
						+ insertStr
						+ oldText.substring(pos);
				comp.setText(sb);

				comp.setCaretPosition(pos + 1);
				e.consume();
			}

			altPressed = false;
			altCodes.setLength(0);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

		// when decimal comma typed on numeric keypad on eg German keyboard,
		// replace with .

		if ((e.getKeyCode() == KeyEvent.VK_SEPARATOR || e.getKeyChar() == ',')
				&& e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
			Log.warn("replacing decimal , with decimal .");
			JTextComponent comp = (JTextComponent) e.getComponent();
			int pos = comp.getCaretPosition();
			String oldText = comp.getText();
			StringBuilder sb = new StringBuilder();

			// pos - 1 to remove ","
			sb.append(oldText.substring(0, pos - 1));

			sb.append(".");
			sb.append(oldText.substring(pos));
			comp.setText(sb.toString());

			comp.setCaretPosition(pos);
			e.consume();

		}

		// ctrl pressed on Mac
		// or alt on Windows
		boolean modifierKeyPressed = AppD.isAltDown(e);

		if (modifierKeyPressed) {

			String insertStr = "";

			// works nicely for alt or ctrl pressed (Windows/Mac)
			String keyString = StringUtil
					.toLowerCaseUS(KeyEvent.getKeyText(e.getKeyCode()));

			// support for alt codes
			if (e.isAltDown()
					&& e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
				char c = e.getKeyChar();

				// make sure it's not eg alt-*
				if (c >= '0' && c <= '9') {
					altCodes.append(e.getKeyChar());
				}
			}

			boolean numpad = e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD;

			// Numeric keypad numbers eg NumPad-8, NumPad *
			if (!e.isAltDown() && numpad) {
				keyString = e.getKeyChar() + "";
			}

			Log.debug("Key pressed " + StringUtil.toHexString(e.getKeyChar())
					+ " " + keyString);

			// workaround for different Java versions!!
			if ("minus".equals(keyString)) {
				keyString = "-";
			} else if ("plus".equals(keyString)) {
				keyString = "+";
			} else if ("comma".equals(keyString)) {
				keyString = ",";
			} else if ("period".equals(keyString)) {
				keyString = ".";
			} else if ("equals".equals(keyString)) {
				keyString = "=";
			} else if (keyString.length() > 1) {
				Log.debug("Unknown keyString: " + keyString);
			}

			switch (e.getKeyChar()) {
			default:
				// do nothing
				break;
			// workaround for shifted characters:
			// (different in different locales)
			case '+':
			case '*':
			case '=':
			case '-':
			case '>':
			case '<':
				// Italian keyboard, keyString="unknown keycode: 0x0" for these
				// two
				// French keyboard, keyString= eg "2" (so we need to leave it
				// for Alt-2 to work)
			case Unicode.e_GRAVE:
			case Unicode.e_ACUTE:
				if (keyString.length() > 1) {
					keyString = e.getKeyChar() + "";
				}
			}

			// don't want to act on eg "Shift"
			if (keyString.length() == 1) {

				insertStr = AltKeys.getAltSymbols(
						Character.toUpperCase(keyString.charAt(0)),
						e.isShiftDown(), false);

				if (insertStr == null) {
					insertStr = "";
				}

			}

			// insert into the text component
			if (!"".equals(insertStr)) {
				JTextComponent comp = (JTextComponent) e.getComponent();
				int pos = comp.getCaretPosition();

				// if we have a DynamicTextInputPane then using setText to
				// insert will destroy any dynamic objects, so use its
				// insertString method instead.
				if (comp instanceof DynamicTextInputPane) {
					((DynamicTextInputPane) comp).insertString(pos, insertStr,
							null);
				} else {
					// all other cases use setText
					String oldText = comp.getText();
					String sb = oldText.substring(0, pos)
							+ insertStr
							+ oldText.substring(pos);
					comp.setText(sb);
					comp.setCaretPosition(pos + insertStr.length());
				}

				e.consume();
			}
		}
	}
}
