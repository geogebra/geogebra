package org.geogebra.web.cas.giac;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * ClientBundle for old giac.js. Remove when we drop support for IE11.
 */
public interface GiacJsResources extends ClientBundle {

	GiacJsResources INSTANCE = GWT.create(GiacJsResources.class);

	/** @return giac.js */
	@Source("org/geogebra/web/resources/js/giac.js")
	TextResource giacJs();
}
