package org.geogebra.web.html5.bridge;

import java.util.HashMap;

import org.gwtproject.dom.client.Element;

import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class MapAttributeProvider implements AttributeProvider {

	private final HashMap<String, String> map = new HashMap<>();
	private Element element;

	/**
	 * @param map app options as a plain JS object
	 */
	public MapAttributeProvider(JsPropertyMap<?> map) {
		if (map != null) {
			element = Js.uncheckedCast(map.get("element"));
			map.delete("element");
			map.forEach(key -> this.map.put(key, map.getAsAny(key).asString()));
		}
	}

	@Override
	public String getAttribute(String attribute) {
		return map.get(attribute);
	}

	@Override
	public boolean hasAttribute(String attribute) {
		return map.containsKey(attribute);
	}

	@Override
	public void removeAttribute(String attribute) {
		map.remove(attribute);
	}

	@Override
	public void setAttribute(String attribute, String value) {
		map.put(attribute, value);
	}

	@Override
	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}
}
