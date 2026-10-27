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

package org.geogebra.common.gui.settings;

import org.jspecify.annotations.NonNull;

/**
 * A page in the settings sheet's navigation stack. Pages are created and disposed by
 * {@link SettingsViewModel}; platforms only render them.
 */
public abstract sealed class SettingsPage permits PropertiesPage, ColorPickerPage {

	SettingsPage() {
		// only subclasses in this package
	}

	/** @return the title to show in the sheet header */
	public abstract @NonNull String getTitle();

	/** Releases the page's resources. Called once when the page leaves the stack. */
	abstract void dispose();
}
