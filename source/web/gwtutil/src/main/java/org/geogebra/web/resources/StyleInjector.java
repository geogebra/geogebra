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

package org.geogebra.web.resources;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.GeoGebraConstants;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLLinkElement;
import elemental2.dom.HTMLStyleElement;

/**
 * Injects stylesheets to the parent document.
 * Based on {@code com.google.gwt.client.StyleInjector}.
 */
public class StyleInjector {

	public static final String CLASSNAME = "ggw_resource";

	private static final List<String> stylesInLoading = new ArrayList<>();
	private static final List<Runnable> onStylesReady = new ArrayList<>();
	private final String moduleBaseUrl;

	public StyleInjector(String moduleBaseURL) {
		this.moduleBaseUrl = normalizeUrl(moduleBaseURL);
	}

	/**
	 * @param baseUrl (relative or absolute) base url of css file
	 * @param name name of the css file, without extension
	 * @return this for chaining
	 */
	public StyleInjector inject(String baseUrl, String name) {
		// to avoid conflicts with other elements on the page with this id
		String prefixedName = "ggbstyle_" + name;
		if (DomGlobal.document.getElementById(prefixedName) == null) {
			HTMLLinkElement element
					= (HTMLLinkElement) DomGlobal.document.createElement("link");

			stylesInLoading.add(name);
			element.onload = (e) -> {
				stylesInLoading.remove(name);
				checkIfAllStylesLoaded();
			};

			element.className = CLASSNAME;
			element.id = prefixedName;
			element.rel = "stylesheet";
			element.type = "text/css";
			element.href = moduleBaseUrl + "../" + baseUrl + "/" + name + ".css";
			DomGlobal.document.head.appendChild(element);
		}
		return this;
	}

	/**
	 * when localhost:8888/dev is used as codebase, the styles are one level above <br>
	 * also, when using /apps/latest as codebase, make sure to load the styles by using the
	 * (unique) ggb version
	 * @param moduleBaseURL codebase
	 * @return canonical codebase
	 */
	public static String normalizeUrl(String moduleBaseURL) {
		return moduleBaseURL.replace(":8888/dev", ":8888")
				.replace("geogebra.org/apps/latest",
						"geogebra.org/apps/" + GeoGebraConstants.VERSION_STRING);
	}

	private static void checkIfAllStylesLoaded() {
		if (stylesInLoading.isEmpty()) {
			for (Runnable r : onStylesReady) {
				r.run();
			}
			onStylesReady.clear();
		}
	}

	/**
	 * @param style stylesheet content
	 * @return HTML style element
	 */
	public static HTMLStyleElement injectStyleSheet(String style) {
		HTMLStyleElement element
				= (HTMLStyleElement) DomGlobal.document.createElement("style");
		element.className = CLASSNAME;
		element.innerHTML = style;
		return element;
	}

	/**
	 * @param runnable callback to run after all styles are loaded
	 */
	public static void onStylesLoaded(Runnable runnable) {
		onStylesReady.add(runnable);
		checkIfAllStylesLoaded();
	}
}
