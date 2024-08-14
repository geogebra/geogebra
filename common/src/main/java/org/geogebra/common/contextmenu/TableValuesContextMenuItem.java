package org.geogebra.common.contextmenu;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;

public enum TableValuesContextMenuItem implements ContextMenuItem {
	Edit("Edit"),
	ClearColumn("ClearColumn"),
	RemoveColumn("RemoveColumn"),
	ShowPoints("ShowPoints"),
	HidePoints("HidePoints"),
	ImportData("ContextMenu.ImportData"),
	Regression("Regression"),
	Statistics1("AStatistics"),
	Statistics2("AStatistics"),
	Separator("");

	private final String translationKey;
	private String[] translationPlaceholderValues;

	TableValuesContextMenuItem(String translationKey) {
		this.translationKey = translationKey;
		this.translationPlaceholderValues = new String[]{};
	}

	@Nonnull
	@Override
	public String getLocalizedTitle(@Nonnull Localization localization) {
		return localization.getPlainArray(translationKey, null, translationPlaceholderValues);
	}

	void setTranslationPlaceholderValues(String[] translationPlaceholderValues) {
		this.translationPlaceholderValues = translationPlaceholderValues;
	}
}
