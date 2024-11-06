package org.geogebra.common.exam.restrictions;

import org.geogebra.common.main.settings.Settings;

/**
 * Interface to save and restore settings
 */
public interface RestorableSettings {
	/**
	 * Save settings here thats needs to be restored later (for examle finishing exam)
	 * @param settings {@link Settings}
	 */
	void save(Settings settings);

	/**
	 * Restore the previosly saved settings.
	 * @param settings {@link Settings}
	 */
	void restore(Settings settings);
}
