package org.geogebra.web.html5.main;

public class ExamUtil {
	public native static boolean toggleFullscreen(boolean full) /*-{
		if ($wnd.ggbExamMode) {
			$wnd.ggbExamMode(full);
			return true;
		}
		$wnd.toggleFullScreen(full);
		return false;
	}-*/;
}
