package org.geogebra.web.html5.cas.giac;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface CASResources extends ClientBundle {

	/**
	 * maybe it's better if INSTANCE is created later?
	 */
	CASResources INSTANCE = GWT.create(CASResources.class);

	@Source("org/geogebra/web/resources/js/giac.js")
	TextResource giacJs();
}
