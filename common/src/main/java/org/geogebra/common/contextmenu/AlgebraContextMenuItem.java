package org.geogebra.common.contextmenu;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AttributedString;

public enum AlgebraContextMenuItem implements ContextMenuItem {
	Statistics("Statistics"),
	Delete("Delete"),
	DuplicateInput("ContextMenu.DuplicateInput"),
	DuplicateOutput("ContextMenu.DuplicateOutput"),
	Settings("Settings"),
	SpecialPoints("Suggestion.SpecialPoints"),
	CreateTableValues("CreateTableValues"),
	RemoveLabel("RemoveLabel"),
	AddLabel("AddLabel"),
	CreateSlider("Suggestion.CreateSlider"),
	RemoveSlider("RemoveSlider"),
	Solve("Solve");

	private final String translationKey;

	AlgebraContextMenuItem(String translationKey) {
		this.translationKey = translationKey;
	}

	@Nonnull
	@Override
	public AttributedString getLocalizedTitle(@Nonnull Localization localization) {
		return new AttributedString(localization.getMenu(translationKey));
	}
}