package org.geogebra.common.main.settings;

/**
 * Label settings.
 */
public class LabelSettings implements Resetable {

	private LabelVisibility labelVisibility;
	private LabelVisibility labelVisibilityForMenu;

	/**
	 * This constructor is protected because it should be called only by the SettingsBuilder.
	 */
	LabelSettings() {
		initVisibilities();
	}

	private void initVisibilities() {
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
		labelVisibilityForMenu = labelVisibility;
	}

	public void resetLabelVisibilityForMenu() {
		labelVisibilityForMenu = LabelVisibility.NotSet;
	}

	@Override
	public void resetDefaults() {
		initVisibilities();
	}
}
