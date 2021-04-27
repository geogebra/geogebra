package org.geogebra.web.full.main;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.openfileview.OpenFileView;
import org.geogebra.web.full.gui.openfileview.OpenTemporaryFileView;

public class OpenSearch {
	private final GeoGebraFrameFull frame;
	private AppWFull app;
	private GuiManagerW guiManager;

	public OpenSearch(AppWFull app) {
		this.app = app;
		frame = app.getAppletFrame();
		guiManager = app.getGuiManager();
	}


	private void showBrowser(MyHeaderPanel bg) {
		EuclidianController evController = app.getActiveEuclidianView().getEuclidianController();
		if (evController != null) {
			evController.hideDynamicStylebar();
		}
		frame.setApplication(app);
		frame.showPanel(bg);
	}

	public final void open(String query) {
		app.hideMenu();
		if (app.isWhiteboardActive()
				&& !app.getLoginOperation().isLoggedIn()) {
			app.getActivity().markSearchOpen();
			guiManager.listenToLogin();
			app.getLoginOperation().showLoginDialog();
			guiManager.setRunAfterLogin(() -> {
				((OpenFileView) guiManager
						.getBrowseView()).updateMaterials();
				showBrowser((MyHeaderPanel) guiManager.getBrowseView(query));
			});
			return;
		}
		if (app.isWhiteboardActive()
				&& guiManager.browseGUIwasLoaded()
				&& StringUtil.emptyTrim(query)
				&& guiManager.getBrowseView() instanceof OpenFileView) {
			((OpenFileView) guiManager.getBrowseView())
					.updateMaterials();
		}
		showBrowser((MyHeaderPanel) guiManager.getBrowseView(query));
		if (app.getAppletParameters().getDataParamPerspective()
				.startsWith("search:")) {
			app.getAppletParameters().setAttribute("perspective", "");
		}
	}

	/**
	 * Open temporary saved files view in exam mode.
	 */
	public final void openSearchInExamMode() {
		app.hideMenu();
		OpenTemporaryFileView openFileView =
				(OpenTemporaryFileView) guiManager.getBrowseView();
		if (guiManager.browseGUIwasLoaded()) {
			openFileView.loadAllMaterials();
		}
		showBrowser(openFileView.getPanel());
	}
}
