package org.geogebra.common.main.settings;

public class LabelSettings {

	private LabelVisibility labelVisibility = LabelVisibility.PointsOnly;
	private LabelVisibility labelVisibilityForMenu = LabelVisibility.NotSet;

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
}
