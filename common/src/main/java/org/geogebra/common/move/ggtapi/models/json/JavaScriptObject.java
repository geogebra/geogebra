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

/**
 * An opaque handle to a native JavaScript object. A
 * <code>JavaScriptObject</code> cannot be created directly.
 * <code>JavaScriptObject</code> should be declared as the return type of a
 * JSNI method that returns native (non-Java) objects. A
 * <code>JavaScriptObject</code> passed back into JSNI from Java becomes the
 * original object, and can be accessed in JavaScript as expected.
 */
public class JavaScriptObject extends HashMap<String, JSONValue> implements JavaScriptObjectBase {

 
  /**
   * Not directly instantiable. All subclasses must also define a protected,
   * empty, no-arg constructor.
   */
  protected JavaScriptObject() {
  }

  /**
   * A helper method to enable cross-casting from any {@link JavaScriptObject}
   * type to any other {@link JavaScriptObject} type.
   *
   * @param <T> the target type
   * @return this object as a different type
   */
  @SuppressWarnings("unchecked")
  public final <T extends JavaScriptObject> T cast() {
    return (T) this;
  }

  /**
   * Returns <code>true</code> if the objects are JavaScript identical
   * (triple-equals).
   */
  @Override
  public final boolean equals(Object other) {
    return super.equals(other);
  }

  

  

  /**
   * Makes a best-effort attempt to get a useful debugging string describing the
   * given JavaScriptObject. In Production Mode with assertions disabled, this
   * will either call and return the JSO's toString() if one exists, or just
   * return "[JavaScriptObject]". In Development Mode, or with assertions
   * enabled, some stronger effort is made to represent other types of JSOs,
   * including inspecting for document nodes' outerHTML and innerHTML, etc.
   */
  @Override
  public final String toString() {
    return this.toString();
  }

/**
 * @return a new javascript object
 */
public static JavaScriptObject createObject() {
	return new JavaScriptObject();
}

/**
 * @return new javascript array
 */
public static JavaScriptArray createArray() {
	return new JavaScriptArray();
}
}
