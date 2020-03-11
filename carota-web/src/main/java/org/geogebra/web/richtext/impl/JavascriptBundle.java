package org.geogebra.web.richtext.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Bundle containing the Carota javascript.
 */
interface JavascriptBundle extends ClientBundle {

	JavascriptBundle INSTANCE = GWT.create(JavascriptBundle.class);

	@Source("org/geogebra/web/richtext/js/carota.min.js")
	TextResource carotaJs();
}
