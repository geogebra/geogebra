/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

LessReference.java is based on SVG resource generator by Lukas Laag
 */
package org.geogebra.web.resources;


import org.geogebra.web.generator.LessReferenceGenerator;

import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.ext.DefaultExtensions;
import com.google.gwt.resources.ext.ResourceGeneratorType;

/**
 * Dummy resource to force recompile
 *
 */
@DefaultExtensions(value = { ".less" })
@ResourceGeneratorType(LessReferenceGenerator.class)
public interface LessReference extends TextResource {
	//this is a hacked version of SVGResource, alll fancy stuff is gone
}
