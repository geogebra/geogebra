package org.geogebra.common.contextmenu;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ContextMenuItem {
	@Nonnull
	String getTranslationId();
	List<String> getTranslationParameters();
	int getGroupId();
}



