/*
 * Copyright 2008 Google Inc.
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
package org.geogebra.common.move.ggtapi.models.json;

import java.util.HashMap;
import java.util.Set;

/**
 * Represents a JSON object. A JSON object consists of a set of properties.
 */
public class JSONObject extends JSONValue {



	private final HashMap<String, JSONValue> jsObject;

	public JSONObject() {
		jsObject = new HashMap<String, JSONValue>();
	}

	/**
	 * Creates a new JSONObject from the supplied JavaScript value.
	 */
	/*
	 * public JSONObject(HashMap<String, Object> jsValue) { jsObject = jsValue;
	 * }
	 */

	/**
	 * Tests whether or not this JSONObject contains the specified property.
	 * 
	 * @param key
	 *            the property to search for
	 * @return <code>true</code> if the JSONObject contains the specified
	 *         property
	 */
	public boolean containsKey(String key) {
		return jsObject.containsKey(key);
	}

	/**
	 * Returns <code>true</code> if <code>other</code> is a {@link JSONObject}
	 * wrapping the same underlying object.
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof JSONObject)) {
			return false;
		}
		return jsObject.equals(((JSONObject) other).jsObject);
	}

	/**
	 * Gets the JSONValue associated with the specified property.
	 * 
	 * @param key
	 *            the property to access
	 * @return the value of the specified property, or <code>null</code> if the
	 *         property does not exist
	 * @throws NullPointerException
	 *             if key is <code>null</code>
	 */
	public JSONValue get(String key) {
		if (key == null) {
			throw new NullPointerException();
		}
		return jsObject.get(key);
	}

	/**
	 * Returns the underlying JavaScript object that this object wraps.
	 */
	/*
	 * public JavaScriptObject getJavaScriptObject() { return jsObject; }
	 */

	@Override
	public int hashCode() {
		return jsObject.hashCode();
	}

	/**
	 * Returns <code>this</code>, as this is a JSONObject.
	 */
	@Override
	public JSONObject isObject() {
		return this;
	}

	/**
	 * Returns the set of properties defined on this JSONObject. The returned
	 * set is immutable.
	 */
	public Set<String> keySet() {
		return jsObject.keySet();
	}

	/**
	 * Assign the specified property to the specified value in this JSONObject.
	 * If the property already has an associated value, it is overwritten.
	 * 
	 * @param key
	 *            the property to assign
	 * @param jsonValue
	 *            the value to assign
	 * @return the previous value of the property, or <code>null</code> if the
	 *         property did not exist
	 * @throws NullPointerException
	 *             if key is <code>null</code>
	 */
	public JSONValue put(String key, JSONValue jsonValue) {
		if (key == null) {
			throw new NullPointerException();
		}
		JSONValue previous = get(key);
		jsObject.put(key, jsonValue);
		return previous;
	}

	/**
	 * Determines the number of properties on this object.
	 */
	public int size() {
		return jsObject.size();
	}

	/**
	 * Converts a JSONObject into a JSON representation that can be used to
	 * communicate with a JSON service.
	 * 
	 * @return a JSON string representation of this JSONObject instance
	 */
	@Override
	public void appendTo(StringBuffer sb) {

		sb.append("{");
		boolean first = true;
		// String[] keys = computeKeys();
		for (String key : jsObject.keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(JsonUtils.escapeValue(key));
			sb.append(":");
			get(key).appendTo(sb);
		}
		sb.append("}");
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		appendTo(sb);
		return sb.toString();
	}

	private String[] computeKeys() {
		return jsObject.keySet().toArray(new String[jsObject.size()]);
	}

	/**
	 * Assigns given string value to given key
	 * @param key key
	 * @param token string value
	 */
	public void put(String key, String token) {
		this.put(key, new JSONString(token));
	}

	/**
	 * @param key key
	 * @return whether there is a value assigned to the key
	 */
	public boolean has(String key) {
		return keySet().contains(key);
	}
	/**
	 * @param key key
	 * @return string value for given key; rounds if value is double
	 */
	public String getString(String key) {
		JSONValue val = this.get(key);
		if (val instanceof JSONString) {
			return ((JSONString) val).stringValue();
		}
		throw new JSONException();
	}

	/**
	 * @param key key
	 * @return int value for given key; rounds if value is double
	 */
	public int getInt(String key) {
		JSONValue val = this.get(key);
		if (val instanceof JSONNumber) {
			return (int) ((JSONNumber) val).doubleValue();
		}
		throw new JSONException();
	}

	/**
	 * @param key key
	 * @return object for given key
	 */
	public JSONObject getJSONObject(String key) {
		JSONValue val = this.get(key);
		if (val instanceof JSONObject) {
			return (JSONObject) val;
		}
		throw new JSONException();
	}

}
