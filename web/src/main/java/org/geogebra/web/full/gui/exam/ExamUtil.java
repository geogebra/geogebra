package org.geogebra.web.full.gui.exam;

import org.geogebra.common.util.ExternalAccess;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;

/**
 * Utility class for exam mode
 */
public class ExamUtil {

	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public ExamUtil(AppW app) {
		this.app = app;
	}

	/**
	 * check and log window resize and focus lost/gained window resize is
	 * checked first - if window is not in full screen mode "cheating" can't be
	 * stopped (only going back to full screen ends "cheating") if window is in
	 * full screen losing focus starts "cheating", gaining focus stops
	 * "cheating"
	 * 
	 * @param tabletMode
	 *            whether we are in tablet app
	 */
	private native void visibilityEventMain(boolean tabletMode) /*-{
		var gui = this;
		// fix for firefox and iexplorer (e.g. fullscreen goes to 1079px instead of 1080px)
		var fullscreen = true;
		if ($wnd.innerHeight < screen.height - 5
				|| $wnd.innerWidth < screen.width - 5) {
			fullscreen = false;
		}

		var startCheating = function() {
			gui.@org.geogebra.web.full.gui.exam.ExamUtil::startCheating()()
		};
		var stopCheating = function() {
			gui.@org.geogebra.web.full.gui.exam.ExamUtil::stopCheating()()
		};

		if (tabletMode) {
			$wnd.visibilityEventMain(startCheating, stopCheating);
		} else {

			$wnd.onblur = function(event) {
				// Borrowed from http://www.quirksmode.org/js/events_properties.html
				//$wnd.console.log("4");
				var e = event ? event : $wnd.event;
				var targ;
				if (e.target) {
					targ = e.target;
				} else if (e.srcElement) {
					targ = e.srcElement;
				}
				if (targ.nodeType == 3) { // defeat Safari bug
					targ = targ.parentNode;
				}
				// The focusout event should not be caught:
				if (e.type == "blur") { //&& fullscreen == true
					//$wnd.console.log("5");
					startCheating();
				}

			};
			$wnd.onfocus = function(event) {
				//$wnd.console.log("6");
				if (fullscreen) {
					stopCheating();
					//	focus = true;
					//	console.log("focus 3 " + focus);
				}
			}
			// window resize has 2 cases: full screen and not full screen
			$wnd
					.addEventListener(
							"resize",
							function() {
								fullscreen = @org.geogebra.web.html5.Browser::isCoveringWholeScreen()();
								if (!fullscreen) {
									startCheating();
								} else {
									stopCheating();
								}
							});
		}
	}-*/ ;

    @ExternalAccess
    private void startCheating() {
        if (app.getExam() != null && !app.getExam().isClosed()) {
            app.getExam().checkedWindowLeft();
        }
    }

    @ExternalAccess
    private void stopCheating() {
        if (app.getExam() != null) {
            app.getExam().stopCheating();
        }
    }

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

	/**
	 * Listen to focus / blur / resize events on the browser window.
	 */
	public void visibilityEventMain() {
		visibilityEventMain(isTablet());
	}

	private boolean isTablet() {
		return app.getLAF().isTablet()
				&& !"TabletWin".equals(app.getLAF().getFrameStyleName());
	}

	/**
	 * @param element
	 *            element
	 * @param red
	 *            whether it should be red
	 */
	public static native void makeRed(Element element, boolean red) /*-{
		element.style.setProperty("background-color", red ? "#D32F2F" : "",
				red ? "important" : "");
	}-*/;

	/**
	 * @param appW
	 *            app
	 * @return exam status description
	 */
	public static String status(AppW appW) {
		return appW.getLocalization().getMenu("exam_menu_entry") + ": "
				+ (appW.getExam().isCheating()
						? appW.getLocalization().getMenu("exam_alert")
						: appW.getLocalization().getMenu("OK"));
	}

}
