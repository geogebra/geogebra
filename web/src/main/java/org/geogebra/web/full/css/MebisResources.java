package org.geogebra.web.full.css;

import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.resources.SassResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

/**
 * Resources related to the mebis Notes app.
 */
public interface MebisResources extends ClientBundle, StylesProvider, ResourceIconProvider {

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

	@Override
	@Source("org/geogebra/web/resources/scss/mebis/component-styles.scss")
	SassResource componentStyles();

	@Override
	@Source("org/geogebra/web/resources/scss/mebis/settings-styles.scss")
	SassResource settingsStyleScss();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/file_plus.svg")
	SVGResource newFileMenu();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/folder-open.svg")
	SVGResource openFileMenu();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/file.svg")
	SVGResource fileMenu();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/arrow-alt-circle-down.svg")
	SVGResource downloadMenu();

}
