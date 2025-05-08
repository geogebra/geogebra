package org.geogebra.web.html5.bridge;

import org.geogebra.common.annotation.MissingDoc;
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

	@MissingDoc
	Element getElement();
}
