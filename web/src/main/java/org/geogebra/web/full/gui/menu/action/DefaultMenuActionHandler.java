package org.geogebra.web.full.gui.menu.action;

import org.geogebra.web.full.gui.menubar.MenuAction;
import org.geogebra.web.full.gui.menubar.action.ExitExamAction;
import org.geogebra.web.full.gui.menubar.action.FileNewAction;
import org.geogebra.web.full.gui.menubar.action.LicenseAction;
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
