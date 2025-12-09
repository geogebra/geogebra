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

package org.geogebra.web.full.main;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.full.gui.openfileview.HeaderFileView;
import org.geogebra.web.full.gui.openfileview.OpenFileViewMebis;
import org.geogebra.web.full.gui.openfileview.OpenTemporaryFileView;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.util.AppletParameters;

/**
 * Class to open the corresponding material browsing view.
 *
 */
public class OpenSearch {
	private final GeoGebraFrameFull frame;
	private final AppWFull app;
	private final GuiManagerW guiManager;
	private final AppletParameters appletParameters;

	/**
	 *
	 * @param app the application.
	 */
	public OpenSearch(AppWFull app) {
		this.app = app;
		frame = app.getAppletFrame();
		guiManager = app.getGuiManager();
		appletParameters = app.getAppletParameters();
	}

	/**
	 * Show the corresponding browser view.

	 * @param query to filter the materials.
	 */
	public final void show(String query) {
		app.hideMenu();

		if (isOnMebisWithoutLogin()) {
			app.getActivity().markSearchOpen();
			app.getLoginOperation().showLoginDialog();
		} else {
			open(query);
		}
	}

	private void open(String query) {
		if (hasOpenFileViewOnWhiteboard(query)) {
			updateMaterials();
		}

		showBrowserView(query);

		if (hasSearchPerspective()) {
			clearPerspective();
		}
	}

	private void showBrowserView(String query) {
		BrowseViewI browseView = guiManager.getBrowseView(query);
		if (browseView instanceof AnimatingPanel) {
			showBrowser((AnimatingPanel) browseView);
		} else {
			showBrowser((HeaderFileView) browseView);
		}
	}

	private boolean hasSearchPerspective() {
		return appletParameters.getDataParamPerspective()
				.startsWith("search:");
	}

	private boolean isOnMebisWithoutLogin() {
		return app.isByCS()
				&& !app.getLoginOperation().isLoggedIn();
	}

	private void updateMaterials() {
		((OpenFileViewMebis) guiManager.getBrowseView())
				.updateMaterials();
	}

	private boolean hasOpenFileViewOnWhiteboard(String query) {
		return app.isWhiteboardActive()
				&& guiManager.isOpenFileViewLoaded()
				&& StringUtil.emptyTrim(query)
				&& guiManager.getBrowseView() instanceof OpenFileViewMebis;
	}

	private void showBrowser(HeaderFileView fileView) {
		showBrowser(fileView.getPanel());
	}

	private void showBrowser(AnimatingPanel headerPanel) {
		EuclidianController evController = app.getActiveEuclidianView().getEuclidianController();
		if (evController != null) {
			evController.hideDynamicStylebar();
		}
		frame.setApplication(app);
		frame.showPanel(headerPanel);
	}

	private void clearPerspective() {
		appletParameters.setAttribute("perspective", "");
	}

	/**
	 * Open temporary saved files view in exam mode.
	 */
	public final void openInExamMode() {
		app.hideMenu();
		OpenTemporaryFileView openFileView =
				(OpenTemporaryFileView) guiManager.getBrowseView();
		if (guiManager.isOpenFileViewLoaded()) {
			openFileView.loadAllMaterials(0);
		}
		showBrowser(openFileView);
	}
}
