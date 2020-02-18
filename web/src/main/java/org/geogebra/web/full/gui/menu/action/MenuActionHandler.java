package org.geogebra.web.full.gui.menu.action;

public interface MenuActionHandler {

	void startClassic();

	void startGraphing();

	void startScientific();

	void startGeometry();

	void startCasCalculator();

	void startGraphing3d();

	void clearConstruction();

	void startExamMode();

	void showSettings();

	void showSearchView();

	void saveFile();

	void shareFile();

	void exportImage();

	void showExamLog();

	void exitExamMode();

	void showTutorials();

	void showForum();

	void reportProblem();

	void showLicense();

	void signIn();

	void signOut();

	void openProfilePage();

	void showDownloadAs();

	void downloadGgb();

	void downloadGgs();

	void downloadPng();

	void downloadSvg();

	void downloadPdf();

	void downloadStl();

	void downloadColladaDae();

	void downloadColladaHTML();

	void previewPrint();
}
