package org.geogebra.common.contextmenu;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;

public interface ContextMenuItem {
	@Nonnull
	String getLocalizedTitle(@Nonnull Localization localization);

	@CheckForNull
	default Icon getIcon() {
		return null;
	}

	enum Icon {
		Expression, Text, Help, Delete
	}
}
