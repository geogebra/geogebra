package geogebra.main;

import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.DrawTextField;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.main.KeyCodes;
import geogebra.common.util.CopyPaste;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.GuiManager;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.app.MyFileFilter;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.util.Util;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.MouseInfo;
import java.awt.Point;
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

		return handleGeneralKeys(KeyCodes.translateJavacode(event.getKeyCode()), event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.getSource() instanceof JTable, event.getSource() instanceof EuclidianView);
	}




	/**
	 * Handle pressed key for selected GeoElements
	 * 
	 * @return if key was consumed
	 */
	private boolean handleSelectedGeosKeys(KeyEvent event,
			ArrayList<GeoElement> geos) {

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
				p.translate(-comp.getLocationOnScreen().x,
						-comp.getLocationOnScreen().y);
				((Application)app).getGuiManager().toggleDrawingPadPopup(comp, p);
				return true;
			}

			// Get the EuclidianView which has the focus
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			int width = ev.getWidth();
			int height = ev.getHeight();
			if (ev.hasFocus() && app.isShiftDragZoomEnabled())
				switch (keyCode) {

				case KeyEvent.VK_PAGE_UP:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(0, (int) (height * base),
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case KeyEvent.VK_PAGE_DOWN:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(0, -(int) (height * base),
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case KeyEvent.VK_INSERT:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove((int) (height * base), 0,
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case KeyEvent.VK_HOME:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(-(int) (height * base), 0,
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case KeyEvent.VK_DOWN:
					if (app.isUsingFullGui()
							&& ((Application)app).getGuiManager().noMenusOpen()) {
						ev.rememberOrigins();
						ev.setCoordSystemFromMouseMove(0,
								(int) (height / 100.0 * base),
								AbstractEuclidianController.MOVE_VIEW);
						return true;
					}
				case KeyEvent.VK_UP:
					if (app.isUsingFullGui()
							&& ((Application)app).getGuiManager().noMenusOpen()) {
						ev.rememberOrigins();
						ev.setCoordSystemFromMouseMove(0,
								-(int) (height / 100.0 * base),
								AbstractEuclidianController.MOVE_VIEW);
						return true;
					}
				case KeyEvent.VK_LEFT:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(
							-(int) (width / 100.0 * base), 0,
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case KeyEvent.VK_RIGHT:
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

		case KeyEvent.VK_CONTEXT_MENU:
			// if (geos.size() == 1) {
			Component comp = event.getComponent();
			Point p = MouseInfo.getPointerInfo().getLocation();
			p.translate(-comp.getLocationOnScreen().x,
					-comp.getLocationOnScreen().y);
			((Application)app).getGuiManager().togglePopupMenu(geos, comp, p);
			// } else {
			// app.getGuiManager().showPropertiesDialog(app.getSelectedGeos());
			// }
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
				JTextComponent textComponent = ((geogebra.javax.swing.JTextComponent)app.getGuiManager()
						.getAlgebraInputTextField()).getImpl();

				StringBuilder sb = new StringBuilder();
				sb.append('{');

				it = geos.iterator();
				while (it.hasNext()) {
					sb.append(it.next().getFormulaString(StringTemplate.defaultTemplate,
							false));
					if (it.hasNext())
						sb.append(',');
				}
				sb.append('}');

				textComponent.setText(sb.toString());
				break;

			}
			return true;

		case KeyEvent.VK_F1:
			app.getDialogManager().openToolHelp();
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
			// G.Sturr 2010-5-2: let the spreadsheet handle delete
			if (((Application)app).getGuiManager().getSpreadsheetView().hasFocus())
				return false;
			// DELETE selected objects
			if (!app.isApplet() || app.isRightClickEnabled()) {
				app.deleteSelectedObjects();
				return true;
			}

		case KeyEvent.VK_BACK_SPACE:
			// G.Sturr 2010-5-2: let the spreadsheet handle delete
			if (((Application)app).getGuiManager().getSpreadsheetView().hasFocus())
				return false;
			// DELETE selected objects
			// Note: ctrl-h generates a KeyEvent.VK_BACK_SPACE event, so check
			// for ctrl too
			if (!event.isControlDown()
					&& (!app.isApplet() || app.isRightClickEnabled())) {
				app.deleteSelectedObjects();
				return true;
			}
			break;
		}

		// ignore key events coming from tables like the spreadsheet to
		// allow start editing, moving etc
		if (event.getSource() instanceof JTable
				|| (app.isUsingFullGui()
						&& ((Application)app).getGuiManager().hasSpreadsheetView() && ((Application)app)
						.getGuiManager().getSpreadsheetView().hasFocus())) {
			return false;
		}

		// check for arrow keys: try to move objects accordingly
		boolean moved = false;

		switch (keyCode) {
		case KeyEvent.VK_UP:

			// make sure arrow keys work in menus
			if (((Application)app).isUsingFullGui() && !((Application)app).getGuiManager().noMenusOpen())
				return false;

			changeVal = base;
			moved = handleArrowKeyMovement(geos, 0, changeVal, 0);
			break;

		case KeyEvent.VK_DOWN:

			// make sure arrow keys work in menus
			if (app.isUsingFullGui() && !((Application)app).getGuiManager().noMenusOpen())
				return false;

			changeVal = -base;
			moved = handleArrowKeyMovement(geos, 0, changeVal, 0);
			break;

		case KeyEvent.VK_RIGHT:

			// make sure arrow keys work in menus
			if (app.isUsingFullGui() && !((Application)app).getGuiManager().noMenusOpen())
				return false;

			changeVal = base;
			moved = handleArrowKeyMovement(geos, changeVal, 0, 0);
			break;

		case KeyEvent.VK_LEFT:

			// make sure arrow keys work in menus
			if (app.isUsingFullGui() && !((Application)app).getGuiManager().noMenusOpen())
				return false;

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
			if (app.isUsingFullGui()) {
				((Application)app).getGuiManager().startEditing(geos.get(0));
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

	/**
	 * Handles function key for given GeoElement: F3: copy definition to input
	 * field F4: copy value to input field F5: copy name to input field
	 * 
	 * @param fkey
	 *            number
	 * @param geo
	 */
	public void handleFunctionKeyForAlgebraInput(int fkey, GeoElement geo) {
		if (!app.isUsingFullGui() || !app.showAlgebraInput())
			return;
		JTextComponent textComponent = ((geogebra.javax.swing.JTextComponent)app.getGuiManager()
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

			GuiManager gui = ((Application)app).getGuiManager();
			gui.getLayout().getDockManager()
			.moveFocus(!isShiftDown);

			return true;

		} else if (app.getActiveEuclidianView().hasFocus()
				|| ((Application)app).getGuiManager().getAlgebraView().hasFocus()) {
			if (isShiftDown) {
				app.selectLastGeo();
			} else {
				app.selectNextGeo();
			}

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






}
