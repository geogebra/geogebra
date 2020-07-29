package org.geogebra.desktop.main;

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

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GlobalKeyDispatcher;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.app.GeoGebraFrame;
import org.geogebra.desktop.gui.app.MyFileFilter;
import org.geogebra.desktop.gui.inputbar.AlgebraInputD;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.gui.menubar.GeoGebraMenuBar;
import org.geogebra.desktop.gui.util.OOMLConverter;
import org.geogebra.desktop.util.UtilD;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.editor.share.util.KeyCodes;

/**
 * Handles global keys like ESC, DELETE, and function keys.
 * 
 * @author Markus Hohenwarter
 */
public class GlobalKeyDispatcherD extends GlobalKeyDispatcher
		implements KeyEventDispatcher {

	private boolean newWindowAllowed = true;

	/**
	 * @param app
	 *            application
	 */
	public GlobalKeyDispatcherD(AppD app) {
		super(app);
	}

	/**
	 * This method is called by the current KeyboardFocusManager before they are
	 * dispatched to their targets, allowing it to handle the key event and
	 * consume it.
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		// ignore key events coming from text components (i.e. text fields and
		// text areas)
		// or key events coming from popups (source class = JRootPane)
		if (event.isConsumed() || event.getSource() instanceof JTextComponent
				|| event.getSource() instanceof JRootPane
				|| event.getSource() instanceof MathFieldD) {
			return false;
		}

		boolean consumed = false;
		switch (event.getID()) {
		default:
			// do nothing
			break;
		case KeyEvent.KEY_PRESSED:
			consumed = handleKeyPressed(event);
			break;

		case KeyEvent.KEY_TYPED:
			consumed = handleKeyTyped(event);
			break;

		case KeyEvent.KEY_RELEASED:
			newWindowAllowed = true;
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
	 *            event
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
		return handleSelectedGeosKeys(event, selection.getSelectedGeos());
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
		char ch = event.getKeyChar();
		if (!event.isMetaDown() && !event.isAltDown()
				&& !event.isControlDown()) {
			return renameStarted(ch);
		}
		return false;
	}

	/**
	 * Handles key event by disassembling it into primitive types and handling
	 * it using the mothod from common
	 * 
	 * @param event
	 *            event
	 * @return whether key was consumed
	 */
	public boolean handleGeneralKeys(KeyEvent event) {

		// use event.isAltDown rather than AppD.isControlDown(event)
		// as we need to distinguish <AltGr>2 and <Ctrl>2
		// #2390 #908
		return handleGeneralKeys(KeyCodes.translateJavacode(event.getKeyCode()),
				event.isShiftDown(), AppD.isControlDown(event),
				event.isAltDown(), event.getSource() instanceof JTable,
				event.getSource() instanceof EuclidianViewD);
	}

	private boolean handleSelectedGeosKeys(KeyEvent event,
			ArrayList<GeoElement> geos) {

		// use event.isAltDown rather than AppD.isAltDown(event)
		// as Ctrl-Arrow on OSX does something special
		// so we actually want to use Alt
		return handleSelectedGeosKeys(
				KeyCodes.translateJavacode(event.getKeyCode()), geos,
				event.isShiftDown(), AppD.isControlDown(event),
				event.isAltDown(), event.getSource() instanceof JTable);
	}

	@Override
	protected boolean handleEnter() {
		if (super.handleEnter()) {
			return true;
		}

		if (app.isUsingFullGui()
				&& app.getGuiManager().noMenusOpen()) {
			if (app.showAlgebraInput() && !((GuiManagerD) app.getGuiManager())
					.getAlgebraInput().hasFocus()) {
				// focus this frame (needed for external view windows)
				if (!app.isApplet() && ((AppD) app).getFrame() != null) {
					((AppD) app).getFrame().toFront();
				}

				((GuiManagerD) app.getGuiManager()).getAlgebraInput()
						.requestFocus();

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean handleTabDesktop(boolean isControlDown, boolean isShiftDown) {

		app.getActiveEuclidianView().closeDropdowns();

		if (isControlDown && app.isUsingFullGui()) {

			GuiManagerInterface gui = app.getGuiManager();
			((LayoutD) gui.getLayout()).getDockManager()
					.moveFocus(!isShiftDown);

			return true;

		}
		boolean useTab = app.getActiveEuclidianView().hasFocus()
				|| app.getAlgebraView().hasFocus();

		// make sure TAB works in Input Boxes but also in Spreadsheet, Input Bar
		Component owner = ((AppD) app).getFrame().getFocusOwner();
		if (owner instanceof AutoCompleteTextField
				&& ((AutoCompleteTextField) owner).usedForInputBox()) {
			useTab = true;
		}

		if (useTab) {
			EuclidianView ev = app.getActiveEuclidianView();

			ev.closeDropdowns();

			if (isShiftDown) {
				selection.selectPreviousGeo();
			} else {
				selection.selectNextGeo();
			}
			return true;
		}

		return false;
	}

	@Override
	protected void handleCopyCut(boolean cut) {
		if (!(((GuiManagerD) app.getGuiManager()).getSpreadsheetView()
				.hasFocus())
				&& !(((AlgebraInputD) ((GuiManagerD) app.getGuiManager())
						.getAlgebraInput()).getTextField().hasFocus())) {
			CopyPaste.handleCutCopy(app, cut);
		}
	}

	@Override
	protected void handleCtrlV() {
		if (!(((GuiManagerD) app.getGuiManager()).getSpreadsheetView()
				.hasFocus())
				&& !(((AlgebraInputD) ((GuiManagerD) app.getGuiManager())
						.getAlgebraInput()).getTextField().hasFocus())) {

			app.setWaitCursor();
			app.getCopyPaste().pasteFromXML(app);
			app.setDefaultCursor();
			tryPasteEquation();
		}
	}

	/**
	 * Handle pasted OOML (MS Office equation)
	 */
	protected void tryPasteEquation() {
		String html = ((GuiManagerD) app.getGuiManager())
				.getStringFromClipboard();
		if (html != null && html.indexOf("<m:oMath") > 0) {
			int blockBegin = html.indexOf("<m:oMathPara>");
			int blockEnd;
			if (blockBegin == -1) {
				blockBegin = html.indexOf("<m:oMath>");
				blockEnd = html.indexOf("</m:oMath>") + 10;
			} else {
				blockEnd = html.indexOf("</m:oMathPara>") + 14;
			}
			String mathml = OOMLConverter
					.oomlToMathml(html.substring(blockBegin, blockEnd)
							.replace('\n', ' ').replace('\r', ' '));
			app.getGgbApi().evalCommand(mathml);
		}
	}

	@Override
	protected boolean handleCtrlShiftN(boolean isAltDown) {
		ArrayList<GeoGebraFrame> ggbInstances = GeoGebraFrame.getInstances();
		int size = ggbInstances.size();
		if (size == 1) {
			// load next file in folder

			// ask if OK to discard current file
			if (app.isSaved() || ((AppD) app).saveCurrentFile()) {
				MyFileFilter fileFilter = new MyFileFilter();
				fileFilter.addExtension(FileExtensions.GEOGEBRA);
				File[] options = ((AppD) app).getCurrentPath()
						.listFiles(fileFilter);
				if (options == null) {
					return false;
				}
				// no current file, just load the first file in the
				// folder
				if (((AppD) app).getCurrentFile() == null) {
					if (options.length > 0) {
						((GuiManagerD) app.getGuiManager()).loadFile(options[0],
								false);
						return true;
					}
					return false;
				}
				TreeSet<File> sortedSet = new TreeSet<>(
						UtilD.getFileComparator());
				for (File option : options) {
					if (option.isFile()) {
						sortedSet.add(option);
					}
				}

				String currentFile = ((AppD) app).getCurrentFile().getName();

				Iterator<File> iterator = sortedSet.iterator();
				File fileToLoad = null;
				while (iterator.hasNext() && fileToLoad == null) {
					if (iterator.next().getName().equals(currentFile)) {
						// check if we're at the end
						if (iterator.hasNext()) {
							fileToLoad = iterator.next();
						} else {
							fileToLoad = options[0];
						}
					}
				}
				((GuiManagerD) app.getGuiManager()).loadFile(fileToLoad, false);
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
		JTextComponent textComponent = ((AlgebraInputD) ((GuiManagerD) app
				.getGuiManager()).getAlgebraInput()).getTextField();

		StringBuilder sb = new StringBuilder();
		sb.append('{');

		Iterator<GeoElement> it = geos.iterator();
		while (it.hasNext()) {
			sb.append(it.next().getFormulaString(StringTemplate.defaultTemplate,
					false));
			if (it.hasNext()) {
				sb.append(',');
			}
		}
		sb.append('}');
		textComponent.setText(sb.toString());
	}

	@Override
	protected void showPrintPreview(App app2) {
		GeoGebraMenuBar.showPrintPreview((AppD) app);
	}

	@Override
	protected void createNewWindow() {
		if (newWindowAllowed) {
			app.setWaitCursor();
			if (app instanceof AppD) {
				((AppD) app).createNewWindow();
			}
			app.setDefaultCursor();
			newWindowAllowed = false;
		}
	}

	@Override
	protected KeyCodes translateKey(int i) {
		return KeyCodes.translateJavacode(i);
	}
}
