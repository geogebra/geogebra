package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.settings.AbstractSettings;

/**
 * Property that may change value when settings change.
 */
public interface SettingsDependentProperty {

	/**
	 * @return euclidian settings
	 */
	AbstractSettings getSettings();
}
