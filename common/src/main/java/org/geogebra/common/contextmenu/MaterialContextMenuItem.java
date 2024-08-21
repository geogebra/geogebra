package org.geogebra.common.contextmenu;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AttributedString;

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
	public AttributedString getLocalizedTitle(@Nonnull Localization localization) {
		return new AttributedString(localization.getMenu(translationKey));
	}

	@Override
	public Icon getIcon() {
		return icon;
	}
}

