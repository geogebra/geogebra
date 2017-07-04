package org.geogebra.web.html5.main;

/**
 * Helper class for exam
 *
 */
public class ExamUtil {
	/**
	 * @param full
	 *            whether to switch to fullscreen
	 */
	public native static void toggleFullscreen(boolean full) /*-{
		if ($wnd.ggbExamMode && (full == !$wnd.ggbExamMode.running)) {
			$wnd.ggbExamMode.running = full;
			$wnd.ggbExamMode(full);
		}
	}-*/;
}
