package org.geogebra.web.html5.gui.util;

import elemental2.dom.Element.FocusOptionsType;
import jsinterop.base.Js;

public class FocusUtil {
	public static void focusNoScroll(org.gwtproject.dom.client.Element element) {
		FocusOptionsType op = FocusOptionsType.create();
		elemental2.dom.Element el = Js.uncheckedCast(element);
		op.setPreventScroll(true);
		el.focus(op);
	}
}
