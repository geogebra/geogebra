package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;

/**
 * Shows search view.
 */
public class ShowSearchView extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, AppWFull app) {
		showBrowser(app, (MyHeaderPanel) app.getGuiManager().getBrowseView(null));
		AppletParameters articleElement = app.getAppletParameters();
		if (articleElement.getDataParamPerspective().startsWith("search:")) {
			articleElement.setAttribute("perspective", "");
		}
	}

	private void showBrowser(AppWFull app, MyHeaderPanel bg) {
		EuclidianController evController = app.getActiveEuclidianView().getEuclidianController();
		if (evController != null) {
			evController.hideDynamicStylebar();
		}
		app.getAppletFrame().setApplication(app);
		app.getAppletFrame().showPanel(bg);
	}
}
