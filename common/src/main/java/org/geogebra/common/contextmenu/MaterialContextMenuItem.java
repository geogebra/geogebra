package org.geogebra.common.contextmenu;

import java.util.List;

import javax.annotation.Nullable;

public enum MaterialContextMenuItem implements ContextMenuItem {
	Delete("Delete");

	private final String translationId;

	private MaterialContextMenuItem(String translationId) {
		this.translationId = translationId;
	}

	@Override
	public String getTranslationId() {
		return translationId;
	}

	@Override
	public List<String> getTranslationParameters() {
		return List.of();
	}

	@Override
	public int getGroupId() {
		return 0;
	}
}

