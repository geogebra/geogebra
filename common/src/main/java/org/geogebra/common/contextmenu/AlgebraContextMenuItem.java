package org.geogebra.common.contextmenu;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	private final String translationId;

	private AlgebraContextMenuItem(String translationId) {
		this.translationId = translationId;
	}

	@Override
	public String getTranslationId() {
		return translationId;
	}

	@Override
	@Nonnull
	public List<String> getTranslationParameters() {
		return List.of();
	}

	@Override
	public int getGroupId() {
		return 0;
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return null;
	}
}