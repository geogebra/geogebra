package org.geogebra.web.full.gui.menubar.action;

import com.google.gwt.user.client.Window;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Opens the forum.
 */
public class ShowForumAction extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, AppWFull app) {
		Window.open(GeoGebraConstants.FORUM_URL, "_blank", "");
	}
}
