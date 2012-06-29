package geogebra.web.main;

import geogebra.common.awt.Color;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.AbstractEuclidianView;
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
import geogebra.common.main.GWTKeycodes;
import geogebra.common.main.KeyCodes;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.CopyPaste;
import geogebra.web.euclidian.EuclidianView;
import geogebra.web.gui.applet.GeoGebraFrame;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class GlobalKeyDispatcherW extends
        geogebra.common.main.GlobalKeyDispatcher implements KeyUpHandler, KeyDownHandler {

	public GlobalKeyDispatcherW(AbstractApplication app) {
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
	    //we Must find out something here to identify the component that fired this, like class names for example,
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
		
		return handleGeneralKeys(KeyCodes.translateGWTcode(event.getNativeKeyCode()), event.isShiftKeyDown(), event.isControlKeyDown(), event.isAltKeyDown(), false, true);

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

				case GWTKeycodes.KEY_PAGEUP:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(0, (int) (height * base),
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case GWTKeycodes.KEY_PAGEDOWN:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(0, -(int) (height * base),
							AbstractEuclidianController.MOVE_VIEW);
					return true;
					
				case GWTKeycodes.KEY_INSERT:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove((int) (height * base), 0,
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case GWTKeycodes.KEY_HOME:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(-(int) (height * base), 0,
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case GWTKeycodes.KEY_DOWN:
					//if (app.isUsingFullGui() && app.getGuiManager().noMenusOpen())
					{
						ev.rememberOrigins();
						ev.setCoordSystemFromMouseMove(0,
								(int) (height / 100.0 * base),
								AbstractEuclidianController.MOVE_VIEW);
						return true;
					}
				case GWTKeycodes.KEY_UP:
					//if (app.isUsingFullGui() && app.getGuiManager().noMenusOpen())
					{
						ev.rememberOrigins();
						ev.setCoordSystemFromMouseMove(0,
								-(int) (height / 100.0 * base),
								AbstractEuclidianController.MOVE_VIEW);
						return true;
					}
				case GWTKeycodes.KEY_LEFT:
					ev.rememberOrigins();
					ev.setCoordSystemFromMouseMove(
							-(int) (width / 100.0 * base), 0,
							AbstractEuclidianController.MOVE_VIEW);
					return true;
				case GWTKeycodes.KEY_RIGHT:
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
		case GWTKeycodes.KEY_PAGEUP:
			Iterator<GeoElement> it = geos.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geo.setLayer(geo.getLayer() + 1);
			}
			break;

		case GWTKeycodes.KEY_PAGEDOWN:
			it = geos.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geo.setLayer(geo.getLayer() - 1);
			}
			break;

		case GWTKeycodes.KEY_F3:
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

		case GWTKeycodes.KEY_F4:
			// F4 key: copy value to input field
			handleFunctionKeyForAlgebraInput(4, geos.get(0));
			return true;

		case GWTKeycodes.KEY_F5:
			// F5 key: copy label to input field
			handleFunctionKeyForAlgebraInput(5, geos.get(0));
			return true;

		case GWTKeycodes.KEY_DELETE:
			// G.Sturr 2010-5-2: let the spreadsheet handle delete
			//if (app.getGuiManager().getSpreadsheetView().hasFocus())
			//	return false;
			// DELETE selected objects
			if (!app.isApplet() || app.isRightClickEnabled()) {
				app.deleteSelectedObjects();
				return true;
			}

		case GWTKeycodes.KEY_BACKSPACE:
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
		case GWTKeycodes.KEY_UP:

			// make sure arrow keys work in menus
			//if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen())
			//	return false;

			changeVal = base;
			moved = handleArrowKeyMovement(geos, 0, changeVal, 0);
			break;

		case GWTKeycodes.KEY_DOWN:

			// make sure arrow keys work in menus
			//if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen())
			//	return false;

			changeVal = -base;
			moved = handleArrowKeyMovement(geos, 0, changeVal, 0);
			break;

		case GWTKeycodes.KEY_RIGHT:

			// make sure arrow keys work in menus
			//if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen())
			//	return false;

			changeVal = base;
			moved = handleArrowKeyMovement(geos, changeVal, 0, 0);
			break;

		case GWTKeycodes.KEY_LEFT:

			// make sure arrow keys work in menus
			//if (app.isUsingFullGui() && !app.getGuiManager().noMenusOpen())
			//	return false;

			changeVal = -base;
			moved = handleArrowKeyMovement(geos, changeVal, 0, 0);
			break;

		case GWTKeycodes.KEY_PAGEUP:
			changeVal = base;
			moved = handleArrowKeyMovement(geos, 0, 0, changeVal);
			break;

		case GWTKeycodes.KEY_PAGEDOWN:
			changeVal = -base;
			moved = handleArrowKeyMovement(geos, 0, 0, changeVal);
			break;

		}

		if (moved)
			return true;

		boolean vertical = true;

		// F2, PLUS, MINUS keys
		switch (keyCode) {
		case GWTKeycodes.KEY_F2:
			// handle F2 key to start editing first selected element
			if (app.isUsingFullGui()) {
				//app.getGuiManager().startEditing(geos.get(0));
				return true;
			}
			break;

		case GWTKeycodes.KEY_NUMPADPLUS:
		//case MyKeyCodes.KEY_ADD: // can be own key on some keyboard
		case GWTKeycodes.KEY_EQUALS: // same key as plus (on most keyboards)
		case GWTKeycodes.KEY_UP:
			changeVal = base;
			vertical = true;
			break;
		case GWTKeycodes.KEY_RIGHT:
			changeVal = base;
			vertical = false;
			break;

		case GWTKeycodes.KEY_MINUS:
		case GWTKeycodes.KEY_NUMPADMINUS:
		//case MyKeyCodes.KEY_SUBTRACT:
		case GWTKeycodes.KEY_DOWN:
			changeVal = -base;
			vertical = true;
			break;
		case GWTKeycodes.KEY_LEFT:
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

	@Override
    protected boolean handleCtrlShiftN(boolean isAltDown) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    protected boolean handleTab(boolean isControlDown, boolean isShiftDown) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    protected boolean handleEnter() {
	    // TODO Auto-generated method stub
	    return false;
    }

}
