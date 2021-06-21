package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.draw.DrawDropDownList;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.MoveGeos;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.util.KeyCodes;

/**
 * Handles keyboard events. This class only dispatches
 */
public abstract class GlobalKeyDispatcher {

	private static final double AUTOSTEPS_PER_KEY = 5;

	/** application */
	@Weak
	public final App app;
	/** selection */
	@Weak
	protected final SelectionManager selection;

	private TreeSet<AlgoElement> tempSet;
	private Coords tempVec;
	private boolean hasUnsavedGeoChanges;

	/**
	 * @param app2
	 *            app
	 */
	public GlobalKeyDispatcher(App app2) {
		this.app = app2;
		this.selection = app.getSelectionManager();
	}

	/**
	 * Handle Fx keys for input bar when geo is selected
	 * 
	 * @param fkey
	 *            eg 3 for F3 key
	 * @param geo
	 *            selected geo
	 */
	public void handleFunctionKeyForAlgebraInput(int fkey, GeoElement geo) {
		if (!app.showAlgebraInput() || app.getGuiManager() == null) {
			return;
		}

		switch (fkey) {
		default:
			// do nothing
			break;
		case 3: // F3 key: copy definition to input field
			app.getGuiManager().setInputText(geo.getDefinitionForInputBar());
			break;

		case 4: // F4 key: copy value to input field
			app.getGuiManager().replaceInputSelection(
					" " + geo.getValueForInputBar() + " ");
			break;

		case 5: // F5 key: copy name to input field
			app.getGuiManager().replaceInputSelection(
					" " + geo.getLabel(StringTemplate.defaultTemplate) + " ");
			break;
		}
	}

	/**
	 * @return temporary set of algos
	 */
	protected TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<>();
		}
		return tempSet;
	}

	/**
	 * Open rename dialog when first letter is typed
	 * 
	 * @param ch
	 *            letter typed
	 * @return whether we show the dialog
	 */
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

		if ((Character.isLetter(ch)) || geo instanceof GeoInputBox) {

			// open rename dialog
			if (geo != null && geo.isRenameable()) {

				if (geo instanceof GeoInputBox) {
					DrawInputBox dt = (DrawInputBox) app
							.getActiveEuclidianView().getDrawableFor(geo);
					if (dt != null) {
						dt.setFocus(ch + "");
					}
				} else {
					if (app.getDialogManager() != null) {
						app.getDialogManager().showRenameDialog(geo, true,
								Character.toString(ch), false);
					}
				}
				return true;
			}
		}
		if (ch == '/') {
			toggleSelectionVisibility();
		}

		if (app.getGuiManager() != null) {
			if (ch == '\n' || ch == '\r') {
				startEdit(geo);
			} else if (ch == '.') {
				openSettingsInAV(geo);
			}
		}

		// don't instantiate: could steal focus
		if (app.getActiveEuclidianView().hasDynamicStyleBar()) {
			app.getActiveEuclidianView().getDynamicStyleBar().setVisible(false);
		}
		return false;
	}

	/**
	 * Start editing in AV
	 * 
	 * @param geoElement
	 *            element to edit
	 */
	protected void startEdit(GeoElement geoElement) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().startEditing(geoElement);
		}
	}

	/**
	 * Open settings in AV
	 * 
	 * @param geo
	 *            open settings of.
	 */
	protected void openSettingsInAV(GeoElement geo) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().openMenuInAVFor(geo);
			Log.debug(
					"[lac] open settings for " + geo.getDefinitionForEditor());
		}
	}

	private void toggleSelectionVisibility() {
		int selSize = selection.selectedGeosSize();
		if (selSize > 0 && app.getGuiManager() != null
				&& app.getGuiManager().hasAlgebraView()) {
			for (int i = 0; i < selSize; i++) {
				GeoElement geo1 = selection.getSelectedGeos().get(i);
				geo1.setEuclidianVisible(!geo1.isSetEuclidianVisible());
				geo1.update();
			}
			app.getKernel().notifyRepaint();
		}
	}

	private boolean handleUpDownArrowsForDropdown(ArrayList<GeoElement> geos,
												  boolean down) {
		if (geos.size() == 1 && geos.get(0).isGeoList()) {
			DrawDropDownList dl = DrawDropDownList.asDrawable(app, geos.get(0));
			if (dl == null || !((GeoList) geos.get(0)).drawAsComboBox()) {
				return false;
			}
			if (!dl.isOptionsVisible()) {
				dl.toggleOptions();
			} else {
				dl.moveSelectorVertical(down);
			}
			return true;
		}
		return false;
	}

	private boolean handleLeftRightArrowsForDropdown(ArrayList<GeoElement> geos,
													 boolean left) {
		if (geos.size() == 1 && geos.get(0).isGeoList()) {
			DrawDropDownList dl = DrawDropDownList.asDrawable(app, geos.get(0));
			if (dl == null) {
				return false;
			}
			if (!dl.isOptionsVisible()) {
				dl.toggleOptions();
			}

			if (dl.isMultiColumn()) {
				if (dl.isOptionsVisible()) {
					dl.moveSelectorHorizontal(left);
					return true;
				}
			} else {
				return handleUpDownArrowsForDropdown(geos, left);
			}

		}
		return false;
	}

	/**
	 * Tries to move the given objects after pressing an arrow key on the
	 * keyboard.
	 * 
	 * @param geos
	 *            moved geos
	 * @param diff
	 *            translation in x, y and z directions
	 * @param increment
	 *            multiplier for x,y,z
	 * 
	 * @return whether any object was moved
	 */
	public boolean handleArrowKeyMovement(List<GeoElement> geos,
			double[] diff, double increment) {
		app.getActiveEuclidianView().getEuclidianController().splitSelectedStrokes(true);
		GeoElement geo = geos.get(0);

		boolean allSliders = true;
		for (GeoElement geoi : geos) {
			if (!geoi.isGeoNumeric() || !geoi.isChangeable()) {
				allSliders = false;
				break;
			}
		}

		// don't move sliders, they will be handled later
		if (allSliders) {
			return false;
		}

		// set translation vector
		if (tempVec == null) {
			tempVec = new Coords(4); // 4 coords for 3D
		}

		tempVec.set(diff);
		tempVec.mulInside(increment);

		// move objects
		boolean moved = MoveGeos.moveObjects(geos, tempVec, null, null,
				app.getActiveEuclidianView());
		if (app.getActiveEuclidianView() != null) {
			app.getActiveEuclidianView().getEuclidianController()
					.onArrowKeyTyped();
		}

		if (app.isEuclidianView3Dinited()) {
			EuclidianView3DInterface view3d = app.getEuclidianView3D();
			if (view3d.isShowing()) {
				view3d.setCursor3DVisible(false);
			}
		}

		// nothing moved
		if (!moved) {
			for (GeoElement geoElement : geos) {
				geo = geoElement;
				// toggle boolean value
				if (geo.isChangeable() && geo.isGeoBoolean()) {
					GeoBoolean bool = (GeoBoolean) geo;
					bool.setValue(!bool.getBoolean());
					bool.updateCascade();
					moved = true;
				}
			}
		}

		if (moved) {
			ScreenReader.readGeoMoved(geo);
			app.getKernel().notifyRepaint();
		}

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
		default:
			// do nothing
			break;
		case ESCAPE:

			// ESC: set move mode
			handleEscForDropdown();
			if (!app.isApplet() || app.showToolBar()) {
				app.setMoveMode();
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
			if (app.isDesktop()) {
				consumed = handleTabDesktop(isControlDown, isShiftDown);
			}

			break;

		// open Tool Help
		case F1:
			app.getDialogManager().openToolHelp();
			return true;

		// F9 updates construction
		// cmd-f9 on Mac OS
		case F9:
			if (!app.isApplet() || keyboardShortcutsEnabled()) {
				app.getKernel().updateConstruction(true);
				app.setUnsaved();
				consumed = true;
			}
			break;

		case CONTEXT_MENU:
		case F10: // <Shift>F10 -> Right-click
			if ((isShiftDown || key == KeyCodes.CONTEXT_MENU)
					&& keyboardShortcutsEnabled()) {
				if (app.getGuiManager() != null) {

					EuclidianView view = app.getActiveEuclidianView();

					ArrayList<GeoElement> selectedGeos = app
							.getSelectionManager().getSelectedGeos();
					if (selectedGeos != null && selectedGeos.size() > 0) {

						GeoElement geo = selectedGeos.get(0);
						DrawableND drawable = view.getDrawableFor(geo);
						GRectangle2D bounds;
						if (drawable != null) {

							bounds = drawable.getBoundsForStylebarPosition();

						} else {
							// probably 3D, just open in corner
							bounds = AwtFactory.getPrototype().newRectangle2D();
						}
						if (bounds != null) {
							GPoint p = new GPoint((int) bounds.getMinX(),
									(int) bounds.getMinY());

							app.getGuiManager().showPopupChooseGeo(
									app.getSelectionManager().getSelectedGeos(),
									app.getSelectionManager().getSelectedGeoList(),
									app.getActiveEuclidianView(), p);
						}
					} else {
						// open in corner
						app.getGuiManager().showDrawingPadPopup(
								app.getActiveEuclidianView(), new GPoint(0, 0));

					}
				}
				return true;
			}
			break;
		}

		// Ctrl key down (and not Alt, so that AltGr works for special
		// characters)
		if (isControlDown && !isAltDown) {
			consumed = consumed || handleCtrlKey(key, isShiftDown,
					fromSpreadsheet, fromEuclidianView);

		}

		return consumed;
	}

	protected boolean handleTabDesktop(boolean isControlDown, boolean isShiftDown) {
		return false; // overridden in desktop
	}

	/**
	 * Translate keycode of event for KeyCodes instance
	 * 
	 * @param i
	 *            event keycode
	 * @return translated keycode
	 */
	protected abstract KeyCodes translateKey(int i);

	private boolean handleCtrlKey(KeyCodes key, boolean isShiftDown,
			boolean fromSpreadsheet, boolean fromEuclidianView) {
		boolean consumed = false;
		switch (key) {
		case K1:
		case NUMPAD1:
			// event.isShiftDown() doesn't work if NumLock on
			// however .isAltDown() stops AltGr-1 from working (| on some
			// keyboards)
			if (isShiftDown && keyboardShortcutsEnabled()
					&& app.getGuiManager() != null) { // ||
																// event.isAltDown())
																// {
				app.getGuiManager().setShowView(
						!app.getGuiManager().showView(App.VIEW_EUCLIDIAN),
						App.VIEW_EUCLIDIAN);
				consumed = true;

			} else { // make sure not triggered on
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
			if (isShiftDown && keyboardShortcutsEnabled()
					&& app.getGuiManager() != null) { // ||
																// event.isAltDown())
																// {
				app.getGuiManager().setShowView(
						!app.getGuiManager().showView(App.VIEW_EUCLIDIAN2),
						App.VIEW_EUCLIDIAN2);
				consumed = true;

			} else { // make sure not triggered on
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
			if (isShiftDown && keyboardShortcutsEnabled()
					&& app.getGuiManager() != null
					&& app.supportsView(App.VIEW_EUCLIDIAN3D)) { // ||
																	// event.isAltDown())
																	// {
				app.getGuiManager().setShowView(
						!app.getGuiManager().showView(App.VIEW_EUCLIDIAN3D),
						App.VIEW_EUCLIDIAN3D);
				consumed = true;

			} else { // make sure not triggered on
				// AltGr
				// Ctrl-3: set black/white mode printing and visually
				// impaired users
				changeFontsAndGeoElements(app, app.getFontSize(), true, true);
				consumed = true;
			}
			break;

		case A:
			if (isShiftDown) {
				if (keyboardShortcutsEnabled() && app.isUsingFullGui()
						&& app.getGuiManager() != null) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(App.VIEW_ALGEBRA),
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
				if (keyboardShortcutsEnabled() && app.isUsingFullGui()
						&& app.getGuiManager() != null
						&& app.supportsView(App.VIEW_CAS)) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(App.VIEW_CAS),
							App.VIEW_CAS);
					consumed = true;
				}
			}
			break;

		case L:
			if (isShiftDown) {
				if (keyboardShortcutsEnabled() && app.isUsingFullGui()
						&& app.getGuiManager() != null) {
					app.getGuiManager().setShowView(
							!app.getGuiManager()
									.showView(App.VIEW_CONSTRUCTION_PROTOCOL),
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
				if (keyboardShortcutsEnabled() && app.isUsingFullGui()
						&& app.getGuiManager() != null) {
					app.getGuiManager().setShowView(
							!app.getGuiManager()
									.showView(App.VIEW_PROBABILITY_CALCULATOR),
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

		case F10: // <Shift>F10 -> Right-click
			if (isShiftDown) {
				Log.error("shift f10");
				consumed = true;
			}
			break;

		case I: // Edit -> Invert Selection
			if (!isShiftDown) {
				selection.invertSelection();
				consumed = true;
			}
			break;
		case X:
			// Ctrl-shift-c: copy graphics view to clipboard
			// should also work in applets with no menubar

			// check not spreadsheet
			if (!fromSpreadsheet) {
				handleCopyCut(true);
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
					handleCopyCut(false);
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
			if (isShiftDown) {
				selection.showHideSelectionLabels();
			} else {
				selection.showHideSelection();
			}
			consumed = true;
			break;

		// Ctrl + E: open object properties (needed here for spreadsheet)
		case E:
			if (keyboardShortcutsEnabled() && app.isUsingFullGui()
					&& app.getGuiManager() != null) {
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
				handleCtrlShiftN(false);
			} else {
				createNewWindow();
			}
			break;

		// needed for detached views and MacOS
		// Ctrl + Z: Undo
		case Z:
			if (app.getGuiManager() != null) {
				if (isShiftDown) {
					app.getGuiManager().redo();
				} else {
					app.getGuiManager().undo();
				}
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
			if (!app.isApplet() || keyboardShortcutsEnabled()) {
				app.getKernel().updateConstruction(true);
				app.setUnsaved();
				consumed = true;
			}
			break;

		// ctrl-shift-s (toggle spreadsheet)
		case S:
			if (isShiftDown) {
				if (keyboardShortcutsEnabled() && app.isUsingFullGui()
						&& app.getGuiManager() != null) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(App.VIEW_SPREADSHEET),
							App.VIEW_SPREADSHEET);
					consumed = true;
				}
			} else if (app.getGuiManager() != null) {
				app.getGuiManager().save();
				consumed = true;
			}
			break;

		case Y:
			 if (!isShiftDown && app.getGuiManager() != null) {
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
			if (isShiftDown) {
				selection.selectAllDescendants();
			} else {
				selection.selectAllPredecessors();
			}
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
			if (!EuclidianView
					.isPenMode(app.getActiveEuclidianView().getMode())) {

				boolean spanish = app.getLocalization().getLanguage()
						.startsWith("es");

				// AltGr+ on Spanish keyboard is ] so
				// allow <Ctrl>+ (zoom) but not <Ctrl><Alt>+ (fast zoom)
				// from eg Input Bar
				if (!spanish || fromEuclidianView) {
					EuclidianController ec = app.getActiveEuclidianView().getEuclidianController();
					double factor = key.equals(KeyCodes.MINUS) || key.equals(KeyCodes.SUBTRACT)
							? 1d / EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
							: EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR;

					ec.zoomInOut(factor, 15, ec.mouseLoc.x, ec.mouseLoc.y);
					app.setUnsaved();
					consumed = true;
				}
			}
			break;

		// Ctrl + D: toggles algebra style: value, definition, command
		case D:
		case BACK_QUOTE:
			if (!isShiftDown) {
				toggleAlgebraStyle(app);
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
				TreeSet<GeoElement> objects = app.getKernel().getConstruction()
						.getGeoSetConstructionOrder();
				Iterator<GeoElement> it = objects.iterator();
				while (it.hasNext()) {
					GeoElement geo = it.next();
					if (!geo.isSelectionAllowed(app.getActiveEuclidianView())) {
						selectionAllowed = true;
						break;
					}
				}

				it = objects.iterator();
				while (it.hasNext()) {
					GeoElement geo = it.next();

					if ((geo instanceof AbsoluteScreenLocateable
							&& ((AbsoluteScreenLocateable) geo).isFurniture())
							|| (geo.isGeoNumeric() && geo.isIndependent())
							|| geo.isGeoBoolean()
							|| (geo.isGeoPoint() && !geo.isLocked())) {

						geo.setSelectionAllowed(true);

						// fix/unfix sliders
						if ((geo.isGeoNumeric() && geo.isIndependent())) {
							((GeoNumeric) geo)
									.setSliderFixed(!selectionAllowed);
						}

					} else {
						geo.setSelectionAllowed(selectionAllowed);
					}
				}

			}
			break;
		}
		return consumed;
	}

	/**
	 * Change algebra style value -&gt; definition -&gt; description ...
	 * 
	 * @param app
	 *            application
	 */
	public static void toggleAlgebraStyle(App app) {
		Kernel kernel = app.getKernel();
		kernel.setAlgebraStyle((kernel.getAlgebraStyle() + 1) % 3);
		kernel.setAlgebraStyleSpreadsheet(
				(kernel.getAlgebraStyleSpreadsheet() + 1) % 3);

		kernel.updateConstruction(false);
		/*
		 * if (app.hasOptionsMenu()) {
		 * app.getOptionsMenu(null).updateMenuViewDescription(); }
		 */
		app.setUnsaved();
	}

	/**
	 * enableRightClick also enables/disables some keyboard shortcuts eg Delete
	 * and Ctrl + R
	 * 
	 * @return
	 */
	private boolean keyboardShortcutsEnabled() {
		return app.isRightClickEnabled();
	}

	private void handleEscForDropdown() {
		// Log.debug("handleEscForDropdown");
		ArrayList<GeoElement> geos = selection.getSelectedGeos();
		if (geos.size() == 1 && geos.get(0).isGeoList()) {
			DrawDropDownList dl = DrawDropDownList.asDrawable(app, geos.get(0));
			if (dl != null && dl.isOptionsVisible()) {
				dl.toggleOptions();
			}
		}
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
	 * Handle Ctrl+V
	 */
	protected void handleCtrlV() {
		// overridden in desktop, in web, we listen to paste events
	}

	/**
	 * @param isAltDown
	 *            whether alt is down
	 * @return whether keys were consumed
	 */
	protected abstract boolean handleCtrlShiftN(boolean isAltDown);

	/**
	 * Overridden in desktop, in web we listen to cut and copy events
	 *
	 * @param cut
	 *            whether to cut (false = copy)
	 */
	protected void handleCopyCut(boolean cut) {
		// overridden in desktop, in web, we listen to paste events
	}

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
		if (app.isApplet()) {
			return false;
		}

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
				cd.getDefaultDependentPointSize() + incr);
		cd.setDefaultAngleSize(cd.getDefaultAngleSize() + angleSizeIncr);
		// blackWhiteMode: set defaults for new GeoElements
		cd.setBlackWhiteMode(blackWhiteMode);

		// change application font size
		app.setFontSize(fontSize, true);
		if (app.isUsingFullGui() && app.getGuiManager() != null) {
			app.getGuiManager().updateSpreadsheetColumnWidths();
		}

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

		app.getKernel().updateConstruction(false);
		app.setUnsaved();
		app.storeUndoInfo();

		app.setDefaultCursor();
		return true;
	}

	private static int getPointSizeInc(int oldFontSize, int newFontSize) {
		if (oldFontSize == newFontSize) {
			return 0;
		}
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
			int geoLineThickness = geo.getLineThickness();
			if (geoLineThickness != 0) {
				int lineThickness = Math.max(2,
						geoLineThickness + lineThicknessIncr);
				geo.setLineThickness(lineThickness);
			}
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
		if (isShiftDown) {
			// shift-equals -> plus
			// don't want multiplier so that + and - do the same
			if (!KeyCodes.EQUALS.equals(key)) {
				base = 0.1;
			}
		}
		if (isControlDown) {
			base = 10;
		}
		if (isAltDown) {
			base = 100;
		}

		if (geos == null || geos.size() == 0) {

			// Get the EuclidianView which has the focus
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			int width = ev.getWidth();
			int height = ev.getHeight();
			if (ev.hasFocus() && app.isShiftDragZoomEnabled()) {
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
					ev.translateCoordSystemInPixels((int) (height * base), 0, 0);
					return true;
				case HOME:
					ev.rememberOrigins();
					ev.translateCoordSystemInPixels(-(int) (height * base), 0, 0);
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
									(int) (height / 100.0 * base), 0);
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
									-(int) (height / 100.0 * base), 0);
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
										view.getYZero(), view.getXscale() * 0.9,
										view.getYscale());
							}
						} else {
							ev.rememberOrigins();
							ev.translateCoordSystemInPixels(
									-(int) (width / 100.0 * base), 0, 0);
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
										view.getYZero(), view.getXscale() / 0.9,
										view.getYscale());
							}
						} else {
							ev.rememberOrigins();
							ev.translateCoordSystemInPixels(
									(int) (width / 100.0 * base), 0, 0);
						}
					}
					return true;
				}
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
			if (geos.size() == 1) {
				handleFunctionKeyForAlgebraInput(3, geos.get(0));
			} else {
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
					&& app.getGuiManager().getSpreadsheetView().hasFocus()) {
				return false;
			}
			// DELETE selected objects
			if (!app.isApplet() || keyboardShortcutsEnabled()) {
				app.getActiveEuclidianView().getEuclidianController().splitSelectedStrokes(true);
				app.deleteSelectedObjects(false);
				return true;
			}

		case BACKSPACE:
			// G.Sturr 2010-5-2: let the spreadsheet handle delete
			if (app.getGuiManager() != null
					&& app.getGuiManager().getSpreadsheetView().hasFocus()) {
				return false;
			}
			// DELETE selected objects
			// Note: ctrl-h generates a KeyEvent.VK_BACK_SPACE event, so check
			// for ctrl too
			if (!isControlDown
					&& (!app.isApplet() || keyboardShortcutsEnabled())) {
				app.deleteSelectedObjects(false);
				return true;
			}
			break;
		}

		// ignore key events coming from tables like the spreadsheet to
		// allow start editing, moving etc
		if (fromSpreadsheet || (app.isUsingFullGui()
				&& app.getGuiManager() != null
				&& app.getGuiManager().hasSpreadsheetView()
				&& app.getGuiManager().getSpreadsheetView().hasFocus())) {
			return false;
		}

		// check for arrow keys: try to move objects accordingly
		boolean moved = false;
		switch (key) {
		default:
			// do nothing
			break;
		case UP:
			// make sure arrow keys work in menus
			if (app.getGuiManager() != null && app.isUsingFullGui()
					&& !app.getGuiManager().noMenusOpen()) {
				return false;
			}
			if (!fromSpreadsheet
					&& handleUpDownArrowsForDropdown(geos, false)) {
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
			if (!fromSpreadsheet
					&& handleUpDownArrowsForDropdown(geos, true)) {
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
			if (!fromSpreadsheet
					&& handleLeftRightArrowsForDropdown(geos, true)) {
				return true;
			}
			changeValX = base;
			break;

		case LEFT:

			// make sure arrow keys work in menus
			if (app.getGuiManager() != null && app.isUsingFullGui()
					&& !app.getGuiManager().noMenusOpen()) {
				return false;
			}
			if (!fromSpreadsheet
					&& handleLeftRightArrowsForDropdown(geos, false)) {
				return true;
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
			double[] diff = new double[] { changeValX, changeValY, changeValZ };
			moved = handleArrowKeyMovement(geos, diff, getIncrement(geos));
			hasUnsavedGeoChanges = true;
		}

		if (moved) {
			return true;
		}

		// when 2 or 3 sliders selected they can be controlled separately with
		// plus/minus/up/down/left/right
		int index = -1;

		double changeVal = 0;
		// F2, PLUS, MINUS keys
		switch (key) {
		default:
			// do nothing
			break;
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
			changeVal = base;
			index = 2;
			break;
		case UP:
			changeVal = base;
			index = 0;
			break;
		case RIGHT:
			changeVal = base;
			index = 1;
			break;

		case MINUS:
		case SUBTRACT:
			changeVal = -base;
			index = 2;
			break;
		case HOME:
			// got to start of slider
			changeVal = Double.NEGATIVE_INFINITY;
			index = 0;
			break;
		case END:
			// got to end of slider
			changeVal = Double.POSITIVE_INFINITY;
			index = 0;
			break;
		case PAGEDOWN:
			changeVal = -10 * base;
			index = 0;
			break;
		case PAGEUP:
			changeVal = 10 * base;
			index = 0;
			break;
		case DOWN:
			changeVal = -base;
			index = 0;
			break;
		case LEFT:
			changeVal = -base;
			index = 1;
			break;
		}

		// change all geoelements
		if (changeVal != 0) {

			// exactly 2 or 3 sliders selected
			boolean multipleSliders = geos.size() > 1
					&& geos.get(0).isGeoNumeric()
					&& geos.get(1).isGeoNumeric()
					&& (geos.size() == 2 || (geos.size() == 3
							&& geos.get(2).isGeoNumeric()));

			for (int i = geos.size() - 1; i >= 0; i--) {

				GeoElement geo = geos.get(i);

				if (geo.isPointerChangeable()) {

					// update number
					if (geo.isGeoNumeric()
							&& (!multipleSliders || index == i)) {
						GeoNumeric num = (GeoNumeric) geo;
						double numStep = getAnimationStep(num);
						double newValue = num.getValue()
								+ changeVal * numStep;

						// HOME / END keys
						if (Double.isInfinite(changeVal)) {
							newValue = changeVal > 0 ? num.getIntervalMax()
									: num.getIntervalMin();
						}

						if (numStep > Kernel.MIN_PRECISION) {
							// round to decimal fraction, e.g. 2.800000000001 to
							// 2.8
							if (num.isGeoAngle()) {
								newValue = Kernel.PI_180
										* DoubleUtil.checkDecimalFraction(
												newValue * Kernel.CONST_180_PI,
												1 / numStep);
							} else {
								newValue = DoubleUtil.checkDecimalFraction(newValue,
										1 / numStep);
							}
						}

						// stop all animation if slider dragged
						if (num.isAnimating()) {
							num.getKernel().getAnimatonManager().stopAnimation();
						}

						num.setValue(newValue);
						hasUnsavedGeoChanges = true;
					}

					// update point on path
					else if (geo instanceof GeoPointND) {
						GeoPointND p = (GeoPointND) geo;
						if (p.isPointOnPath()) {
							p.addToPathParameter(
									changeVal * p.getAnimationStep());
							ScreenReader.readGeoMoved((GeoElement) p);
						}
						hasUnsavedGeoChanges = true;
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

	private double getIncrement(ArrayList<GeoElement> geos) {
		GeoElement geo = geos.get(0);
		double increment = geo.getAnimationStep();

		// eg for Polygon(A,B,C)
		// use increment of A
		if (!geo.isGeoNumeric() && !geo.isGeoPoint()) {

			ArrayList<GeoPointND> freeInputPoints = geo
					.getFreeInputPoints(app.getActiveEuclidianView());

			if (freeInputPoints != null && freeInputPoints.size() > 0) {
				increment = freeInputPoints.get(0).getAnimationStep();
			}

		}
		return increment;
	}

	private static double getAnimationStep(GeoNumeric num) {
		return num.isAutoStep() ? num.getAnimationStep() * AUTOSTEPS_PER_KEY
				: num.getAnimationStep();
	}

	/**
	 * Copies definitions of geos to input bar and wraps them in a list
	 * 
	 * @param geos
	 *            list of geos
	 */
	protected abstract void copyDefinitionsToInputBarAsList(
			ArrayList<GeoElement> geos);

	/**
	 * @return handles enter
	 */
	protected boolean handleEnter() {
		if (selection.getSelectedGeos().size() == 1) {
			GeoElement geo = selection.getSelectedGeos().get(0);
			if (geo.isGeoList()) {
				DrawDropDownList dropdown = DrawDropDownList.asDrawable(app, geo);
				if (dropdown != null) {
					dropdown.selectCurrentItem();
					ScreenReader.readDropDownItemSelected(geo);
					return true;
				}
			} else if (geo.isGeoInputBox() && geo.isEuclidianVisible()) {
				app.getActiveEuclidianView()
						.focusAndShowTextField((GeoInputBox) geo);
			}
		}
		return false;
	}

	/**
	 * Store undo point if some objects were moved.
	 */
	public void storeUndoInfoIfChanged() {
		if (hasUnsavedGeoChanges) {
			app.storeUndoInfo();
			hasUnsavedGeoChanges = false;
		}
	}
}
