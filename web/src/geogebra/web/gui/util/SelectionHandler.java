package geogebra.web.gui.util;

import com.google.gwt.dom.client.Element;
/**
 * Handle native selection mode
 * @author Zbynek, based on snippet from the Internet
 *
 */
public class SelectionHandler {
	/**
	 * http://forgetmenotes.blogspot.cz/2009/05/gwt-disable-text-selection-in-table.html
	 * @param e element
	 * @param disable true to disable, false to enable
	 */
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
