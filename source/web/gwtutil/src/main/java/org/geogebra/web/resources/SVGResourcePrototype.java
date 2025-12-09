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

	public static final SVGResource EMPTY = new SVGResourcePrototype("empty",
			"<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1\" height=\"1\"/>");
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
		String filled = createFilled(color, svg);
		return new SVGResourcePrototype(name, filled);
	}

	/**
	 * @param color CSS color
	 * @param svg SVG content
	 * @return filled SVG
	 */
	public static String createFilled(String color, String svg) {
		Document doc = parser.parseFromString(svg, "image/svg+xml");
		CSSStyleDeclaration style = Js.uncheckedCast(Js.asPropertyMap(doc)
				.nestedGet("rootElement.style"));
		if (style != null) {
			style.setProperty("fill", color);
		}
		return serializer.serializeToString(doc);
	}

	@Override
	public String getSVG() {
		return svg;
	}

	public String getUrl() {
		return "data:image/svg+xml;base64," + DomGlobal.btoa(svg);
	}
}
