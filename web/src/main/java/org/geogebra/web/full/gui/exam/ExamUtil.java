package org.geogebra.web.full.gui.exam;

import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;

import elemental2.dom.DomGlobal;

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
	private void visibilityEventMain(boolean tabletMode) {
		if (tabletMode) {
			GeoGebraGlobal.visibilityEventMain(this::startCheating, this::stopCheating);
		} else {

			DomGlobal.window.onblur = (event) -> {
				// The focusout event should not be caught:
				if ("blur".equals(event.type)) {
					startCheating();
				}
				return null;
			};
			DomGlobal.window.onfocus = (event) -> {
				if (Browser.isCoveringWholeScreen()) {
					stopCheating();
				}
				return null;
			};
			DomGlobal.window.addEventListener("resize",
					(evt) -> {
						boolean fullscreen = Browser.isCoveringWholeScreen();
						if (!fullscreen) {
							startCheating();
						} else {
							stopCheating();
						}
			});
		}
	}

	private void startCheating() {
		if (app.getExam() != null && !app.getExam().isClosed()) {
			app.getExam().checkedWindowLeft();
		}
	}

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
