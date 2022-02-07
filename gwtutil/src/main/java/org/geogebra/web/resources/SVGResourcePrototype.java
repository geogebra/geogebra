package org.geogebra.web.resources;

import org.geogebra.gwtutil.DOMParser;
import org.geogebra.gwtutil.XMLSerializer;
import org.gwtproject.safehtml.shared.SafeUri;
import org.gwtproject.safehtml.shared.UriUtils;

import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.Document;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

/**
 * Implementation of SVGResource.
 */
public class SVGResourcePrototype implements SVGResource {

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
	public SVGResourcePrototype(String name, String svg) {
		this.name = name;
		this.svg = svg;
	}

	@Override
	public SafeUri getSafeUri() {
		return UriUtils.fromTrustedString(getUrl());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public SVGResource withFill(String color) {
		String filled = createFilled(color);
		return new SVGResourcePrototype(name, filled);
	}

	private String createFilled(String color) {
		Document doc = parser.parseFromString(svg, "image/svg+xml");
		CSSStyleDeclaration style = Js.uncheckedCast(Js.asPropertyMap(doc)
				.nestedGet("rootElement.style"));
		style.setProperty("fill", color);
		return serializer.serializeToString(doc);
	}

	public String getUrl() {
		return "data:image/svg+xml;base64," + DomGlobal.btoa(svg);
	}
}
