package org.geogebra.web.html5.util;

import org.geogebra.web.html5.Browser;

import com.google.gwt.user.client.ui.ScrollPanel;

public class CustomScrollbar {

	/**
	 * @param scroller panel to be scrolled
	 */
	public static void apply(ScrollPanel scroller) {
		String style = Browser.isSafariByVendor() ? "customScrollbarSafari" : "customScrollbar";
		scroller.addStyleName(style);
	}
}
