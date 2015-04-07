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

import java.util.ArrayList;

/**
 * Represents an array of {@link com.google.gwt.json.client.JSONValue} objects.
 */
public class JSONArray extends JSONValue {
  
  /**
   * Called from {@link #getUnwrapper()}. 
   */
	/*
	 * private static JavaScriptObjectBase unwrap(JSONArray value) { return
	 * value.jsArray; }
	 */

	private final ArrayList<JSONValue> jsArray;

  /**
   * Creates an empty JSONArray.
   */
  public JSONArray() {
    jsArray = JavaScriptObject.createArray();
  }

  /**
   * Creates a new JSONArray from the supplied JavaScriptObject representing a
   * JavaScript array.
   * 
   * @param arr a JavaScript array
   */
	/*
	 * public JSONArray(JavaScriptArray arr) { jsArray = arr; }
	 */

  /**
   * Returns <code>true</code> if <code>other</code> is a {@link JSONArray}
   * wrapping the same underlying object.
   */
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof JSONArray)) {
      return false;
    }
    return jsArray.equals(((JSONArray) other).jsArray);
  }

  /**
   * Returns the value at the specified index position.
   * 
   * @param index the index of the array item to retrieve
   * @return the value at this index, or <code>null</code> if this index is
   *         empty
   */
  public JSONValue get(int index) {
	  if (jsArray.size() > index) {
		  return jsArray.get(index);
	  }
	  return null;
  }

  /**
   * Returns the underlying JavaScript array that this object wraps.
   */
	/*
	 * public JavaScriptObjectBase getJavaScriptObject() { return jsArray; }
	 */

  @Override
  public int hashCode() {
    return jsArray.hashCode();
  }

  /**
   * Returns <code>this</code>, as this is a JSONArray.
   */
  @Override
  public JSONArray isArray() {
    return this;
  }

  /**
   * Sets the specified index to the given value.
   * 
   * @param index the index to set
   * @param value the value to set
   */
	public void set(int index, JSONValue value) {
    jsArray.add(value);
  }

  /**
   * Returns the number of elements in this array.
   * 
   * @return size of this array
   */
  public int size() {
	    return jsArray.size();
  };

  /**
   * Create the JSON encoded string representation of this JSONArray instance.
   * This method may take a long time to execute if the underlying array is
   * large.
   */
  @Override
	public void appendTo(StringBuffer sb) {

		sb.append('[');
		for (int i = 0, c = size(); i < c; i++) {
			if (i > 0) {
				sb.append(',');
			}
			get(i).appendTo(sb);
		}
		sb.append(']');

	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		appendTo(sb);
		return sb.toString();
	}


}
