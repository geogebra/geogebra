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
		el.setAttribute(DATA_PARAM + attribute, value);
	}

	@Override
	public Element getElement() {
		return el;
	}

}
