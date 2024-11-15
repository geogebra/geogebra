package org.geogebra.desktop.main;

import java.awt.Component;
import java.io.File;

import javax.swing.ListCellRenderer;

import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.util.FileExtensions;

public interface GuiManagerInterfaceD extends GuiManagerInterface {

	void setToolBarDefinition(String allTools);

	@Override
	String getToolbarDefinition();

	File showSaveDialog(FileExtensions fileExtension, File currentFile,
			String string, boolean promptOverwrite, boolean dirsOnly);

	void removeFromToolbarDefinition(int i);

	boolean loadFile(File file, boolean isMacroFile);

	void updateMenuBarLayout();

	Component getMenuBar();

	boolean saveAs();

	void initMenubar();

	void setFocusedPanel(int viewSpreadsheet, boolean updatePropertiesView);

	File getDataFile();

	boolean belongsToToolCreator(ListCellRenderer renderer);

	Component getInputHelpPanel();

	void resetCasView();

	void exitAllCurrent();

	void openURL();

	void updateMenuWindow();

	void updateMenuFile();

	void allowGUIToRefresh();

	void updateFrameTitle();

	void setShowToolBarHelp(boolean b);

	boolean saveCurrentFile();

	DialogManager getDialogManager();

	void setLabels();

	boolean loadURL(String urlString, boolean suppressErrorMsg);

	void showURLinBrowser(String strURL);

	void updateToolbarDefinition();

}