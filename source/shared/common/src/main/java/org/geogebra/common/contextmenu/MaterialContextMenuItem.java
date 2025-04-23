package org.geogebra.common.contextmenu;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AttributedString;

/**
 * Menu items for material context menu.
 */
public enum MaterialContextMenuItem implements ContextMenuItem {
	Delete("Delete", ContextMenuIcon.Delete);

	private final String translationKey;
	private final ContextMenuIcon icon;

	MaterialContextMenuItem(String translationKey, ContextMenuIcon icon) {
		this.translationKey = translationKey;
		this.icon = icon;
	}

	@Nonnull
	@Override
	public AttributedString getLocalizedTitle(@Nonnull Localization localization) {
		return new AttributedString(localization.getMenu(translationKey));
	}

	@Override
	public ContextMenuIcon getIcon() {
		return icon;
	}
}

