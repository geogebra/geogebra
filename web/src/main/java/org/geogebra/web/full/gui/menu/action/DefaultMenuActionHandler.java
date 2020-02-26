package org.geogebra.web.full.gui.menu.action;

import org.geogebra.web.full.gui.menubar.MenuAction;
import org.geogebra.web.full.gui.menubar.action.ExitExamAction;
import org.geogebra.web.full.gui.menubar.action.ExportColladaDaeAction;
import org.geogebra.web.full.gui.menubar.action.ExportColladaHtmlAction;
import org.geogebra.web.full.gui.menubar.action.ExportDefaultFormatAction;
import org.geogebra.web.full.gui.menubar.action.ExportPdfAction;
import org.geogebra.web.full.gui.menubar.action.ExportPngAction;
import org.geogebra.web.full.gui.menubar.action.ExportStlAction;
import org.geogebra.web.full.gui.menubar.action.ExportSvgAction;
import org.geogebra.web.full.gui.menubar.action.ClearAllAction;
import org.geogebra.web.full.gui.menubar.action.ShowLicenseAction;
import org.geogebra.web.full.gui.menubar.action.ShowPrintPreviewAction;
import org.geogebra.web.full.gui.menubar.action.SignInAction;
import org.geogebra.web.full.gui.menubar.action.SignOutAction;
import org.geogebra.web.full.gui.menubar.action.ReportProblemAction;
import org.geogebra.web.full.gui.menubar.action.SaveAction;
import org.geogebra.web.full.gui.menubar.action.ShareAction;
import org.geogebra.web.full.gui.menubar.action.ShowForumAction;
import org.geogebra.web.full.gui.menubar.action.ShowSettingsAction;
import org.geogebra.web.full.gui.menubar.action.ShowTutorialsAction;
import org.geogebra.web.full.gui.menubar.action.StartAppAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Implements handling of the menu actions.
 */
public class DefaultMenuActionHandler implements MenuActionHandler {

	private AppWFull app;
	private MenuAction clearAllAction;
	private MenuAction exitExamAction;
	private MenuAction showLicenseAction;
	private MenuAction showSettingsAction;
	private MenuAction saveAction;
	private MenuAction shareAction;
	private MenuAction showTutorialsAction;
	private MenuAction showForumAction;
	private MenuAction reportProblemAction;
	private MenuAction signInAction;
	private MenuAction signOutAction;
	private MenuAction exportPngAction;
	private MenuAction exportDefaultFormatAction;
	private MenuAction exportSvgAction;
	private MenuAction exportPdfAction;
	private MenuAction exportStlAction;
	private MenuAction exportColladaDaeAction;
	private MenuAction exportColladaHtmlAction;
	private MenuAction showPrintPreviewAction;
	private MenuAction startGraphingAction;
	private MenuAction startGeometryAction;
	private MenuAction start3dAction;
	private MenuAction startScientificAction;
	private MenuAction startClassicAction;
	private MenuAction startCasAction;

	/**
	 * Create a DefaultMenuActionHandler
	 * @param app app
	 */
	public DefaultMenuActionHandler(AppWFull app) {
		this.app = app;
		createActions();
	}

	private void createActions() {
		clearAllAction = new ClearAllAction(true);
		exitExamAction = new ExitExamAction();
		showLicenseAction = new ShowLicenseAction();
		showSettingsAction = new ShowSettingsAction();
		saveAction = new SaveAction();
		shareAction = new ShareAction();
		showTutorialsAction = new ShowTutorialsAction();
		showForumAction = new ShowForumAction();
		reportProblemAction = new ReportProblemAction();
		signInAction = new SignInAction();
		signOutAction = new SignOutAction();
		exportDefaultFormatAction = new ExportDefaultFormatAction();
		exportPngAction = new ExportPngAction(app);
		exportSvgAction = new ExportSvgAction(app);
		exportPdfAction = new ExportPdfAction(app);
		exportStlAction = new ExportStlAction();
		exportColladaDaeAction = new ExportColladaDaeAction();
		exportColladaHtmlAction = new ExportColladaHtmlAction();
		showPrintPreviewAction = new ShowPrintPreviewAction();
		startGraphingAction = StartAppAction.create(app, "graphing");
		startGeometryAction = StartAppAction.create(app, "geometry");
		start3dAction = StartAppAction.create(app, "3d");
		startScientificAction = StartAppAction.create(app, "calculator");
		startClassicAction = StartAppAction.create(app, "classic");
		startCasAction = StartAppAction.create(app, "cas");
	}

	@Override
	public void startClassic() {
		startClassicAction.execute();
	}

	@Override
	public void startGraphing() {
		startGraphingAction.execute();
	}

	@Override
	public void startScientific() {
		startScientificAction.execute();
	}

	@Override
	public void startGeometry() {
		startGeometryAction.execute();
	}

	@Override
	public void startCasCalculator() {
		startCasAction.execute();
	}

	@Override
	public void startGraphing3d() {
		start3dAction.execute();
	}

	@Override
	public void clearConstruction() {
		clearAllAction.execute(null, app);
	}

	@Override
	public void startExamMode() {
		// TODO
	}

	@Override
	public void showSettings() {
		showSettingsAction.execute(null, app);
	}

	@Override
	public void showSearchView() {
		app.openSearch(null);
	}

	@Override
	public void saveFile() {
		saveAction.execute(null, app);
	}

	@Override
	public void shareFile() {
		shareAction.execute(null, app);
	}

	@Override
	public void exportImage() {
		exportPngAction.execute();
	}

	@Override
	public void showExamLog() {
		// TODO
	}

	@Override
	public void exitExamMode() {
		exitExamAction.execute(null, app);
	}

	@Override
	public void showTutorials() {
		showTutorialsAction.execute();
	}

	@Override
	public void showForum() {
		showForumAction.execute();
	}

	@Override
	public void reportProblem() {
		reportProblemAction.execute();
	}

	@Override
	public void showLicense() {
		showLicenseAction.execute(null, app);
	}

	@Override
	public void signIn() {
		signInAction.execute(null, app);
	}

	@Override
	public void signOut() {
		signOutAction.execute(null, app);
	}

	@Override
	public void openProfilePage() {
		// TODO
	}

	@Override
	public void showDownloadAs() {
		// TODO
	}

	@Override
	public void downloadGgb() {
		exportDefaultFormatAction.execute(null, app);
	}

	@Override
	public void downloadGgs() {
		exportDefaultFormatAction.execute(null, app);
	}

	@Override
	public void downloadPng() {
		exportPngAction.execute();
	}

	@Override
	public void downloadSvg() {
		exportSvgAction.execute();
	}

	@Override
	public void downloadPdf() {
		exportPdfAction.execute();
	}

	@Override
	public void downloadStl() {
		exportStlAction.execute(null, app);
	}

	@Override
	public void downloadColladaDae() {
		exportColladaDaeAction.execute(null, app);
	}

	@Override
	public void downloadColladaHTML() {
		exportColladaHtmlAction.execute(null, app);
	}

	@Override
	public void previewPrint() {
		showPrintPreviewAction.execute(null, app);
	}
}
