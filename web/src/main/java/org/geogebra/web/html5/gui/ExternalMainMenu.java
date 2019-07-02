package org.geogebra.web.html5.gui;

import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.GlobalHeader;

public class ExternalMainMenu {

	public static void menuToGlobalHeader(AppW app) {
		if (GlobalHeader.isInDOM()) {
			MenuToggleButton btn = new MenuToggleButton(app);
			btn.setExternal(true);
			btn.addToGlobalHeader();
		}
	}

}
