package org.geogebra.web.full.css;

import org.geogebra.web.resources.SassResource;

/**
 * Class providing style resources.
 */
public interface StylesProvider {

	/**
	 * @return mowStyle resource
	 */
	SassResource mowStyle();

	/**
	 * @return mowToolbarStyle resource
	 */
	SassResource mowToolbarStyle();

	/**
	 * @return openScreenStyle resource
	 */
	SassResource openScreenStyle();

	/**
	 * @return dialogStylesScss resource
	 */
	SassResource dialogStylesScss();

	/**
	 * @return settingsStyles resources
	 */
	SassResource settingsStyleScss();

	/**
	 * @return componentStyles resources
	 */
	SassResource componentStyles();
}
