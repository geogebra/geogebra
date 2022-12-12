package org.geogebra.web.full.util;

import org.geogebra.web.html5.Browser;

import com.google.gwt.user.client.ui.Panel;

public class CustomScrollbar {

	/**
	 * @param scroller panel to be scrolled
	 */
	public static void apply(Panel scroller) {
		String style = Browser.isSafariByVendor() ? "customScrollbarSafari" : "customScrollbar";
		scroller.addStyleName(style);
	}
}
