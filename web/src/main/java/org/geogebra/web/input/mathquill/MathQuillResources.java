package org.geogebra.web.input.mathquill;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface MathQuillResources extends ClientBundle {

	MathQuillResources INSTANCE = GWT.create(MathQuillResources.class);

	@Source("org/geogebra/web/input/mathquill/mathquill-customized.css")
	TextResource mathquillcss();

	@Source("org/geogebra/web/input/mathquill/mathquill.js")
	TextResource mathquilljs();

	@Source("org/geogebra/web/input/mathquill/jquery-1.4.4.js")
	TextResource jqueryjs();

}
