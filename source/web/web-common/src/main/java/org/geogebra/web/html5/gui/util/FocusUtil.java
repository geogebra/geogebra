/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
