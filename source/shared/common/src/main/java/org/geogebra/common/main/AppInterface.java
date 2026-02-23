/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.main;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.ImageManager;

/**
 * Facade for the App class.
 */
public interface AppInterface {
	/**
	 * Schedule a task to run on main thread after current task is finished.
	 * @param runnable task to schedule
	 */
	void invokeLater(Runnable runnable);

	/**
	 * @return whether this is an "applet" rather than full-sized app.
	 */
	boolean isApplet();

	/**
	 * Store current state of construction for undo/redo purposes
	 */
	void storeUndoInfo();

	@MissingDoc
	void closePopups();

	/**
	 * Creates a new Timer.
	 *
	 * @param delay
	 *            Milliseconds to run timer after start()1.
	 * @return GTimer descendant instance.
	 */
	GTimer newTimer(GTimerListener listener, int delay);

	/**
	 * @return true if we have access to complete gui (menubar, toolbar); false
	 *         for minimal applets (just one EV, no gui)
	 */
	boolean isUsingFullGui();

	/**
	 *
	 * @param view
	 *            view ID
	 * @return whether view with given ID is visible
	 */
	boolean showView(int view);

	/**
	 * Show error dialog with given text
	 *
	 * @param localizedError
	 *            error message
	 */
	void showError(String localizedError);

	/**
	 * Show error in two-line message dialog.
	 * @param string error title
	 * @param str error details
	 */
	void showError(String string, String str);

	/**
	 * @return algebra view
	 */
	AlgebraView getAlgebraView();

	/**
	 * @return image manager
	 */
	ImageManager getImageManager();

	/**
	 * @return gui manager (it's null in minimal applets)
	 */
	GuiManagerInterface getGuiManager();

	/**
	 * @return dialog manager
	 */
	DialogManager getDialogManager();

	/**
	 * Runs JavaScript
	 *
	 * @param app
	 *            application
	 * @param script
	 *            JS method name
	 * @param arg
	 *            arguments
	 * @throws RuntimeException
	 *             when script contains errors
	 */
	void evalJavaScript(App app, String script, String arg);

	/**
	 * @return width of the whole application (central panel) This is needed for
	 *         Corner[6]
	 */
	double getWidth();

	/**
	 * @return height of the whole application (central panel) This is needed
	 *         for Corner[6]
	 */
	double getHeight();

	/**
	 * @deprecated FontCreator.newSansSerifFont should be used instead.
	 *
	 * In Desktop gives current font, in Web creates a new one
	 * 
	 * @return font
	 */
	@Deprecated
	GFont getPlainFontCommon();

	/**
	 * TODO maybe we should create another factory for internal classes like
	 * this
	 *
	 * @return new graphics adapter for geo
	 */
	GeoElementGraphicsAdapter newGeoElementGraphicsAdapter();

	/**
	 * Switch current cursor to wait cursor
	 */
	void setWaitCursor();

	/**
	 * Update stylebars of all views
	 */
	void updateStyleBars();

	/**
	 * Update dynamic stylebars of all views
	 */
	void updateDynamicStyleBars();

	/**
	 * Changes current mode to mode of the toolbar's 1rst tool.
	 */
	void set1rstMode();

	/**
	 * @return spreadsheet table model
	 */
	SpreadsheetTableModel getSpreadsheetTableModel();

	/**
	 * Evaluate XML content.
	 * @param string XML content
	 * @param clearAll whether to clear construction first
	 */
	void setXML(String string, boolean clearAll);

	/**
	 * Returns API that can be used from external applications
	 *
	 * @return GeoGebra API
	 */
	GgbAPI getGgbApi();

	/**
	 * @return sound manager
	 */
	SoundManager getSoundManager();

	/**
	 * @return whether input bar is visible
	 */
	boolean showAlgebraInput();

	/**
	 * @return global key dispatcher. Can be null (eg Android, iOS)
	 */
	GlobalKeyDispatcher getGlobalKeyDispatcher();

	/**
	 * Call external javascript function.
	 * @param string function name
	 * @param args function argument
	 */
	void callAppletJavaScript(String string, String args);

	/**
	 * Updates menubar
	 */
	void updateMenubar();

	/**
	 * Recursively update all components with current look and feel
	 */
	void updateUI();

	/**
	 * Opens browser with given URL
	 *
	 * @param string
	 *            URL
	 */
	void showURLinBrowser(String string);

	/**
	 * Updates application layout
	 */
	void updateApplicationLayout();

	/**
	 * Clears construction
	 *
	 * @return true if successful otherwise false (eg user clicks "Cancel")
	 */
	boolean clearConstruction();

	/**
	 * Clear construction and reset settings from preferences
	 */
	void fileNew();

	/**
	 * copy bitmap of EV to clipboard
	 */
	void copyGraphicsViewToClipboard();

	@MissingDoc
	void exitAll();

	/**
	 * @param geo1
	 *            geo
	 * @param string
	 *            parameter (for input box scripts)
	 */
	void runScripts(GeoElement geo1, String string);

	/**
	 * @return true if we have critically low free memory
	 */
	boolean freeMemoryIsCritical();

	/**
	 * @return Approximate amount of remaining memory in bytes
	 */
	long freeMemory();

	/**
	 * Makes given view active
	 *
	 * @param evID
	 *            view id
	 */
	void setActiveView(int evID);

	/**
	 * Returns undo manager
	 *
	 * @param cons
	 *            construction
	 * @return undo manager
	 */
	UndoManager getUndoManager(Construction cons);

	/**
	 * @return whether we are running in HTML5 applet
	 */
	boolean isHTML5Applet();

	@MissingDoc
	CASFactory getCASFactory();

	@MissingDoc
	Factory getFactory();

	@MissingDoc
	void reset();

	@MissingDoc
	void resetUniqueId();

	@MissingDoc
	Localization getLocalization();

	/**
	 * Create XML serializer and deserializer.
	 * @param cons construction
	 * @return XML IO
	 */
	MyXMLio createXMLio(Construction cons);

	/**
	 * Show the toolbar customization UI.
	 */
	void showCustomizeToolbarGUI();

	@MissingDoc
	boolean isSelectionRectangleAllowed();

	/**
	 * @param filename
	 *            filename
	 * @return image wrapped in GBufferedImage
	 */
	MyImage getExternalImageAdapter(String filename, int width, int height);

	/**
	 * @param filename
	 *            filename
	 * @return image wrapped in GBufferedImage
	 */
	MyImage getInternalImageAdapter(String filename, int width, int height);

	@MissingDoc
	ScriptManager newScriptManager();
}
