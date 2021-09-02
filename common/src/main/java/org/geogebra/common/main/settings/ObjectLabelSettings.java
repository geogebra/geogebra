package org.geogebra.common.main.settings;

/**
 * Label settings.
 */
public class ObjectLabelSettings extends AbstractSettings implements Resetable {

	private static final LabelVisibility DEFAULT_OBJECT_LABEL_VISIBILITY =
			LabelVisibility.PointsOnly;

	private LabelVisibility labelVisibility;

	/**
	 * This constructor is protected because it should be called only by the SettingsBuilder.
	 */
	ObjectLabelSettings() {
		labelVisibility = DEFAULT_OBJECT_LABEL_VISIBILITY;
	}

	public LabelVisibility getLabelVisibility() {
		return labelVisibility;
	}

	/**
	 * Sets the label visibility for the menu as well.
	 * @param labelVisibility label visibility
	 */
	public void setLabelVisibility(LabelVisibility labelVisibility) {
		this.labelVisibility = labelVisibility;
		notifyListeners();
	}

	@Override
	public void resetDefaults() {
		labelVisibility = DEFAULT_OBJECT_LABEL_VISIBILITY;
	}
}
