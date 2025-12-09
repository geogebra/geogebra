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

package org.geogebra.web.html5.gui.laf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.util.lang.Language;
import org.geogebra.web.html5.gui.zoompanel.FullScreenHandler;
import org.geogebra.web.html5.gui.zoompanel.MebisFullscreenHandler;

/**
 * ByCS specific settings
 */
public class ByCSSettings implements VendorSettings {

	@Override
	public String getAppTitle(AppConfig config) {
		return "Board";
	}

	@Override
	public FullScreenHandler getFullscreenHandler() {
		return new MebisFullscreenHandler();
	}

	@Override
	public GColor getPrimaryColor() {
		return GeoGebraColorConstants.MEBIS_ACCENT;
	}

	@Override
	public String getMenuLocalizationKey(String key) {
		return key + ".Mebis";
	}

	@Override
	public String getStyleName(String styleName) {
		return styleName + "Mebis";
	}

	@Override
	public List<FontFamily> getTextToolFonts() {
		return Arrays.asList(FontFamily.values());
	}

	@Override
	public boolean canSessionExpire() {
		return true;
	}

	@Override
	public boolean hasBitmapSecurity() {
		return true;
	}

	@Override
	public List<Integer> getProtractorTools(Language language) {
		return Collections.singletonList(EuclidianConstants.MODE_TRIANGLE_PROTRACTOR);
	}
}
