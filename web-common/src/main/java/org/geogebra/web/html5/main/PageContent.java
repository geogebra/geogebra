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
