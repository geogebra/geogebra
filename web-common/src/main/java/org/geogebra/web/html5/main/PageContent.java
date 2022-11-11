package org.geogebra.web.html5.main;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class PageContent {
	public String xml;
	public String[] objects;
	public String thumbnail;
	public String title;
	public int order;

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
