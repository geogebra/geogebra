package geogebra.gui.app;

/**
 * 
 *
 */
public interface NewInstanceListener {

	/**
	 * when registered, this is called whenever a new instance of GeoGebraFrame is created.
	 * @param frame the new instance
	 */
	void newInstance(GeoGebraFrame frame);
}
