package org.geogebra.web.full.gui.menu;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.SubmenuItem;
import org.geogebra.web.full.gui.menu.action.MenuActionHandler;

class MenuActionRouter {

	private MenuActionHandler menuActionHandler;

	public MenuActionRouter(MenuActionHandler menuActionHandler) {
		this.menuActionHandler = menuActionHandler;
	}

	void handleMenuItem(MenuItem menuItem) {
		if (menuItem instanceof ActionableItem) {
			handleAction(((ActionableItem) menuItem).getAction());
		} else if (menuItem instanceof SubmenuItem) {
			// ToDo
		}
	}

	void handleAction(Action action) {
		switch (action) {
			case START_CLASSIC:
				menuActionHandler.startClassic();
			case START_GRAPHING:
				menuActionHandler.startGraphing();
			case START_SCIENTIFIC:
				menuActionHandler.startScientific();
			case START_GEOMETRY:
				menuActionHandler.startGeometry();
			case START_CAS_CALCULATOR:
				menuActionHandler.startCasCalculator();
			case START_GRAPHING_3D:
				menuActionHandler.startGraphing3d();
			case CLEAR_CONSTRUCTION:
				menuActionHandler.clearConstruction();
			case START_EXAM_MODE:
				menuActionHandler.startExamMode();
			case SHOW_SETTINGS:
				menuActionHandler.showSettings();
			case SHOW_SEARCH_VIEW:
				menuActionHandler.showSearchView();
			case SAVE_FILE:
				menuActionHandler.saveFile();
			case SHARE_FILE:
				menuActionHandler.shareFile();
			case EXPORT_IMAGE:
				menuActionHandler.exportImage();
			case SHOW_EXAM_LOG:
				menuActionHandler.showExamLog();
			case EXIT_EXAM_MODE:
				menuActionHandler.exitExamMode();
			case SHOW_TUTORIALS:
				menuActionHandler.showTutorials();
			case SHOW_FORUM:
				menuActionHandler.showForum();
			case REPORT_PROBLEM:
				menuActionHandler.reportProblem();
			case SHOW_LICENSE:
				menuActionHandler.showLicense();
			case SIGN_IN:
				menuActionHandler.signIn();
			case SIGN_OUT:
				menuActionHandler.signOut();
			case OPEN_PROFILE_PAGE:
				menuActionHandler.openProfilePage();
			case SHOW_DOWNLOAD_AS:
				menuActionHandler.showDownloadAs();
			case DOWNLOAD_GGB:
				menuActionHandler.downloadGgb();
			case DOWNLOAD_GGS:
				menuActionHandler.downloadGgs();
			case DOWNLOAD_PNG:
				menuActionHandler.downloadPng();
			case DOWNLOAD_SVG:
				menuActionHandler.downloadSvg();
			case DOWNLOAD_PDF:
				menuActionHandler.downloadPdf();
			case DOWNLOAD_STL:
				menuActionHandler.downloadStl();
			case DOWNLOAD_COLLADA_DAE:
				menuActionHandler.downloadColladaDae();
			case DOWNLOAD_COLLADA_HTML:
				menuActionHandler.downloadColladaHTML();
			case PREVIEW_PRINT:
				menuActionHandler.previewPrint();
		}
	}
}
