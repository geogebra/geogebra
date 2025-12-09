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

import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.resources.ext.DefaultExtensions;
import org.gwtproject.resources.ext.ResourceGeneratorType;
import org.gwtproject.safehtml.shared.SafeUri;

/**
 * A resource that contains SVG that should be incorporated into the compiled output. 
 * Based on https://github.com/laaglu/lib-gwt-svg but provides a small subset of functionality.
 */
@DefaultExtensions({".svg"})
@ResourceGeneratorType("org.geogebra.web.generator.SVGResourceGenerator")
public interface SVGResource extends ResourcePrototype {

	/**
	 * Override this explicitly for dev mode to work properly
	 * @return safe URI
	 */
	SafeUri getSafeUri();

	/**
	 * Copies this resource and sets the fill color.
	 *
	 * @param color color
	 * @return transformed SVG
	 */
	SVGResource withFill(String color);

	/**
	 * @return raw SVG content
	*/
	String getSVG();
}

