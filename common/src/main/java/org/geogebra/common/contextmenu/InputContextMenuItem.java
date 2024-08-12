package org.geogebra.common.contextmenu;

import java.util.List;

import javax.annotation.Nullable;

public enum InputContextMenuItem implements ContextMenuItem {
	Expression("Expression"),
	Text("Text"),
	Help("Help");

	private final String translationId;

	private InputContextMenuItem(String translationId) {
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
