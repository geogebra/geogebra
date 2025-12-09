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

package org.geogebra.desktop.gui.inputfield;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.geogebra.desktop.gui.GeoGebraKeys;
import org.geogebra.desktop.main.AppD;

/*
 * Michael Borcherds
 * 
 * Extends JTextField
 * adds support for alt-codes (and alt-shift-) for special characters
 * (ctrl on MacOS)
 */

public class MathTextField extends MyTextFieldD implements KeyListener {

	private static final long serialVersionUID = 1L;

	private final GeoGebraKeys ggbKeys;

	/**
	 * @param app application
	 */
	public MathTextField(AppD app) {
		super(app);
		ggbKeys = new GeoGebraKeys();
		addKeyListener(this);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		ggbKeys.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		ggbKeys.keyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		ggbKeys.keyTyped(e);
	}
}
