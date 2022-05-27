package org.geogebra.web.html5.js;

import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.ScriptLoadCallback;
import org.geogebra.web.resources.StyleInjector;

import com.google.gwt.core.client.GWT;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.NodeList;

/**
 * Injects the javascript and css resources needed in WebSimple
 */
public class ResourcesInjector {

	private static boolean resourcesInjected = false;

	/**
	 * Inject all JS/CSS resources
	 * @param ae article element
	 */
	public void injectResources(AppletParameters ae) {
		if (resourcesInjected) {
			return;
		}
		setResourcesInjected();

		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.clipboardJs());

		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.fflateJs(), true);
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.base64Js());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.domvas());

		injectResourcesGUI(ae);

		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.xmlUtil());
	}

	private void setResourcesInjected() { // extracted to make SpotBugs happy
		resourcesInjected = true;
	}

	/**
	 * Inject resources for GUI, such as CSS, english properties, and fonts (only in full)
	 * @param parameters applet parameters (for determining vendor)
	 */
	protected void injectResourcesGUI(AppletParameters parameters) {
		new StyleInjector(GWT.getModuleBaseURL())
				.inject("css/bundles", "simple-bundle");
	}

	/**
	 * removes the added resources
	 */
	public static void removeResources() {
		resourcesInjected = false;
		NodeList<Element> resources = DomGlobal.document
				.querySelectorAll("." + StyleInjector.CLASSNAME);
		for (int i = 0; i < resources.getLength(); i++) {
			resources.getAt(i).remove();
		}

		NodeList<Element> scripts = DomGlobal.document
		        .querySelectorAll("script[src$=\"cache.js\"]");
		for (int i = 0; i < scripts.getLength(); i++) {
			scripts.getAt(i).remove();
		}
	}

	private static void addLoadHandler(Element el, ScriptLoadCallback handler) {
		el.addEventListener("load", (evt) -> handler.onLoad());
		el.addEventListener("error", (evt) -> handler.onError());
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

	/**
	 * Load Mathsans font if needed + additional fonts if specified by param.
	 *
	 * @param dataParamFontsCssUrl
	 *            font CSS url
	 */
	public void loadWebFont(String dataParamFontsCssUrl) {
		// intentionally
	}
}
