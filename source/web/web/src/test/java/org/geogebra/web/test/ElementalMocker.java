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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.geogebra.common.util.debug.Log;
import org.mockito.Mockito;

import elemental2.core.Global;
import elemental2.core.JSONType;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.Console;
import elemental2.dom.DOMTokenList;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLBodyElement;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHtmlElement;
import elemental2.dom.HTMLImageElement;
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
			HTMLDocument document = Mockito.spy(new HTMLDocument());
			when(document.createElement(any())).thenAnswer(invocationOnMock -> {
				String tagName = invocationOnMock.getArgumentAt(0, String.class);
				HTMLElement ret = switch (tagName) {
					case "img" -> new HTMLImageElement();
					case "canvas" -> new HTMLCanvasElement();
					default -> new HTMLDivElement();
				};
				ret.style = new CSSStyleDeclaration();
				ret.classList = new DOMTokenList();
				return ret;
			});
			setFinalStatic(DomGlobal.class.getField("document"), document);
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
