package org.geogebra.web.html5.gui.zoompanel;

public class MebisFullscreenHandler implements FullScreenHandler {

	@Override
	public native void toggleFullscreen() /*-{
		try {
			$wnd.parent.$($wnd.parent.document.body).toggleClass(
					"fullscreen-app");
		} catch (ex) {
			$wnd.console.log(ex);
		}
	}-*/;
}
