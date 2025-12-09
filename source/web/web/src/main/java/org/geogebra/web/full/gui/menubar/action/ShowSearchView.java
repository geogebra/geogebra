/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.gui.openfileview.HeaderFileView;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;

/**
 * Shows search view.
 */
public class ShowSearchView extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		showBrowser(app, (HeaderFileView) app.getGuiManager().getBrowseView());
		AppletParameters articleElement = app.getAppletParameters();
		if (articleElement.getDataParamPerspective().startsWith("search:")) {
			articleElement.setAttribute("perspective", "");
		}
	}

	private void showBrowser(AppWFull app, HeaderFileView bg) {
		EuclidianController evController = app.getActiveEuclidianView().getEuclidianController();
		if (evController != null) {
			evController.hideDynamicStylebar();
		}
		app.getAppletFrame().setApplication(app);
		app.getAppletFrame().showPanel(bg.getPanel());
	}
}
