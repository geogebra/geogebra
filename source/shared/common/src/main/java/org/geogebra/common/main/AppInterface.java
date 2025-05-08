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
import org.geogebra.common.util.NormalizerMinimal;

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
	public abstract boolean isApplet();

	/**
	 * Store current state of construction for undo/redo purposes
	 */
	public abstract void storeUndoInfo();

	@MissingDoc
	public abstract void closePopups();

	/**
	 * Creates a new Timer.
	 *
	 * @param delay
	 *            Milliseconds to run timer after start()1.
	 * @return GTimer descendant instance.
	 */
	public abstract GTimer newTimer(GTimerListener listener, int delay);

	/**
	 * @return true if we have access to complete gui (menubar, toolbar); false
	 *         for minimal applets (just one EV, no gui)
	 */
	public abstract boolean isUsingFullGui();

	/**
	 *
	 * @param view
	 *            view ID
	 * @return whether view with given ID is visible
	 */
	public abstract boolean showView(int view);

	/**
	 * Show error dialog with given text
	 *
	 * @param localizedError
	 *            error message
	 */
	public abstract void showError(String localizedError);

	/**
	 * Show error in two-line message dialog.
	 * @param string error title
	 * @param str error details
	 */
	public abstract void showError(String string, String str);

	/**
	 * @return algebra view
	 */
	public abstract AlgebraView getAlgebraView();

	/**
	 * @return image manager
	 */
	public abstract ImageManager getImageManager();

	/**
	 * @return gui manager (it's null in minimal applets)
	 */
	public abstract GuiManagerInterface getGuiManager();

	/**
	 * @return dialog manager
	 */
	public abstract DialogManager getDialogManager();

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
	public abstract double getWidth();

	/**
	 * @return height of the whole application (central panel) This is needed
	 *         for Corner[6]
	 */
	public abstract double getHeight();

	/**
	 * @deprecated FontCreator.newSansSerifFont should be used instead.
	 *
	 * In Desktop gives current font, in Web creates a new one
	 * 
	 * @return font
	 */
	@Deprecated
	public abstract GFont getPlainFontCommon();

	/**
	 * TODO maybe we should create another factory for internal classes like
	 * this
	 *
	 * @return new graphics adapter for geo
	 */
	public abstract GeoElementGraphicsAdapter newGeoElementGraphicsAdapter();

	/**
	 * Switch current cursor to wait cursor
	 */
	public abstract void setWaitCursor();

	/**
	 * Update stylebars of all views
	 */
	public abstract void updateStyleBars();

	/**
	 * Update dynamic stylebars of all views
	 */
	public abstract void updateDynamicStyleBars();

	/**
	 * Changes current mode to mode of the toolbar's 1rst tool.
	 */
	public abstract void set1rstMode();

	/**
	 * @return spreadsheet table model
	 */
	public abstract SpreadsheetTableModel getSpreadsheetTableModel();

	/**
	 * Evaluate XML content.
	 * @param string XML content
	 * @param clearAll whether to clear construction first
	 */
	public abstract void setXML(String string, boolean clearAll);

	/**
	 * Returns API that can be used from external applications
	 *
	 * @return GeoGebra API
	 */
	public abstract GgbAPI getGgbApi();

	/**
	 * @return sound manager
	 */
	public abstract SoundManager getSoundManager();

	/**
	 * @return whether input bar is visible
	 */
	public abstract boolean showAlgebraInput();

	/**
	 * @return global key dispatcher. Can be null (eg Android, iOS)
	 */
	public abstract GlobalKeyDispatcher getGlobalKeyDispatcher();

	/**
	 * Call external javascript function.
	 * @param string function name
	 * @param args function argument
	 */
	public abstract void callAppletJavaScript(String string, String args);

	/**
	 * Updates menubar
	 */
	public abstract void updateMenubar();

	/**
	 * Recursively update all components with current look and feel
	 */
	public abstract void updateUI();

	/**
	 * Opens browser with given URL
	 *
	 * @param string
	 *            URL
	 */
	public abstract void showURLinBrowser(String string);

	/**
	 * Updates application layout
	 */
	public abstract void updateApplicationLayout();

	/**
	 * Clears construction
	 *
	 * @return true if successful otherwise false (eg user clicks "Cancel")
	 */
	public abstract boolean clearConstruction();

	/**
	 * Clear construction and reset settings from preferences
	 */
	public abstract void fileNew();

	/**
	 * copy bitmap of EV to clipboard
	 */
	public abstract void copyGraphicsViewToClipboard();

	@MissingDoc
	public abstract void exitAll();

	/**
	 * @param geo1
	 *            geo
	 * @param string
	 *            parameter (for input box scripts)
	 */
	public abstract void runScripts(GeoElement geo1, String string);

	/**
	 * @return true if we have critically low free memory
	 */
	public abstract boolean freeMemoryIsCritical();

	/**
	 * @return Approximate amount of remaining memory in bytes
	 */
	public abstract long freeMemory();

	/**
	 * Makes given view active
	 *
	 * @param evID
	 *            view id
	 */
	public abstract void setActiveView(int evID);

	/**
	 * Returns undo manager
	 *
	 * @param cons
	 *            construction
	 * @return undo manager
	 */
	public abstract UndoManager getUndoManager(Construction cons);

	/**
	 * @return whether we are running in HTML5 applet
	 */
	public abstract boolean isHTML5Applet();

	@MissingDoc
	public abstract CASFactory getCASFactory();

	@MissingDoc
	public abstract Factory getFactory();

	@MissingDoc
	public abstract NormalizerMinimal getNormalizer();

	@MissingDoc
	public abstract void reset();

	@MissingDoc
	public abstract void resetUniqueId();

	@MissingDoc
	public abstract Localization getLocalization();

	/**
	 * Create XML serializer and deserializer.
	 * @param cons construction
	 * @return XML IO
	 */
	public abstract MyXMLio createXMLio(Construction cons);

	/**
	 * Show the toolbar customization UI.
	 */
	public abstract void showCustomizeToolbarGUI();

	@MissingDoc
	public abstract boolean isSelectionRectangleAllowed();

	/**
	 * @param filename
	 *            filename
	 * @return image wrapped in GBufferedImage
	 */
	public abstract MyImage getExternalImageAdapter(String filename, int width, int height);

	/**
	 * @param filename
	 *            filename
	 * @return image wrapped in GBufferedImage
	 */
	public abstract MyImage getInternalImageAdapter(String filename, int width, int height);

	@MissingDoc
	abstract ScriptManager newScriptManager();
}
