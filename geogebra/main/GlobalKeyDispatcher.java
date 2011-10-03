package geogebra.main;

import geogebra.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.GuiManager;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.app.MyFileFilter;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoTextField;
import geogebra.kernel.Kernel;
import geogebra.kernel.PointProperties;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.util.CopyPaste;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileFilter;
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
public class GlobalKeyDispatcher implements KeyEventDispatcher {

	protected Application app;

	public GlobalKeyDispatcher(Application app) {
		this.app = app;
	}

	/**
	 * This method is called by the current KeyboardFocusManager 
	 * before they are dispatched to their targets, 
	 * allowing it to handle the key event and consume it. 
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {

		// ignore key events coming from text components (i.e. text fields and text areas)
		// or key events coming from popups (source class = JRootPane)
		if (event.isConsumed() || event.getSource() instanceof JTextComponent || event.getSource() instanceof JRootPane) {			
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
	 * "Key typed" events are higher-level and generally 
	 * do not depend on the platform or keyboard layout. 
	 * They are generated when a Unicode character is entered, 
	 * and are the preferred way to find out about character input.
	 */
	private boolean handleKeyTyped(KeyEvent event) {	
		// ignore key events coming from tables like the spreadsheet to
		// allow start editing
		if (event.getSource() instanceof JTable) {			
			return false;
		} 	


		// show RENAME dialog when a letter is typed		
		char ch = event.getKeyChar();		
		if (Character.isLetter(ch) && 
				!event.isMetaDown() &&
				!event.isAltDown() &&
				!event.isControlDown()) 
		{
			GeoElement geo;					
			if (app.selectedGeosSize() == 1) {
				// selected geo
				geo = app.getSelectedGeos().get(0);										
			}				
			else {
				// last created geo
				geo = app.getLastCreatedGeoElement();			
			}	

			// open rename dialog
			if (geo != null) {	

				if (geo instanceof GeoTextField) {
					GeoTextField tf = (GeoTextField)geo;
					tf.setFocus(ch+"");
				} else app.getGuiManager().showRenameDialog(geo, true, Character.toString(ch), false);
				return true;
			}
		}		

		return false;
	}

	/**
	 * Handles general keys like ESC and function keys that don't involved
	 * selected GeoElements.
	 * @param event 
	 * @return if key was consumed
	 */
	public boolean handleGeneralKeys(KeyEvent event) {
		boolean consumed = false;

		// ESC and function keys
		switch (event.getKeyCode()) {					
		case KeyEvent.VK_ESCAPE:		
			// ESC: set move mode				
			app.setMoveMode();
			app.getActiveEuclidianView().getEuclidianController().deletePastePreviewSelected();
			consumed = true;			
			break;	

		case KeyEvent.VK_ENTER:	
			// check not spreadsheet
			if (!(event.getSource() instanceof JTable)) {			

				// ENTER: set focus to input field
				if (app.useFullGui() && app.getGuiManager().noMenusOpen())
				{
					if (app.showAlgebraInput() && 
							!app.getGuiManager().getAlgebraInput().hasFocus()) 
					{	
						// focus this frame (needed for external view windows)
						if(!app.isApplet() && app.getFrame() != null) {
							app.getFrame().toFront();
						}

						app.getGuiManager().getAlgebraInput().requestFocus();

						consumed = true;
					}
				}
			}
			break;			

			// toggle boolean or run script when Spacebar pressed
		case KeyEvent.VK_SPACE:	
			// check not spreadsheet
			if (!(event.getSource() instanceof JTable)) {			

				ArrayList<GeoElement> selGeos = app.getSelectedGeos();
				if (selGeos.size() == 1) {
					if (selGeos.get(0).isGeoBoolean()) {
						GeoBoolean geoBool = (GeoBoolean)selGeos.get(0);
						geoBool.setValue(!geoBool.getBoolean());
						geoBool.updateRepaint();
					} else {
						selGeos.get(0).runScripts(null);
					}  

					consumed = true;

				}

			}
			break;			

		case KeyEvent.VK_TAB:
			if (event.isControlDown() && app.useFullGui()) {
				consumed = true;
				GuiManager gui = app.getGuiManager();
				gui.getLayout().getDockManager().moveFocus(!event.isShiftDown());

			} else if (app.getEuclidianView().hasFocus()|| app.getGuiManager().getAlgebraView().hasFocus()) {
				if (event.isShiftDown()) app.selectLastGeo(); else app.selectNextGeo();
				consumed = true;
			}

			break;


			// F9 updates construction
			// cmd-f9 on Mac OS
		case KeyEvent.VK_F9:
			if (!app.isApplet() || app.isRightClickEnabled()) {
				app.getKernel().updateConstruction();
				app.setUnsaved();
				consumed = true;
			}
			break;
		}

		int keyCode = event.getKeyCode();

		// make sure Ctrl-1/2/3 works on the Numeric Keypad even with Numlock off
		// **** NB if NumLock on, event.isShiftDown() always returns false with Numlock on!!! (Win 7)
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

		// Ctrl key down
		if (Application.isControlDown(event)) {

			switch (keyCode) {				
			case KeyEvent.VK_1:
			case KeyEvent.VK_NUMPAD1:
				// event.isShiftDown() doesn't work if NumLock on
				// however .isAltDown() stops AltGr-1 from working (| on some keyboards)
				if (event.isShiftDown()) {// || event.isAltDown()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(Application.VIEW_EUCLIDIAN),
							Application.VIEW_EUCLIDIAN);
					consumed = true;

				} else if (!event.isAltDown()) { // make sure not triggered on AltGr
					// Ctrl-1: set objects back to the default size (for font size 12)
					changeFontsAndGeoElements(app, 12, false);
					consumed = true;
				}
				break;

			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_2:
				// event.isShiftDown() doesn't work if NumLock on
				// however .isAltDown() stops AltGr-2 from working (superscript 2 on some keyboards)
				if (event.isShiftDown()) {// || event.isAltDown()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(Application.VIEW_EUCLIDIAN2),
							Application.VIEW_EUCLIDIAN2);
					consumed = true;

				} else if (!event.isAltDown()) { // make sure not triggered on AltGr
					// Ctrl-2: large font size and thicker lines for projectors etc
					int fontSize = Math.min(32, app.getFontSize() + 4);
					changeFontsAndGeoElements(app, fontSize, false);
					consumed = true;	
				}
				break;

			case KeyEvent.VK_NUMPAD3:
			case KeyEvent.VK_3:
				// event.isShiftDown() doesn't work if NumLock on
				// however .isAltDown() stops AltGr-3 from working (^ on Croatian keyboard)
				if (event.isShiftDown()){ // || event.isAltDown()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(Application.VIEW_EUCLIDIAN3D),
							Application.VIEW_EUCLIDIAN3D);
					consumed = true;

				} else if (!event.isAltDown()){ // make sure not triggered on AltGr
					// Ctrl-3: set black/white mode printing and visually impaired users
					changeFontsAndGeoElements(app, app.getFontSize(), true);
					consumed = true;
				}
				break;

			case KeyEvent.VK_C:
				// Ctrl-shift-c: copy graphics view to clipboard
				//   should also work in applets with no menubar
				if (event.isShiftDown()) {
					app.copyGraphicsViewToClipboard();	
					consumed = true;
				} else {
					// check not spreadsheet
					if (!(event.getSource() instanceof JTable) &&
							!(app.getGuiManager().getSpreadsheetView().hasFocus()) &&
							!(((AlgebraInput)app.getGuiManager().getAlgebraInput()).getTextField().hasFocus())) {

						// Copy selected geos
						app.setWaitCursor();
						CopyPaste.copyToXML(app, app.getSelectedGeos());
						app.updateMenubar();
						app.setDefaultCursor();
						consumed = true;
					}
				}
				break;

				// Ctrl + H / G: Show Hide objects (labels)
			case KeyEvent.VK_G:
			case KeyEvent.VK_H:
				if (event.isShiftDown()) 
					app.showHideSelectionLabels();
				else
					app.showHideSelection();
				consumed = true;								
				break;

				// Ctrl + E: open object properties (needed here for spreadsheet)
			case KeyEvent.VK_E:
				app.getGuiManager().showPropertiesDialog();
				consumed = true;								
				break;

				// Ctrl + F: refresh views
			case KeyEvent.VK_F:
				app.refreshViews();
				consumed = true;								
				break;

			case KeyEvent.VK_M:
				if (!app.isApplet() && event.isShiftDown()) {
					app.exportToLMS();
					consumed = true;
				} else if (!app.isApplet() || app.isRightClickEnabled()) {
					app.setStandardView();
					consumed = true;					
				}
				break;


				/*
				 * send next instance to front
				 * (alt - last)
				 */
			case KeyEvent.VK_N:
				if (event.isShiftDown()) {
					ArrayList<GeoGebraFrame> ggbInstances = GeoGebraFrame.getInstances();
					int size = ggbInstances.size();
					if (size == 1) {
						// load next file in folder
						

						// ask if OK to discard current file
						if (app.isSaved() || app.saveCurrentFile()) {

							MyFileFilter fileFilter = new MyFileFilter();
							fileFilter.addExtension("ggb");

							File[] options = app.getCurrentPath().listFiles((FileFilter) fileFilter);


							// no current file, just load the first file in the folder
							if (app.getCurrentFile() == null) {
								if (options.length > 0) {
									app.getGuiManager().loadFile(options[0], false);
									consumed = true;
								}
								break;
							}

							TreeSet<File> sortedSet = new TreeSet<File>(Util.getFileComparator());
							for (int i = 0 ; i < options.length ; i++) {
								if (options[i].isFile())
									sortedSet.add(options[i]);
							}

							String currentFile = app.getCurrentFile().getName();


							Iterator<File> iterator = sortedSet.iterator();
							File fileToLoad = null;
							while (iterator.hasNext() && fileToLoad == null) {
								if (iterator.next().getName().equals(currentFile)) {
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
							GeoGebraFrame ggb = (GeoGebraFrame) ggbInstances.get(i);
							Application application = ggb.getApplication();

							if (app == application) {
								int n = event.isAltDown() ? ((i-1+size)%size) : ((i+1)%size);
								ggb = (GeoGebraFrame) ggbInstances.get(n); // next/last instance
								ggb.toFront();
								ggb.requestFocus();
								break; // break from if loop

							}
						}

						consumed = true;

					}
				}
				break;



				// needed for detached views and MacOS
				// Cmd + Y: Redo
			case KeyEvent.VK_Y:
				app.getGuiManager().redo();
				consumed = true;
				break;

				// needed for detached views and MacOS
				// Ctrl + Z: Undo
			case KeyEvent.VK_Z:
				if (event.isShiftDown())
					app.getGuiManager().redo();
				else
					app.getGuiManager().undo();
				consumed = true;
				break;

			case KeyEvent.VK_V:
				// check not spreadsheet, not inputbar
				if (!(event.getSource() instanceof JTable) &&
						!(app.getGuiManager().getSpreadsheetView().hasFocus()) &&
						!(((AlgebraInput)app.getGuiManager().getAlgebraInput()).getTextField().hasFocus())) {

					app.setWaitCursor();
					CopyPaste.pasteFromXML(app);
					app.setDefaultCursor();
					consumed = true;
				}
				break;

				// ctrl-R updates construction
				// make sure it works in applets without a menubar
			case KeyEvent.VK_R:
				if (!app.isApplet() || app.isRightClickEnabled()) {
					app.getKernel().updateConstruction();
					app.setUnsaved();
					consumed = true;
				}
				break;

				// ctrl-shift-s (toggle spreadsheet)
			case KeyEvent.VK_S:
				if (event.isShiftDown() && app.useFullGui()) {
					app.getGuiManager().setShowView(
							!app.getGuiManager().showView(Application.VIEW_SPREADSHEET),
							Application.VIEW_SPREADSHEET);
					consumed = true;
				}
				break;

				// Ctrl-(shift)-Q (deprecated - doesn't work on MacOS)
				// Ctrl-(shift)-J
			case KeyEvent.VK_J:
			case KeyEvent.VK_Q:
				if (event.isShiftDown())
					app.selectAllDescendants();
				else
					app.selectAllPredecessors();
				consumed = true;
				break;

				// Ctrl + "+", Ctrl + "-" zooms in or out in graphics view
			case KeyEvent.VK_PLUS:
			case KeyEvent.VK_MINUS:
			case KeyEvent.VK_EQUALS:	
				
				// disable zooming in PEN mode
				if (app.getEuclidianView().getMode() != EuclidianConstants.MODE_PEN) {

					boolean spanish = app.getLocale().toString().startsWith("es");
	
					// AltGr+ on Spanish keyboard is ] so
					// allow <Ctrl>+ (zoom) but not <Ctrl><Alt>+ (fast zoom)
					// from eg Input Bar
					if (!spanish || !event.isAltDown() || (event.getSource() instanceof EuclidianView)) {
						((EuclidianView)app.getActiveEuclidianView()).getEuclidianController().zoomInOut(event);
						app.setUnsaved();
						consumed = true;				
					}
				}
				break;

				// Ctrl + D: toggles algebra style: value, definition, command
			case KeyEvent.VK_D:
			case KeyEvent.VK_BACK_QUOTE:
				if (!event.isShiftDown()) {
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
	 * Handle pressed key for selected GeoElements
	 * 
	 * @return if key was consumed
	 */
	private boolean handleSelectedGeosKeys(KeyEvent event, ArrayList<GeoElement> geos) {

		int keyCode = event.getKeyCode();

		// SPECIAL KEYS
		double changeVal = 0; // later: changeVal = base or -base
		// Shift : base = 0.1
		// Default : base = 1
		// Ctrl : base = 10
		// Alt : base = 100
		double base = 1;
		if (event.isShiftDown())
			base = 0.1;
		if (Application.isControlDown(event))
			base = 10;
		if (event.isAltDown())
			base = 100;



		if (geos == null || geos.size() == 0) {

			// needs to work even if ev doesn't have focus
			if (keyCode == KeyEvent.VK_CONTEXT_MENU) {
				Component comp = event.getComponent();
				Point p = MouseInfo.getPointerInfo().getLocation();
				p.translate(-comp.getLocationOnScreen().x, -comp.getLocationOnScreen().y);
				app.getGuiManager().toggleDrawingPadPopup(comp, p);
				return true;
			}

			// Get the EuclidianView which has the focus
			EuclidianViewInterface ev=app.getActiveEuclidianView();
			int width = ev.getWidth();
			int height = ev.getHeight();
			if (ev.hasFocus())
				switch (keyCode) {

				case KeyEvent.VK_PAGE_UP:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(0, (int)(height * base), EuclidianController.MOVE_VIEW);
					return true;
				case KeyEvent.VK_PAGE_DOWN:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(0, -(int)(height * base), EuclidianController.MOVE_VIEW);
					return true;
				case KeyEvent.VK_INSERT:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove((int)(height * base), 0, EuclidianController.MOVE_VIEW);
					return true;
				case KeyEvent.VK_HOME:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(-(int)(height * base), 0, EuclidianController.MOVE_VIEW);
					return true;
				case KeyEvent.VK_DOWN:
					if (app.useFullGui() && app.getGuiManager().noMenusOpen()) {
						ev.rememberOrigins();
						ev.setCoordSystemFromMouseMove(0, (int)(height / 100.0 * base), EuclidianController.MOVE_VIEW);
						return true;
					}
				case KeyEvent.VK_UP:
					if (app.useFullGui() && app.getGuiManager().noMenusOpen()) {
						ev.rememberOrigins();
						ev.setCoordSystemFromMouseMove(0, -(int)(height / 100.0 * base), EuclidianController.MOVE_VIEW);
						return true;
					}
				case KeyEvent.VK_LEFT:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(-(int)(width / 100.0 * base), 0, EuclidianController.MOVE_VIEW);
					return true;
				case KeyEvent.VK_RIGHT:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove((int)(width / 100.0 * base), 0, EuclidianController.MOVE_VIEW);
					return true;
				}

			return false;
		}

		// FUNCTION and DELETE keys
		switch (keyCode) {

		case KeyEvent.VK_CONTEXT_MENU:
			//if (geos.size() == 1) {
			Component comp = event.getComponent();
			Point p = MouseInfo.getPointerInfo().getLocation();
			p.translate(-comp.getLocationOnScreen().x, -comp.getLocationOnScreen().y);
			app.getGuiManager().togglePopupMenu(geos, comp, p);
			//} else {
			//	app.getGuiManager().showPropertiesDialog(app.getSelectedGeos());
			//}
			break;
		case KeyEvent.VK_PAGE_UP:
			Iterator<GeoElement> it = geos.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geo.setLayer(geo.getLayer() + 1);
			}			
			break;

		case KeyEvent.VK_PAGE_DOWN:
			it = geos.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geo.setLayer(geo.getLayer() - 1);
			}			
			break;


		case KeyEvent.VK_F3:
			// F3 key: copy definition to input field				
			if (geos.size() == 1)
				handleFunctionKeyForAlgebraInput(3, geos.get(0));
			else {
				// F3 key: copy definitions to input field as list 			
				JTextComponent textComponent = app.getGuiManager().getAlgebraInputTextField();				

				StringBuilder sb = new StringBuilder();
				sb.append('{');

				it = geos.iterator();
				while (it.hasNext()) {
					sb.append(it.next().getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false));
					if (it.hasNext()) sb.append(",");
				}
				sb.append('}');

				textComponent.setText(sb.toString());
				break;

			}
			return true;

		case KeyEvent.VK_F4:
			// F4 key: copy value to input field				
			handleFunctionKeyForAlgebraInput(4, geos.get(0));
			return true;

		case KeyEvent.VK_F5:
			// F5 key: copy label to input field				
			handleFunctionKeyForAlgebraInput(5, geos.get(0));
			return true;

		case KeyEvent.VK_DELETE:
			//G.Sturr 2010-5-2: let the spreadsheet handle delete
			if (app.getGuiManager().getSpreadsheetView().hasFocus()) 			
				return false;
			// DELETE selected objects
			if (!app.isApplet() || app.isRightClickEnabled()) {
				app.deleteSelectedObjects();
				return true;
			}

		case KeyEvent.VK_BACK_SPACE:
			//G.Sturr 2010-5-2: let the spreadsheet handle delete
			if (app.getGuiManager().getSpreadsheetView().hasFocus()) 			
				return false;
			// DELETE selected objects
			// Note: ctrl-h generates a KeyEvent.VK_BACK_SPACE event, so check for ctrl too
			if (!event.isControlDown() && (!app.isApplet() || app.isRightClickEnabled())) {
				app.deleteSelectedObjects();
				return true;
			}
			break;						
		}				

		// ignore key events coming from tables like the spreadsheet to
		// allow start editing, moving etc
		if (event.getSource() instanceof JTable
				|| (app.useFullGui() &&  
						app.getGuiManager().hasSpreadsheetView() &&
						app.getGuiManager().getSpreadsheetView().hasFocus())) {			
			return false;
		} 	


		// check for arrow keys: try to move objects accordingly
		boolean moved = false;

		switch (keyCode) {
		case KeyEvent.VK_UP:

			// make sure arrow keys work in menus
			if (app.useFullGui() && !app.getGuiManager().noMenusOpen()) return false;

			changeVal = base;			
			moved = handleArrowKeyMovement(geos, 0, changeVal, 0);
			break;

		case KeyEvent.VK_DOWN:

			// make sure arrow keys work in menus
			if (app.useFullGui() && !app.getGuiManager().noMenusOpen()) return false;

			changeVal = -base;
			moved = handleArrowKeyMovement(geos, 0, changeVal, 0);
			break;

		case KeyEvent.VK_RIGHT:

			// make sure arrow keys work in menus
			if (app.useFullGui() && !app.getGuiManager().noMenusOpen()) return false;

			changeVal = base;
			moved = handleArrowKeyMovement(geos, changeVal, 0, 0);
			break;

		case KeyEvent.VK_LEFT:

			// make sure arrow keys work in menus
			if (app.useFullGui() && !app.getGuiManager().noMenusOpen()) return false;

			changeVal = -base;
			moved = handleArrowKeyMovement(geos, changeVal, 0, 0);
			break;

		case KeyEvent.VK_PAGE_UP:
			changeVal = base;			
			moved = handleArrowKeyMovement(geos, 0, 0, changeVal);
			break;

		case KeyEvent.VK_PAGE_DOWN:
			changeVal = -base;
			moved = handleArrowKeyMovement(geos, 0, 0, changeVal);
			break;




		}


		if (moved)
			return true;

		boolean vertical = true;

		// F2, PLUS, MINUS keys
		switch (keyCode) {
		case KeyEvent.VK_F2:
			// handle F2 key to start editing first selected element
			if (app.useFullGui()) {
				app.getGuiManager().startEditing(geos.get(0));
				return true;
			}			
			break;

		case KeyEvent.VK_PLUS:
		case KeyEvent.VK_ADD: // can be own key on some keyboard
		case KeyEvent.VK_EQUALS: // same key as plus (on most keyboards)
		case KeyEvent.VK_UP:
			changeVal = base;
			vertical = true;
			break;
		case KeyEvent.VK_RIGHT:
			changeVal = base;
			vertical = false;
			break;

		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_SUBTRACT:
		case KeyEvent.VK_DOWN:
			changeVal = -base;
			vertical = true;
			break;
		case KeyEvent.VK_LEFT:
			changeVal = -base;
			vertical = false;
			break;
		}

		if (changeVal == 0) {
			char keyChar = event.getKeyChar();
			if (keyChar == '+')
				changeVal = base;
			else if (keyChar == '-')
				changeVal = -base;
		}

		// change all geoelements
		if (changeVal != 0) {

			boolean twoSliders = geos.size() == 2 && geos.get(0).isGeoNumeric() && geos.get(1).isGeoNumeric();

			for (int i = geos.size() - 1; i >= 0; i--) {

				GeoElement geo = geos.get(i);								

				if (geo.isChangeable()) {

					// update number
					if (geo.isGeoNumeric() && (!twoSliders || ((vertical && i == 0) || (!vertical && i == 1)))) {
						GeoNumeric num = (GeoNumeric) geo;
						double newValue = num.getValue() + changeVal * num.getAnimationStep();
						if (num.getAnimationStep() > Kernel.MIN_PRECISION) {
							// round to decimal fraction, e.g. 2.800000000001 to 2.8
							if (num.isGeoAngle())
								newValue = Kernel.PI_180 * app.getKernel().checkDecimalFraction(newValue * Kernel.CONST_180_PI, 1 / num.getAnimationStep());
							else
								newValue = app.getKernel().checkDecimalFraction(newValue, 1 / num.getAnimationStep());
						}
						num.setValue(newValue);					
					} 

					// update point on path
					else if (geo.isGeoPoint() && !geo.isGeoElement3D()) {
						GeoPoint p = (GeoPoint) geo;
						if (p.hasPath()) {
							p.addToPathParameter(changeVal * p.getAnimationStep());
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

	private TreeSet<AlgoElement> tempSet;	
	private TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElement>();
		}
		return tempSet;
	}


	/**
	 * Handles function key for given GeoElement:	 
	 * F3: copy definition to input field
	 * F4: copy value to input field
	 * F5: copy name to input field
	 * @param fkey number
	 * @param geo 
	 */
	public void handleFunctionKeyForAlgebraInput(int fkey, GeoElement geo) {
		if (!app.useFullGui() || !app.showAlgebraInput()) 
			return;		
		JTextComponent textComponent = app.getGuiManager().getAlgebraInputTextField();				

		switch (fkey) {				
		case 3: // F3 key: copy definition to input field
			textComponent.setText(geo.getDefinitionForInputBar());
			break;

		case 4: // F4 key: copy value to input field	
			textComponent.replaceSelection(" " + geo.getValueForInputBar() + " ");
			break;

		case 5: // F5 key: copy name to input field					
			textComponent.replaceSelection(" " + geo.getLabel() + " ");
			break;				
		}

		textComponent.requestFocusInWindow();			
	}

	/**
	 * Tries to move the given objects after pressing an arrow key on the keyboard.
	 * 
	 * @param keyCode VK_UP, VK_DOWN, VK_RIGHT, VK_LEFT
	 * @return whether any object was moved
	 */
	private boolean handleArrowKeyMovement(ArrayList<GeoElement> geos, double xdiff, double ydiff, double zdiff) {	
		GeoElement geo = geos.get(0);

		boolean allSliders = true;
		for (int i = 0 ; i < geos.size() ; i++) {
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
		tempVec.setX(xd);tempVec.setY(yd);;tempVec.setZ(zd);

		// move objects
		boolean moved = GeoElement.moveObjects(geos, tempVec, null, null);

		// nothing moved
		if (!moved) {
			for (int i=0; i< geos.size(); i++) {
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
	private Coords tempVec;

	/**
	 * Changes the font size of the user interface and construction element styles (thickness,
	 * size) for a given fontSize. 
	 * @param app 
	 * @param fontSize 12-32pt
	 * @param blackWhiteMode whether only black should be used as a color
	 * @return whether change was performed
	 */	
	public static boolean changeFontsAndGeoElements(Application app, int fontSize, boolean blackWhiteMode) {
		if (app.isApplet()) 
			return false;

		app.setWaitCursor();

		// determine styles
		// set new default line thickness
		int oldFontSize = app.getFontSize();
		int angleSizeIncr = fontSize - oldFontSize;
		int incr = getPointSizeInc(oldFontSize, fontSize);

		// construction defaults
		ConstructionDefaults cd = app.getKernel().getConstruction().getConstructionDefaults();
		cd.setDefaultLineThickness(EuclidianView.DEFAULT_LINE_THICKNESS + incr);
		cd.setDefaultPointSize(EuclidianView.DEFAULT_POINT_SIZE + incr);
		cd.setDefaultAngleSize(EuclidianView.DEFAULT_ANGLE_SIZE + angleSizeIncr);
		// blackWhiteMode: set defaults for new GeoElements
		cd.setBlackWhiteMode(blackWhiteMode);

		// change application font size
		app.setFontSize(fontSize);
		if (app.useFullGui())
			app.getGuiManager().updateSpreadsheetColumnWidths();

		// apply styles to to selected or all geos
		Iterator<GeoElement> it = null;
		if (app.getSelectedGeos().size() == 0) {
			// change all geos
			it = app.getKernel().getConstruction().getGeoSetConstructionOrder().iterator();
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
		if (oldFontSize == newFontSize) return 0;
		int step = newFontSize > oldFontSize ? 1 : -1;

		int left = Math.min(oldFontSize, newFontSize);
		int right = Math.max(oldFontSize, newFontSize);
		int [] borders = { 16, 22, 28 };
		int incr = 0;
		for (int i=0; i < borders.length; i++) {
			if (left < borders[i] && borders[i] <= right) {
				incr = incr + step;
			}
		}

		return incr;
	}

	private static void setGeoProperties(GeoElement geo, int lineThicknessIncr, int pointSizeIncr, 
			int angleSizeIncr, boolean blackWhiteMode) 
	{
		if (!geo.isGeoText() && !geo.isGeoImage() && !geo.isGeoPolygon()) { // affects bounding box
			int lineThickness = Math.max(0, geo.getLineThickness() + lineThicknessIncr);
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
			geo.setObjColor(Color.black);
		}
	}

}
