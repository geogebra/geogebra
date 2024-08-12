package org.geogebra.common.contextmenu;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum MaterialContextMenuItem implements ContextMenuItem {
	Delete("Delete", Icon.Delete);

	private final String translationId;
	private final Icon icon;

	MaterialContextMenuItem(String translationId, Icon icon) {
		this.translationId = translationId;
		this.icon = icon;
	}

	@Nonnull
	@Override
	public String getTranslationId() {
		return translationId;
	}

	@Nonnull
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

