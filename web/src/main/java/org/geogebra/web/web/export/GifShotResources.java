package org.geogebra.web.web.export;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface GifShotResources extends ClientBundle {
	GifShotResources INSTANCE = GWT.create(GifShotResources.class);

	@Source("org/geogebra/web/resources/js/gifshot.image.min.js")
	TextResource gifShotJs();
}
