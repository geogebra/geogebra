package org.geogebra.common.contextmenu;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AttributedString;

public enum InputContextMenuItem implements ContextMenuItem {
	Expression("Expression", ContextMenuIcon.Expression),
	Text("Text", ContextMenuIcon.Text),
	Help("Help", ContextMenuIcon.Help),
	Image("Image", ContextMenuIcon.Image);

	private final String translationKey;
	private final ContextMenuIcon icon;

	InputContextMenuItem(String translationKey, ContextMenuIcon icon) {
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
