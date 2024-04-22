package org.geogebra.web.html5.gui.util;

import elemental2.dom.Element.FocusOptionsType;
import jsinterop.base.Js;

/**
 * Helper class to focus element with options.
 * In these cases element.focus() is not enough.
 */
public class FocusUtil {

	/**
	 * Focus element with preventScroll option set.
	 *
	 * @param element to focus.
	 */
	public static void focusNoScroll(org.gwtproject.dom.client.Element element) {
		FocusOptionsType op = FocusOptionsType.create();
		elemental2.dom.Element el = Js.uncheckedCast(element);
		op.setPreventScroll(true);
		el.focus(op);
	}
}
