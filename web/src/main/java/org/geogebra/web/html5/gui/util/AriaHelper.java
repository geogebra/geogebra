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
	 * Avoid setting title (so that screen reader only reads the image alt) Set
	 * aria-label for desktop screen reader and data-title for visual tooltips.
	 * 
	 * @param ui
	 *            UI element
	 * @param title
	 *            title
	 * @param app
	 *            for feature flag
	 */
	public static void setTitle(UIObject ui, String title, App app) {
		if (app.has(Feature.TOOLTIP_DESIGN) && !Browser.isMobile()) {
			if (!"".equals(title)) {
				ui.getElement().setAttribute("data-title", title);
			}
		}
		ui.getElement().removeAttribute("title");
		ui.getElement().setAttribute("aria-label", title);
	}
}
