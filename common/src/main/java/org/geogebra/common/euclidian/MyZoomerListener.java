package org.geogebra.common.euclidian;

/**
 * Listener for zooming Euclidian view
 * 
 * @author laszlo
 *
 */
public interface MyZoomerListener {
	/** invoke when zoom starts */
	void onZoomStart();

	/** invoke when zoom step happened */
	void onZoomStep();

	/** invoke when zoom ends */
	void onZoomEnd();
}
