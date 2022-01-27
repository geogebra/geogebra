/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of lib-gwt-svg.
 * 
 * libgwtsvg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with libgwtsvg.  If not, see http://www.gnu.org/licenses/
 **********************************************/

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
}

