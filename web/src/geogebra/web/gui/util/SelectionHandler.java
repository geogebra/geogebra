package geogebra.web.gui.util;

import com.google.gwt.dom.client.Element;

public class SelectionHandler {
	public native static void disableTextSelectInternal(Element e,
	        boolean disable)/*-{
		if (disable) {
			e.ondrag = function() {
				return false;
			};
			e.onselectstart = function() {
				return false;
			};
			e.style.MozUserSelect = "none"
		} else {
			e.ondrag = null;
			e.onselectstart = null;
			e.style.MozUserSelect = "text"
		}
	}-*/;
}
