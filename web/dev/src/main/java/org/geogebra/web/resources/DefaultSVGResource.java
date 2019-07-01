package org.geogebra.web.resources;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

/**
 * Implementation of SVGResource.
 */
public class DefaultSVGResource implements SVGResource {

	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	private static JavaScriptObject parser;

	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	private static JavaScriptObject serializer;

	static {
		parser = createParser();
		serializer = createSerializer();
	}

	private static native JavaScriptObject createParser() /*-{
		return new DOMParser();
	}-*/;

	private static native JavaScriptObject createSerializer() /*-{
		return new XMLSerializer();
	}-*/;


	private String svg;
	private String name;

	/**
	 * Creates a new SVG Resource.
	 *
	 * @param svg
	 * @param name
	 */
	public DefaultSVGResource(String svg, String name) {
		this.svg = svg;
		this.name = name;
	}

	@Override
	public SafeUri getSafeUri() {
		return UriUtils.fromSafeConstant(getUrl());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public SVGResource withFill(String color) {
		String filled = createFilled(color);
		return new DefaultSVGResource(filled, name);
	}

	private native String createFilled(String color) /*-{
	    var that = this;
		var parser = @org.geogebra.web.resources.DefaultSVGResource::parser;
		var serializer = @org.geogebra.web.resources.DefaultSVGResource::serializer;
		var svg = that.@org.geogebra.web.resources.DefaultSVGResource::svg;
		var doc = parser.parseFromString(svg, "image/svg+xml");
		doc.rootElement.style.fill = color;
		var xml = serializer.serializeToString(doc);
		return xml;
	}-*/;

	@Override
	public String getUrl() {
		return "data:image/svg+xml;base64," +
				Base64Encoder.encodeBase64(svg);
	}
}
