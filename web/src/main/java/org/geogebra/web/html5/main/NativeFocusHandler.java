package org.geogebra.web.html5.main;

import com.google.gwt.dom.client.Element;

public class NativeFocusHandler {
	/**
	 * @param el
	 *            element
	 * @param app
	 *            app listening to focus (see {@link AppW#addFocusToApp()})
	 */
	public static native void addNativeFocusHandler(Element el, AppW app)/*-{
		el.onfocus = function(event) {
			app.@org.geogebra.web.html5.main.AppW::addFocusToApp()();
		}
	}-*/;
}