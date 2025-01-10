package org.geogebra.common.main.settings.updater;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.FontSettings;
import org.geogebra.common.util.Util;

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
		if (fontSize == fontSettings.getAppFontSize()) {
			return;
		}
		fontSettings.setAppFontSize(Util.getValidFontSize(fontSize));
	}

	/**
	 * Sets the app font size and updates the views.
	 * @param fontSize font size
	 */
	public void setAppFontSizeAndUpdateViews(int fontSize) {
		setAppFontSize(fontSize);
		updateEuclidianViews();
		resetFonts();
		app.updateUI();
	}

	private void updateEuclidianViews() {
		EuclidianView ev1 = app.getEuclidianView1();
		if (ev1 != null && ev1.hasStyleBar()) {
			ev1.getStyleBar().reinit();
		}

		if (app.hasEuclidianView2(1)) {
			EuclidianView ev2 = app.getEuclidianView2(1);
			if (ev2 != null && ev2.hasStyleBar()) {
				ev2.getStyleBar().reinit();
			}
		}

		if (app.isEuclidianView3Dinited() && app.getEuclidianView3D().hasStyleBar()) {
			app.getEuclidianView3D().getStyleBar().reinit();
		}
	}

	/**
	 * Update font sizes of all components to match current GUI font size
	 */
	public void resetFonts() {
		app.getFontManager().setFontSize(getGUIFontSize());
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
		int guiFontSize = fontSettings.getGuiFontSize();
		return guiFontSize == -1 ? fontSettings.getAppFontSize() : guiFontSize;
	}

	protected FontSettings getFontSettings() {
		return fontSettings;
	}

	protected App getApp() {
		return app;
	}
}
