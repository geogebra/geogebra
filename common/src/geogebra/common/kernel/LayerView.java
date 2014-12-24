package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoElement;

/**
 * Extends the View functionality by layer listener
 * 
 * @author kondr
 *
 */
public interface LayerView extends View {
	/**
	 * Called when layer is being changed
	 * 
	 * @param g
	 *            element that changed layer
	 * @param oldLayer
	 *            old layer
	 * @param newLayer
	 *            new layer
	 */
	public void changeLayer(GeoElement g, int oldLayer, int newLayer);

}
