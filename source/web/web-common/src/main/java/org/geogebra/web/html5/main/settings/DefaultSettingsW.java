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

package org.geogebra.web.html5.main.settings;

import org.geogebra.common.main.settings.DefaultSettings;

/**
 * Default settings for web.
 */
public class DefaultSettingsW implements DefaultSettings {

	private static final int APP_FONT_SIZE = 16;
	private static final int GUI_FONT_SIZE = 14;

	@Override
	public int getAppFontSize() {
		return APP_FONT_SIZE;
	}

	@Override
	public int getGuiFontSize() {
		return GUI_FONT_SIZE;
	}
}
