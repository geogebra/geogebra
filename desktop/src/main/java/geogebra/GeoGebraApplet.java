package geogebra;

import java.awt.Graphics;

import javax.swing.JApplet;

// Wrapper for backward compatibility for old JNLP based applets.

class GeoGebraApplet extends JApplet {
	
	private static final long serialVersionUID = 652840263046810738L;

	final public void paint(Graphics g) {
		org.geogebra.desktop.GeoGebraApplet a = new org.geogebra.desktop.GeoGebraApplet();
		a.paint(g);
	}

}

