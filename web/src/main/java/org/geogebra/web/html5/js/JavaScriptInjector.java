package org.geogebra.web.html5.js;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.DOM;

public class JavaScriptInjector {
	private static HeadElement head;

	public static void inject(TextResource scriptResource) {
		if (DOM.getElementById(scriptResource.getName()) == null) {
			ScriptElement element = createScriptElement(scriptResource
			        .getName());
			element.setText(scriptResource.getText());
			getHead().appendChild(element);
		}
	}

	private static ScriptElement createScriptElement(String id) {
		ScriptElement script = Document.get().createScriptElement();
		script.setAttribute("language", "javascript");
		script.setAttribute("id", id);
		script.setClassName(ResourcesInjector.CLASSNAME);
		return script;
	}

	private static HeadElement getHead() {
		if (JavaScriptInjector.head == null) {
			Element element = Document.get().getElementsByTagName("head")
			        .getItem(0);
			assert element != null : "HTML Head element required";
			JavaScriptInjector.head = HeadElement.as(element);
		}
		return JavaScriptInjector.head;
	}

	/**
	 * @param js
	 *            to inject
	 * @return wheter the js injected already, or not. This check is made during
	 *         injection, but can be useful.
	 */
	public static boolean injected(TextResource js) {
		return DOM.getElementById(js.getName()) != null;
	}
}
