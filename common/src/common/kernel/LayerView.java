package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoElement;

/**
 * Extends the View functionality by layer listener
 * @author kondr
 *
 */
public interface LayerView extends View{
	/**
	 * Called when layer is being changed
	 * @param g
	 * @param oldLayer
	 * @param newLayer
	 */
	public void changeLayer(GeoElement g, int oldLayer,int newLayer);

}
