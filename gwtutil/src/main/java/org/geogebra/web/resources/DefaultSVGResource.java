package org.geogebra.web.resources;

import org.geogebra.gwtutil.DOMParser;
import org.geogebra.gwtutil.XMLSerializer;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.Document;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

/**
 * Implementation of SVGResource.
 */
public class DefaultSVGResource implements SVGResource {

	private static DOMParser parser;

	private static XMLSerializer serializer;

	static {
		parser = new DOMParser();
		serializer = new XMLSerializer();
	}

	private String svg;
	private String name;

	/**
	 * Creates a new SVG Resource.
	 *
	 * @param svg content
	 * @param name name
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

	private String createFilled(String color) {
		Document doc = parser.parseFromString(svg, "image/svg+xml");
		CSSStyleDeclaration style = Js.uncheckedCast(Js.asPropertyMap(doc)
				.nestedGet("rootElement.style"));
		style.setProperty("fill", color);
		return serializer.serializeToString(doc);
	}

	@Override
	public String getUrl() {
		return "data:image/svg+xml;base64," + DomGlobal.btoa(svg);
	}
}
