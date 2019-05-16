/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

LessResource.java is based on SVG resource generator by Lukas Laag
 */

package org.geogebra.web.resources;


import org.geogebra.web.generator.SassResourceGenerator;

import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.ext.DefaultExtensions;
import com.google.gwt.resources.ext.ResourceGeneratorType;
//import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * A resource for compiled LESS file
 */
@DefaultExtensions({ ".scss" })
@ResourceGeneratorType(SassResourceGenerator.class)
public interface SassResource extends TextResource {
	//this is a hacked version of SVGResource, alll fancy stuff is gone
}
