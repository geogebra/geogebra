package org.geogebra.common.main.settings;

/**
 * Sets the label visibility for the menu.
 */
public class MenuLabelSettings extends AbstractSettings implements Resetable {

	private static final LabelVisibility DEFAULT_MENU_LABEL_VISIBILITY = LabelVisibility.NotSet;

	private LabelVisibility menuLabelVisibility;

	/**
	 * This constructor is protected because it should be called only by the SettingsBuilder.
	 */
	MenuLabelSettings() {
		menuLabelVisibility = DEFAULT_MENU_LABEL_VISIBILITY;
	}

	public LabelVisibility getLabelVisibility() {
		return menuLabelVisibility;
	}

	/**
	 * Sets the label visibility and notifies listeners.
	 * @param labelVisibility label visibility
	 */
	public void setLabelVisibility(LabelVisibility labelVisibility) {
		menuLabelVisibility = labelVisibility;
		notifyListeners();
	}

	@Override
	public void resetDefaults() {
		menuLabelVisibility = DEFAULT_MENU_LABEL_VISIBILITY;
	}
}
