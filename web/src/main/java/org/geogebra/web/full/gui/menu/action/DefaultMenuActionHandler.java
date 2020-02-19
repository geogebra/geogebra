package org.geogebra.web.full.gui.menu.action;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.main.AppWFull;

public class DefaultMenuActionHandler implements MenuActionHandler {

	private AppWFull app;

	public DefaultMenuActionHandler(AppWFull app) {
		this.app = app;
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
		app.getDialogManager().getSaveDialog().showIfNeeded(new AsyncOperation<Boolean>() {
			@Override
			public void callback(Boolean obj) {
				// ignore active: don't save means we want new construction
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();

				if (!app.isUnbundledOrWhiteboard()) {
					app.showPerspectivesPopup();
				}
				if (app.getPageController() != null) {
					app.getPageController().resetPageControl();
				}
			}
		});
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
