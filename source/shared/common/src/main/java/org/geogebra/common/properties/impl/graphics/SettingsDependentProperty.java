package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.properties.Property;

/**
 * Property that may change value when settings change.
 */
public interface SettingsDependentProperty extends Property {

	/**
	 * @return the settings object which this property is dependent on.
	 */
	AbstractSettings getSettings();
}
