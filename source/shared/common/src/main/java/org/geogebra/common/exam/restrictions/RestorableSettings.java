/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
