package org.geogebra.web.html5.gui.zoompanel;

import org.geogebra.web.html5.MebisGlobal;

/**
 * Emulated fullscreen handler for embedding in Mebis website
 */
public class MebisFullscreenHandler implements FullScreenHandler {

	@Override
	public void toggleFullscreen() {
		MebisGlobal.toggleFullscreen();
	}
}
