package org.geogebra.web.full.gui.menu.action;

import org.geogebra.web.full.gui.menubar.action.ExitExamAction;
import org.geogebra.web.full.gui.menubar.action.FileNewAction;
import org.geogebra.web.full.gui.menubar.action.LicenseAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Implements handling of the menu actions.
 */
public class DefaultMenuActionHandler implements MenuActionHandler {

	private AppWFull app;
	private FileNewAction fileNewAction;
	private ExitExamAction exitExamAction;
	private LicenseAction licenseAction;

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
	}

	@Override
	public void startClassic() {
		// TODO
	}

	@Override
	public void startGraphing() {
		// TODO
	}

	@Override
	public void startScientific() {
		// TODO
	}

	@Override
	public void startGeometry() {
		// TODO
	}

	@Override
	public void startCasCalculator() {
		// TODO
	}

	@Override
	public void startGraphing3d() {
		// TODO
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
		// TODO
	}

	@Override
	public void showSearchView() {
		app.openSearch(null);
	}

	@Override
	public void saveFile() {
		// TODO
	}

	@Override
	public void shareFile() {
		// TODO
	}

	@Override
	public void exportImage() {
		// TODO
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
		// TODO
	}

	@Override
	public void showForum() {
		// TODO
	}

	@Override
	public void reportProblem() {
		// TODO
	}

	@Override
	public void showLicense() {
		licenseAction.execute(null, app);
	}

	@Override
	public void signIn() {
		// TODO
	}

	@Override
	public void signOut() {
		// TODO
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
