package org.geogebra.web.html5.main;

public class ExamUtil {
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
