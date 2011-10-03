package geogebra.gui.inputfield;

import geogebra.gui.GeoGebraKeys;
import geogebra.main.Application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 * Michael Borcherds
 * 
 * Extends JTextField
 * adds support for alt-codes (and alt-shift-) for special characters
 * (ctrl on MacOS)
 */

public class MathTextField extends MyTextField implements KeyListener {

	private GeoGebraKeys ggbKeys;
	
	public MathTextField(Application app) {
		super(app);
		ggbKeys = new GeoGebraKeys(app);
		addKeyListener(this);
	}
	
	public MathTextField(Application app, int length) {
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
