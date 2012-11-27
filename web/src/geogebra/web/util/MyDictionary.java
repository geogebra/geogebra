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
package geogebra.web.util;

import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * adapted from com.google.gwt.i18n.client.Dictionary to allow lookup by language and section
 * 
 * eg __GGB__keysVar[en][command][Excentricity]
 */
public final class MyDictionary {

  private static Map<String, MyDictionary> cache =
    new HashMap<String, MyDictionary>();
  private static final int MAX_KEYS_TO_SHOW = 20;

  /**
   * Returns the <code>Dictionary</code> object associated with the given
   * name.
   * 
   * @param section eg command, colors
   * @param language eg "en", "deAT"
   * @return specified dictionary
   * @throws MissingResourceException
   */
  public static MyDictionary getDictionary(String section, String language) {
	  MyDictionary target = cache.get(section+language);
    if (target == null) {
      target = new MyDictionary(section, language);
      cache.put(section+language, target);
    }
    return target;
  }

  static void resourceErrorBadType(String name) {
    throw new MissingResourceException("'" + name
        + "' is not a JavaScript object and cannot be used as a Dictionary",
        null, name);
  }

  private JavaScriptObject dict;

  private String label;

  /**
   * Constructor for <code>Dictionary</code>.
   * 
   * @param name name of linked JavaScript Object
   */
  private MyDictionary(String section, String language) {
    if (section == null || "".equals(section)) {
      throw new IllegalArgumentException(
          "Cannot create a Dictionary with a null or empty name");
    }
    this.label = "Dictionary " + section+language;
    attach(section, language);
    if (dict == null) {
    	// this is not working on the second call, don't know why
      //throw new MissingResourceException(
      //    "Cannot find JavaScript object with the name '__GGB__keysVar["+language+"]["+section+"]'", section+language,
      //    null);
    	App.debug("Problem finding JavaScript object with the name '__GGB__keysVar["+language+"]["+section+"]'");
    }
  }

  /**
   * Get the value associated with the given Dictionary key.
   * 
   * We have to call Object.hasOwnProperty to verify that the value is
   * defined on this object, rather than a superclass, since normal Object
   * properties are also visible on this object.
   * 
   * @param key to lookup
   * @return the value
   * @throws MissingResourceException if the value is not found
   */
  public native String get(String key) /*-{
    // In Firefox, jsObject.hasOwnProperty(key) requires a primitive string
    key = String(key);
    var map = this.@com.google.gwt.i18n.client.Dictionary::dict;
    var value = map[key];
    if (value == null || !map.hasOwnProperty(key)) {
      this.@com.google.gwt.i18n.client.Dictionary::resourceError(Ljava/lang/String;)(key);
    }
    return String(value);
  }-*/;

  /**
   * The set of keys associated with this dictionary.
   * 
   * @return the Dictionary set
   */
  public Set<String> keySet() {
    HashSet<String> s = new HashSet<String>();
    addKeys(s);
    return s;
  }

  @Override
  public String toString() {
    return label;
  }

  /**
   * Collection of values associated with this dictionary.
   * 
   * @return the values
   */
  public Collection<String> values() {
    ArrayList<String> s = new ArrayList<String>();
    addValues(s);
    return s;
  }

  void resourceError(String key) {
    String error = "Cannot find '" + key + "' in " + this;
    throw new MissingResourceException(error, this.toString(), key);
  }

  private native void addKeys(HashSet<String> s) /*-{
    var map = this.@com.google.gwt.i18n.client.Dictionary::dict
    for (var key in map) {
      if (map.hasOwnProperty(key)) {
        s.@java.util.HashSet::add(Ljava/lang/Object;)(key);
      }
    }
  }-*/;

  private native void addValues(ArrayList<String> s) /*-{
    var map = this.@com.google.gwt.i18n.client.Dictionary::dict
    for (var key in map) {
      if (map.hasOwnProperty(key)) {
        var value = this.@com.google.gwt.i18n.client.Dictionary::get(Ljava/lang/String;)(key);
        s.@java.util.ArrayList::add(Ljava/lang/Object;)(value);
      }
    }
  }-*/;
  
  private native void attach(String section, String language)/*-{
    try {
      if (typeof($wnd["__GGB__keysVar"][language][section]) != "object") {
        @com.google.gwt.i18n.client.Dictionary::resourceErrorBadType(Ljava/lang/String;)(name);
      }
      this.@com.google.gwt.i18n.client.Dictionary::dict = $wnd["__GGB__keysVar"][language][section];
      //alert($wnd["__GGB__keysVar"][language]["command"]["Excentricity"]);
    } catch(e) {
      @com.google.gwt.i18n.client.Dictionary::resourceErrorBadType(Ljava/lang/String;)(name);
    }
  }-*/;
}
