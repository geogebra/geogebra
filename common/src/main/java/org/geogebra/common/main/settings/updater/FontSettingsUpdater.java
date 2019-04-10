package org.geogebra.common.main.settings.updater;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.FontSettings;
import org.geogebra.common.util.Util;

public class FontSettingsUpdater {

	private App app;
	private FontSettings fontSettings;

	FontSettingsUpdater(App app) {
		this.app = app;
		fontSettings = app.getSettings().getFontSettings();
	}

	/**
	 * Changes font size and possibly resets fonts
	 *
	 * @param points
	 *            font size
	 * @see #resetFonts()
	 */
	public void setAppFontSize(int points) {
		if (points == fontSettings.getAppFontSize()) {
			return;
		}
		fontSettings.setAppFontSize(Util.getValidFontSize(points));
	}

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
	private void resetFonts() {
		setAppFontSize(getGUIFontSize());
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
	}

	public void resetGuiFontSize() {
		fontSettings.resetGuiFontSize();
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
}
