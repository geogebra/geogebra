package org.geogebra.web.html5.gui.zoompanel;

/**
 * Provides functionality for toggling emulated fullscreen (iOS) when embedded
 * as iframe into specific sites.
 */
public interface FullScreenHandler {

	/**
	 * Change fullscreen state of the iframe.
	 */
	public void toggleFullscreen();
}
