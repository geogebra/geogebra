package org.geogebra.common.contextmenu;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;

public enum InputContextMenuItem implements ContextMenuItem {
	Expression("Expression", Icon.Expression),
	Text("Text", Icon.Text),
	Help("Help", Icon.Help);

	private final String translationKey;
	private final ContextMenuItem.Icon icon;

	InputContextMenuItem(String translationKey, Icon icon) {
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
