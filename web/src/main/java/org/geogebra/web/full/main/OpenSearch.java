package org.geogebra.web.full.main;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.openfileview.OpenFileView;
import org.geogebra.web.full.gui.openfileview.OpenTemporaryFileView;
import org.geogebra.web.html5.util.AppletParameters;

public class OpenSearch {
	private final GeoGebraFrameFull frame;
	private final AppWFull app;
	private final GuiManagerW guiManager;
	private final AppletParameters appletParameters;

	public OpenSearch(AppWFull app) {
		this.app = app;
		frame = app.getAppletFrame();
		guiManager = app.getGuiManager();
		appletParameters = app.getAppletParameters();
	}

	public final void open(String query) {
		app.hideMenu();

		if (isUnloggedOnWhiteboard()) {
			loginAndOpen(query);
			return;
		}

		if (hasOpenFileViewOnWhiteboard(query)) {
			updateMaterials();
		}

		showBrowserView(query);

		if (hasSearchPerspective()) {
			clearPerspective();
		}
	}

	private void showBrowserView(String query) {
		showBrowser((MyHeaderPanel) guiManager.getBrowseView(query));
	}

	private boolean hasSearchPerspective() {
		return appletParameters.getDataParamPerspective()
				.startsWith("search:");
	}

	private boolean isUnloggedOnWhiteboard() {
		return app.isWhiteboardActive()
				&& !app.getLoginOperation().isLoggedIn();
	}

	private void updateMaterials() {
		((OpenFileView) guiManager.getBrowseView())
				.updateMaterials();
	}

	private boolean hasOpenFileViewOnWhiteboard(String query) {
		return app.isWhiteboardActive()
				&& guiManager.browseGUIwasLoaded()
				&& StringUtil.emptyTrim(query)
				&& guiManager.getBrowseView() instanceof OpenFileView;
	}

	private void showBrowser(MyHeaderPanel bg) {
		EuclidianController evController = app.getActiveEuclidianView().getEuclidianController();
		if (evController != null) {
			evController.hideDynamicStylebar();
		}
		frame.setApplication(app);
		frame.showPanel(bg);
	}

	private void loginAndOpen(String query) {
		app.getActivity().markSearchOpen();
		guiManager.listenToLogin();
		app.getLoginOperation().showLoginDialog();
		guiManager.setRunAfterLogin(() -> {
			updateMaterials();
			showBrowserView(query);
		});
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
		if (guiManager.browseGUIwasLoaded()) {
			openFileView.loadAllMaterials();
		}
		showBrowser(openFileView.getPanel());
	}
}
