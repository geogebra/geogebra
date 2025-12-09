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

package org.geogebra.desktop.export.pstricks;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * @author loic
 *
 */
public class TextValue extends JTextField implements KeyListener {
	private static final long serialVersionUID = 1L;
	// do we allow negative values in the textfeld?
	private boolean ALLOW_NEGATIVE = false;
	JFrame jf;
	String actionCommand = "";

	TextValue(JFrame jf, String s, boolean b, String actionCommand) {
		super(s, 15);
		this.jf = jf;
		this.ALLOW_NEGATIVE = b;
		addKeyListener(this);
		this.actionCommand = actionCommand;
	}

	/**
	 * @return parsed value
	 * @throws NumberFormatException if value is not a number
	 */
	public double getValue() throws NumberFormatException {
		return Double.parseDouble(getText());
	}

	/**
	 * @param d double value
	 */
	public void setValue(double d) {
		String s = String.valueOf(d);
		setText(s);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Accept only numerical characters
		char c = e.getKeyChar();
		if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE)
				|| (c == KeyEvent.VK_DELETE) || (c == '.'))) {
			if (c != '-' || !ALLOW_NEGATIVE) {
				e.consume();
			} else if (getText().indexOf('-') != -1 || getCaretPosition() != 0) {
				e.consume();
			}
		}

		// if character is '.', check there's no other '.' in the number
		else if (c == '.' && getText().indexOf('.') != -1) {
			e.consume();
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		//
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//
	}

	@Override
	public String toString() {
		return actionCommand;
	}
}
