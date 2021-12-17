package org.geogebra.web.resources;

import org.gwtproject.resources.client.TextResource;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.DOM;

/**
 * Injects scripts into parent document
 */
public class JavaScriptInjector {
	private static HeadElement head;

	/**
	 * @param scriptResource
	 *            javascript file
	 */
	public static void inject(TextResource scriptResource) {
		inject(scriptResource.getName(), scriptResource.getText());
	}

	/**
	 * @param name unique name of the script element
	 * @param text script content
	 */
	public static void inject(String name, String text) {
		if (DOM.getElementById(name) == null) {
			ScriptElement element = createScriptElement(name);
			element.setText(text);
			getHead().appendChild(element);
		}
	}

	private static ScriptElement createScriptElement(String id) {
		ScriptElement script = Document.get().createScriptElement();
		script.setAttribute("id", id);
		script.setClassName(StyleInjector.CLASSNAME);
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
