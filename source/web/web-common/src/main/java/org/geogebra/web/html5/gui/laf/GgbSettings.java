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

/**
 * Ggb specific settings
 */
public class GgbSettings implements VendorSettings {

	@Override
	public String getAppTitle(AppConfig config) {
		return config.getAppTitle();
	}

	@Override
	public FullScreenHandler getFullscreenHandler() {
		return null;
	}

	@Override
	public GColor getPrimaryColor() {
		return GeoGebraColorConstants.GEOGEBRA_ACCENT;
	}

	@Override
	public String getMenuLocalizationKey(String key) {
		return key;
	}

	@Override
	public String getStyleName(String styleName) {
		return styleName;
	}

	@Override
	public List<FontFamily> getTextToolFonts() {
		return Arrays.asList(FontFamily.ARIAL,
				FontFamily.CALIBRI,
				FontFamily.CENTURY_GOTHIC,
				FontFamily.COMIC_SANS,
				FontFamily.COURIER,
				FontFamily.GEORGIA,
				FontFamily.ROBOTO,
				FontFamily.SF_MONO,
				FontFamily.SF_PRO,
				FontFamily.TIMES,
				FontFamily.TREBUCHET,
				FontFamily.VERDANA);
	}

	@Override
	public boolean canSessionExpire() {
		return false;
	}

	@Override
	public boolean hasBitmapSecurity() {
		return false;
	}

	@Override
	public List<Integer> getProtractorTools(Language language) {
		return "en".equals(language.language)
				? Collections.singletonList(EuclidianConstants.MODE_PROTRACTOR)
				: Arrays.asList(EuclidianConstants.MODE_PROTRACTOR,
					EuclidianConstants.MODE_TRIANGLE_PROTRACTOR);
	}
}
