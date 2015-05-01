package geogebra;

import javax.swing.JApplet;

// Wrapper for backward compatibility for old JNLP based applets.

class GeoGebraAppletPreloader extends JApplet {

	private static final long serialVersionUID = -7309225855770453563L;

	@Override
	public void init() {
		org.geogebra.desktop.GeoGebraAppletPreloader preloader = new org.geogebra.desktop.GeoGebraAppletPreloader();
		preloader.init();
	}
}
