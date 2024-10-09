package org.geogebra.web.html5.bridge;

import org.gwtproject.dom.client.Element;

import jsinterop.base.Js;

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

	String getAttribute(String attribute);

	boolean hasAttribute(String attribute);

	void removeAttribute(String attribute);

	void setAttribute(String attribute, String value);

	Element getElement();
}
