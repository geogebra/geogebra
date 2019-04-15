package org.geogebra.common.main.settings;

public class LabelSettings implements Resetable {

	private LabelVisibility labelVisibility;
	private LabelVisibility labelVisibilityForMenu;

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
