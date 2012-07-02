package geogebra.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.euclidian.DrawTextField;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.KeyCodes;
import geogebra.euclidian.EuclidianViewD;
import geogebra.gui.GuiManagerD;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.app.MyFileFilter;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.gui.menubar.GeoGebraMenuBar;
import geogebra.util.Util;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

/**
 * Handles global keys like ESC, DELETE, and function keys.
 * 
 * @author Markus Hohenwarter
 */
public class GlobalKeyDispatcherD extends geogebra.common.main.GlobalKeyDispatcher implements KeyEventDispatcher {

	public GlobalKeyDispatcherD(Application app) {
		this.app = app;
	}

	/**
	 * This method is called by the current KeyboardFocusManager before they are
	 * dispatched to their targets, allowing it to handle the key event and
	 * consume it.
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {

		// ignore key events coming from text components (i.e. text fields and
		// text areas)
		// or key events coming from popups (source class = JRootPane)
		if (event.isConsumed() || event.getSource() instanceof JTextComponent
				|| event.getSource() instanceof JRootPane) {
			return false;
		}

		boolean consumed = false;
		switch (event.getID()) {
		case KeyEvent.KEY_PRESSED:
			consumed = handleKeyPressed(event);
			break;

		case KeyEvent.KEY_TYPED:
			consumed = handleKeyTyped(event);
			break;
		}

		if (consumed) {
			event.consume();
		}
		return consumed;
	}

	/**
	 * The "key pressed" event is generated when a key is pushed down.
	 * 
	 * @param event
	 * @return if key was consumed
	 */
	protected boolean handleKeyPressed(KeyEvent event) {

		// GENERAL KEYS:
		// handle ESC, function keys, zooming with Ctrl +, Ctrl -, etc.
		if (handleGeneralKeys(event)) {
			return true;
		}

		// SELECTED GEOS:
		// handle function keys, arrow keys, +/- keys for selected geos, etc.
		if (handleSelectedGeosKeys(event, app.getSelectedGeos())) {
			return true;
		}

		return false;
	}

	/**
	 * "Key typed" events are higher-level and generally do not depend on the
	 * platform or keyboard layout. They are generated when a Unicode character
	 * is entered, and are the preferred way to find out about character input.
	 */
	private boolean handleKeyTyped(KeyEvent event) {
		// ignore key events coming from tables like the spreadsheet to
		// allow start editing
		if (event.getSource() instanceof JTable) {
			return false;
		}

		GeoElement geo;
		if (app.selectedGeosSize() == 1) {
			// selected geo
			geo = app.getSelectedGeos().get(0);
		} else {
			// last created geo
			geo = app.getLastCreatedGeoElement();
		}

		// show RENAME dialog when a letter is typed
		// or edit Textfield for any keypress
		char ch = event.getKeyChar();
		if ((Character.isLetter(ch) && !event.isMetaDown() && !event.isAltDown()
				&& !event.isControlDown()) || geo instanceof GeoTextField) {

			// open rename dialog
			if (geo != null) {

				if (geo instanceof GeoTextField) {
					DrawTextField dt =
							(DrawTextField) app.getActiveEuclidianView().getDrawableFor(geo);
					dt.setFocus(ch+"");
				} else {
					app.getDialogManager()
					.showRenameDialog(geo, true,
							Character.toString(ch), false);
				}
				return true;
			}
		}

		return false;
	}

	public boolean handleGeneralKeys(KeyEvent event) {

		return handleGeneralKeys(KeyCodes.translateJavacode(event.getKeyCode()), event.isShiftDown(), Application.isControlDown(event), Application.isAltDown(event), event.getSource() instanceof JTable, event.getSource() instanceof EuclidianViewD);
	}

	private boolean handleSelectedGeosKeys(KeyEvent event,
			ArrayList<GeoElement> geos) {
		
		return handleSelectedGeosKeys(KeyCodes.translateJavacode(event.getKeyCode()), geos, event.isShiftDown(), Application.isControlDown(event), Application.isAltDown(event), event.getSource() instanceof JTable);
	}





	/**
	 * Handles function key for given GeoElement: F3: copy definition to input
	 * field F4: copy value to input field F5: copy name to input field
	 * 
	 * @param fkey
	 *            number
	 * @param geo
	 */
	@Override
	public void handleFunctionKeyForAlgebraInput(int fkey, GeoElement geo) {
		if (!app.isUsingFullGui() || !app.showAlgebraInput())
			return;
		JTextComponent textComponent = ((geogebra.javax.swing.GTextComponentD)app.getGuiManager()
				.getAlgebraInputTextField()).getImpl();

		switch (fkey) {
		case 3: // F3 key: copy definition to input field
			textComponent.setText(geo.getDefinitionForInputBar());
			break;

		case 4: // F4 key: copy value to input field
			textComponent.replaceSelection(" " + geo.getValueForInputBar()
					+ " ");
			break;

		case 5: // F5 key: copy name to input field
			textComponent.replaceSelection(" " + geo.getLabel(StringTemplate.defaultTemplate) + " ");
			break;
		}

		textComponent.requestFocusInWindow();
	}

	@Override
	protected boolean handleEnter() {
		if (((Application)app).isUsingFullGui() && ((Application)app).getGuiManager().noMenusOpen()) {
			if (app.showAlgebraInput()
					&& !((Application)app).getGuiManager().getAlgebraInput()
					.hasFocus()) {
				// focus this frame (needed for external view windows)
				if (!app.isApplet() && ((Application)app).getFrame() != null) {
					((Application)app).getFrame().toFront();
				}

				((Application)app).getGuiManager().getAlgebraInput().requestFocus();

				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean handleTab(boolean isControlDown, boolean isShiftDown) {
		if (isControlDown && app.isUsingFullGui()) {

			GuiManagerD gui = ((Application)app).getGuiManager();
			gui.getLayout().getDockManager()
			.moveFocus(!isShiftDown);

			return true;

		} else if (app.getActiveEuclidianView().hasFocus()
				|| ((Application)app).getGuiManager().getAlgebraView().hasFocus()) {
			
			super.handleTab(isControlDown, isShiftDown);

			return true;
		}

		return false;
	}

	@Override
	protected boolean handleCtrlC() {
		if (!(((Application)app).getGuiManager().getSpreadsheetView()
				.hasFocus())
				&& !(((AlgebraInput) ((Application)app).getGuiManager()
						.getAlgebraInput()).getTextField()
						.hasFocus())) {

			super.handleCtrlC();
		}

		return false;

	}

	@Override
	protected boolean handleCtrlV() {
		if (!(((Application)app).getGuiManager().getSpreadsheetView()
				.hasFocus())
				&& !(((AlgebraInput) ((Application)app).getGuiManager()
						.getAlgebraInput()).getTextField().hasFocus())) {

			super.handleCtrlV();
		}
		
		return false;
	}
	
	@Override
	protected boolean handleCtrlShiftN(boolean isAltDown) {
		ArrayList<GeoGebraFrame> ggbInstances = GeoGebraFrame
				.getInstances();
		int size = ggbInstances.size();
		if (size == 1) {
			// load next file in folder

			// ask if OK to discard current file
			if (((Application)app).isSaved() || ((Application)app).saveCurrentFile()) {

				MyFileFilter fileFilter = new MyFileFilter();
				fileFilter.addExtension("ggb");

				File[] options = ((Application)app).getCurrentPath().listFiles(
						fileFilter);

				// no current file, just load the first file in the
				// folder
				if (((Application)app).getCurrentFile() == null) {
					if (options.length > 0) {
						((Application)app).getGuiManager().loadFile(options[0],
								false);
						return true;
					}
					return false;
				}

				TreeSet<File> sortedSet = new TreeSet<File>(
						Util.getFileComparator());
				for (int i = 0; i < options.length; i++) {
					if (options[i].isFile())
						sortedSet.add(options[i]);
				}

				String currentFile = ((Application)app).getCurrentFile().getName();

				Iterator<File> iterator = sortedSet.iterator();
				File fileToLoad = null;
				while (iterator.hasNext() && fileToLoad == null) {
					if (iterator.next().getName()
							.equals(currentFile)) {
						// check if we're at the end
						if (iterator.hasNext())
							fileToLoad = iterator.next();
						else
							fileToLoad = options[0];
					}
				}

				((Application)app).getGuiManager().loadFile(fileToLoad, false);

				return true;
			}
			for (int i = 0; i < size; i++) {
				GeoGebraFrame ggb = ggbInstances.get(i);
				Application application = ggb.getApplication();

				if (app == application) {
					int n = isAltDown ? ((i - 1 + size) % size)
							: ((i + 1) % size);
					ggb = ggbInstances.get(n); // next/last
					// instance
					ggb.toFront();
					ggb.requestFocus();
					break; // break from if loop

				}
			}

			return true;

		}	

		return false;

	}

	@Override
	protected void copyDefinitionsToInputBarAsList(ArrayList<GeoElement> geos) {
		JTextComponent textComponent = ((geogebra.javax.swing.GTextComponentD)app.getGuiManager()
				.getAlgebraInputTextField()).getImpl();

		StringBuilder sb = new StringBuilder();
		sb.append('{');

		Iterator<GeoElement> it = geos.iterator();
		while (it.hasNext()) {
			sb.append(it.next().getFormulaString(StringTemplate.defaultTemplate,
					false));
			if (it.hasNext())
				sb.append(',');
		}
		sb.append('}');

		textComponent.setText(sb.toString());	}

	@Override
	protected void createNewWindow(Object object) {
		app.setWaitCursor();
		GeoGebraFrame.createNewWindow(null);
		app.setDefaultCursor();
	}

	@Override
	protected void showPrintPreview(AbstractApplication app2) {
		GeoGebraMenuBar.showPrintPreview((Application) app);
	}


}
