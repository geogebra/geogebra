package org.geogebra.gwtutil;

import org.geogebra.web.resources.StyleInjector;
import org.gwtproject.resources.client.TextResource;

import elemental2.dom.DomGlobal;
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
		inject(scriptResource, false);
	}

	/**
	 * @param scriptResource
	 *            javascript file
	 * @param noDefine
	 *            whether to protect against using requirejs (see APPS-3018)
	 */
	public static void inject(TextResource scriptResource, boolean noDefine) {
		String name = scriptResource.getName();
		if (DomGlobal.document.getElementById(name) == null) {
			HTMLScriptElement element = createScriptElement(name);
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
	 * @return wheter the js injected already, or not. This check is made during
	 *         injection, but can be useful.
	 */
	public static boolean injected(TextResource js) {
		return DomGlobal.document.getElementById(js.getName()) != null;
	}
}
