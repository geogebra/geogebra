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


import org.geogebra.web.generator.SVGResourceGenerator;

import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.ext.DefaultExtensions;
import com.google.gwt.resources.ext.ResourceGeneratorType;
//import org.vectomatic.dom.svg.OMSVGSVGElement;
import com.google.gwt.safehtml.shared.SafeUri;

/**
 * A resource that contains SVG that should be incorporated into the compiled output. 
 * Note that by default SVG resources are validated against the SVG 1.1 XSD schema.
 * You can opt out of validation by setting the <code>validated="false"</code>
 * attribute on the annotation.
 * @author laaglu
 */
@DefaultExtensions(value = {".svg"})
@ResourceGeneratorType(SVGResourceGenerator.class)
public interface SVGResource extends DataResource {
	//this is a hacked version of SVGResource, alll fancy stuff is gone
	/**
	 * Override this explicitly for dev mode to work properly
	 */
	SafeUri getSafeUri();

	/**
	 * Copies this resource and sets the fill color.
	 *
	 * @param color color
	 */
	SVGResource withFill(String color);
}

