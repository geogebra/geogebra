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

package org.geogebra.common.main.settings;

/**
 * Listener interface for settings.
 * 
 * A setting listener attached to a setting container is notified if settings
 * were changed. This notification is issued immediately in normal mode. In
 * batch mode this notification is issued after all settings were changed.
 * 
 * @author Florian Sonner
 */
@FunctionalInterface
public interface SettingListener {
	/**
	 * Notification that settings were changed.
	 * 
	 * @param settings
	 *            Setting container.
	 */
	void settingsChanged(AbstractSettings settings);
}
