package org.geogebra.common.exam.restrictions;

import org.geogebra.common.main.settings.Settings;

/**
 * Interface to save and restore settings.
 */
public interface RestorableSettings {
	/**
	 * Save settings that needs to be restored later (for example, after finishing an exam).
	 * @param settings {@link Settings}
	 */
	void save(Settings settings);

	/**
	 * Restore the previously saved settings.
	 * @param settings {@link Settings}
	 */
	void restore(Settings settings);
}
