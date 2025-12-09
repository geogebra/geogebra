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

import java.util.HashMap;
import java.util.Locale;

import org.gwtproject.dom.client.Element;

import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class MapAttributeProvider implements AttributeProvider {

	private final HashMap<String, /* NonNull */ String> map = new HashMap<>();
	private Element element;

	/**
	 * @param map app options as a plain JS object
	 */
	public MapAttributeProvider(JsPropertyMap<?> map) {
		if (map != null) {
			element = Js.uncheckedCast(map.get("element"));
			map.delete("element");
			map.forEach(key -> {
				Any value = map.getAsAny(key);
				if (value != null) {
					setAttribute(key, value.toString());
				}
			});
		}
	}

	@Override
	public String getAttribute(String attribute) {
		return map.get(attribute.toLowerCase(Locale.US));
	}

	@Override
	public boolean hasAttribute(String attribute) {
		return map.containsKey(attribute.toLowerCase(Locale.US));
	}

	@Override
	public void removeAttribute(String attribute) {
		map.remove(attribute.toLowerCase(Locale.US));
	}

	@Override
	public void setAttribute(String attribute, String value) {
		if (value == null) {
			removeAttribute(attribute);
		} else {
			map.put(attribute.toLowerCase(Locale.US), value);
		}
	}

	@Override
	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}
}
