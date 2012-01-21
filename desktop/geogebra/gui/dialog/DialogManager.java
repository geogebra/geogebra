package geogebra.gui.dialog;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.gui.GuiManager;
import geogebra.gui.InputHandler;
import geogebra.gui.app.MyFileFilter;
import geogebra.gui.autocompletion.AutoCompletion;
import geogebra.gui.dialog.handler.NumberChangeSignInputHandler;
import geogebra.gui.dialog.handler.NumberInputHandler;
import geogebra.gui.dialog.handler.RedefineInputHandler;
import geogebra.gui.dialog.handler.RenameInputHandler;
import geogebra.gui.dialog.options.OptionsDialog;
import geogebra.gui.toolbar.ToolbarConfigDialog;
import geogebra.gui.util.GeoGebraFileChooser;
import geogebra.gui.view.functioninspector.FunctionInspector;
import geogebra.main.Application;
import geogebra.main.MyResourceBundle;

/**
 * Class to manage all kind of dialogs, including the file chooser, appearing in
 * GeoGebra. Supports (explicit) lazy initialization so that dialogs have to be
 * created manually if needed.
 */
public class DialogManager extends geogebra.common.gui.dialog.DialogManager {
	/**
	 * Application instance.
	 */
	protected Application app;

	/**
	 * The option dialog where the user can change all application settings.
	 */
	private OptionsDialog optionsDialog;

	/**
	 * Object which provides an option dialog if requested. Used because
	 * different option dialogs are needed for GeoGebra 4 and 5.
	 */
	private OptionsDialog.Factory optionsDialogFactory;

	/**
	 * Dialog to change object properties.
	 */
	private PropertiesDialog propDialog;

	/**
	 * Dialog to view properties of a function.
	 */
	private FunctionInspector functionInspector;

	/**
	 * Dialog for styling text objects.
	 */
	private TextInputDialog textInputDialog;

	/**
	 * Dialog to select new files, either for loading or saving. Various file
	 * types are supported.
	 */
	private GeoGebraFileChooser fileChooser;

	/**
	 * Properties for translation of file chooser UI in languages Java doesn't
	 * support.
	 */
	private ResourceBundle rbJavaUI;

	/**
	 * Keep track of the current locale for file chooser UI updating.
	 */
	private Locale currentLocale;

	public DialogManager(Application app) {
		this.app = app;
	}

	/**
	 * Initialize the properties panel.
	 */
	public synchronized void initPropertiesDialog() {
		if (propDialog == null) {
			propDialog = new PropertiesDialog(app);
		}
	}

	/**
	 * Reinitialize the properties panel.
	 */
	public synchronized void reinitPropertiesDialog() {
		if (propDialog != null && propDialog.isVisible())
			propDialog.setVisible(false);

		propDialog = null;
		propDialog = new PropertiesDialog(app);

	}

	/**
	 * Update the fonts used in the dialogs.
	 */
	public void updateFonts() {
		if (functionInspector != null)
			functionInspector.updateFonts();

		if (textInputDialog != null)
			textInputDialog.updateFonts();

		if (propDialog != null)
			// changed to force all panels to be updated
			reinitPropertiesDialog(); // was propDialog.initGUI();

		if (optionsDialog != null) {
			GuiManager.setFontRecursive(optionsDialog, app.getPlainFont());
			SwingUtilities.updateComponentTreeUI(optionsDialog);
		}

		if (fileChooser != null) {
			fileChooser.setFont(app.getPlainFont());
			SwingUtilities.updateComponentTreeUI(fileChooser);
		}
	}

	/**
	 * Update labels in the GUI.
	 */
	public void setLabels() {
		if (propDialog != null)
			// changed to force all language strings to be updated
			reinitPropertiesDialog(); // was propDialog.initGUI();

		if (optionsDialog != null)
			optionsDialog.setLabels();

		if (functionInspector != null)
			functionInspector.setLabels();

		if (textInputDialog != null)
			textInputDialog.setLabels();

		if (fileChooser != null)
			updateJavaUILanguage();
	}

	/**
	 * Displays the options dialog.
	 * 
	 * @param tabIndex
	 *            Index of the tab. Use OptionsDialog.TAB_* constants for this,
	 *            or -1 for the default, -2 to hide.
	 */
	public void showOptionsDialog(int tabIndex) {
		if (optionsDialog == null)
			optionsDialog = optionsDialogFactory.create(app);
		else
			optionsDialog.updateGUI();

		if (tabIndex > -1)
			optionsDialog.showTab(tabIndex);

		optionsDialog.setVisible(tabIndex != -2);
	}

	/**
	 * Displays the properties dialog for geos
	 */
	public void showPropertiesDialog(ArrayList<GeoElement> geos) {
		if (!app.letShowPropertiesDialog())
			return;

		// save the geos list: it will be cleared by setMoveMode()
		ArrayList<GeoElement> selGeos = null;
		if (geos == null)
			geos = app.getSelectedGeos();

		if (geos != null) {
			tempGeos.clear();
			tempGeos.addAll(geos);
			selGeos = tempGeos;
		}

		app.setMoveMode();
		app.setWaitCursor();

		// open properties dialog
		initPropertiesDialog();
		propDialog.setVisibleWithGeos(selGeos);

		// double-click on slider -> open properties at slider tab
		if (geos != null && geos.size() == 1
				&& geos.get(0).isEuclidianVisible()
				&& geos.get(0) instanceof GeoNumeric)
			propDialog.showSliderTab();

		app.setDefaultCursor();
	}

	private ArrayList<GeoElement> tempGeos = new ArrayList<GeoElement>();

	public void showPropertiesDialog() {
		showPropertiesDialog(null);
	}

	/**
	 * Displays the configuration dialog for the toolbar
	 */
	public void showToolbarConfigDialog() {
		app.getEuclidianView().resetMode();
		ToolbarConfigDialog dialog = new ToolbarConfigDialog(app);
		dialog.setVisible(true);
	}

	/**
	 * Displays the rename dialog for geo
	 */
	public void showRenameDialog(GeoElement geo, boolean storeUndo,
			String initText, boolean selectInitText) {
		if (!app.isRightClickEnabled())
			return;

		geo.setLabelVisible(true);
		geo.updateRepaint();

		InputHandler handler = new RenameInputHandler(app, geo, storeUndo);

		// Michael Borcherds 2008-03-25
		// a Chinese friendly version
		InputDialog id = new InputDialog(app, "<html>"
				+ app.getPlain("NewNameForA", "<b>" + geo.getNameDescription()
						+ "</b>") + // eg New name for <b>Segment a</b>
				"</html>", app.getPlain("Rename"), initText, false, handler,
				false, selectInitText, null);

		/*
		 * InputDialog id = new InputDialog( this, "<html>" +
		 * app.getPlain("NewName") + " " + app.getPlain("for") + " <b>" +
		 * geo.getNameDescription() + "</b></html>", app.getPlain("Rename"),
		 * initText, false, handler, true, selectInitText);
		 */

		id.setVisible(true);
	}

	/**
	 * Displays the redefine dialog for geo
	 * 
	 * @param allowTextDialog
	 *            whether text dialog should be used for texts
	 */
	public void showRedefineDialog(GeoElement geo, boolean allowTextDialog) {
		if (allowTextDialog && geo.isGeoText() && !geo.isTextCommand()) {
			showTextDialog((GeoText) geo);
			return;
		}

		String str = geo.getRedefineString(false, true);

		InputHandler handler = new RedefineInputHandler(app, geo, str);

		InputDialog id = new InputDialog(app, geo.getNameDescription(),
				app.getPlain("Redefine"), str, true, handler, geo);
		id.showSymbolTablePopup(true);
		id.setVisible(true);
	}

	/**
	 * Displays the text dialog for a given text.
	 */
	public void showTextDialog(GeoText text) {
		showTextDialog(text, null);
	}

	/**
	 * Creates a new text at given startPoint
	 */
	public void showTextCreationDialog(GeoPointND startPoint) {
		showTextDialog(null, startPoint);
	}

	private void showTextDialog(GeoText text, GeoPointND startPoint) {
		app.setWaitCursor();

		if (textInputDialog == null) {
			textInputDialog = (TextInputDialog) createTextDialog(text,
					startPoint);
		} else {
			textInputDialog.reInitEditor(text, startPoint);
		}

		textInputDialog.setVisible(true);
		app.setDefaultCursor();
	}

	public JDialog createTextDialog(GeoText text, GeoPointND startPoint) {
		boolean isTextMode = app.getMode() == EuclidianConstants.MODE_TEXT;
		TextInputDialog id = new TextInputDialog(app, app.getPlain("Text"),
				text, startPoint, 30, 6, isTextMode);
		return id;
	}

	/**
	 * Shows the function inspector dialog. If none exists, a new inspector is
	 * created.
	 */
	public boolean showFunctionInspector(GeoFunction function) {
		boolean success = true;

		try {
			if (functionInspector == null) {
				functionInspector = new FunctionInspector(app, function);
			} else {
				functionInspector.insertGeoElement(function);
			}
			functionInspector.setVisible(true);

		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}

	/**
	 * Creates a new checkbox at given startPoint
	 */
	public void showBooleanCheckboxCreationDialog(geogebra.common.awt.Point loc, GeoBoolean bool) {
		Point location = new Point(loc.x, loc.y);
		CheckboxCreationDialog d = new CheckboxCreationDialog(app, location, bool);
		d.setVisible(true);
	}

	/**
	 * Shows a modal dialog to enter a number or number variable name.
	 */
	public NumberValue showNumberInputDialog(String title, String message,
			String initText) {
		// avoid labeling of num
		Construction cons = app.getKernel().getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialog id = new InputDialog(app, message, title, initText, false,
				handler, true, false, null);
		id.setVisible(true);

		cons.setSuppressLabelCreation(oldVal);
		return handler.getNum();
	}

	/**
	 * Shows a modal dialog to enter a number or number variable name.
	 */
	public NumberValue showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText) {
		// avoid labeling of num
		Construction cons = app.getKernel().getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberChangeSignInputHandler handler = new NumberChangeSignInputHandler(
				app.getKernel().getAlgebraProcessor());
		NumberChangeSignInputDialog id = new NumberChangeSignInputDialog(app,
				message, title, initText, handler, changingSign, checkBoxText);
		id.setVisible(true);

		cons.setSuppressLabelCreation(oldVal);

		return handler.getNum();
	}

	public void showNumberInputDialogRegularPolygon(String title,
			GeoPoint2 geoPoint1, GeoPoint2 geoPoint2) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialog id = new InputDialogRegularPolygon(app, title, handler,
				geoPoint1, geoPoint2, app.getKernel());
		id.setVisible(true);

	}

	public void showNumberInputDialogCirclePointRadius(String title,
			GeoPointND geoPoint1, AbstractEuclidianView view) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialog id = new InputDialogCirclePointRadius(app, title, handler,
				(GeoPoint2) geoPoint1, app.getKernel());
		id.setVisible(true);

	}

	public void showNumberInputDialogRotate(String title, GeoPolygon[] polys,
			GeoPoint2[] points, GeoElement[] selGeos) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialog id = new InputDialogRotate(app, title, handler, polys,
				points, selGeos, app.getKernel());
		id.setVisible(true);

	}

	public void showNumberInputDialogAngleFixed(String title,
			GeoSegment[] segments, GeoPoint2[] points, GeoElement[] selGeos) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialog id = new InputDialogAngleFixed(app, title, handler,
				segments, points, selGeos, app.getKernel());
		id.setVisible(true);

	}

	public void showNumberInputDialogDilate(String title, GeoPolygon[] polys,
			GeoPoint2[] points, GeoElement[] selGeos) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialog id = new InputDialogDilate(app, title, handler, points,
				selGeos, app.getKernel());
		id.setVisible(true);

	}

	public void showNumberInputDialogSegmentFixed(String title,
			GeoPoint2 geoPoint1) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialog id = new InputDialogSegmentFixed(app, title, handler,
				geoPoint1, app.getKernel());
		id.setVisible(true);

	}

	/**
	 * Shows a modal dialog to enter an angle or angle variable name.
	 * 
	 * @return: Object[] with { NumberValue, AngleInputDialog } pair
	 */
	public Object[] showAngleInputDialog(String title, String message,
			String initText) {
		// avoid labeling of num
		Construction cons = app.getKernel().getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		AngleInputDialog id = new AngleInputDialog(app, message, title,
				initText, false, handler, true);
		id.setVisible(true);

		cons.setSuppressLabelCreation(oldVal);
		Object[] ret = { handler.getNum(), id };
		return ret;
	}

	/**
	 * Close all open dialogs.
	 * 
	 * @remark Just closes the properties dialog at the moment.
	 */
	public void closeAll() {
		closePropertiesDialog();
	}

	/**
	 * Close the properties dialog. Has no side-effects if the dialog has not
	 * yet been used or is invisible already.
	 */
	public void closePropertiesDialog() {
		if (propDialog != null && propDialog.isShowing()) {
			propDialog.cancel();
		}
	}

	/**
	 * Creates a new slider at given location (screen coords).
	 * 
	 * @return whether a new slider (number) was create or not
	 */
	public boolean showSliderCreationDialog(int x, int y) {
		app.setWaitCursor();

		SliderDialog dialog = new SliderDialog(app, x, y);
		dialog.setVisible(true);

		app.setDefaultCursor();

		return true;
	}

	/**
	 * Creates a new JavaScript button at given location (screen coords).
	 * 
	 * @return whether a new slider (number) was create or not
	 */
	public boolean showButtonCreationDialog(int x, int y, boolean textfield) {
		ButtonDialog dialog = new ButtonDialog(app, x, y, textfield);
		dialog.setVisible(true);
		return true;
	}

	/**
	 * Close the properties dialog if it is not the current selection listener.
	 * Has no side-effects if the dialog is has not yet been used or if if it
	 * invisible already.
	 * 
	 * @see #closePropertiesDialog()
	 */
	public void closePropertiesDialogIfNotListener() {
		// close properties dialog
		// if it is not the current selection listener
		if (propDialog != null && propDialog.isShowing()
				&& propDialog != app.getCurrentSelectionListener()) {
			propDialog.setVisible(false);
		}
	}

	public synchronized void initFileChooser() {
		if (fileChooser == null) {
			try {
				setFileChooser(new GeoGebraFileChooser(app,
						app.getCurrentImagePath())); // non-restricted
				// Added for Intergeo File Format (Yves Kreis) -->
				fileChooser.addPropertyChangeListener(
						JFileChooser.FILE_FILTER_CHANGED_PROPERTY,
						new FileFilterChangedListener());
				// <-- Added for Intergeo File Format (Yves Kreis)
			} catch (Exception e) {
				// fix for java.io.IOException: Could not get shell folder ID
				// list
				// Java bug
				// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6544857
				AbstractApplication
						.debug("Error creating GeoGebraFileChooser - using fallback option");
				setFileChooser(new GeoGebraFileChooser(app,
						app.getCurrentImagePath(), true)); // restricted version
			}

			updateJavaUILanguage();
		}
	}

	/**
	 * Loads java-ui.properties and sets all key-value pairs using
	 * UIManager.put(). This is needed to translate JFileChooser to languages
	 * not supported by Java natively.
	 */
	private void updateJavaUILanguage() {
		// load properties jar file
		if (currentLocale == app.getLocale())
			return;

		// update locale
		currentLocale = app.getLocale();

		// load javaui properties file 
		rbJavaUI = MyResourceBundle.createBundle(Application.RB_JAVA_UI, currentLocale);
		Enumeration<String> keys = rbJavaUI.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = rbJavaUI.getString(key);
			UIManager.put(key, value);
		}

		// update file chooser
		if (getFileChooser() != null) {
			getFileChooser().setLocale(currentLocale);
			SwingUtilities.updateComponentTreeUI(getFileChooser());

			// Unfortunately the preceding line removes the event listener from
			// the
			// internal JTextField inside the file chooser. This means that the
			// listener has to be registered again. (e.g. a simple call to
			// 'AutoCompletion.install(this);' inside the GeoGebraFileChooser
			// constructor is not sufficient)
			AutoCompletion.install(getFileChooser(), true);
		}
	}

	public OptionsDialog getOptionsDialog() {
		return optionsDialog;
	}

	public PropertiesDialog getPropDialog() {
		return propDialog;
	}

	public GeoGebraFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(GeoGebraFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public FunctionInspector getFunctionInspector() {
		return functionInspector;
	}

	public TextInputDialog getTextInputDialog() {
		return textInputDialog;
	}

	public OptionsDialog.Factory getOptionsDialogFactory() {
		return optionsDialogFactory;
	}

	public void setOptionsDialogFactory(
			OptionsDialog.Factory optionsDialogFactory) {
		this.optionsDialogFactory = optionsDialogFactory;
	}

	// Added for Intergeo File Format (Yves Kreis) -->
	/*
	 * PropertyChangeListener implementation to handle file filter changes
	 */
	private class FileFilterChangedListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (getFileChooser().getFileFilter() instanceof geogebra.gui.app.MyFileFilter) {
				String fileName = null;
				if (getFileChooser().getSelectedFile() != null) {
					fileName = getFileChooser().getSelectedFile().getName();
				}

				// fileName = getFileName(fileName);

				if (fileName != null && fileName.indexOf(".") > -1) {
					fileName = fileName.substring(0, fileName.lastIndexOf("."))
							+ "."
							+ ((MyFileFilter) getFileChooser().getFileFilter())
									.getExtension();

					getFileChooser().setSelectedFile(
							new File(getFileChooser().getCurrentDirectory(),
									fileName));
				}
			}
		}
	}

	/**
	 * Factory for the {@link DialogManager} class.
	 */
	public static class Factory {
		/**
		 * @param app
		 *            Application instance
		 * @return a new {@link DialogManager}
		 */
		public DialogManager create(Application app) {
			DialogManager dialogManager = new DialogManager(app);
			dialogManager.setOptionsDialogFactory(new OptionsDialog.Factory());
			return dialogManager;
		}
	}
}
