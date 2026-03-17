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

package org.geogebra.common.main.settings.updater;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.FontSettings;

import com.google.j2objc.annotations.Weak;

/**
 * This class updates the font settings.
 * Every complex (longer than 1 line) logic related to font settings
 * should be implemented in this class.
 */
public class FontSettingsUpdater {

	@Weak
	private App app;
	private FontSettings fontSettings;

	/**
	 * This constructor is protected because it should be called only by the SettingsUpdaterBuilder.
	 * @param app app
	 */
	protected FontSettingsUpdater(App app) {
		this.app = app;
		fontSettings = app.getSettings().getFontSettings();
	}

	/**
	 * Changes font size and possibly resets fonts
	 *
	 * @param fontSize
	 *            font size
	 * @see #resetFonts()
	 */
	public void setAppFontSize(int fontSize) {
		fontSettings.setAppFontSizeNoFire(fontSize);
	}

	/**
	 * Sets the app font size and updates the views.
	 * @param fontSize font size
	 */
	public void setAppFontSizeAndUpdateViews(int fontSize) {
		fontSettings.setAppFontSize(fontSize);
		resetFonts();
		app.updateUI();
	}

	/**
	 * Update font sizes of all components to match current GUI font size
	 */
	public void resetFonts() {
		updateEuclidianViewFonts();
	}

	protected void updateEuclidianViewFonts() {
		EuclidianView euclidianView = app.getEuclidianView1();
		if (euclidianView != null) {
			euclidianView.updateFonts();
		}

		if (app.getGuiManager() != null) {
			app.getGuiManager().updateFonts();
			if (app.hasEuclidianView2(1)) {
				app.getEuclidianView2(1).updateFonts();
			}
		}
		if (app.getCompanion() != null) {
			app.getCompanion().updateFonts3D();
		}
	}

	/**
	 * @param size
	 *            GUI font size
	 */
	public void setGUIFontSizeAndUpdate(int size) {
		fontSettings.setGuiFontSize(size);
		resetFonts();
	}

	/**
	 * @return font size for GUI; if not specified, general font size is
	 *         returned
	 */
	public int getGUIFontSize() {
		return fontSettings.getGuiFontSizeSafe();
	}

	protected FontSettings getFontSettings() {
		return fontSettings;
	}

	protected App getApp() {
		return app;
	}
}
