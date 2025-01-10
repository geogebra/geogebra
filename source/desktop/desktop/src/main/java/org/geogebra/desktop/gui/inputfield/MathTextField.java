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
