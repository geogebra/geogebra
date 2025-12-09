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

import jsinterop.base.Js;

/**
 * Provider of applet parameters, maps parameter names to values and supports overwriting.
 */
public interface AttributeProvider {

	/**
	 * Converts element or JS object to an attribute provider
	 * @param options element
	 * @return attribute provider
	 */
	static AttributeProvider as(Object options) {
		return Element.is(Js.uncheckedCast(options))
				? new DOMAttributeProvider((Element) options)
				: new MapAttributeProvider(Js.asPropertyMap(options));
	}

	/**
	 * @param attribute attribute name
	 * @return attribute value
	 */
	String getAttribute(String attribute);

	/**
	 * @param attribute attribute name
	 * @return whether attribute is set
	 */
	boolean hasAttribute(String attribute);

	/**
	 * Removes (unsets) an attribute.
	 * @param attribute attribute name
	 */
	void removeAttribute(String attribute);

	/**
	 * Sets an attribute. If {@code null} is supplied, removes the attribute.
	 * @param attribute attribute name
	 * @param value value
	 */
	void setAttribute(String attribute, String value);

	/**
	 * @return element in which the applet should be rendered
	 */
	Element getElement();
}
