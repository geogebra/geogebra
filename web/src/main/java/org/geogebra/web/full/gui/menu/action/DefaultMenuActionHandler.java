package org.geogebra.web.full.gui.menu.action;

import org.geogebra.web.full.gui.menubar.action.ExitExamAction;
import org.geogebra.web.full.gui.menubar.action.FileNewAction;
import org.geogebra.web.full.gui.menubar.action.LicenseAction;
import org.geogebra.web.full.main.AppWFull;

public class DefaultMenuActionHandler implements MenuActionHandler {

	private AppWFull app;
	private FileNewAction fileNewAction;
	private ExitExamAction exitExamAction;
	private LicenseAction licenseAction;

	public DefaultMenuActionHandler(AppWFull app) {
		this.app = app;
		createActions();
	}

	private void createActions() {
		fileNewAction = new FileNewAction(true);
		exitExamAction = new ExitExamAction();
		licenseAction = new LicenseAction();
	}

	@Override
	public void startClassic() {

	}

	@Override
	public void startGraphing() {

	}

	@Override
	public void startScientific() {

	}

	@Override
	public void startGeometry() {

	}

	@Override
	public void startCasCalculator() {

	}

	@Override
	public void startGraphing3d() {

	}

	@Override
	public void clearConstruction() {
		fileNewAction.execute(null, app);
	}

	@Override
	public void startExamMode() {

	}

	@Override
	public void showSettings() {

	}

	@Override
	public void showSearchView() {
		app.openSearch(null);
	}

	@Override
	public void saveFile() {

	}

	@Override
	public void shareFile() {

	}

	@Override
	public void exportImage() {

	}

	@Override
	public void showExamLog() {

	}

	@Override
	public void exitExamMode() {
		exitExamAction.execute(null, app);
	}

	@Override
	public void showTutorials() {

	}

	@Override
	public void showForum() {

	}

	@Override
	public void reportProblem() {

	}

	@Override
	public void showLicense() {
		licenseAction.execute(null, app);
	}

	@Override
	public void signIn() {

	}

	@Override
	public void signOut() {

	}

	@Override
	public void openProfilePage() {

	}

	@Override
	public void showDownloadAs() {

	}

	@Override
	public void downloadGgb() {

	}

	@Override
	public void downloadGgs() {

	}

	@Override
	public void downloadPng() {

	}

	@Override
	public void downloadSvg() {

	}

	@Override
	public void downloadPdf() {

	}

	@Override
	public void downloadStl() {

	}

	@Override
	public void downloadColladaDae() {

	}

	@Override
	public void downloadColladaHTML() {

	}

	@Override
	public void previewPrint() {

	}
}
