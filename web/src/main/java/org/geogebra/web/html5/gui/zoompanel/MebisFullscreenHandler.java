package org.geogebra.web.html5.gui.zoompanel;

/**
 * Emulated fullscreen handler for embedding in Mebis website
 */
public class MebisFullscreenHandler implements FullScreenHandler {

	@Override
	public native void toggleFullscreen() /*-{
		try {
			// the CSS class is defined in https://git.geogebra.org/mow/mow-front/blob/master/app.php
			$wnd.parent.$($wnd.parent.document.body).toggleClass(
					"fullscreen-app");
		} catch (ex) {
			// possibly the Mebis iframe is embedded in some non-standard page
			$wnd.console.log(ex);
		}
	}-*/;
}
