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

