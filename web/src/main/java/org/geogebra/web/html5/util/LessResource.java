/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

LessResource.java is based on SVG resource generator by Lukas Laag
 */

package org.geogebra.web.html5.util;


import org.geogebra.web.vectomatic.LessResourceGenerator;

import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.ext.DefaultExtensions;
import com.google.gwt.resources.ext.ResourceGeneratorType;
//import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * A resource for compiled LESS file
 */
@DefaultExtensions(value = { ".less" })
@ResourceGeneratorType(LessResourceGenerator.class)
public interface LessResource extends TextResource {
	//this is a hacked version of SVGResource, alll fancy stuff is gone
}
