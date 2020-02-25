package org.geogebra.web.full.gui.menu.action;

import org.geogebra.web.full.gui.menubar.MenuAction;
import org.geogebra.web.full.gui.menubar.action.ExitExamAction;
import org.geogebra.web.full.gui.menubar.action.ExportImageAction;
import org.geogebra.web.full.gui.menubar.action.FileNewAction;
import org.geogebra.web.full.gui.menubar.action.LicenseAction;
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
	private FileNewAction fileNewAction;
	private ExitExamAction exitExamAction;
	private LicenseAction licenseAction;
	private MenuAction showSettingsAction;
	private SaveAction saveAction;
	private ShareAction shareAction;
	private ExportImageAction exportImageAction;
	private ShowTutorialsAction showTutorialsAction;
	private ShowForumAction showForumAction;
	private ReportProblemAction reportProblemAction;
	private SignInAction signInAction;
	private SignOutAction signOutAction;
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
		fileNewAction = new FileNewAction(true);
		exitExamAction = new ExitExamAction();
		licenseAction = new LicenseAction();
		showSettingsAction = new ShowSettingsAction();
		saveAction = new SaveAction();
		shareAction = new ShareAction();
		exportImageAction = new ExportImageAction();
		showTutorialsAction = new ShowTutorialsAction();
		showForumAction = new ShowForumAction();
		reportProblemAction = new ReportProblemAction();
		signInAction = new SignInAction();
		signOutAction = new SignOutAction();
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
		fileNewAction.execute(null, app);
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
		exportImageAction.execute(null, app);
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
		licenseAction.execute(null, app);
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
		// TODO
	}

	@Override
	public void downloadGgs() {
		// TODO
	}

	@Override
	public void downloadPng() {
		// TODO
	}

	@Override
	public void downloadSvg() {
		// TODO
	}

	@Override
	public void downloadPdf() {
		// TODO
	}

	@Override
	public void downloadStl() {
		// TODO
	}

	@Override
	public void downloadColladaDae() {
		// TODO
	}

	@Override
	public void downloadColladaHTML() {
		// TODO
	}

	@Override
	public void previewPrint() {
		// TODO
	}
}
