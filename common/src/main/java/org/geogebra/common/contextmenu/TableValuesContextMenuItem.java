package org.geogebra.common.contextmenu;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum TableValuesContextMenuItem implements ContextMenuItem {
	Edit("Edit", 0),
	ClearColumn("ClearColumn", 0),
	RemoveColumn("RemoveColumn", 0),
	ShowPoints("ShowPoints", 0),
	HidePoints("HidePoints", 0),
	ImportData("ContextMenu.ImportData", 0),
	Regression("Regression", 1),
	Statistics1("AStatistics", 1),
	Statistics2("AStatistics", 1);

	private final String translationId;
	private List<String> translationParameters;
	private final int groupId;

	TableValuesContextMenuItem(String translationId, int groupId) {
		this.translationId = translationId;
		this.translationParameters = List.of();
		this.groupId = groupId;
	}

	@Nonnull
	@Override
	public String getTranslationId() {
		return translationId;
	}

	@Nonnull
	@Override
	public List<String> getTranslationParameters() {
		return translationParameters;
	}

	@Override
	public int getGroupId() {
		return groupId;
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return null;
	}

	void setTranslationParameters(List<String> translationParameters) {
		this.translationParameters = translationParameters;
	}
}
