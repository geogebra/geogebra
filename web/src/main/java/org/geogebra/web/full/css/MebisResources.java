package org.geogebra.web.full.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.geogebra.web.resources.SassResource;

/**
 * Resources related to the mebis Notes app.
 */
public interface MebisResources extends ClientBundle, StylesProvider {

	MebisResources INSTANCE = GWT.create(MebisResources.class);

	@ClientBundle.Source("org/geogebra/web/resources/scss/mebis/mow.scss")
	SassResource mowStyle();

	@ClientBundle.Source("org/geogebra/web/resources/scss/mebis/mow-toolbar.scss")
	SassResource mowToolbarStyle();

	@ClientBundle.Source("org/geogebra/web/resources/scss/mebis/open-screen.scss")
	SassResource openScreenStyle();

	@ClientBundle.Source("org/geogebra/web/resources/scss/mebis/dialog-styles.scss")
	SassResource dialogStylesScss();
}
