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

import elemental2.dom.DomGlobal;

/**
 * Mebis specific settings
 */
public class MebisSettings implements VendorSettings {

	private static final String MEBIS_LICENSE_PATH = "/static/license.html?";

	@Override
	public String getLicenseURL() {
		if (!DomGlobal.location.protocol.startsWith("http")) {
			return "https://tafel.mebis.bayern.de" + MEBIS_LICENSE_PATH;
		}
		return MEBIS_LICENSE_PATH;
	}

	@Override
	public String getAppTitle(AppConfig config) {
		return "Tafel";
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
