package org.geogebra.common.contextmenu;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ContextMenuItem {
	@Nonnull
	String getTranslationId();
	@Nonnull
	List<String> getTranslationParameters();
	int getGroupId();
	@Nullable
	Icon getIcon();

	enum Icon {
		Expression, Text, Help, Delete
	}
}



