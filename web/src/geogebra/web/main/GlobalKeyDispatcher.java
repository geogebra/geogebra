package geogebra.web.main;

import java.util.ArrayList;
import java.util.Iterator;

import geogebra.common.awt.Color;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.web.gui.app.GeoGebraFrame;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

public class GlobalKeyDispatcher extends
        geogebra.common.main.GlobalKeyDispatcher implements KeyPressHandler {

	private Application app;

	public GlobalKeyDispatcher(Application app) {
		this.app = app;
    }

	@Override
	public void handleFunctionKeyForAlgebraInput(int i, GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void onKeyPress(KeyPressEvent event) {
		//no it is private, but can be public, also it is void, but can return boolean as in desktop, if needed
	    dispatchEvent(event);
    }

	private void dispatchEvent(KeyPressEvent event) {
	    //we ust find out somethinkg here to identify the component that fired this, like class names for example,
		//id-s or data-param-attributes
		
		//we have keypress here only
		handleKeyPressed(event);
	    
    }

	private boolean handleKeyPressed(KeyPressEvent event) {
		// GENERAL KEYS:
		// handle ESC, function keys, zooming with Ctrl +, Ctrl -, etc.
		if (handleGeneralKeys(event)) {
					return true;
		}
		return false;
    }

	private boolean handleGeneralKeys(KeyPressEvent event) {
		boolean consumed = false;

		// ESC and function keys
		switch (event.getCharCode()) {
		case MyKeyCodes.KEY_ESCAPE:
			// ESC: set move mode
			app.setMoveMode();
			app.getActiveEuclidianView().getEuclidianController()
					.deletePastePreviewSelected();
			consumed = true;
			break;

		case MyKeyCodes.KEY_ENTER:
			// check not spreadsheet
		GWT.log("enter");
			/*if (!(event.getSource() instanceof JTable)) {

				// ENTER: set focus to input field
				if (app.isUsingFullGui() && app.getGuiManager().noMenusOpen()) {
					if (app.showAlgebraInput()
							&& !app.getGuiManager().getAlgebraInput()
									.hasFocus()) {
						// focus this frame (needed for external view windows)
						if (!app.isApplet() && app.getFrame() != null) {
							app.getFrame().toFront();
						}

						app.getGuiManager().getAlgebraInput().requestFocus();

						consumed = true;
					}
				}
			}*/
			break;

		// toggle boolean or run script when Spacebar pressed
		case MyKeyCodes.KEY_SPACE:
			// check not spreadsheet
			//TEMPORARY COMMENTED OUT TCO from here if (!(event.getSource() instanceof JTable)) {

				ArrayList<GeoElement> selGeos = app.getSelectedGeos();
				if (selGeos.size() == 1) {
					if (selGeos.get(0).isGeoBoolean()) {
						GeoBoolean geoBool = (GeoBoolean) selGeos.get(0);
						geoBool.setValue(!geoBool.getBoolean());
						geoBool.updateRepaint();
					} else {
						selGeos.get(0).runScripts(null);
					}

					consumed = true;

				}

			//TEMPORARY COMMENTED OUT}
			break;

		case MyKeyCodes.KEY_TAB:
			if (event.isControlKeyDown() && app.isUsingFullGui()) {
				consumed = true;
				//TCOGuiManager gui = app.getGuiManager();
				//TCOgui.getLayout().getDockManager()
				//TCO		.moveFocus(!event.isShiftDown());

			} else if (app.getActiveEuclidianView().hasFocus()
					/*TCO|| app.getGuiManager().getAlgebraView().hasFocus()*/) {
				if (event.isShiftKeyDown())
					app.selectLastGeo();
				else
					app.selectNextGeo();
				consumed = true;
			}

			break;

		// F9 updates construction
		// cmd-f9 on Mac OS
		case MyKeyCodes.KEY_F9:
			if (!app.isApplet() || app.isRightClickEnabled()) {
				app.getKernel().updateConstruction();
				app.setUnsaved();
				consumed = true;
			}
			break;
		}

		int keyCode = event.getCharCode();

		// make sure Ctrl-1/2/3 works on the Numeric Keypad even with Numlock
		// off
		// **** NB if NumLock on, event.isShiftDown() always returns false with
		// Numlock on!!! (Win 7)
		/*DO WE NEED THIS?if (event.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
			String keyText = KeyEvent.getKeyText(keyCode);
			if (keyText.equals("End")) {
				keyCode = KeyEvent.VK_1;
			} else if (keyText.equals("Down")) {
				keyCode = KeyEvent.VK_2;
			} else if (keyText.equals("Page Down")) {
				keyCode = KeyEvent.VK_3;
			}

		}*/

		// Ctrl key down (and not Alt, so that AltGr works for special
		// characters)
		if (/*TCO?Application.isControlDown(event)*/event.isControlKeyDown() && !event.isAltKeyDown()) {

			switch (keyCode) {
			case MyKeyCodes.KEY_1:
			case MyKeyCodes.KEY_NUMPAD1:
				// event.isShiftDown() doesn't work if NumLock on
				// however .isAltDown() stops AltGr-1 from working (| on some
				// keyboards)
				if (event.isShiftKeyDown()) {// || event.isAltDown()) {
				/*TCO	app.getGuiManager().setShowView(
							!app.getGuiManager().showView(
									AbstractApplication.VIEW_EUCLIDIAN),
							AbstractApplication.VIEW_EUCLIDIAN);*/
					consumed = true;

				} else if (!event.isAltKeyDown()) { // make sure not triggered on
													// AltGr
					// Ctrl-1: set objects back to the default size (for font
					// size 12)
					changeFontsAndGeoElements(app, 12, false);
					consumed = true;
				}
				break;

			case MyKeyCodes.KEY_NUMPAD2:
			case MyKeyCodes.KEY_2:
				// event.isShiftDown() doesn't work if NumLock on
				// however .isAltDown() stops AltGr-2 from working (superscript
				// 2 on some keyboards)
				if (event.isShiftKeyDown()) {// || event.isAltDown()) {
					/*TCOapp.getGuiManager().setShowView(
							!app.getGuiManager().showView(
									AbstractApplication.VIEW_EUCLIDIAN2),
							AbstractApplication.VIEW_EUCLIDIAN2);*/
					consumed = true;

				} else if (!event.isAltKeyDown()) { // make sure not triggered on
													// AltGr
					// Ctrl-2: large font size and thicker lines for projectors
					// etc
					int fontSize = Math.min(32, app.getFontSize() + 4);
					changeFontsAndGeoElements(app, fontSize, false);
					consumed = true;
				}
				break;

			case MyKeyCodes.KEY_NUMPAD3:
			case MyKeyCodes.KEY_3:
				// event.isShiftDown() doesn't work if NumLock on
				// however .isAltDown() stops AltGr-3 from working (^ on
				// Croatian keyboard)
				if (event.isShiftKeyDown()) { // || event.isAltDown()) {
					/*TCOapp.getGuiManager().setShowView(
							!app.getGuiManager().showView(
									AbstractApplication.VIEW_EUCLIDIAN3D),
							AbstractApplication.VIEW_EUCLIDIAN3D);*/
					consumed = true;

				} else if (!event.isAltKeyDown()) { // make sure not triggered on
													// AltGr
					// Ctrl-3: set black/white mode printing and visually
					// impaired users
					changeFontsAndGeoElements(app, app.getFontSize(), true);
					consumed = true;
				}
				break;

			case MyKeyCodes.KEY_C:
				// Ctrl-shift-c: copy graphics view to clipboard
				// should also work in applets with no menubar
				if (event.isShiftKeyDown()) {
					//TCOapp.copyGraphicsViewToClipboard();
					consumed = true;
				} else {
					// check not spreadsheet
					/*TCO if (!(event.getSource() instanceof JTable)
							&& !(app.getGuiManager().getSpreadsheetView()
									.hasFocus())
							&& !(((AlgebraInput) app.getGuiManager()
									.getAlgebraInput()).getTextField()
									.hasFocus())) {
						*/
						// Copy selected geos
						app.setWaitCursor();
						//TCO CopyPaste.copyToXML(app, app.getSelectedGeos());
						app.updateMenubar();
						app.setDefaultCursor();
						consumed = true;
					//TCO}
				}
				break;

			// Ctrl + H / G: Show Hide objects (labels)
			case MyKeyCodes.KEY_G:
			case MyKeyCodes.KEY_H:
				if (event.isShiftKeyDown())
					app.showHideSelectionLabels();
				else
					app.showHideSelection();
				consumed = true;
				break;

			// Ctrl + E: open object properties (needed here for spreadsheet)
			case MyKeyCodes.KEY_E:
				app.getDialogManager().showPropertiesDialog();
				consumed = true;
				break;

			// Ctrl + F: refresh views
			case MyKeyCodes.KEY_F:
				app.refreshViews();
				consumed = true;
				break;

			case MyKeyCodes.KEY_M:
				if (!app.isApplet() && event.isShiftKeyDown()) {
					//TCO app.exportToLMS();
					consumed = true;
				} else if (!app.isApplet() || app.isRightClickEnabled()) {
					//TCO app.setStandardView();
					consumed = true;
				}
				break;

			/*
			 * send next instance to front (alt - last)
			 */
			case MyKeyCodes.KEY_N:
				if (event.isShiftKeyDown()) {
					ArrayList<GeoGebraFrame> ggbInstances = GeoGebraFrame
							.getInstances();
					int size = ggbInstances.size();
					if (size == 1) {
						// load next file in folder

						// ask if OK to discard current file
						/*TCO if (app.isSaved() || app.saveCurrentFile()) {

							MyFileFilter fileFilter = new MyFileFilter();
							fileFilter.addExtension("ggb");

							File[] options = app.getCurrentPath().listFiles(
									fileFilter);

							// no current file, just load the first file in the
							// folder
							if (app.getCurrentFile() == null) {
								if (options.length > 0) {
									app.getGuiManager().loadFile(options[0],
											false);
									consumed = true;
								}
								break;
							}

							TreeSet<File> sortedSet = new TreeSet<File>(
									Util.getFileComparator());
							for (int i = 0; i < options.length; i++) {
								if (options[i].isFile())
									sortedSet.add(options[i]);
							}

							String currentFile = app.getCurrentFile().getName();

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

							app.getGuiManager().loadFile(fileToLoad, false);

							break;
						}
						for (int i = 0; i < size; i++) {
							GeoGebraFrame ggb = ggbInstances.get(i);
							Application application = ggb.getApplication();

							if (app == application) {
								int n = event.isAltKeyDown() ? ((i - 1 + size) % size)
										: ((i + 1) % size);
								ggb = ggbInstances.get(n); // next/last
															// instance
								ggb.toFront();
								ggb.requestFocus();
								break; // break from if loop

							}
						} TCO*/

						consumed = true;

					}
				}
				break;

			// needed for detached views and MacOS
			// Cmd + Y: Redo
			case MyKeyCodes.KEY_Y:
				//TCO app.getGuiManager().redo();
				consumed = true;
				break;

			// needed for detached views and MacOS
			// Ctrl + Z: Undo
			case MyKeyCodes.KEY_Z:
				/*TOC if (event.isShiftKeyDown())
					app.getGuiManager().redo();
				else
					app.getGuiManager().undo();
				consumed = true;*/
				break;

			case MyKeyCodes.KEY_V:
				// check not spreadsheet, not inputbar
				/*TCO if (!(event.getSource() instanceof JTable)
						&& !(app.getGuiManager().getSpreadsheetView()
								.hasFocus())
						&& !(((AlgebraInput) app.getGuiManager()
								.getAlgebraInput()).getTextField().hasFocus())) {

					app.setWaitCursor();
					CopyPaste.pasteFromXML(app);
					app.setDefaultCursor();
					consumed = true;
				}*/
				break;

			// ctrl-R updates construction
			// make sure it works in applets without a menubar
			case MyKeyCodes.KEY_R:
				if (!app.isApplet() || app.isRightClickEnabled()) {
					app.getKernel().updateConstruction();
					app.setUnsaved();
					consumed = true;
				}
				break;

			// ctrl-shift-s (toggle spreadsheet)
			case MyKeyCodes.KEY_S:
				/*TCO if (event.isShiftKeyDown() && app.isUsingFullGui()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(
									AbstractApplication.VIEW_SPREADSHEET),
							AbstractApplication.VIEW_SPREADSHEET);
					consumed = true;
				}*/
				break;

			// Ctrl-(shift)-Q (deprecated - doesn't work on MacOS)
			// Ctrl-(shift)-J
			case MyKeyCodes.KEY_J:
			case MyKeyCodes.KEY_Q:
				if (event.isShiftKeyDown())
					app.selectAllDescendants();
				else
					app.selectAllPredecessors();
				consumed = true;
				break;

			// Ctrl + "+", Ctrl + "-" zooms in or out in graphics view
			case MyKeyCodes.KEY_PLUS:
			case MyKeyCodes.KEY_MINUS:
			case MyKeyCodes.KEY_EQUALS:

				// disable zooming in PEN mode
				if (app.getActiveEuclidianView().getMode() != EuclidianConstants.MODE_PEN
						|| app.getActiveEuclidianView().getMode() != EuclidianConstants.MODE_FREEHAND) {

					/*TCO boolean spanish = app.getLocale().toString()
							.startsWith("es");
					// AltGr+ on Spanish keyboard is ] so
					// allow <Ctrl>+ (zoom) but not <Ctrl><Alt>+ (fast zoom)
					// from eg Input Bar
					if (!spanish || !event.isAltKeyDown()
							|| (event.getSource() instanceof EuclidianView)) {
						((EuclidianView) app.getActiveEuclidianView())
								.getEuclidianController().zoomInOut(event);
						app.setUnsaved();
						consumed = true;
					}*/
				}
				break;

			// Ctrl + D: toggles algebra style: value, definition, command
			case MyKeyCodes.KEY_D:
			//??case KeyEvent.VK_BACK_QUOTE:
				if (!event.isShiftKeyDown()) {
					Kernel kernel = app.getKernel();
					kernel.setAlgebraStyle((kernel.getAlgebraStyle() + 1) % 3);
					kernel.updateConstruction();
					app.setUnsaved();
					consumed = true;
					break;
				}
			}
		}

		return consumed;
    }
	
	/**
	 * Changes the font size of the user interface and construction element
	 * styles (thickness, size) for a given fontSize.
	 * 
	 * @param app
	 * @param fontSize
	 *            12-32pt
	 * @param blackWhiteMode
	 *            whether only black should be used as a color
	 * @return whether change was performed
	 */
	public static boolean changeFontsAndGeoElements(Application app,
			int fontSize, boolean blackWhiteMode) {
		if (app.isApplet())
			return false;

		app.setWaitCursor();

		// determine styles
		// set new default line thickness
		int oldFontSize = app.getFontSize();
		int angleSizeIncr = fontSize - oldFontSize;
		int incr = getPointSizeInc(oldFontSize, fontSize);

		// construction defaults
		ConstructionDefaults cd = app.getKernel().getConstruction()
				.getConstructionDefaults();
		cd.setDefaultLineThickness(EuclidianStyleConstants.DEFAULT_LINE_THICKNESS
				+ incr);
		cd.setDefaultPointSize(EuclidianStyleConstants.DEFAULT_POINT_SIZE
				+ incr);
		cd.setDefaultAngleSize(EuclidianStyleConstants.DEFAULT_ANGLE_SIZE
				+ angleSizeIncr);
		// blackWhiteMode: set defaults for new GeoElements
		cd.setBlackWhiteMode(blackWhiteMode);

		// change application font size
		app.setFontSize(fontSize);
		if (app.isUsingFullGui()) {
			//TCO app.getGuiManager().updateSpreadsheetColumnWidths();
		}
		// apply styles to to selected or all geos
		Iterator<GeoElement> it = null;
		if (app.getSelectedGeos().size() == 0) {
			// change all geos
			it = app.getKernel().getConstruction().getGeoSetConstructionOrder()
					.iterator();
		} else {
			// just change selected geos
			it = app.getSelectedGeos().iterator();
		}
		while (it.hasNext()) {
			GeoElement geo = it.next();
			setGeoProperties(geo, incr, incr, angleSizeIncr, blackWhiteMode);
		}

		app.getKernel().updateConstruction();
		app.setUnsaved();
		app.storeUndoInfo();

		app.setDefaultCursor();
		return true;
	}
	
	private static int getPointSizeInc(int oldFontSize, int newFontSize) {
		if (oldFontSize == newFontSize)
			return 0;
		int step = newFontSize > oldFontSize ? 1 : -1;

		int left = Math.min(oldFontSize, newFontSize);
		int right = Math.max(oldFontSize, newFontSize);
		int[] borders = { 16, 22, 28 };
		int incr = 0;
		for (int i = 0; i < borders.length; i++) {
			if (left < borders[i] && borders[i] <= right) {
				incr = incr + step;
			}
		}

		return incr;
	}

	private static void setGeoProperties(GeoElement geo, int lineThicknessIncr,
			int pointSizeIncr, int angleSizeIncr, boolean blackWhiteMode) {
		if (!geo.isGeoText() && !geo.isGeoImage() && !geo.isGeoPolygon()) { // affects
																			// bounding
																			// box
			int lineThickness = Math.max(0, geo.getLineThickness()
					+ lineThicknessIncr);
			geo.setLineThickness(lineThickness);
		}

		if (geo instanceof PointProperties) {
			PointProperties p = (PointProperties) geo;
			int pointSize = Math.max(0, p.getPointSize() + pointSizeIncr);
			p.setPointSize(pointSize);
		}

		if (geo.isGeoAngle()) {
			GeoAngle angle = (GeoAngle) geo;
			int angleSize = Math.max(0, angle.getArcSize() + angleSizeIncr);
			angle.setArcSize(angleSize);
		}

		if (blackWhiteMode) {
			geo.setAlphaValue(0f);
			geo.setObjColor(Color.BLACK);
		}
	}

}
