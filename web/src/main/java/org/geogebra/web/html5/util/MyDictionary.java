/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.geogebra.web.html5.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

import org.geogebra.web.html5.GeoGebraGlobal;

import elemental2.core.JsObject;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * adapted from com.google.gwt.i18n.client.Dictionary to allow lookup by
 * language and section
 * 
 * eg __GGB__keysVar[en][command][Excentricity]
 */
public final class MyDictionary {

	private static Map<String, MyDictionary> cache = new HashMap<>();
	private JsPropertyMap<String> dict;

	private String label;

	/**
	 * Returns the <code>Dictionary</code> object associated with the given
	 * name.
	 * 
	 * @param section
	 *            eg command, colors
	 * @param language
	 *            eg "en", "deAT"
	 * @return specified dictionary
	 * @throws MissingResourceException
	 *             when no dictionary is available for given language
	 */
	public static MyDictionary getDictionary(String section, String language)
			throws MissingResourceException {
		MyDictionary target = cache.get(section + language);
		if (target == null) {
			target = new MyDictionary(section, language);
			cache.put(section + language, target);
		}
		return target;
	}

	private static void resourceErrorBadType(String name) {
		throw new MissingResourceException(
				"Dictionary '" + name + "' not found.", null, name);
	}

	/**
	 * Constructor for <code>Dictionary</code>.
	 * 
	 * @param section
	 *            dictionary section
	 * @param language
	 *            dictionary language
	 */
	private MyDictionary(String section, String language)
			throws MissingResourceException {
		if (section == null || "".equals(section)) {
			throw new IllegalArgumentException(
			        "Cannot create a Dictionary with a null or empty name");
		}
		this.label = "Dictionary " + section + language;
		attach(section, language);
		if (dict == null) {
			// this is not working on the second call, don't know why
			throw new MissingResourceException(
			        "Cannot find JavaScript object with the name '__GGB__keysVar["
			                + language + "][" + section + "]'", section
			                + language, null);
		}
	}

	/**
	 * Get the value associated with the given Dictionary key.
	 * 
	 * We have to call Object.hasOwnProperty to verify that the value is defined
	 * on this object, rather than a superclass, since normal Object properties
	 * are also visible on this object.
	 * 
	 * @param key
	 *            to lookup
	 * @return the value
	 * @throws MissingResourceException
	 *             if the value is not found
	 */
	public String get(String key) {
		Object value = dict.get(key);
		if (value == null) {
			String error = "Cannot find '" + key + "' in " + this;
			throw new MissingResourceException(error, this.toString(), key);
		}
		return Js.asString(value);
	}

	/**
	 * The set of keys associated with this dictionary.
	 * 
	 * @return the Dictionary set
	 */
	public Set<String> keySet() {
		HashSet<String> s = new HashSet<>();
		addKeys(s);
		return s;
	}

	@Override
	public String toString() {
		return label;
	}

	private void addKeys(HashSet<String> s) {
		dict.forEach(key -> {
			JsObject asObj = Js.uncheckedCast(dict);
			if (asObj.hasOwnProperty(key)) {
				s.add(key);
			}
		});
	}

	private void attach(String section, String language) {
		try {
			JsPropertyMap<String> props = GeoGebraGlobal.__GGB__keysVar.get(language).get(section);
			if (!"object".equals(Js.typeof(props))) {
				resourceErrorBadType(section + language);
			}
			dict = props;
		} catch (Throwable e) {
			resourceErrorBadType(section + language);
		}
	}
}
