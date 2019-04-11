package org.geogebra.common.main.settings.updater;

import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.DefaultSettings;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.main.settings.Settings;

public class SettingsUpdater {

	private DefaultSettings defaultSettings;
	private Settings settings;
	private AppConfig appConfig;
	private Kernel kernel;
	private FontSettingsUpdater fontSettingsUpdater;
	private LabelSettingsUpdater labelSettingsUpdater;

	SettingsUpdater() {}

	private void resetSettingsOnAppStart() {
		resetSettingsOnlyOnAppStart();
		resetSettingsAfterClearAll();
	}

	public void resetSettingsOnlyOnAppStart() {
		kernel.setPrintDecimals(appConfig.getDefaultPrintDecimals());
		labelSettingsUpdater.setLabelVisibility(LabelVisibility.PointsOnly);
	}

	public void resetSettingsAfterClearAll() {
		fontSettingsUpdater.setAppFontSizeAndUpdateViews(defaultSettings.getAppFontSize());
		setSortModeForCompactOutput();
	}

	private void setSortModeForCompactOutput() {
		settings.getAlgebra().setTreeMode(AlgebraView.SortMode.ORDER);
	}

	void setDefaultSettings(DefaultSettings defaultSettings) {
		this.defaultSettings = defaultSettings;
	}

	void setSettings(Settings settings) {
		this.settings = settings;
	}

	void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}

	void setFontSettingsUpdater(FontSettingsUpdater fontSettingsUpdater) {
		this.fontSettingsUpdater = fontSettingsUpdater;
	}

	public FontSettingsUpdater getFontSettingsUpdater() {
		return fontSettingsUpdater;
	}

	void setLabelSettingsUpdater(LabelSettingsUpdater labelSettingsUpdater) {
		this.labelSettingsUpdater = labelSettingsUpdater;
	}

	public LabelSettingsUpdater getLabelSettingsUpdater() {
		return labelSettingsUpdater;
	}
}
