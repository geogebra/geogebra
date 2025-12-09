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
