package org.geogebra.web.full.gui.exam;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.StringUtil;
import org.geogebra.gwtutil.SecureBrowser;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

/**
 * Utility class for exam mode
 */
public class ExamUtil {

	private AppW app;
	private static boolean examModeRunning = false;
	private static final ExamController examController = GlobalScope.examController;

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
	private void addVisibilityAndBlurHandlers(boolean tabletMode) {
		if (tabletMode) {
			app.getGlobalHandlers().addEventListener(DomGlobal.document,
					"visibilitychange", (e) -> {
				if (Js.isTruthy(DomGlobal.document.hidden)) {
					startCheating();
				} else {
					stopCheating();
				}
			});
		} else {
			app.getGlobalHandlers().addEventListener(DomGlobal.window, "blur", (event) -> {
				// The focusout event should not be caught:
				if ("blur".equals(event.type)) {
					startCheating();
				}
			});
			app.getGlobalHandlers().addEventListener(DomGlobal.window, "focus", (event) -> {
				if (Browser.isCoveringWholeScreen()) {
					stopCheating();
				}
			});
			app.getGlobalHandlers().addEventListener(DomGlobal.window, "resize",
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
		if (examController.isExamActive() && SecureBrowser.get() == null) {
			examController.getCheatingEvents().addWindowLeftEvent();
		}
	}

	private void stopCheating() {
		if (examController.isExamActive()) {
			examController.getCheatingEvents().addWindowEnteredEvent();
		}
	}

	/**
	 * @param full
	 *            whether to switch to fullscreen
	 */
	public static void toggleFullscreen(boolean full) {
		if (GeoGebraGlobal.getGgbExamMode() != null && (full == !examModeRunning)) {
			examModeRunning = full;
			GeoGebraGlobal.getGgbExamMode().accept(full);
		}
	}

	/**
	 * Listen to focus / blur / resize events on the browser window.
	 */
	public void addVisibilityAndBlurHandlers() {
		addVisibilityAndBlurHandlers(app.getLAF().isTablet());
	}

	/**
	 * @param element
	 *            element
	 * @param red
	 *            whether it should be red
	 */
	public static void makeRed(Element element, boolean red) {
		if (red) {
			Dom.setImportant(element.getStyle(), "background-color",
					StringUtil.toHtmlColor(GColor.DARK_RED));
		} else {
			element.getStyle().setBackgroundColor("");
		}
	}

	/**
	 * @param appW
	 *            app
	 * @return exam status description
	 */
	public static String status(AppW appW) {
		return appW.getLocalization().getMenu("exam_menu_entry") + ": "
				+ (examController.isCheating()
						? appW.getLocalization().getMenu("exam_alert")
						: appW.getLocalization().getMenu("OK"));
	}

}
