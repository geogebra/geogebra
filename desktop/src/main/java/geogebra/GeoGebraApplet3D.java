package geogebra;

// Wrapper for backward compatibility for old JNLP based applets.

class GeoGebraApplet3D extends GeoGebraApplet {
	
	private static final long serialVersionUID = 4726831610762115524L;

	protected synchronized void initAppletImplementation() {
		org.geogebra.desktop.GeoGebraApplet3D applet = new org.geogebra.desktop.GeoGebraApplet3D();
		applet.initAppletImplementation();
	}

}
