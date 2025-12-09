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

package org.geogebra.web.html5.js;

import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.util.AppletParameters;
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

		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.fflateJs(), true, false);
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
