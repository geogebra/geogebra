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

package org.geogebra.gwtutil;

import org.geogebra.web.resources.StyleInjector;
import org.gwtproject.resources.client.TextResource;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLScriptElement;
import jsinterop.base.Js;

/**
 * Injects scripts into parent document
 */
public class JavaScriptInjector {

	/**
	 * @param scriptResource
	 *            javascript file
	 */
	public static void inject(TextResource scriptResource) {
		inject(scriptResource, false, false);
	}

	/**
	 * @param scriptResource
	 *            javascript file
	 * @param noDefine
	 *            whether to protect against using requirejs (see APPS-3018)
	 */
	public static void inject(TextResource scriptResource, boolean noDefine, boolean asModule) {
		String name = scriptResource.getName();
		if (DomGlobal.document.getElementById(name) == null) {
			HTMLScriptElement element = createScriptElement(name);
			if (asModule) {
				element.type = "module";
			}
			if (noDefine) {
				element.text = "(function(define){" + scriptResource.getText() + "})()";
			} else {
				element.text = scriptResource.getText();
			}
			DomGlobal.document.head.appendChild(element);
		}
	}

	private static HTMLScriptElement createScriptElement(String id) {
		HTMLScriptElement script = Js.uncheckedCast(DomGlobal.document
				.createElement("script"));
		script.id = id;
		script.className = StyleInjector.CLASSNAME;
		return script;
	}

	/**
	 * @param js
	 *            to inject
	 * @return whether the js injected already, or not. This check is made during
	 *         injection, but can be useful.
	 */
	public static boolean injected(TextResource js) {
		return DomGlobal.document.getElementById(js.getName()) != null;
	}

	/**
	 * @param url
	 *            script url
	 * @param handler
	 *            if script loaded, calls the callback that implements interface
	 *            ScriptLoadHandler
	 */
	public static void loadJS(String url, ScriptLoadCallback handler) {
		Element script = DomGlobal.document.createElement("script");
		script.setAttribute("src", url);
		if (handler != null) {
			addLoadHandler(script, handler);
		}
		DomGlobal.document.body.appendChild(script);
	}

	private static void addLoadHandler(Element el, ScriptLoadCallback handler) {
		el.addEventListener("load", (evt) -> handler.onLoad());
		el.addEventListener("error", (evt) -> handler.onError());
	}
}
