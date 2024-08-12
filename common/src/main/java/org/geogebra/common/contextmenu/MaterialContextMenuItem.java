package org.geogebra.common.contextmenu;

import java.util.List;

import javax.annotation.Nullable;

public enum MaterialContextMenuItem implements ContextMenuItem {
	Delete("Delete", Icon.Delete);

	private final String translationId;
	private final Icon icon;

	private MaterialContextMenuItem(String translationId, Icon icon) {
		this.translationId = translationId;
		this.icon = icon;
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

	@Nullable
	@Override
	public Icon getIcon() {
		return icon;
	}
}

