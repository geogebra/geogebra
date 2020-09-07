package org.geogebra.web.html5.util;

import java.util.HashMap;

public class MapAttributeProvider implements AttributeProvider {

	private HashMap<String, String> map = new HashMap<>();

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
}
