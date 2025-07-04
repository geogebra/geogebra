package org.geogebra.desktop.main;

import java.awt.Component;
import java.io.File;
import java.util.Set;

import javax.swing.ListCellRenderer;

import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.ManualPage;

public interface GuiManagerInterfaceD extends GuiManagerInterface {

	/**
	 * @param allTools toolbar definition
	 */
	void setToolBarDefinition(String allTools);

	@Override
	String getToolbarDefinition();

	/**
	 * @param fileExtension file extension
	 * @param currentFile current file
	 * @param fileDescription file description
	 * @param promptOverwrite whether to prompt on overwrite
	 * @param dirsOnly whether to show directories only
	 * @return picked file
	 */
	File showSaveDialog(FileExtensions fileExtension, File currentFile,
			String fileDescription, boolean promptOverwrite, boolean dirsOnly);

	/**
	 * Remove a tool from the toolbar
	 * @param mode tool's mode ID
	 */
	void removeFromToolbarDefinition(int mode);

	/**
	 * @param file file to load
	 * @param isMacroFile whether it's a .ggt file
	 * @return success
	 */
	boolean loadFile(File file, boolean isMacroFile);

	/**
	 * @return the menu bar
	 */
	Component getMenuBar();

	/**
	 * Show the file save picker and save the current file.
	 * @return success
	 */
	boolean saveAs();

	/**
	 * Initialize the menu bar.
	 */
	void initMenubar();

	/**
	 * @param viewID view ID
	 * @param updatePropertiesView whether to update the properties view
	 */
	void setFocusedPanel(int viewID, boolean updatePropertiesView);

	/**
	 * Opens file chooser and returns a data file for the spreadsheet
	 * @return data file
	 */
	File getDataFile();

	/**
	 * @param renderer renderer
	 * @return whether renderer belongs to tool dialog
	 */
	boolean belongsToToolCreator(ListCellRenderer renderer);

	/**
	 * @return input help panel
	 */
	Component getInputHelpPanel();

	/**
	 * Reset the CAS view
	 */
	void resetCasView();

	/**
	 * Close all panels for the current app.
	 */
	void exitAllCurrent();

	/**
	 * Show dialog for opening a file via URL.
	 */
	void openURL();

	/**
	 * Update the Window menu.
	 */
	void updateMenuWindow();

	/**
	 * Update the File menu.
	 */
	void updateMenuFile();

	/**
	 * Allow GUI to refresh.
	 */
	void allowGUIToRefresh();

	/**
	 * Update the main frame title.
	 */
	void updateFrameTitle();

	/**
	 * Save the current file.
	 * @return success
	 */
	boolean saveCurrentFile();

	/**
	 * @return the dialog manager
	 */
	DialogManager getDialogManager();

	/**
	 * Update localized labels.
	 */
	void setLabels();

	/**
	 * @param urlString URL to load
	 * @param suppressErrorMsg whether to suppress error messages
	 * @return success
	 */
	boolean loadURL(String urlString, boolean suppressErrorMsg);

	/**
	 * Open URL in system browser.
	 * @param strURL URL to open
	 */
	void showURLinBrowser(String strURL);

	/**
	 * Open a help page.
	 * @param page the help page type
	 * @param detail help page specifier
	 */
	void openHelp(ManualPage page, String detail);

	/**
	 * Update toolbar definition.
	 */
	void updateToolbarDefinition();

	/**
	 * @param duplicateLabels labels to be renamed
	 * @return whether to rename the objects (UNKNOWN if canceled)
	 */
	ExtendedBoolean shouldRenameObjectsOnInsertFile(Set<String> duplicateLabels);

	/**
	 * Show dialog listing renamed objects.
	 * @param overwrite whether to overwrite
	 * @param duplicateLabels duplicated labels
	 */
	void showRenamedObjectsDialog(boolean overwrite, Set<String> duplicateLabels);

}