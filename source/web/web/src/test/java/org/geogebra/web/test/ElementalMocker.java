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

package org.geogebra.web.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.geogebra.common.util.debug.Log;

import elemental2.core.Global;
import elemental2.core.JSONType;
import elemental2.dom.Console;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLBodyElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLHtmlElement;
import elemental2.dom.Location;
import elemental2.dom.Navigator;
import elemental2.webstorage.WebStorageWindow;
import jsinterop.base.JsPropertyMap;

public class ElementalMocker {

	/**
	 * Initiate some global variables (window, document, ...)
	 */
	public static void setupElemental() {
		try {
			DomGlobal.console = new Console();
			DomGlobal.window = new WebStorageWindow();
			Location location = new Location();
			location.search = "";
			setFinalStatic(DomGlobal.class.getField("location"), location);
			setFinalStatic(DomGlobal.class.getField("document"), new HTMLDocument());
			Navigator newValue = new Navigator();
			newValue.platform = "SunOS";
			newValue.userAgent = "Chrome";
			setFinalStatic(DomGlobal.class.getField("navigator"), newValue);
			Global.JSON = new JSONType() {
				@Override
				public Object parse(String s) {
					return JsPropertyMap.of();
				}
			};
			DomGlobal.document.documentElement = new HTMLHtmlElement();
			DomGlobal.document.body = new HTMLBodyElement();
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Failed to set up elemental2 mocks", e);
		}
	}

	private static void setFinalStatic(Field field, Object newValue) throws IllegalAccessException {
		field.set(null, newValue);
	}
}
