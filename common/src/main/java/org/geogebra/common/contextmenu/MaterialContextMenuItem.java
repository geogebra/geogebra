package org.geogebra.common.contextmenu;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;

public enum MaterialContextMenuItem implements ContextMenuItem {
	Delete("Delete", Icon.Delete);

	private final String translationKey;
	private final Icon icon;

	MaterialContextMenuItem(String translationKey, Icon icon) {
		this.translationKey = translationKey;
		this.icon = icon;
	}

	@Nonnull
	@Override
	public String getLocalizedTitle(@Nonnull Localization localization) {
		return localization.getMenu(translationKey);
	}

	@Override
	public Icon getIcon() {
		return icon;
	}
}

