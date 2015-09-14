package org.geogebra.web.html5.js;

import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.StyleInjector;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.keyboard.KeyboardResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

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
	public static String CLASSNAME = "ggw_resource";
	private static boolean resourcesInjected = false;

	/**
	 */
	public static void injectResources() {
		if (resourcesInjected) {
			return;
		}
		resourcesInjected = true;
		// always need English properties available, eg Function.sin
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE
		        .propertiesKeysJS());

		String myModuleBase = GWT.getModuleBaseForStaticFiles();
		String mathquillggbcss = GuiResourcesSimple.INSTANCE
		        .mathquillggbCss()
		        .getText()
		        .replace("url(web/font/Symbola",
		                "url(" + myModuleBase + "font/Symbola");
		StyleInjector.inject(mathquillggbcss);

		// insert zip.js
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.zipJs());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jQueryJs());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.mathquillggbJs());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.visibilityJs());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jqueryUI());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.domvas());
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.modernStyle()
				.getText());
		StyleInjector.inject(GuiResourcesSimple.INSTANCE.generalStyleLTR()
				.getText());

		StyleInjector.inject(GuiResourcesSimple.INSTANCE.jqueryStyle()
				.getText());

		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());

		Browser.webWorkerSupported = Browser.checkWorkerSupport(GWT
		        .getModuleBaseURL());
		if (!Browser.webWorkerSupported) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.deflateJs());
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.inflateJs());
		}
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.arrayBufferJs());
		// strange, but iPad can blow it away again...
		if (Browser.checkIfFallbackSetExplicitlyInArrayBufferJs()
		        && Browser.webWorkerSupported) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.deflateJs());
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.inflateJs());
		}
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.dataViewJs());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.base64Js());
		// GIF exporting library
		// It also needs gif.worker.js
		// JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.gifJs());
		JavaScriptInjector
		        .inject(GuiResourcesSimple.INSTANCE.realsenseinfoJs());

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

	public static void injectRealSenseResources() {
		// TODO check if jquery supports promises?
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.promiseJs());
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.realsenseJs());

	}

}
