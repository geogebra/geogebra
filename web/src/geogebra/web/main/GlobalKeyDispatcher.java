package geogebra.web.main;

import geogebra.common.awt.Color;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.main.AbstractApplication;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.CopyPaste;
import geogebra.web.euclidian.EuclidianView;
import geogebra.web.gui.applet.GeoGebraFrame;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class GlobalKeyDispatcher extends
        geogebra.common.main.GlobalKeyDispatcher implements KeyUpHandler, KeyDownHandler {

	public GlobalKeyDispatcher(AbstractApplication app) {
		this.app = app;
    }

	@Override
	public void handleFunctionKeyForAlgebraInput(int i, GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void onKeyUp(KeyUpEvent event) {
		//AbstractApplication.debug("onkeyup");
		event.preventDefault();
		event.stopPropagation();
		//no it is private, but can be public, also it is void, but can return boolean as in desktop, if needed
		dispatchEvent(event);
    }

	private void dispatchEvent(KeyUpEvent event) {
	    //we ust find out somethinkg here to identify the component that fired this, like class names for example,
		//id-s or data-param-attributes
		
		//we have keypress here only
		handleKeyPressed(event);
	    
    }

	private boolean handleKeyPressed(KeyUpEvent event) {
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

	public boolean handleGeneralKeys(KeyUpEvent event) {
		boolean consumed = false;
		
		//AbstractApplication.debug("key: "+event.getNativeKeyCode());

		// ESC and function keys
		switch (event.getNativeKeyCode()) {
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

		int keyCode = event.getNativeKeyCode();

		// make sure Ctrl-1/2/3 works on the Numeric Keypad even with Numlock
		// off
		// **** NB if NumLock on, event.isShiftDown() always returns false with
		// Numlock on!!! (Win 7)
		/*DO WE NEED THIS?if (event.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
			String keyText = KeyEvent.getKeyText(keyCode);
			if (keyText.equals("End")) {
				keyCode = MyKeyCodes.KEY_1;
			} else if (keyText.equals("Down")) {
				keyCode = MyKeyCodes.KEY_2;
			} else if (keyText.equals("Page Down")) {
				keyCode = MyKeyCodes.KEY_3;
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
						CopyPaste.copyToXML(app, app.getSelectedGeos());
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
				app.setWaitCursor();
				app.getKernel().redo();//FIXME: write this instead after GuiManager is ready: app.getGuiManager().redo();
				consumed = true;
				app.setDefaultCursor();
				break;

			// needed for detached views and MacOS
			// Ctrl + Z: Undo
			case MyKeyCodes.KEY_Z:
				app.setWaitCursor();
				if (event.isShiftKeyDown())
					app.getKernel().redo();//FIXME: write this instead after GuiManager is ready: app.getGuiManager().redo();
				else
					app.getKernel().undo();//FIXME: write this instead after GuiManager is ready: app.getGuiManager().undo();
				app.setDefaultCursor();
				consumed = true;
				break;

			case MyKeyCodes.KEY_V:
				
				app.setWaitCursor();
				CopyPaste.pasteFromXML(app);
				consumed = true;
				app.setDefaultCursor();
				
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
			case MyKeyCodes.KEY_NUMPADPLUS:
			case MyKeyCodes.KEY_NUMPADMINUS:
			case MyKeyCodes.KEY_MINUS:
			case MyKeyCodes.KEY_EQUALS:
				
				EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();

				// disable zooming in PEN mode
				if (ev.getMode() != EuclidianConstants.MODE_PEN
						|| ev.getMode() != EuclidianConstants.MODE_FREEHAND_FUNCTION) {

					//TCO boolean spanish = app.getLocale().toString()
					//		.startsWith("es");
					// AltGr+ on Spanish keyboard is ] so
					// allow <Ctrl>+ (zoom) but not <Ctrl><Alt>+ (fast zoom)
					// from eg Input Bar
					//if (!spanish || !event.isAltKeyDown()
					//		|| (event.getSource() instanceof EuclidianView)) {
						((EuclidianView) ev)
								.getEuclidianController().zoomInOut(event.isAltKeyDown(), event.getNativeKeyCode() == MyKeyCodes.KEY_MINUS || event.getNativeKeyCode() == MyKeyCodes.KEY_NUMPADMINUS );
						app.setUnsaved();
						consumed = true;
					//}
				}
				break;

			// Ctrl + D: toggles algebra style: value, definition, command
			case MyKeyCodes.KEY_D:
			case MyKeyCodes.KEY_BACK_QUOTE:
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
	public static boolean changeFontsAndGeoElements(AbstractApplication app,
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

	public void onKeyDown(KeyDownEvent event) {
		//AbstractApplication.debug("onkeydown");
	    event.preventDefault();
	    event.stopPropagation();
    }

	/**
	 * Handle pressed key for selected GeoElements
	 * 
	 * @return if key was consumed
	 */
	private boolean handleSelectedGeosKeys(KeyUpEvent event,
			ArrayList<GeoElement> geos) {

		int keyCode = event.getNativeKeyCode();

		// SPECIAL KEYS
		double changeVal = 0; // later: changeVal = base or -base
		// Shift : base = 0.1
		// Default : base = 1
		// Ctrl : base = 10
		// Alt : base = 100
		double base = 1;
		if (event.isShiftKeyDown())
			base = 0.1;
		if (event.isControlKeyDown())
			base = 10;
		if (event.isAltKeyDown())
			base = 100;

		if (geos == null || geos.size() == 0) {
/*
			// needs to work even if ev doesn't have focus
			if (keyCode == MyKeyCodes.KEY_CONTEXT_MENU) {
				Component comp = event.getComponent();
				Point p = MouseInfo.getPointerInfo().getLocation();
				p.translate(-comp.getLocationOnScreen().x,
						-comp.getLocationOnScreen().y);
				app.getGuiManager().toggleDrawingPadPopup(comp, p);
				return true;
			}
*/
			// Get the EuclidianView which has the focus
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			int width = ev.getWidth();
			int height = ev.getHeight();
			if (ev.hasFocus())
				switch (keyCode) {

				case MyKeyCodes.KEY_PAGEUP:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(0, (int) (height * base),
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case MyKeyCodes.KEY_PAGEDOWN:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(0, -(int) (height * base),
							AbstractEuclidianController.MOVE_VIEW);
					return true;
					
				case MyKeyCodes.KEY_INSERT:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove((int) (height * base), 0,
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case MyKeyCodes.KEY_HOME:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(-(int) (height * base), 0,
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case MyKeyCodes.KEY_DOWN:
					//if (app.isUsingFullGui() && app.getGuiManager().noMenusOpen())
					{
						ev.rememberOrigins();
						ev.setCoordSystemFromMouseMove(0,
								(int) (height / 100.0 * base),
								AbstractEuclidianController.MOVE_VIEW);
						return true;
					}
				case MyKeyCodes.KEY_UP:
					//if (app.isUsingFullGui() && app.getGuiManager().noMenusOpen())
					{
						ev.rememberOrigins();
						ev.setCoordSystemFromMouseMove(0,
								-(int) (height / 100.0 * base),
								AbstractEuclidianController.MOVE_VIEW);
						return true;
					}
				case MyKeyCodes.KEY_LEFT:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(
							-(int) (width / 100.0 * base), 0,
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case MyKeyCodes.KEY_RIGHT:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(
							(int) (width / 100.0 * base), 0,
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				}

			return false;
		}

		// FUNCTION and DELETE keys
		switch (keyCode) {

		/*
		case MyKeyCodes.KEY_CONTEXT_MENU:
			Component comp = event.getComponent();
			Point p = MouseInfo.getPointerInfo().getLocation();
			p.translate(-comp.getLocationOnScreen().x,
					-comp.getLocationOnScreen().y);
			app.getGuiManager().togglePopupMenu(geos, comp, p);
			break;*/
		case MyKeyCodes.KEY_PAGEUP:
			Iterator<GeoElement> it = geos.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geo.setLayer(geo.getLayer() + 1);
			}
			break;

		case MyKeyCodes.KEY_PAGEDOWN:
			it = geos.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geo.setLayer(geo.getLayer() - 1);
			}
			break;

		case MyKeyCodes.KEY_F3:
			// F3 key: copy definition to input field
			if (geos.size() == 1)
				handleFunctionKeyForAlgebraInput(3, geos.get(0));
			else {
				// F3 key: copy definitions to input field as list
				//JTextComponent textComponent = ((geogebra.javax.swing.JTextComponent)app.getGuiManager()
				//		.getAlgebraInputTextField()).getImpl();

				StringBuilder sb = new StringBuilder();
				sb.append('{');

				it = geos.iterator();
				while (it.hasNext()) {
					sb.append(it.next().getFormulaString(StringTemplate.defaultTemplate,
							false));
					if (it.hasNext())
						sb.append(",");
				}
				sb.append('}');
				
				AbstractApplication.debug("unimplemented "+sb.toString());

				//textComponent.setText(sb.toString());
				break;

			}
			return true;

		case MyKeyCodes.KEY_F4:
			// F4 key: copy value to input field
			handleFunctionKeyForAlgebraInput(4, geos.get(0));
			return true;

		case MyKeyCodes.KEY_F5:
			// F5 key: copy label to input field
			handleFunctionKeyForAlgebraInput(5, geos.get(0));
			return true;

		case MyKeyCodes.KEY_DELETE:
			// G.Sturr 2010-5-2: let the spreadsheet handle delete
			//if (app.getGuiManager().getSpreadsheetView().hasFocus())
			//	return false;
			// DELETE selected objects
			if (!app.isApplet() || app.isRightClickEnabled()) {
				app.deleteSelectedObjects();
				return true;
			}

		case MyKeyCodes.KEY_BACKSPACE:
			// G.Sturr 2010-5-2: let the spreadsheet handle delete
			//if (app.getGuiManager().getSpreadsheetView().hasFocus())
			//	return false;
			// DELETE selected objects
			// Note: ctrl-h generates a MyKeyCodes.KEY_BACK_SPACE event, so check
			// for ctrl too
			if (!event.isControlKeyDown()
					&& (!app.isApplet() || app.isRightClickEnabled())) {
				app.deleteSelectedObjects();
				return true;
			}
			break;
		}
/*
		// ignore key events coming from tables like the spreadsheet to
		// allow start editing, moving etc
		if (event.getSource() instanceof JTable
				|| (app.isUsingFullGui()
						&& app.getGuiManager().hasSpreadsheetView() && app
						.getGuiManager().getSpreadsheetView().hasFocus())) {
			return false;
		}
*/
		// check for arrow keys: try to move objects accordingly
		boolean moved = false;

		switch (keyCode) {
		case MyKeyCodes.KEY_UP:

			// make sure arrow keys work in menus
			//if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen())
			//	return false;

			changeVal = base;
			moved = handleArrowKeyMovement(geos, 0, changeVal, 0);
			break;

		case MyKeyCodes.KEY_DOWN:

			// make sure arrow keys work in menus
			//if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen())
			//	return false;

			changeVal = -base;
			moved = handleArrowKeyMovement(geos, 0, changeVal, 0);
			break;

		case MyKeyCodes.KEY_RIGHT:

			// make sure arrow keys work in menus
			//if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen())
			//	return false;

			changeVal = base;
			moved = handleArrowKeyMovement(geos, changeVal, 0, 0);
			break;

		case MyKeyCodes.KEY_LEFT:

			// make sure arrow keys work in menus
			//if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen())
			//	return false;

			changeVal = -base;
			moved = handleArrowKeyMovement(geos, changeVal, 0, 0);
			break;

		case MyKeyCodes.KEY_PAGEUP:
			changeVal = base;
			moved = handleArrowKeyMovement(geos, 0, 0, changeVal);
			break;

		case MyKeyCodes.KEY_PAGEDOWN:
			changeVal = -base;
			moved = handleArrowKeyMovement(geos, 0, 0, changeVal);
			break;

		}

		if (moved)
			return true;

		boolean vertical = true;

		// F2, PLUS, MINUS keys
		switch (keyCode) {
		case MyKeyCodes.KEY_F2:
			// handle F2 key to start editing first selected element
			if (app.isUsingFullGui()) {
				//app.getGuiManager().startEditing(geos.get(0));
				return true;
			}
			break;

		case MyKeyCodes.KEY_NUMPADPLUS:
		//case MyKeyCodes.KEY_ADD: // can be own key on some keyboard
		case MyKeyCodes.KEY_EQUALS: // same key as plus (on most keyboards)
		case MyKeyCodes.KEY_UP:
			changeVal = base;
			vertical = true;
			break;
		case MyKeyCodes.KEY_RIGHT:
			changeVal = base;
			vertical = false;
			break;

		case MyKeyCodes.KEY_MINUS:
		case MyKeyCodes.KEY_NUMPADMINUS:
		//case MyKeyCodes.KEY_SUBTRACT:
		case MyKeyCodes.KEY_DOWN:
			changeVal = -base;
			vertical = true;
			break;
		case MyKeyCodes.KEY_LEFT:
			changeVal = -base;
			vertical = false;
			break;
		}

		if (changeVal == 0) {
			char keyChar = (char) event.getNativeKeyCode();//.getKeyChar();
			if (keyChar == '+')
				changeVal = base;
			else if (keyChar == '-')
				changeVal = -base;
		}

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
						GeoPoint2 p = (GeoPoint2) geo;
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

}
