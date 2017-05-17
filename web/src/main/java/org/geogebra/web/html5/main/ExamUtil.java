package org.geogebra.web.html5.main;

public class ExamUtil {
	/**
	 * @param full
	 *            whether to switch to fullscreen
	 * @param browserAPI
	 *            whether to use browser API also; otherwise only wrapper
	 *            (Android) native callback is used
	 */
	public native static void toggleFullscreen(boolean full,
			boolean browserAPI) /*-{
		if ($wnd.ggbExamMode && (full == !$wnd.ggbExamMode.running)) {
			$wnd.ggbExamMode.running = full;
			$wnd.ggbExamMode(full);
		}
		if (browserAPI) {
			$wnd.toggleFullScreen(full);
		}
	}-*/;
}
