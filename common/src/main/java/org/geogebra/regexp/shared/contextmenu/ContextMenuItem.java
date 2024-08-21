package org.geogebra.regexp.shared.contextmenu;

import java.text.AttributedString;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;

/**
 * Items contained in context menus
 */
public interface ContextMenuItem {
	/**
	 * Title/label of an item which may contain special attributes for subscripts
	 *
	 * @param localization Used for translating the title
	 * @return The title of the item with possible subscript attributes marked with
	 * 		   {@link java.awt.font.TextAttribute#SUPERSCRIPT} attribute key and
	 * 		   {@link java.awt.font.TextAttribute#SUPERSCRIPT_SUB} value
	 */
	@Nonnull
	AttributedString getLocalizedTitle(@Nonnull Localization localization);

	/**
	 * @return The item's icon, or null if the context menu item does not have an icon
	 */
	@CheckForNull
	default Icon getIcon() {
		return null;
	}

	/**
	 * An identifier for the possible icon values
	 */
	enum Icon {
		Expression, Text, Help, Delete
	}
}
