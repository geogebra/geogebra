package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.draw.DrawList;
import org.geogebra.common.euclidian.draw.DrawTextField;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.Furniture;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoTextField;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.CopyPaste;

/**
 * Handles keyboard events. This class only dispatches
 */
public abstract class GlobalKeyDispatcher {

	public GlobalKeyDispatcher(App app2) {
		this.app = app2;
		this.selection = app.getSelectionManager();
	}

	/**
	 * Handle Fx keys for input bar when geo is selected
	 * 
	 * @param i
	 *            x when Fx is pressed
	 * @param geo
	 *            selected geo
	 */
	public abstract void handleFunctionKeyForAlgebraInput(int i, GeoElement geo);

	/** application */
	protected final App app;
	protected final SelectionManager selection;

	private TreeSet<AlgoElement> tempSet;

	/**
	 * @return temporary set of algos
	 */
	protected TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElement>();
		}
		return tempSet;
	}

	private Coords tempVec;

	protected boolean renameStarted(char ch) {
		GeoElement geo;
		if (selection.selectedGeosSize() == 1) {
			// selected geo
			geo = selection.getSelectedGeos().get(0);
		} else {
			// last created geo
			geo = app.getLastCreatedGeoElement();
		}

		// show RENAME dialog when a letter is typed
		// or edit Textfield for any keypress

		if ((Character.isLetter(ch)) || geo instanceof GeoTextField) {

			// open rename dialog
			if (geo != null && geo.isRenameable()) {

				if (geo instanceof GeoTextField) {
					DrawTextField dt = (DrawTextField) app
							.getActiveEuclidianView().getDrawableFor(geo);
					dt.setFocus(ch + "");
				} else {
					app.getDialogManager().showRenameDialog(geo, true,
							Character.toString(ch), false);
				}
				return true;
			}
		}

		return false;
	}

	private boolean handleArrowsForDropdown(ArrayList<GeoElement> geos,
			boolean down) {
		if (geos.size() == 1 && geos.get(0).isGeoList()) {
			DrawList.asDrawable(app, geos.get(0)).moveSelection(down);
			return true;
		}
		return false;
	}

	/**
	 * Tries to move the given objects after pressing an arrow key on the
	 * keyboard.
	 * 
	 * @param geos
	 *            moved geos
	 * @param xdiff
	 *            translation in x direction
	 * @param ydiff
	 *            translation in y direction
	 * @param zdiff
	 *            translation in z direction
	 * 
	 * @return whether any object was moved
	 */
	protected boolean handleArrowKeyMovement(ArrayList<GeoElement> geos,
			double xdiff, double ydiff, double zdiff) {
		GeoElement geo = geos.get(0);

		boolean allSliders = true;
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geoi = geos.get(i);
			if (!geoi.isGeoNumeric() || !geoi.isChangeable()) {
				allSliders = false;
				continue;
			}
		}

		// don't move sliders, they will be handled later
		if (allSliders) {
			return false;
		}

		// set translation vector
		if (tempVec == null)
			tempVec = new Coords(4); // 4 coords for 3D
		double xd = geo.getAnimationStep() * xdiff;
		double yd = geo.getAnimationStep() * ydiff;
		double zd = geo.getAnimationStep() * zdiff;
		tempVec.setX(xd);
		tempVec.setY(yd);
		tempVec.setZ(zd);

		// move objects
		boolean moved = GeoElement.moveObjects(geos, tempVec, null, null, null);

		// nothing moved
		if (!moved) {
			for (int i = 0; i < geos.size(); i++) {
				geo = geos.get(i);
				// toggle boolean value
				if (geo.isChangeable() && geo.isGeoBoolean()) {
					GeoBoolean bool = (GeoBoolean) geo;
					bool.setValue(!bool.getBoolean());
					bool.updateCascade();
					moved = true;
				}
			}
		}

		if (moved)
			app.getKernel().notifyRepaint();

		return moved;
	}

	/**
	 * Handles general keys like ESC and function keys that don't involved
	 * selected GeoElements.
	 * 
	 * @param key
	 *            key code
	 * @param isShiftDown
	 *            whether shift is down
	 * @param isControlDown
	 *            whether control is down
	 * @param isAltDown
	 *            whether alt is down
	 * @param fromSpreadsheet
	 *            whether this event comes from spreadsheet
	 * @param fromEuclidianView
	 *            whether this event comes from EV
	 * 
	 * @return if key was consumed
	 */
	protected boolean handleGeneralKeys(KeyCodes key, boolean isShiftDown,
			boolean isControlDown, boolean isAltDown, boolean fromSpreadsheet,
			boolean fromEuclidianView) {

		// eventually make an undo point (e.g. after zooming)
		app.storeUndoInfoIfSetCoordSystemOccured();

		boolean consumed = false;

		// ESC and function keys
		switch (key) {
		case ESCAPE:
			// ESC: set move mode
			if (app.isApplet() && !app.showToolBar()) {
				app.loseFocus();
			} else {
				app.setMoveMode();
				app.getActiveEuclidianView().getEuclidianController()
						.deletePastePreviewSelected();
			}
			consumed = true;
			break;

		case ENTER:
			// check not spreadsheet
			if (!fromSpreadsheet) {

				// ENTER: set focus to input field
				consumed = handleEnter();

			}
			break;

		// toggle boolean or run script when Spacebar pressed
		case SPACE:
			// check not spreadsheet
			if (!fromSpreadsheet) {
				consumed = app.handleSpaceKey();
			}
			break;

		case TAB:

			consumed = handleTab(isControlDown, isShiftDown, true);

			break;

		// open Tool Help
		case F1:
			app.getDialogManager().openToolHelp();
			return true;

			// F9 updates construction
			// cmd-f9 on Mac OS
		case F9:
			if (!app.isApplet() || app.isRightClickEnabled()) {
				app.getKernel().updateConstruction();
				app.setUnsaved();
				consumed = true;
			}
			break;
		}

		/*
		 * // make sure Ctrl-1/2/3 works on the Numeric Keypad even with Numlock
		 * // off // **** NB if NumLock on, event.isShiftDown() always returns
		 * false with // Numlock on!!! (Win 7) if (event.getKeyLocation() ==
		 * KeyEvent.KEY_LOCATION_NUMPAD) { String keyText =
		 * KeyEvent.getKeyText(keyCode); if (keyText.equals("End")) { keyCode =
		 * KeyEvent.VK_1; } else if (keyText.equals("Down")) { keyCode =
		 * KeyEvent.VK_2; } else if (keyText.equals("Page Down")) { keyCode =
		 * KeyEvent.VK_3; }
		 * 
		 * }
		 */

		// Ctrl key down (and not Alt, so that AltGr works for special
		// characters)
		if (isControlDown && !isAltDown) {

			switch (key) {
			case K1:
			case NUMPAD1:
				// event.isShiftDown() doesn't work if NumLock on
				// however .isAltDown() stops AltGr-1 from working (| on some
				// keyboards)
				if (isShiftDown && app.getGuiManager() != null) {// ||
																	// event.isAltDown())
																	// {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(App.VIEW_EUCLIDIAN),
							App.VIEW_EUCLIDIAN);
					consumed = true;

				} else if (!isAltDown) { // make sure not triggered on
					// AltGr
					// Ctrl-1: set objects back to the default size (for font
					// size 12)
					changeFontsAndGeoElements(app, 12, false, false);
					consumed = true;
				}
				break;

			case NUMPAD2:
			case K2:
				// event.isShiftDown() doesn't work if NumLock on
				// however .isAltDown() stops AltGr-2 from working (superscript
				// 2 on some keyboards)
				if (isShiftDown && app.getGuiManager() != null) {// ||
																	// event.isAltDown())
																	// {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(App.VIEW_EUCLIDIAN2),
							App.VIEW_EUCLIDIAN2);
					consumed = true;

				} else if (!isAltDown) { // make sure not triggered on
					// AltGr
					// Ctrl-2: large font size and thicker lines for projectors
					// etc
					int fontSize = Math.min(32, app.getFontSize() + 4);
					changeFontsAndGeoElements(app, fontSize, false, true);
					consumed = true;
				}
				break;

			case NUMPAD3:
			case K3:
				// event.isShiftDown() doesn't work if NumLock on
				// however .isAltDown() stops AltGr-3 from working (^ on
				// Croatian keyboard)
				if (isShiftDown && app.getGuiManager() != null) { // ||
																	// event.isAltDown())
																	// {
					app.getGuiManager()
							.setShowView(
									!app.getGuiManager().showView(
											App.VIEW_EUCLIDIAN3D),
									App.VIEW_EUCLIDIAN3D);
					consumed = true;

				} else if (!isAltDown) { // make sure not triggered on
					// AltGr
					// Ctrl-3: set black/white mode printing and visually
					// impaired users
					changeFontsAndGeoElements(app, app.getFontSize(), true,
							true);
					consumed = true;
				}
				break;

			case A:
				if (isShiftDown) {
					if (app.isUsingFullGui() && app.getGuiManager() != null) {
						app.getGuiManager()
								.setShowView(
										!app.getGuiManager().showView(
												App.VIEW_ALGEBRA),
										App.VIEW_ALGEBRA);
						consumed = true;
					}
				} else {
					selection.selectAll(-1);
					consumed = true;
				}
				break;

			case K:
				if (isShiftDown) {
					if (app.isUsingFullGui() && app.getGuiManager() != null) {
						app.getGuiManager().setShowView(
								!app.getGuiManager().showView(App.VIEW_CAS),
								App.VIEW_CAS);
						consumed = true;
					}
				}
				break;

			case L:
				if (isShiftDown) {
					if (app.isUsingFullGui() && app.getGuiManager() != null) {
						app.getGuiManager().setShowView(
								!app.getGuiManager().showView(
										App.VIEW_CONSTRUCTION_PROTOCOL),
								App.VIEW_CONSTRUCTION_PROTOCOL);
						consumed = true;
					}
				} else {
					selection.selectAll(selection.getSelectedLayer());
					consumed = true;
				}
				break;

			case O: // File -> Open
				if (!isShiftDown && app.getGuiManager() != null) {
					app.getGuiManager().openFile();
					consumed = true;
				}
				break;
			case P:

				if (isShiftDown) {
					// toggle Probability View
					if (app.isUsingFullGui() && app.getGuiManager() != null) {
						app.getGuiManager().setShowView(
								!app.getGuiManager().showView(
										App.VIEW_PROBABILITY_CALCULATOR),
								App.VIEW_PROBABILITY_CALCULATOR);
					}
				} else {
					showPrintPreview(app);
				}
				consumed = true;

				break;
			case T: // File -> Export -> PSTricks
				if (isShiftDown && app.getGuiManager() != null) {
					app.getGuiManager().showPSTricksExport();
					consumed = true;
				}
				break;
			case W: // File -> Export -> Webpage
				if (isShiftDown && app.getGuiManager() != null) {
					app.getGuiManager().showWebpageExport();
					consumed = true;
				} else {
					// File -> Close (under Mac: Command-W)
					app.exitAll();
					// Under Ubuntu/Unity this will close all windows.
					consumed = true;
				}
				break;
			case F4: // File -> Exit
				if (!isShiftDown) {
					app.exitAll();
					consumed = true;
				}
				break;

			case I: // Edit -> Invert Selection
				if (!isShiftDown) {
					selection.invertSelection();
					consumed = true;
				}
				break;

			case C:
				// Ctrl-shift-c: copy graphics view to clipboard
				// should also work in applets with no menubar
				if (isShiftDown) {
					app.copyGraphicsViewToClipboard();
					consumed = true;
				} else {
					// check not spreadsheet
					if (!fromSpreadsheet) {
						handleCtrlC();
					}

				}
				break;
			case M:
				if (isShiftDown) {
					app.copyFullHTML5ExportToClipboard();
				} else {
					// Ctrl-M: standard view
					app.setStandardView();
				}
				break;

			case B:
				// copy base64 string to clipboard
				if (isShiftDown) {
					app.copyBase64ToClipboard();
				}
				break;

			// Ctrl + H / G: Show Hide objects (labels)
			case G:
			case H:
				if (isShiftDown)
					selection.showHideSelectionLabels();
				else
					selection.showHideSelection();
				consumed = true;
				break;

			// Ctrl + E: open object properties (needed here for spreadsheet)
			case E:
				if (app.isUsingFullGui() && app.getGuiManager() != null) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(App.VIEW_PROPERTIES),
							App.VIEW_PROPERTIES, false);
				}
				consumed = true;
				break;

			// Ctrl + F: refresh views
			case F:
				app.refreshViews();
				consumed = true;
				break;

			/*
			 * send next instance to front (alt - last)
			 */
			case N:
				if (isShiftDown) {
					handleCtrlShiftN(isAltDown);
				} else {
					app.setWaitCursor();
					createNewWindow();
					app.setDefaultCursor();
				}
				break;

			// needed for detached views and MacOS
			// Ctrl + Z: Undo
			case Z:
				if (app.getGuiManager() != null) {
					if (isShiftDown)
						app.getGuiManager().redo();
					else
						app.getGuiManager().undo();
				}
				consumed = true;
				break;

			case U:
				if (isShiftDown && app.getGuiManager() != null) {
					app.getGuiManager().showGraphicExport();
					consumed = true;
				}
				break;

			case V:
				// check not spreadsheet, not inputbar
				if (!(fromSpreadsheet)) {
					handleCtrlV();
				}
				break;

			// ctrl-R updates construction
			// make sure it works in applets without a menubar
			case R:
				if (!app.isApplet() || app.isRightClickEnabled()) {
					app.getKernel().updateConstruction();
					app.setUnsaved();
					consumed = true;
				}
				break;

			// ctrl-shift-s (toggle spreadsheet)
			case S:
				if (isShiftDown) {
					if (app.isUsingFullGui() && app.getGuiManager() != null) {
						app.getGuiManager().setShowView(
								!app.getGuiManager().showView(
										App.VIEW_SPREADSHEET),
								App.VIEW_SPREADSHEET);
						consumed = true;
					}
				} else if (app.getGuiManager() != null) {
					app.getGuiManager().save();
					consumed = true;
				}
				break;

			case Y:
				if (isShiftDown) {
					// if (app.isUsingFullGui() && app.getGuiManager() != null)
					// {
					// app.getGuiManager().setShowView(
					// !app.getGuiManager().showView(
					// App.VIEW_PYTHON),
					// App.VIEW_PYTHON);
					// consumed = true;
					// }
				} else if (app.getGuiManager() != null) {
					// needed for detached views and MacOS
					// Cmd + Y: Redo

					app.getGuiManager().redo();
					consumed = true;
				}
				break;

			// Ctrl-(shift)-Q (deprecated - doesn't work on MacOS)
			// Ctrl-(shift)-J
			case J:
			case Q:
				if (isShiftDown)
					selection.selectAllDescendants();
				else
					selection.selectAllPredecessors();
				consumed = true;
				break;

			// Ctrl + "+", Ctrl + "-" zooms in or out in graphics view
			case PLUS:
			case ADD:
			case SUBTRACT:
			case MINUS:
			case EQUALS:

				// in Chrome and IE11, both the applet and the
				// browser are zoomed
				// even when the applet has focus
				if (app.isHTML5Applet()) {
					break;
				}

				// disable zooming in PEN mode
				if (!EuclidianView.isPenMode(app.getActiveEuclidianView()
						.getMode())) {

					boolean spanish = app.getLocalization().getLanguage()
							.startsWith("es");

					// AltGr+ on Spanish keyboard is ] so
					// allow <Ctrl>+ (zoom) but not <Ctrl><Alt>+ (fast zoom)
					// from eg Input Bar
					if (!spanish || !isAltDown || (fromEuclidianView)) {
						(app.getActiveEuclidianView())
								.getEuclidianController()
								.zoomInOut(
										isAltDown,
										key.equals(KeyCodes.MINUS)
												|| key.equals(KeyCodes.SUBTRACT));
						app.setUnsaved();
						consumed = true;
					}
				}
				break;

			// Ctrl + D: toggles algebra style: value, definition, command
			case D:
			case BACK_QUOTE:
				if (!isShiftDown) {
					Kernel kernel = app.getKernel();
					kernel.setAlgebraStyle((kernel.getAlgebraStyle() + 1) % 3);
					kernel.updateConstruction();
					if (app.hasOptionsMenu()) {
						app.getOptionsMenu(null).updateMenuViewDescription();
					}
					app.setUnsaved();
					consumed = true;
				} else {

					// Ctrl-Shift-D
					// toggle "selection allowed" for all objects
					// except visible sliders, unfixed points, buttons,
					// checkboxes, InputBoxes, drop-down lists

					// what to set all objects to
					boolean selectionAllowed = false;

					// check if any geos already have selectionAllowed = false
					// if so then we will set all to be true
					TreeSet<GeoElement> objects = app.getKernel()
							.getConstruction().getGeoSetConstructionOrder();
					Iterator<GeoElement> it = objects.iterator();
					while (it.hasNext()) {
						GeoElement geo = it.next();
						if (!geo.isSelectionAllowed()) {
							selectionAllowed = true;
							break;
						}
					}

					it = objects.iterator();
					while (it.hasNext()) {
						GeoElement geo = it.next();

						if (geo instanceof Furniture
								|| (geo.isGeoNumeric() && ((GeoNumeric) geo)
										.isSlider())
								|| (geo.isGeoList() && ((GeoList) geo)
										.drawAsComboBox())
								|| geo.isGeoBoolean()
								|| (geo.isGeoPoint() && !geo.isFixed())) {

							geo.setSelectionAllowed(true);
						} else {
							geo.setSelectionAllowed(selectionAllowed);
						}
					}

				}
				break;
			}
		}

		return consumed;
	}

	/**
	 * Creates new GGB window
	 */
	protected abstract void createNewWindow();

	/**
	 * Opens print preview dialog
	 * 
	 * @param app2
	 *            application
	 */
	protected abstract void showPrintPreview(App app2);

	/**
	 * Handles Ctrl+V; overridden in desktop Default implementation pastes from
	 * XML and returns true
	 */
	protected void handleCtrlV() {
		app.setWaitCursor();
		CopyPaste.INSTANCE.pasteFromXML(app, false);
		app.setDefaultCursor();
	}

	/**
	 * @param isAltDown
	 *            whether alt is down
	 * @return whether keys were consumed
	 */
	protected abstract boolean handleCtrlShiftN(boolean isAltDown);

	/**
	 * overridden in desktop Default implementation pastes from XML and returns
	 * true
	 */
	protected void handleCtrlC() {
		// Copy selected geos
		app.setWaitCursor();
		CopyPaste.INSTANCE.copyToXML(app, selection.getSelectedGeos(), false);
		app.updateMenubar();
		app.setDefaultCursor();
	}

	/**
	 * @param isControlDown
	 *            whether control is down
	 * @param isShiftDown
	 *            whether shift is down
	 * @return whether key was consumed
	 */
	public boolean handleTab(boolean isControlDown, boolean isShiftDown, boolean cycle) {
		if (app.has(Feature.DRAW_DROPDOWNLISTS_TO_CANVAS)) {
			app.getActiveEuclidianView().closeDropdowns();
		}
		if (isShiftDown) {
			selection.selectLastGeo(app.getActiveEuclidianView());
		} else {
			selection.selectNextGeo(app.getActiveEuclidianView(), cycle);
		}

		return true;
	}

	/**
	 * @return handles enter
	 */
	protected abstract boolean handleEnter();

	/**
	 * Changes the font size of the user interface and construction element
	 * styles (thickness, size) for a given fontSize.
	 * 
	 * @param app
	 *            application
	 * @param fontSize
	 *            12-32pt
	 * @param blackWhiteMode
	 *            whether only black should be used as a color
	 * @param makeAxesBold
	 *            force bold / not bold
	 * @return whether change was performed
	 */
	public static boolean changeFontsAndGeoElements(App app, int fontSize,
			boolean blackWhiteMode, boolean makeAxesBold) {
		if (app.isApplet())
			return false;

		app.setWaitCursor();

		// axes bold / not bold
		for (int ev = 1; ev <= 2; ev++) {
			EuclidianSettings settings = app.getSettings().getEuclidian(ev);
			int style = settings.getAxesLineStyle();

			// set bold
			style = style | EuclidianStyleConstants.AXES_BOLD;

			if (!makeAxesBold) {
				// turn bold off again
				style = style ^ EuclidianStyleConstants.AXES_BOLD;
			}

			settings.setAxesLineStyle(style);
		}

		// determine styles
		// set new default line thickness
		int oldFontSize = app.getFontSize();
		int angleSizeIncr = fontSize - oldFontSize;
		int incr = getPointSizeInc(oldFontSize, fontSize);

		// construction defaults
		ConstructionDefaults cd = app.getKernel().getConstruction()
				.getConstructionDefaults();
		cd.setDefaultLineThickness(cd.getDefaultLineThickness() + incr);
		cd.setDefaultPointSize(cd.getDefaultPointSize() + incr,
				cd.getDefaultDependentPointSize()
				+ incr);
		cd.setDefaultAngleSize(cd.getDefaultAngleSize() + angleSizeIncr);
		// blackWhiteMode: set defaults for new GeoElements
		cd.setBlackWhiteMode(blackWhiteMode);

		// change application font size
		app.setFontSize(fontSize, true);
		if (app.isUsingFullGui() && app.getGuiManager() != null)
			app.getGuiManager().updateSpreadsheetColumnWidths();

		// apply styles to to selected or all geos
		Iterator<GeoElement> it = null;
		if (app.getSelectionManager().getSelectedGeos().size() == 0) {
			// change all geos
			it = app.getKernel().getConstruction().getGeoSetConstructionOrder()
					.iterator();
		} else {
			// just change selected geos
			it = app.getSelectionManager().getSelectedGeos().iterator();
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

		return incr * 2;
	}

	private static void setGeoProperties(GeoElement geo, int lineThicknessIncr,
			int pointSizeIncr, int angleSizeIncr, boolean blackWhiteMode) {
		if (!geo.isGeoText() && !geo.isGeoImage() && !geo.isGeoPolygon()) { // affects
			// bounding
			// box
			int lineThickness = Math.max(2, geo.getLineThickness()
					+ lineThicknessIncr);
			geo.setLineThickness(lineThickness);
		}

		if (geo instanceof PointProperties) {
			PointProperties p = (PointProperties) geo;
			int pointSize = Math.max(2, p.getPointSize() + pointSizeIncr);
			p.setPointSize(pointSize);
		}

		if (geo.isGeoAngle()) {
			GeoAngle angle = (GeoAngle) geo;
			int angleSize = Math.max(2, angle.getArcSize() + angleSizeIncr);
			angle.setArcSize(angleSize);
		}

		if (blackWhiteMode) {
			geo.setAlphaValue(0f);
			geo.setObjColor(GColor.BLACK);
		}
	}

	/**
	 * Handle pressed key for selected GeoElements
	 * 
	 * @param key
	 *            key code
	 * @param geos
	 *            selected geos
	 * @param isShiftDown
	 *            whether shift is down
	 * @param isControlDown
	 *            whether control is down
	 * @param isAltDown
	 *            whether alt is down
	 * @param fromSpreadsheet
	 *            whether this event comes from spreadsheet
	 * 
	 * @return if key was consumed
	 */
	protected boolean handleSelectedGeosKeys(KeyCodes key,
			ArrayList<GeoElement> geos, boolean isShiftDown,
			boolean isControlDown, boolean isAltDown, boolean fromSpreadsheet) {

		// SPECIAL KEYS
		double changeValX = 0; // later: changeVal = base or -base
		double changeValY = 0; // later: changeVal = base or -base
		double changeValZ = 0; // later: changeVal = base or -base
		// Shift : base = 0.1
		// Default : base = 1
		// Ctrl : base = 10
		// Alt : base = 100
		double base = 1;
		if (isShiftDown)
			base = 0.1;
		if (isControlDown)
			base = 10;
		if (isAltDown)
			base = 100;
		// App.debug("key pressed");
		if (geos == null || geos.size() == 0) {

			// Get the EuclidianView which has the focus
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			int width = ev.getWidth();
			int height = ev.getHeight();
			if (ev.hasFocus() && app.isShiftDragZoomEnabled())
				switch (key) {

				case PAGEUP:
					ev.rememberOrigins();
					ev.pageUpDownTranslateCoordSystem((int) (height * base));
					return true;
				case PAGEDOWN:
					ev.rememberOrigins();
					ev.pageUpDownTranslateCoordSystem(-(int) (height * base));
					return true;
				case INSERT:
					ev.rememberOrigins();
					ev.translateCoordSystemInPixels((int) (height * base), 0,
							0, EuclidianController.MOVE_VIEW);
					return true;
				case HOME:
					ev.rememberOrigins();
					ev.translateCoordSystemInPixels(-(int) (height * base), 0,
							0, EuclidianController.MOVE_VIEW);
					return true;
				case DOWN:

					if (app.isUsingFullGui() && app.getGuiManager() != null
							&& app.getGuiManager().noMenusOpen()) {
						if (isShiftDown) {
							EuclidianViewInterfaceCommon view = app
									.getActiveEuclidianView();
							if (!view.isLockedAxesRatio()) {
								view.setCoordSystem(view.getXZero(),
										view.getYZero(), view.getXscale(),
										view.getYscale() * 0.9);
							}

						} else {
							ev.rememberOrigins();
							ev.translateCoordSystemInPixels(0,
									(int) (height / 100.0 * base), 0,
									EuclidianController.MOVE_VIEW);
						}
						return true;
					}

					break;

				case UP:

					if (app.isUsingFullGui() && app.getGuiManager() != null
							&& app.getGuiManager().noMenusOpen()) {
						if (isShiftDown) {
							EuclidianViewInterfaceCommon view = app
									.getActiveEuclidianView();
							if (!view.isLockedAxesRatio()) {
								view.setCoordSystem(view.getXZero(),
										view.getYZero(), view.getXscale(),
										view.getYscale() / 0.9);
							}

						} else {
							ev.rememberOrigins();
							ev.translateCoordSystemInPixels(0,
									-(int) (height / 100.0 * base), 0,
									EuclidianController.MOVE_VIEW);
						}
						return true;
					}
					break;

				case LEFT:

					if (app.isUsingFullGui() && app.getGuiManager() != null
							&& app.getGuiManager().noMenusOpen()) {
						if (isShiftDown) {
							EuclidianViewInterfaceCommon view = app
									.getActiveEuclidianView();
							if (!view.isLockedAxesRatio()) {
								view.setCoordSystem(view.getXZero(),
										view.getYZero(),
										view.getXscale() * 0.9,
										view.getYscale());
							}
						} else {
							ev.rememberOrigins();
							ev.translateCoordSystemInPixels(
									-(int) (width / 100.0 * base), 0, 0,
									EuclidianController.MOVE_VIEW);
						}
						return true;

					}
					break;

				case RIGHT:

					if (app.isUsingFullGui() && app.getGuiManager() != null
							&& app.getGuiManager().noMenusOpen()) {
						if (isShiftDown) {
							EuclidianViewInterfaceCommon view = app
									.getActiveEuclidianView();
							if (!view.isLockedAxesRatio()) {
								view.setCoordSystem(view.getXZero(),
										view.getYZero(),
										view.getXscale() / 0.9,
										view.getYscale());
							}
						} else {
							ev.rememberOrigins();
							ev.translateCoordSystemInPixels(
									(int) (width / 100.0 * base), 0, 0,
									EuclidianController.MOVE_VIEW);
						}
					}
					return true;
				}

			return false;
		}

		Iterator<GeoElement> it;

		// FUNCTION and DELETE keys
		switch (key) {

		case PAGEUP:
			// 3D handled later (move object up/down)
			if (!app.getActiveEuclidianView().isEuclidianView3D()) {
				it = geos.iterator();
				while (it.hasNext()) {
					GeoElement geo = it.next();
					geo.setLayer(geo.getLayer() + 1);
				}
			}
			break;

		case PAGEDOWN:
			// 3D handled later (move object up/down)
			if (!app.getActiveEuclidianView().isEuclidianView3D()) {
				it = geos.iterator();
				while (it.hasNext()) {
					GeoElement geo = it.next();
					geo.setLayer(geo.getLayer() - 1);
				}
			}
			break;

		case F3:
			// F3 key: copy definition to input field
			if (geos.size() == 1)
				handleFunctionKeyForAlgebraInput(3, geos.get(0));
			else {
				// F3 key: copy definitions to input field as list
				copyDefinitionsToInputBarAsList(geos);
				break;

			}
			return true;

		case F1:
			app.getDialogManager().openToolHelp();
			return true;

		case F4:
			// F4 key: copy value to input field
			handleFunctionKeyForAlgebraInput(4, geos.get(0));
			return true;

		case F5:
			// F5 key: copy label to input field
			handleFunctionKeyForAlgebraInput(5, geos.get(0));
			return true;

		case DELETE:
			// G.Sturr 2010-5-2: let the spreadsheet handle delete
			if (app.getGuiManager() != null
					&& app.getGuiManager().hasSpreadsheetView()
					&& app.getGuiManager().getSpreadsheetView().hasFocus())
				return false;
			// DELETE selected objects
			if (!app.isApplet() || app.isRightClickEnabled()) {
				app.deleteSelectedObjects();
				return true;
			}

		case BACKSPACE:
			// G.Sturr 2010-5-2: let the spreadsheet handle delete
			if (app.getGuiManager() != null
					&& app.getGuiManager().getSpreadsheetView().hasFocus())
				return false;
			// DELETE selected objects
			// Note: ctrl-h generates a KeyEvent.VK_BACK_SPACE event, so check
			// for ctrl too
			if (!isControlDown
					&& (!app.isApplet() || app.isRightClickEnabled())) {
				app.deleteSelectedObjects();
				return true;
			}
			break;
		}

		// ignore key events coming from tables like the spreadsheet to
		// allow start editing, moving etc
		if (fromSpreadsheet
				|| (app.isUsingFullGui() && app.getGuiManager() != null
						&& app.getGuiManager().hasSpreadsheetView() && app
						.getGuiManager().getSpreadsheetView().hasFocus())) {
			return false;
		}

		// check for arrow keys: try to move objects accordingly
		boolean moved = false;
		switch (key) {
		case UP:
			// make sure arrow keys work in menus
			if (app.getGuiManager() != null && app.isUsingFullGui()
					&& !app.getGuiManager().noMenusOpen()) {
				return false;
			}
			if (!fromSpreadsheet && handleArrowsForDropdown(geos, false)) {
				return true;
			}
			changeValY = base;
			break;

		case DOWN:

			// make sure arrow keys work in menus
			if (app.getGuiManager() != null && app.isUsingFullGui()
					&& !app.getGuiManager().noMenusOpen()) {
				return false;
			}
			if (!fromSpreadsheet && handleArrowsForDropdown(geos, true)) {
				return true;
			}
			changeValY = -base;
			break;

		case RIGHT:

			// make sure arrow keys work in menus
			if (app.getGuiManager() != null && app.isUsingFullGui()
					&& !app.getGuiManager().noMenusOpen()) {
				return false;
			}

			changeValX = base;
			break;

		case LEFT:

			// make sure arrow keys work in menus
			if (app.getGuiManager() != null && app.isUsingFullGui()
					&& !app.getGuiManager().noMenusOpen()) {
				return false;
			}

			changeValX = -base;
			break;

		case PAGEUP:
			changeValZ = base;
			break;

		case PAGEDOWN:
			changeValZ = -base;

			break;

		}

		if (changeValX != 0 || changeValY != 0 || changeValZ != 0) {
			moved = handleArrowKeyMovement(geos, changeValX, changeValY,
					changeValZ);
		}

		if (moved) {
			return true;
		}

		boolean vertical = true;

		double changeVal = 0;
		// F2, PLUS, MINUS keys
		switch (key) {
		case F2:
			// handle F2 key to start editing first selected element
			if (app.isUsingFullGui() && app.getGuiManager() != null) {
				app.getGuiManager().startEditing(geos.get(0));
				return true;
			}
			break;

		case PLUS:
		case ADD: // can be own key on some keyboard
		case EQUALS: // same key as plus (on most keyboards)
		case UP:
			changeVal = base;
			vertical = true;
			break;
		case RIGHT:
			changeVal = base;
			vertical = false;
			break;

		case MINUS:
		case SUBTRACT:
		case DOWN:
			changeVal = -base;
			vertical = true;
			break;
		case LEFT:
			changeVal = -base;
			vertical = false;
			break;
		}

		/*
		 * if (changeVal == 0) { char keyChar = event.getKeyChar(); if (keyChar
		 * == '+') changeVal = base; else if (keyChar == '-') changeVal = -base;
		 * }
		 */
		App.debug("not yet consumed");
		// change all geoelements
		if (changeVal != 0) {
			App.debug("consumed");
			boolean twoSliders = geos.size() == 2 && geos.get(0).isGeoNumeric()
					&& geos.get(1).isGeoNumeric();

			for (int i = geos.size() - 1; i >= 0; i--) {

				GeoElement geo = geos.get(i);

				if (geo.isChangeable()) {

					// update number
					if (geo.isGeoNumeric()
							&& (!twoSliders || ((vertical && i == 0) || (!vertical && i == 1)))) {
						GeoNumeric num = (GeoNumeric) geo;
						double newValue = num.getValue() + changeVal
								* num.getAnimationStep();
						if (num.getAnimationStep() > Kernel.MIN_PRECISION) {
							// round to decimal fraction, e.g. 2.800000000001 to
							// 2.8
							if (num.isGeoAngle()) {
								app.getKernel();
								app.getKernel();
								newValue = Kernel.PI_180
										* Kernel.checkDecimalFraction(newValue
												* Kernel.CONST_180_PI,
												1 / num.getAnimationStep());
							} else
								newValue = Kernel.checkDecimalFraction(
										newValue, 1 / num.getAnimationStep());
						}
						num.setValue(newValue);
					}

					// update point on path
					else if (geo.isGeoPoint() && !geo.isGeoElement3D()) {
						GeoPoint p = (GeoPoint) geo;
						if (p.hasPath()) {
							p.addToPathParameter(changeVal
									* p.getAnimationStep());
						}
					}
				}

				// update parent algo of dependent geo to update randomNumbers
				else if (!geo.isIndependent()) {
					// update labeled random number
					if (geo.isLabelSet() && geo.isGeoNumeric()) {
						GeoNumeric num = (GeoNumeric) geo;
						if (num.isRandomGeo()) {
							num.updateRandomGeo();
						}
					}

					// update parent algorithm for unlabeled random numbers
					// and all other algorithms
					geo.getParentAlgorithm().update();
				}
			}

			// update all geos together
			GeoElement.updateCascade(geos, getTempSet(), false);
			app.getKernel().notifyRepaint();

			return true;
		}

		return false;
	}

	/**
	 * Copies definitions of geos to input bar and wraps them in a list
	 * 
	 * @param geos
	 *            list of geos
	 */
	protected abstract void copyDefinitionsToInputBarAsList(
			ArrayList<GeoElement> geos);

}
