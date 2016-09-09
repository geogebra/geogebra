package org.geogebra.web.html5.main;

public class ExamUtil {
	public native static boolean toggleFullscreen(boolean full) /*-{
		if ($wnd.ggbExamMode && (full == !$wnd.ggbExamMode.running)) {
			$wnd.ggbExamMode.running = full;
			$wnd.ggbExamMode(full);
		}
		$wnd.toggleFullScreen(full);
		return false;
	}-*/;
}
