package org.geogebra.web.cas.latex;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface MathQuillResources extends ClientBundle {

	MathQuillResources INSTANCE = GWT.create(MathQuillResources.class);

	@Source("org/geogebra/web/resources/js/mathquillggb.js")
	TextResource mathquillggbJs();

	@Source("org/geogebra/web/resources/css/mathquillggb.css")
	TextResource mathquillggbCss();
}
