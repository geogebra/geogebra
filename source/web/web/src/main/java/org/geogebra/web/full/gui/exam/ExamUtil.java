/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.exam;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.ownership.SuiteScope;
import org.geogebra.common.util.StringUtil;
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
	private final ExamController examController;

	/**
	 * @param app
	 *            application
	 */
	public ExamUtil(AppW app) {
		this.app = app;
		this.examController = GlobalScope.getExamController(app);
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
		if (hasExternalSecurityCheck(app)) {
			return;
		}
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

	/**
	 * @return whether we're running in a secure browser
	 */
	public static boolean hasExternalSecurityCheck(AppW app) {
		return app.isLockedExam() && app.isSecuredBrowser();
	}

	private void startCheating() {
		if (examController.isExamActive()) {
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
		SuiteScope suiteScope = GlobalScope.getSuiteScope(appW);
		ExamController examController = suiteScope != null ? suiteScope.examController : null;
		return appW.getLocalization().getMenu("exam_menu_entry") + ": "
				+ (examController != null && examController.isCheating()
						? appW.getLocalization().getMenu("exam_alert")
						: appW.getLocalization().getMenu("OK"));
	}
}
