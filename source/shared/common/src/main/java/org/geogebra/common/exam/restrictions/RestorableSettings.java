package org.geogebra.common.exam.restrictions;

import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.main.settings.Settings;

/**
 * Interface to save and restore settings.
 */
public interface RestorableSettings {
	/**
	 * Save settings that needs to be restored later (for example, after finishing an exam).
	 * @param settings {@link Settings}
	 * @param defaults default styles for construction elements
	 */
	void save(Settings settings, ConstructionDefaults defaults);

	/**
	 * Restore the previously saved settings.
	 * @param settings {@link Settings}
	 * @param defaults default styles for construction elements
	 */
	void restore(Settings settings, ConstructionDefaults defaults);
}
