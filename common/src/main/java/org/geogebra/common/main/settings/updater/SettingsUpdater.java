package org.geogebra.common.main.settings.updater;

import org.geogebra.common.euclidian.EuclidianHost;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.main.settings.DefaultSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.main.settings.Settings;

/**
 * Updates the settings.
 * Every complex (longer than 1 line) logic related to a combination of settings
 * should be implemented in this class.
 */
public class SettingsUpdater {

	private EuclidianHost euclidianHost;
	private DefaultSettings defaultSettings;
	private Settings settings;
	private AppConfig appConfig;
	private Kernel kernel;
	private FontSettingsUpdater fontSettingsUpdater;
	private LabelSettingsUpdater labelSettingsUpdater;

	/**
	 * Resets the settings which should be reset on app start and after Clear All.
	 */
	public void resetSettingsOnAppStart() {
		resetSettingsOnlyOnAppStart();
		resetSettingsAfterClearAll();
	}

	protected void resetSettingsOnlyOnAppStart() {
		kernel.setPrintDecimals(appConfig.getDefaultPrintDecimals());
		labelSettingsUpdater.setLabelVisibility(LabelVisibility.PointsOnly);
		settings.getAlgebra().setStyle(AlgebraStyle.DefinitionAndValue);
	}

	/**
	 * Resets the settings which should be reset after Clear All.
	 */
	public void resetSettingsAfterClearAll() {
		fontSettingsUpdater.setAppFontSizeAndUpdateViews(defaultSettings.getAppFontSize());
		setSortModeForCompactOutput();
		setEuclidianSettings();
	}

	private void setSortModeForCompactOutput() {
		settings.getAlgebra().setTreeMode(AlgebraView.SortMode.ORDER);
	}

	private void setEuclidianSettings() {
		EuclidianSettings euclidianSettings = euclidianHost.getActiveEuclidianView().getSettings();
		euclidianSettings.showGrid(appConfig.showGridOnFileNew());
		euclidianSettings.setShowAxes(appConfig.showAxesOnFileNew());
	}

	void setEuclidianHost(EuclidianHost euclidianHost) {
		this.euclidianHost = euclidianHost;
	}

	void setDefaultSettings(DefaultSettings defaultSettings) {
		this.defaultSettings = defaultSettings;
	}

	protected Settings getSettings() {
		return settings;
	}

	void setSettings(Settings settings) {
		this.settings = settings;
	}

	void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	protected Kernel getKernel() {
		return kernel;
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
