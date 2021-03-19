package org.geogebra.web.html5.js;

import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.ScriptLoadCallback;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ScriptElement;

/**
 * @author gabor
 *
 *         injects the javascript resources
 *
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
		// always need English properties available, eg Function.sin
		fixComputedStyle();

		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.clipboardJs());

		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.fflateJs());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.base64Js());

		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.visibilityJs());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.domvas());

		StyleInjector.inject(GuiResourcesSimple.INSTANCE.reset());
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.modernStyleGlobal());

		injectScss();
		injectResourcesGUI(ae);

		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.xmlUtil());
	}

	private void setResourcesInjected() { // extracted to make SpotBugs happy
		resourcesInjected = true;
	}

	/** Works around https://bugzilla.mozilla.org/show_bug.cgi?id=548397 */
	private static native void fixComputedStyle() /*-{
		var oldCS = $wnd.getComputedStyle;
		$wnd.getComputedStyle = function(el) {
			return oldCS(el) || el.style;
		}
	}-*/;

	/**
	 * Inject resources for GUI, overridden in ReTeX injector (to add JQuery +
	 * JqueryUI for sliders)
	 *
	 * @param ae article element
	 */
	protected void injectResourcesGUI(AppletParameters ae) {
		// overridden elsewhere
	}

	private static void injectScss() {
		StyleInjector
				.inject(GuiResourcesSimple.INSTANCE.colorsScss());
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.layoutScss());
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.evStyleScss());
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.sharedStyleScss());
	}

	/**
	 * removes the added resources
	 */
	public static void removeResources() {
		resourcesInjected = false;
		// this list is live
		NodeList<Element> resources = Dom
				.getElementsByClassName(StyleInjector.CLASSNAME);
		while (resources.getLength() > 0) {
			resources.getItem(resources.getLength() - 1).removeFromParent();
		}

		// this is not :-) Love DOM!
		NodeList<Element> scripts = Dom
		        .querySelectorAll("script[src$=\"cache.js\"]");
		for (int i = 0; i < scripts.getLength(); i++) {
			scripts.getItem(i).removeFromParent();
		}
	}

	private static native void addLoadHandler(ScriptElement el,
			ScriptLoadCallback handler) /*-{
		el
				.addEventListener(
						"load",
						function() {
							handler.@org.geogebra.web.html5.util.ScriptLoadCallback::onLoad()();
						}, false);
		el
				.addEventListener(
						"error",
						function() {
							handler.@org.geogebra.web.html5.util.ScriptLoadCallback::onError()();
						}, false);
	}-*/;

	/**
	 * @param el
	 *            script element
	 * @param handler
	 *            if script loaded, calls the callback that implements interface
	 *            ScriptLoadHandler
	 */
	public static void loadJS(ScriptElement el, ScriptLoadCallback handler) {
		addLoadHandler(el, handler);
		Document.get().getBody().appendChild(el);
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
