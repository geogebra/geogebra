package org.geogebra.web.full.gui.fontmenu;

import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

/**
 * Provides menu to change the font family.
 *
 * @author Laszlo
 */
public class FontMenuItem extends AriaMenuItem {

	/**
	 *
	 * @param app the application.
	 * @param textController to alter text with.
	 */
	public FontMenuItem(AppW app, InlineTextController textController) {
		super(app.getLocalization().getMenu("ContextMenu.Font"), false,
				new FontSubMenu(app, textController));
		addStyleName("no-image");
	}
}
