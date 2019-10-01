package org.geogebra.web.full.gui.util;

import org.geogebra.common.util.ExternalAccess;
import org.geogebra.web.html5.main.AppW;

/**
 * Connector for native APIs in WinStore apps
 */
public class WindowsNativeUIController {
	private static final int BLUR_DELAY = 300;
	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public WindowsNativeUIController(AppW app) {
		this.app = app;
	}

    @ExternalAccess
    private void freezeBlurEvents() {
        if (app.getExam() != null) {
            app.getExam().setIgnoreBlurInterval(BLUR_DELAY);
        }
    }

	/**
	 * Hide the native onscreen keyboard
	 */
	public native void hideKeyboard() /*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			this.@org.geogebra.web.full.gui.util.WindowsNativeUIController::freezeBlurEvents()();
			$wnd.android.callPlugin("CloseKeyboard", []);
		}
	}-*/;
}
