package geogebra.common.main;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.CopyPaste;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

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
	 * @param i x when Fx is pressed
	 * @param geo selected geo
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

	/**
	 * Tries to move the given objects after pressing an arrow key on the
	 * keyboard.
	 * @param geos moved geos
	 * @param xdiff translation in x direction
	 * @param ydiff translation in y direction
	 * @param zdiff translation in z direction
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
		boolean moved = GeoElement.moveObjects(geos, tempVec, null, null);

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
	 * @param key key code 
	 * @param isShiftDown  whether shift is down
	 * @param isControlDown whether control is down
	 * @param isAltDown whether alt is down
	 * @param fromSpreadsheet whether this event comes from spreadsheet
	 * @param fromEuclidianView whether this event comes from EV
	 * 
	 * @return if key was consumed
	 */
	protected boolean handleGeneralKeys(KeyCodes key, boolean isShiftDown, boolean isControlDown, boolean isAltDown, boolean fromSpreadsheet, boolean fromEuclidianView) {
		boolean consumed = false;

		// ESC and function keys
		switch (key) {
		case ESCAPE:
			// ESC: set move mode
			app.setMoveMode();
			app.getActiveEuclidianView().getEuclidianController()
			.deletePastePreviewSelected();
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

				ArrayList<GeoElement> selGeos = selection.getSelectedGeos();
				if (selGeos.size() == 1) {
					if (selGeos.get(0).isGeoBoolean()) {
						GeoBoolean geoBool = (GeoBoolean) selGeos.get(0);
						geoBool.setValue(!geoBool.getBoolean());
						geoBool.updateRepaint();
					} else {
						selGeos.get(0).runClickScripts(null);
					}

					consumed = true;

				}

			}
			break;

		case TAB:


			consumed = handleTab(isControlDown, isShiftDown);

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
		// make sure Ctrl-1/2/3 works on the Numeric Keypad even with Numlock
		// off
		// **** NB if NumLock on, event.isShiftDown() always returns false with
		// Numlock on!!! (Win 7)
		if (event.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
			String keyText = KeyEvent.getKeyText(keyCode);
			if (keyText.equals("End")) {
				keyCode = KeyEvent.VK_1;
			} else if (keyText.equals("Down")) {
				keyCode = KeyEvent.VK_2;
			} else if (keyText.equals("Page Down")) {
				keyCode = KeyEvent.VK_3;
			}

		}
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
				if (isShiftDown) {// || event.isAltDown()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(
									App.VIEW_EUCLIDIAN),
									App.VIEW_EUCLIDIAN);
					consumed = true;

				} else if (!isAltDown) { // make sure not triggered on
					// AltGr
					// Ctrl-1: set objects back to the default size (for font
					// size 12)
					changeFontsAndGeoElements(app, 12, false);
					consumed = true;
				}
				break;

			case NUMPAD2:
			case K2:
				// event.isShiftDown() doesn't work if NumLock on
				// however .isAltDown() stops AltGr-2 from working (superscript
				// 2 on some keyboards)
				if (isShiftDown) {// || event.isAltDown()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(
									App.VIEW_EUCLIDIAN2),
									App.VIEW_EUCLIDIAN2);
					consumed = true;

				} else if (!isAltDown) { // make sure not triggered on
					// AltGr
					// Ctrl-2: large font size and thicker lines for projectors
					// etc
					int fontSize = Math.min(32, app.getFontSize() + 4);
					changeFontsAndGeoElements(app, fontSize, false);
					consumed = true;
				}
				break;

			case NUMPAD3:
			case K3:
				// event.isShiftDown() doesn't work if NumLock on
				// however .isAltDown() stops AltGr-3 from working (^ on
				// Croatian keyboard)
				if (isShiftDown) { // || event.isAltDown()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(
									App.VIEW_EUCLIDIAN3D),
									App.VIEW_EUCLIDIAN3D);
					consumed = true;

				} else if (!isAltDown) { // make sure not triggered on
					// AltGr
					// Ctrl-3: set black/white mode printing and visually
					// impaired users
					changeFontsAndGeoElements(app, app.getFontSize(), true);
					consumed = true;
				}
				break;

			case A:
				if (isShiftDown) {
					if (app.isUsingFullGui()) {
					app.getGuiManager().setShowView(
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
					if (app.isUsingFullGui()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(
									App.VIEW_CAS),
									App.VIEW_CAS);
						consumed = true;
					}
				} 
				break;

			case L:
				if (isShiftDown) {
					if (app.isUsingFullGui()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(
									App.VIEW_CONSTRUCTION_PROTOCOL),
									App.VIEW_CONSTRUCTION_PROTOCOL);
						consumed = true;
					}
				}  else 
					{
						selection.selectAll(selection.getSelectedLayer());
						consumed = true;					
				}
				break;

			case O: // File -> Open
				if (!isShiftDown) {
					app.getGuiManager().openFile();
					consumed = true;
				}
				break;
			case P: // File -> Export -> Graphic
				if (!isShiftDown) {
					showPrintPreview(app);
					consumed = true;
				} else {
					app.getGuiManager().showGraphicExport();
					consumed = true;
				}
				break;
			case T: // File -> Export -> PSTricks
				if (isShiftDown) {
					app.getGuiManager().showPSTricksExport();
					consumed = true;
				}
				break;
			case W: // File -> Export -> Webpage
				if (isShiftDown) {
					app.getGuiManager().showWebpageExport();
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

				// export to GeoGebraWeb
			case B:
				if (!app.isApplet() && isShiftDown) {
					app.exportToLMS(true);
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
				app.getGuiManager().setShowView(
						!app.getGuiManager().showView(App.VIEW_PROPERTIES), App.VIEW_PROPERTIES, false);
				consumed = true;
				break;

				// Ctrl + F: refresh views
			case F:
				app.refreshViews();
				consumed = true;
				break;

			case M:
				if (!app.isApplet() && isShiftDown) {
					app.exportToLMS(false);
					consumed = true;
				} else if (!app.isApplet() || app.isRightClickEnabled()) {
					app.setStandardView();
					consumed = true;
				}
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
				if (isShiftDown)
					app.getGuiManager().redo();
				else
					app.getGuiManager().undo();
				consumed = true;
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
					if (app.isUsingFullGui()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(
									App.VIEW_SPREADSHEET),
									App.VIEW_SPREADSHEET);
						consumed = true;
					}
				} else {
					app.getGuiManager().save();
					consumed = true;
				}
				break;

			case Y:
				if (isShiftDown) {
					if (app.isUsingFullGui()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(
									App.VIEW_PYTHON),
									App.VIEW_PYTHON);
						consumed = true;
					}
				} else {
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

				// disable zooming in PEN mode
				if (!EuclidianView.isPenMode(app.getActiveEuclidianView().getMode())) {

					boolean spanish = app.getLocalization().getLanguage().startsWith("es");

					// AltGr+ on Spanish keyboard is ] so
					// allow <Ctrl>+ (zoom) but not <Ctrl><Alt>+ (fast zoom)
					// from eg Input Bar
					if (!spanish || !isAltDown
							|| (fromEuclidianView)) {
						( app.getActiveEuclidianView())
						.getEuclidianController().zoomInOut(isAltDown, key.equals(KeyCodes.MINUS) || key.equals(KeyCodes.SUBTRACT));
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
						app.getOptionsMenu().updateMenuViewDescription();
					}
					app.setUnsaved();
					consumed = true;
					break;
				}
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
	 * @param app2 application
	 */
	protected abstract void showPrintPreview(App app2);

	/**
	 * Handles Ctrl+V; overridden in desktop
	 * Default implementation pastes from XML and returns true
	 */
	protected void handleCtrlV() {
		app.setWaitCursor();
		CopyPaste.pasteFromXML(app);
		app.setDefaultCursor();
	}

	/**
	 * @param isAltDown whether alt is down
	 * @return whether keys were consumed
	 */
	protected abstract boolean handleCtrlShiftN(boolean isAltDown);

	/**
	 * overridden in desktop
	 * Default implementation pastes from XML and returns true
	 */
	protected void handleCtrlC() {
		// Copy selected geos
		app.setWaitCursor();
		CopyPaste.copyToXML(app, selection.getSelectedGeos());
		app.updateMenubar();
		app.setDefaultCursor();	
	}

	/**
	 * @param isControlDown whether control is down
	 * @param isShiftDown whether shift is down
	 * @return whether key was consumed
	 */
	protected boolean handleTab(boolean isControlDown, boolean isShiftDown) {
		if (isShiftDown) {
			selection.selectLastGeo(app.getActiveEuclidianView());
		} else {
			selection.selectNextGeo(app.getActiveEuclidianView());
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
	 * @param app application
	 * @param fontSize
	 *            12-32pt
	 * @param blackWhiteMode
	 *            whether only black should be used as a color
	 * @return whether change was performed
	 */
	public static boolean changeFontsAndGeoElements(App app,
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
		if (app.isUsingFullGui())
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
			geo.setObjColor(GColor.black);
		}
	}
	
	/**
	 * Handle pressed key for selected GeoElements
	 * @param key key code 
	 * @param geos selected geos
	 * @param isShiftDown  whether shift is down
	 * @param isControlDown whether control is down
	 * @param isAltDown whether alt is down
	 * @param fromSpreadsheet whether this event comes from spreadsheet
	 * 
	 * @return if key was consumed
	 */
	protected boolean handleSelectedGeosKeys(KeyCodes key,
			ArrayList<GeoElement> geos, boolean isShiftDown, boolean isControlDown, boolean isAltDown, boolean fromSpreadsheet) {

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
		
		if (geos == null || geos.size() == 0) {

			// Get the EuclidianView which has the focus
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			int width = ev.getWidth();
			int height = ev.getHeight();
			if (ev.hasFocus() && app.isShiftDragZoomEnabled())
				switch (key) {

				case PAGEUP:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(0, (int) (height * base),
							EuclidianController.MOVE_VIEW);
					return true;
				case PAGEDOWN:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(0, -(int) (height * base),
							EuclidianController.MOVE_VIEW);
					return true;
				case INSERT:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove((int) (height * base), 0,
							EuclidianController.MOVE_VIEW);
					return true;
				case HOME:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(-(int) (height * base), 0,
							EuclidianController.MOVE_VIEW);
					return true;
				case DOWN:

					if (app.isUsingFullGui()
							&& app.getGuiManager().noMenusOpen()) {
						if (isShiftDown) {
							EuclidianViewInterfaceCommon view = app.getActiveEuclidianView();
							if (!view.isLockedAxesRatio()) {
								view.setCoordSystem(view.getXZero(), view.getYZero(),
										view.getXscale(), view.getYscale() * 0.9);
							}

						} else {
							ev.rememberOrigins();
							ev.setCoordSystemFromMouseMove(0,
									(int) (height / 100.0 * base),
									EuclidianController.MOVE_VIEW);
						}
						return true;
					}

					break;

				case UP:

					if (app.isUsingFullGui()
							&& app.getGuiManager().noMenusOpen()) {
						if (isShiftDown) {
							EuclidianViewInterfaceCommon view = app.getActiveEuclidianView();
							if (!view.isLockedAxesRatio()) {
								view.setCoordSystem(view.getXZero(), view.getYZero(),
										view.getXscale(), view.getYscale() / 0.9);
							}

						} else {
							ev.rememberOrigins();
							ev.setCoordSystemFromMouseMove(0,
									-(int) (height / 100.0 * base),
									EuclidianController.MOVE_VIEW);
						}
						return true;
					}
					break;

				case LEFT:

					if (app.isUsingFullGui()
							&& app.getGuiManager().noMenusOpen()) {
						if (isShiftDown) {
							EuclidianViewInterfaceCommon view = app.getActiveEuclidianView();
							if (!view.isLockedAxesRatio()) {
								view.setCoordSystem(view.getXZero(), view.getYZero(), view.getXscale() * 0.9,
										view.getYscale());
							}
						} else {
							ev.rememberOrigins();
							ev.setCoordSystemFromMouseMove(
									-(int) (width / 100.0 * base), 0,
									EuclidianController.MOVE_VIEW);
						}
						return true;

					}
					break;

				case RIGHT:

					if (app.isUsingFullGui()
							&& app.getGuiManager().noMenusOpen()) {
						if (isShiftDown) {
							EuclidianViewInterfaceCommon view = app.getActiveEuclidianView();
							if (!view.isLockedAxesRatio()) {
								view.setCoordSystem(view.getXZero(), view.getYZero(), view.getXscale() / 0.9,
										view.getYscale());
							}
						} else {
							ev.rememberOrigins();
							ev.setCoordSystemFromMouseMove(
									(int) (width / 100.0 * base), 0,
									EuclidianController.MOVE_VIEW);
						}
					}
					return true;
				}
			
			return false;
		}

		// FUNCTION and DELETE keys
		switch (key) {

		case PAGEUP:
			Iterator<GeoElement> it = geos.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geo.setLayer(geo.getLayer() + 1);
			}
			break;

		case PAGEDOWN:
			it = geos.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geo.setLayer(geo.getLayer() - 1);
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
			if (app.getGuiManager().hasSpreadsheetView() && app.getGuiManager().getSpreadsheetView().hasFocus())
				return false;
			// DELETE selected objects
			if (!app.isApplet() || app.isRightClickEnabled()) {
				app.deleteSelectedObjects();
				return true;
			}

		case BACKSPACE:
			// G.Sturr 2010-5-2: let the spreadsheet handle delete
			if (app.getGuiManager().getSpreadsheetView().hasFocus())
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
				|| (app.isUsingFullGui()
						&& app.getGuiManager().hasSpreadsheetView() && app
						.getGuiManager().getSpreadsheetView().hasFocus())) {
			return false;
		}

		// check for arrow keys: try to move objects accordingly
		boolean moved = false;
		switch (key) {
		case UP:
			// make sure arrow keys work in menus
			if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen()) {
				return false;
			}
			changeValY = base;
			break;

		case DOWN:

			// make sure arrow keys work in menus
			if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen()) {
				return false;
			}

			changeValY = -base;
			break;

		case RIGHT:

			// make sure arrow keys work in menus
			if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen()) {
				return false;
			}

			changeValX = base;
			break;

		case LEFT:

			// make sure arrow keys work in menus
			if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen()) {
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
			moved = handleArrowKeyMovement(geos, changeValX, changeValY, changeValZ);
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
			if (app.isUsingFullGui()) {
				app.getGuiManager().startEditing(geos.get(0));
				return true;
			}
			break;

		case PLUS:
		case ADD: // can be own key on some keyboard
		case EQUALS: // same key as plus (on most keyboards)
		case UP:
			changeVal  = base;
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
		if (changeVal == 0) {
			char keyChar = event.getKeyChar();
			if (keyChar == '+')
				changeVal = base;
			else if (keyChar == '-')
				changeVal = -base;
		}
		*/

		// change all geoelements
		if (changeVal != 0) {

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
										* Kernel
										.checkDecimalFraction(
												newValue
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
	 * @param geos list of geos
	 */
	protected abstract void copyDefinitionsToInputBarAsList(ArrayList<GeoElement> geos);


}
