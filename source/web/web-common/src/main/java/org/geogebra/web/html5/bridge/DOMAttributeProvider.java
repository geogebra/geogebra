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

package org.geogebra.web.html5.bridge;

import org.gwtproject.dom.client.Element;

public class DOMAttributeProvider implements AttributeProvider {

	public static final String DATA_PARAM = "data-param-";
	private final Element el;

	public DOMAttributeProvider(Element el) {
		this.el = el;
	}

	@Override
	public String getAttribute(String attribute) {
		return el.getAttribute(DATA_PARAM + attribute);
	}

	@Override
	public boolean hasAttribute(String attribute) {
		return el.hasAttribute(DATA_PARAM + attribute);
	}

	@Override
	public void removeAttribute(String attribute) {
		el.removeAttribute(DATA_PARAM + attribute);
	}

	@Override
	public void setAttribute(String attribute, String value) {
		// TODO value = null should remove it
		el.setAttribute(DATA_PARAM + attribute, value);
	}

	@Override
	public Element getElement() {
		return el;
	}

}
