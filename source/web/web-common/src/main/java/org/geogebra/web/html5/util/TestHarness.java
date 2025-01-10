package org.geogebra.web.html5.util;

import org.gwtproject.user.client.ui.UIObject;

public class TestHarness {

	/**
	 * Set data-test attribute
	 * As using XPaths and CSS selectors is unstable and brittle
	 * please add this attribute to any widget you want to refer to
	 * in your UI tests
	 * @param widget widget to set
	 * @param value value of data-test attribute
	 */
	public static void setAttr(UIObject widget, String value) {
		if (widget != null) {
			widget.getElement().setAttribute("data-test", value);
		}
	}
}
