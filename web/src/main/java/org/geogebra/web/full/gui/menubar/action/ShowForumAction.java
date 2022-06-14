package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

import elemental2.dom.DomGlobal;

/**
 * Opens the forum.
 */
public class ShowForumAction extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, AppWFull app) {
		DomGlobal.window.open(GeoGebraConstants.FORUM_URL, "_blank", "");
	}
}
