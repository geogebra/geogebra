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

package org.geogebra.web.html5.main;

import org.geogebra.common.util.InjectJsInterop;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class PageContent {
	public @InjectJsInterop String xml;
	public @InjectJsInterop String[] objects;
	public @InjectJsInterop String thumbnail;
	public @InjectJsInterop String title;
	public @InjectJsInterop int order;

	/**
	 * @param xml construction XML
	 * @param allObjectNames object names
	 * @param thumbnail thumbnail
	 * @return page content
	 */
	@JsOverlay
	public static PageContent of(String xml, String[] allObjectNames, String thumbnail,
			String title, int order) {
		PageContent content = new PageContent();
		content.xml = xml;
		content.objects = allObjectNames;
		content.thumbnail = thumbnail;
		content.title = title;
		content.order = order;
		return content;
	}
}
