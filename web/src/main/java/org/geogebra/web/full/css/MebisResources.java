package org.geogebra.web.full.css;

import org.geogebra.web.resources.SassResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

/**
 * Resources related to the mebis Notes app.
 */
public interface MebisResources extends ClientBundle, StylesProvider {

	/**
	 * Singleton instance
	 */
	MebisResources INSTANCE = GWT.create(MebisResources.class);

	@Override
	@Source("org/geogebra/web/resources/scss/mebis/mow.scss")
	SassResource mowStyle();

	@Override
	@Source("org/geogebra/web/resources/scss/mebis/mow-toolbar.scss")
	SassResource mowToolbarStyle();

	@Override
	@Source("org/geogebra/web/resources/scss/mebis/open-screen.scss")
	SassResource openScreenStyle();

	@Override
	@Source("org/geogebra/web/resources/scss/mebis/dialog-styles.scss")
	SassResource dialogStylesScss();
}
