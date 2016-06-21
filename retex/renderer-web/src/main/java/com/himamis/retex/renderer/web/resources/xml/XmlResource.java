package com.himamis.retex.renderer.web.resources.xml;

import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.ext.DefaultExtensions;
import com.google.gwt.resources.ext.ResourceGeneratorType;
import com.himamis.retex.generator.XmlResourceGenerator;;
/**
 * Dummy resource to force recompile
 *
 */
@DefaultExtensions(value = { ".xml" })
@ResourceGeneratorType(XmlResourceGenerator.class)
public interface XmlResource extends TextResource {
	// just a text resource with a different generator
}
