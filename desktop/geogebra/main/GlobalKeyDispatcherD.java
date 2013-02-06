package geogebra.main;

import geogebra.common.euclidian.draw.DrawTextField;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.main.App;
import geogebra.common.main.KeyCodes;
import geogebra.euclidian.EuclidianViewD;
import geogebra.gui.GuiManagerD;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.app.MyFileFilter;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.gui.layout.LayoutD;
import geogebra.gui.menubar.GeoGebraMenuBar;
import geogebra.util.Util;

import java.awt.Component;
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

	/**
	 * @param app application
	 */
	public GlobalKeyDispatcherD(AppD app) {
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
	 * @param event event
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

	/**
	 * Handles key event by disassembling it into primitive types and handling it using the mothod
	 * from common
	 * @param event event
	 * @return whether key was consumed
	 */
	public boolean handleGeneralKeys(KeyEvent event) {

		// use event.isAltDown rather than AppD.isControlDown(event)
		// as we need to distinguish <AltGr>2 and <Ctrl>2
		// #2390 #908
		return handleGeneralKeys(KeyCodes.translateJavacode(event.getKeyCode()), event.isShiftDown(), AppD.isControlDown(event), event.isAltDown(), event.getSource() instanceof JTable, event.getSource() instanceof EuclidianViewD);
	}

	private boolean handleSelectedGeosKeys(KeyEvent event,
			ArrayList<GeoElement> geos) {
		
		// use event.isAltDown rather than AppD.isAltDown(event)
		// as Ctrl-Arrow on OSX does something special
		// so we actually want to use Alt
		return handleSelectedGeosKeys(KeyCodes.translateJavacode(event.getKeyCode()), geos, event.isShiftDown(), AppD.isControlDown(event), event.isAltDown(), event.getSource() instanceof JTable);
	}





	/**
	 * Handles function key for given GeoElement: F3: copy definition to input
	 * field F4: copy value to input field F5: copy name to input field
	 * 
	 * @param fkey
	 *            number
	 * @param geo geo
	 */
	@Override
	public void handleFunctionKeyForAlgebraInput(int fkey, GeoElement geo) {
		if (!app.isUsingFullGui() || !app.showAlgebraInput())
			return;
		JTextComponent textComponent = ((geogebra.javax.swing.GTextComponentD)((GuiManagerD)app.getGuiManager())
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
		if (((AppD)app).isUsingFullGui() && ((GuiManagerD)app.getGuiManager()).noMenusOpen()) {
			if (app.showAlgebraInput()
					&& !((GuiManagerD)app.getGuiManager()).getAlgebraInput()
					.hasFocus()) {
				// focus this frame (needed for external view windows)
				if (!app.isApplet() && ((AppD)app).getFrame() != null) {
					((AppD)app).getFrame().toFront();
				}

				((GuiManagerD)app.getGuiManager()).getAlgebraInput().requestFocus();

				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean handleTab(boolean isControlDown, boolean isShiftDown) {
		if (isControlDown && app.isUsingFullGui()) {

			GuiManager gui = ((GuiManagerD)app.getGuiManager());
			((LayoutD)gui.getLayout()).getDockManager()
			.moveFocus(!isShiftDown);

			return true;

		} 
							boolean useTab = app.getActiveEuclidianView().hasFocus()|| ((GuiManagerD)app.getGuiManager()).getAlgebraView().hasFocus();
							
							// make sure TAB works in Input Boxes but also in Spreadsheet, Input Bar
							Component owner = ((AppD) app).getFrame().getFocusOwner();
							if (owner instanceof AutoCompleteTextField && ((AutoCompleteTextField)owner).usedForInputBox()) useTab = true;
							
							if (useTab) {
								super.handleTab(isControlDown, isShiftDown);
								return true;
							}


		

		return false;
	}

	@Override
	protected void handleCtrlC() {
		if (!(((GuiManagerD)app.getGuiManager()).getSpreadsheetView()
				.hasFocus())
				&& !(((AlgebraInput) ((GuiManagerD)app.getGuiManager())
						.getAlgebraInput()).getTextField()
						.hasFocus())) {

			super.handleCtrlC();
		}

	}

	@Override
	protected void handleCtrlV() {
		if (!(((GuiManagerD)app.getGuiManager()).getSpreadsheetView()
				.hasFocus())
				&& !(((AlgebraInput) ((GuiManagerD)app.getGuiManager())
						.getAlgebraInput()).getTextField().hasFocus())) {

			super.handleCtrlV();
		}
	}
	
	@Override
	protected boolean handleCtrlShiftN(boolean isAltDown) {
		ArrayList<GeoGebraFrame> ggbInstances = GeoGebraFrame
				.getInstances();
		int size = ggbInstances.size();
		if (size == 1) {
			// load next file in folder

			// ask if OK to discard current file
			if (((AppD)app).isSaved() || ((AppD)app).saveCurrentFile()) {

				MyFileFilter fileFilter = new MyFileFilter();
				fileFilter.addExtension("ggb");

				File[] options = ((AppD)app).getCurrentPath().listFiles(
						fileFilter);

				// no current file, just load the first file in the
				// folder
				if (((AppD)app).getCurrentFile() == null) {
					if (options.length > 0) {
						((GuiManagerD)app.getGuiManager()).loadFile(options[0],
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

				String currentFile = ((AppD)app).getCurrentFile().getName();

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

				((GuiManagerD)app.getGuiManager()).loadFile(fileToLoad, false);

				return true;
			}
			for (int i = 0; i < size; i++) {
				GeoGebraFrame ggb = ggbInstances.get(i);
				AppD application = ggb.getApplication();

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
		JTextComponent textComponent = ((geogebra.javax.swing.GTextComponentD)((GuiManagerD)app.getGuiManager())
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
	protected void createNewWindow() {
		//no wait cursor needed here, that's taken care of before we call this
		GeoGebraFrame.createNewWindow(null);
	}

	@Override
	protected void showPrintPreview(App app2) {
		GeoGebraMenuBar.showPrintPreview((AppD) app);
	}


}
