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

package org.geogebra.desktop.gui.virtualkeyboard;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

//
/**
 * adapted from
 * http://stackoverflow.com/questions/1248510/convert-string-to-keyevents
 */
public class WindowsUnicodeKeyboard extends Keyboard {

	private final Robot windowsRobot;

	/**
	 * @throws AWTException
	 *             when super fails
	 */
	public WindowsUnicodeKeyboard() throws AWTException {
		super();
		this.windowsRobot = super.robot;
	}

	@Override
	public void type(char character) {
		try {
			super.type(character);
		} catch (IllegalArgumentException e) {

			int unicodeDigits = character;
			windowsRobot.keyPress(KeyEvent.VK_ALT);

			try { // make sure Alt is released!

				// convert to decimal (with leading zero(es))
				int digit = unicodeDigits / 1000;
				unicodeDigits -= digit * 1000;
				typeNumPad(digit);
				digit = unicodeDigits / 100;
				unicodeDigits -= digit * 100;
				typeNumPad(digit);
				digit = unicodeDigits / 10;
				unicodeDigits -= digit * 10;
				typeNumPad(digit);
				typeNumPad(unicodeDigits);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			windowsRobot.keyRelease(KeyEvent.VK_ALT);

		}
	}

	private void typeNumPad(int digit) {
		switch (digit) {
		default:
			// do nothing
			break;
		case 0:
			doType(KeyEvent.VK_NUMPAD0);
			break;
		case 1:
			doType(KeyEvent.VK_NUMPAD1);
			break;
		case 2:
			doType(KeyEvent.VK_NUMPAD2);
			break;
		case 3:
			doType(KeyEvent.VK_NUMPAD3);
			break;
		case 4:
			doType(KeyEvent.VK_NUMPAD4);
			break;
		case 5:
			doType(KeyEvent.VK_NUMPAD5);
			break;
		case 6:
			doType(KeyEvent.VK_NUMPAD6);
			break;
		case 7:
			doType(KeyEvent.VK_NUMPAD7);
			break;
		case 8:
			doType(KeyEvent.VK_NUMPAD8);
			break;
		case 9:
			doType(KeyEvent.VK_NUMPAD9);
			break;
		}
	}

}
// */
