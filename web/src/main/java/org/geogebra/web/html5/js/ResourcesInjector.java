package org.geogebra.web.html5.js;

import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.StyleInjector;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Window.Location;

/**
 * @author gabor
 * 
 *         injects the javascript resources
 *
 */
public class ResourcesInjector {

	/**
	 * resource class name
	 */
	public static final String CLASSNAME = "ggw_resource";
	private static boolean resourcesInjected = false;
	private static ResourcesInjector INSTANCE;

	/**
	 * @param forceReTeX
	 *            whether to force retex only style (=exclude MQ)
	 * 
	 */
	public static void injectResources() {
		if (resourcesInjected) {
			return;
		}
		resourcesInjected = true;
		// always need English properties available, eg Function.sin
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE
		        .propertiesKeysJS());



		// insert zip.js
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.zipJs());

		if (INSTANCE == null) {
			INSTANCE = (ResourcesInjector) GWT.create(ResourcesInjector.class);
		}
		INSTANCE.injectResourcesGUI();

		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.visibilityJs());

		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.domvas());
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.modernStyle()
				.getText());
		injectLTRstyles();


		Browser.setWebWorkerSupported(Location
				.getParameter("GeoGebraDebug") == null
				&& Browser.checkWorkerSupport(GWT
						.getModuleBaseURL()));
		if (!Browser.webWorkerSupported()) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.deflateJs());
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.inflateJs());
		}
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.arrayBufferJs());
		// strange, but iPad can blow it away again...
		if (Browser.checkIfFallbackSetExplicitlyInArrayBufferJs()
				&& Browser.webWorkerSupported()) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.deflateJs());
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.inflateJs());
		}
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.dataViewJs());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.base64Js());

	}

	/**
	 * Inject resources for GUI, overridden in ReTeX injector (to add JQuery +
	 * JqueryUI for sliders)
	 * 
	 */
	protected void injectResourcesGUI() {
		// TODO Auto-generated method stub

	}

	private static void injectLTRstyles() {
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.generalStyleLTR()
				.getText());
		StyleInjector
				.inject(GuiResourcesSimple.INSTANCE.avStyleLTR().getText());

	}

	/**
	 * removes the added resources
	 */
	public static void removeResources() {
		resourcesInjected = false;
		// this list is live
		NodeList<Element> resources = Dom.getElementsByClassName(CLASSNAME);
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

}
