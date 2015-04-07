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

public class MathTextField extends MyTextField implements KeyListener {

	private static final long serialVersionUID = 1L;

	private GeoGebraKeys ggbKeys;

	public MathTextField(AppD app) {
		super(app);
		ggbKeys = new GeoGebraKeys(app);
		addKeyListener(this);
	}

	public MathTextField(AppD app, int length) {
		super(app, length);
		ggbKeys = new GeoGebraKeys(app);
		addKeyListener(this);
	}

	public void keyPressed(KeyEvent e) {
		ggbKeys.keyPressed(e);
	}

	public void keyReleased(KeyEvent e) {
		ggbKeys.keyReleased(e);
	}

	public void keyTyped(KeyEvent e) {
		ggbKeys.keyTyped(e);
	}
}
