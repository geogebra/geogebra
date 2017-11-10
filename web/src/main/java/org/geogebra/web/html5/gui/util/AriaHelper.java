package org.geogebra.web.html5.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;

import com.google.gwt.user.client.ui.UIObject;

/**
 * Helper class for accessibility methods
 */
public class AriaHelper {
	/**
	 * @param ui
	 *            UI element
	 * @param title
	 *            title
	 * @param app
	 *            for feature flag
	 */
	public static void setTitle(UIObject ui, String title, App app) {
		if (app.has(Feature.TOOLTIP_DESIGN) && !Browser.isMobile()) {
			ui.getElement().removeAttribute("title");
			if (!"".equals(title)) {
				ui.getElement().setAttribute("data-title", title);
			}
		} else {
			ui.setTitle(title);
		}

	}
}
