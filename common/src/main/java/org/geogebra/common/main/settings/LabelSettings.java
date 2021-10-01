package org.geogebra.common.main.settings;

/**
 * Label settings.
 */
public class LabelSettings extends AbstractSettings {

	private LabelVisibility labelVisibility;
	private LabelVisibility labelVisibilityForMenu;

	/**
	 * This constructor is protected because it should be called only by the SettingsBuilder.
	 */
	LabelSettings() {
		initLabelVisibility();
	}

	private void initLabelVisibility() {
		labelVisibility = LabelVisibility.PointsOnly;
		labelVisibilityForMenu = LabelVisibility.NotSet;
	}

	public LabelVisibility getLabelVisibility() {
		return labelVisibility;
	}

	public LabelVisibility getLabelVisibilityForMenu() {
		return labelVisibilityForMenu;
	}

	/**
	 * Sets the label visibility for the menu as well.
	 * @param labelVisibility label visibility
	 */
	public void setLabelVisibility(LabelVisibility labelVisibility) {
		this.labelVisibility = labelVisibility;
		this.labelVisibilityForMenu = labelVisibility;
		notifyListeners();
	}

	/**
	 * Sets the labelVisibilityForMenu to NotSet and notifies listeners.
	 */
	public void resetDefaultForMenu() {
		labelVisibilityForMenu = LabelVisibility.NotSet;
		notifyListeners();
	}

	@Override
	public void resetDefaults() {
		initLabelVisibility();
	}
}
